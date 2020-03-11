package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.config.Constants;
import fr.fastmarketeam.pimnow.domain.*;
import fr.fastmarketeam.pimnow.domain.enumeration.AttributType;
import fr.fastmarketeam.pimnow.repository.*;
import fr.fastmarketeam.pimnow.service.ExportService;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExportServiceImpl implements ExportService {
    private final Logger log = LoggerFactory.getLogger(ImportServiceImpl.class);

    private ProductRepository productRepository;

    private AttributValueRepository attributValueRepository;

    public ExportServiceImpl(ProductRepository productRepository, AttributValueRepository attributValueRepository) {
        this.productRepository = productRepository;
        this.attributValueRepository = attributValueRepository;
    }

    public void exportProducts(Customer customer, Mapping currentMapping, String[] listOfIdFProducts, XSSFSheet xssfSheetToSend) {
        Map<String, Integer> map = createExcelHeader(currentMapping, xssfSheetToSend);
        int rowIndex = 1;

        for (String idf: listOfIdFProducts) {
            XSSFRow row = xssfSheetToSend.createRow(rowIndex);
            Optional<Product> optProduct = productRepository.findByIdFIgnoreCaseAndCustomerIdAndIsDeletedIsFalse(idf, customer.getId());
            if (!optProduct.isPresent()) {
                log.error("Product could not be found ("+ idf + ")");
                throw new IllegalStateException("Product could not be found");
            }
            Product p = optProduct.get();

            addSpecialAttributesOfProductsInExcel(map, row, p, currentMapping.getSeparator());

            List<AttributValue> listOfValues = attributValueRepository.findByProductId(p.getId()).stream().filter(opt -> opt.isPresent()).map(Optional::get).collect(Collectors.toList());

            for (AttributValue av : listOfValues) {
                addValuesInRows(map, row, av, currentMapping.getSeparator());
            }

            rowIndex++;
        }
    }

    private void addValuesInRows(Map<String, Integer> map, XSSFRow row, AttributValue av, String delimitor) {
        Attribut attr =  av.getAttribut();
        String idFAttribut = attr.getIdF();
        AttributType attrType = attr.getType();
        if (!map.containsKey(idFAttribut))
            return;
        String value = av.getValue();
        if (attrType == AttributType.MULTIPLE_VALUE) {
            value = value.replace(Constants.STANDARD_SEPARATOR, delimitor);
        }
        row.createCell(map.get(idFAttribut), CellType.STRING).setCellValue(value);
    }

    private void addSpecialAttributesOfProductsInExcel(Map<String, Integer> map, XSSFRow row, Product p, String delimitor) {
        row.createCell(map.get(Attribut.idFAttributIdF), CellType.STRING).setCellValue(p.getIdF());
        row.createCell(map.get(Attribut.idFAttributNom), CellType.STRING).setCellValue(p.getNom());
        row.createCell(map.get(Attribut.idFAttributCategorie), CellType.STRING).setCellValue(p.getCategories().stream().map(Category::getIdF).collect(Collectors.joining(delimitor)));
        row.createCell(map.get(Attribut.idFAttributFamille), CellType.STRING).setCellValue(p.getFamily().getIdF());
        row.createCell(map.get(Attribut.idFAttributDescription), CellType.STRING).setCellValue(p.getDescription());
    }

    private HashMap<String, Integer> createExcelHeader(Mapping currentMapping, XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);
        HashMap<String, Integer> map = new HashMap<>();
        int index = 0;
        for (Association a : currentMapping.getAssociations()) {
            row.createCell(index, CellType.STRING).setCellValue(a.getColumn());
            map.put(a.getIdFAttribut(), index);
            index ++;
        }

        return map;
    }
}
