package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.config.Constants;
import fr.fastmarketeam.pimnow.domain.*;
import fr.fastmarketeam.pimnow.domain.enumeration.AttributType;
import fr.fastmarketeam.pimnow.repository.*;
import fr.fastmarketeam.pimnow.service.ImportService;
import fr.fastmarketeam.pimnow.repository.search.AttributValueSearchRepository;
import fr.fastmarketeam.pimnow.repository.search.ProductSearchRepository;
import fr.fastmarketeam.pimnow.service.errors.ImportRequiredHeaderNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing Imports.
 */
@Service
@Transactional
public class ImportServiceImpl implements ImportService {

    public static final String EVERYTHING_WENT_FINE = "Everything went fine";
    private final Logger log = LoggerFactory.getLogger(ImportServiceImpl.class);

    private final FamilyRepository familyRepository;

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    private final AttributRepository attributRepository;

    private final AttributValueRepository attributValueRepository;

    private final AttributValueSearchRepository attributValueSearchRepository;

    public ImportServiceImpl(FamilyRepository familyRepository, CategoryRepository categoryRepository, ProductRepository productRepository, ProductSearchRepository productSearchRepository, AttributRepository attributRepository, AttributValueRepository attributValueRepository, AttributValueSearchRepository attributValueSearchRepository) {
        this.familyRepository = familyRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
        this.attributRepository = attributRepository;
        this.attributValueRepository = attributValueRepository;
        this.attributValueSearchRepository = attributValueSearchRepository;
    }

    private Optional<AttributValue> parseStandardAttribute(Product newProduct, Cell cell, String separator, String stringCellValue, Attribut attribut) {
        AttributType type = attribut.getType();
        AttributValue newAttributValue = new AttributValue();
        if(newProduct.getId() != null){
            Optional<AttributValue> existingAttributValue = attributValueRepository.findByAttributIdAndProductId(attribut.getId(), newProduct.getId());
            existingAttributValue.ifPresent(attributValue -> newAttributValue.setId(attributValue.getId()));
        }
        switch (type){
            case TEXT:
                newAttributValue.setAttribut(attribut);
                newAttributValue.setValue(stringCellValue);
                return Optional.of(newAttributValue);
            case VALUES_LIST:
                break;
            case MULTIPLE_VALUE:
                String valueWithStandardSeparator = Arrays.asList(stringCellValue.split(separator)).stream().filter(str -> !str.trim().isEmpty()).collect(Collectors.joining(Constants.STANDARD_SEPARATOR));
                newAttributValue.setAttribut(attribut);
                newAttributValue.setValue(valueWithStandardSeparator);
                return Optional.of(newAttributValue);
            case NUMBER:
                try{
                    double cellValue = cell.getNumericCellValue();
                    newAttributValue.setAttribut(attribut);
                    newAttributValue.setValue(Double.toString(cellValue));
                    return Optional.of(newAttributValue);
                } catch(IllegalStateException | NumberFormatException e) {
                    throw new IllegalStateException("This value could not be parsed in number");
                }
            default:
                throw new IllegalStateException("Could not found the type of the attribute");
        }
        return Optional.empty();
    }

    /**
     * This function parse the Cells value and return the optional containing the attribute if it is not a special attribute (IdF, Name, Description, Category, Family)
     * @param listIdFHeader
     * @param newProduct
     * @param index
     * @param cell
     * @param separator
     * @return the optional containing the value of the attribute
     */
    private Optional<AttributValue> getAttribut(List<String> listIdFHeader, Product newProduct, int index, Cell cell, String separator, Customer customer) {
        Long customerId = customer.getId();
        if (cell == null) return Optional.empty();
        DataFormatter formatter = new DataFormatter();
        String stringCellValue = formatter.formatCellValue(cell);
        switch (listIdFHeader.get(index)) {
            case Attribut.idFAttributIdF:
                Optional<Product> potentialExistingproduct = productRepository.findByIdFIgnoreCaseAndCustomerIdAndIsDeletedIsFalse(stringCellValue, customerId);
                potentialExistingproduct.ifPresent(product -> newProduct.setId(product.getId()));
                potentialExistingproduct.ifPresent(product -> newProduct.setFamily(product.getFamily()));
                newProduct.setIdF(stringCellValue);
                return Optional.empty();
            case Attribut.idFAttributNom:
                newProduct.setNom(stringCellValue);
                return Optional.empty();
            case Attribut.idFAttributDescription:
                newProduct.setDescription(stringCellValue);
                return Optional.empty();
            case Attribut.idFAttributFamille:
                if(newProduct.getFamily() != null && !newProduct.getFamily().getIdF().equals(stringCellValue)){
                    throw new IllegalStateException("You can't change the family of an existing product");
                }
                Optional<Family> family = familyRepository.findByIdFIgnoreCaseAndCustomerIdAndDeletedIsFalse(stringCellValue, customerId);
                if (family.isPresent()) {
                    newProduct.setFamily(family.get());
                } else {
                    throw new IllegalStateException("Family doesn't exist (" + stringCellValue + ")");
                }
                return Optional.empty();
            case Attribut.idFAttributCategorie:
                String cellValueWithoutSpaces = stringCellValue.replaceAll("\\s+", "");
                String[] categories = cellValueWithoutSpaces.split(separator);
                if(categories.length == 1 && StringUtils.isBlank(categories[0])){
                    Optional<Category> categoryRoot = categoryRepository.findById(Long.valueOf(customer.getCategoryRoot()));
                    categoryRoot.ifPresent(newProduct::addCategory);
                }
                else if(categories.length == 1){
                    Optional<Category> category = categoryRepository.findByIdFIgnoreCaseAndCustomerIdAndDeletedIsFalse(stringCellValue, customerId);
                    if (category.isPresent()) {
                        newProduct.addCategory(category.get());
                    } else {
                        throw new IllegalStateException("This category doesn't exist (" + stringCellValue + ")");
                    }
                } else {
                    for(String categoryToAdd : categories){
                        Optional<Category> category = categoryRepository.findByIdFIgnoreCaseAndCustomerIdAndDeletedIsFalse(categoryToAdd, customerId);
                        if (category.isPresent()) {
                            newProduct.addCategory(category.get());
                        } else {
                            throw new IllegalStateException("One of the category doesn't exist (" + categoryToAdd + ")");
                        }
                    }
                }
                return Optional.empty();
            case "":
                return Optional.empty();
            default:
                Optional<Attribut> attributOpt = attributRepository.findByIdFIgnoreCaseAndCustomerId(listIdFHeader.get(index), customerId);
                if(attributOpt.isPresent()){
                    Optional<AttributValue> newAttributValue = parseStandardAttribute(newProduct, cell, separator, stringCellValue, attributOpt.get());
                    if (newAttributValue.isPresent()) return newAttributValue;
                } else {
                    throw new IllegalStateException("Attribute not found (" + listIdFHeader.get(index) + ")");
                }
                return Optional.empty();
        }
    }

    /**
     * Add the value of the cell parameter (Source file) to the cell at the indexCell of the row (Response file)
     * @param newRow The row of the response file
     * @param indexCell The index in which the cell will be inserted
     * @param cell the source cell that will be used
     */
    private void addCellInResponseFile(Row newRow, int indexCell, Cell cell) {
        Cell cellToSend = newRow.createCell(indexCell);
        if (cell == null) {
            return;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            cellToSend.setCellValue(cell.getNumericCellValue());
        } else {
            cellToSend.setCellValue(cell.getStringCellValue());
        }
    }

    /**
     * Search for an association with a column name corresponding to the Cell and compute the mapRequiredHeaders and listIdFHeader accordingly
     * @param listIdFHeader
     * @param mapRequiredHeaders
     * @param currentMapping
     * @param cell
     * @return
     */
    private boolean applyAssociationOnCell(List<String> listIdFHeader, Map<String, Boolean> mapRequiredHeaders, Mapping currentMapping, Cell cell) {
        boolean found = false;
        for(Association a : currentMapping.getAssociations()){
            if(cell.getStringCellValue().toLowerCase().equals(a.getColumn().toLowerCase())){
                if (listIdFHeader.contains(a.getIdFAttribut())) {
                    throw new IllegalStateException("The column : " + cell.getStringCellValue() + "is duplicated");
                }

                listIdFHeader.add(a.getIdFAttribut());
                mapRequiredHeaders.computeIfPresent(a.getIdFAttribut(), (k, v) -> true);
                found = true;
                break;
            }
        }
        return found;
    }


    /**
     * Iterate through the first row of an import file to :
     * - check and set true in corresponding entry of the map of required headers
     * - add the column title in the listIdFHeader
     * - create the corresponding header cell in the response xlsx file
     * @param headerRow
     * @param listIdFHeader
     * @param mapRequiredHeaders
     * @param currentMapping
     * @param row
     */
    private void parseHeaderLine(Row headerRow, List<String> listIdFHeader, Map<String, Boolean> mapRequiredHeaders, Mapping currentMapping, Row row) {
        int total = row.getLastCellNum();
        for(int indexCell = 0; indexCell < total; indexCell++) {
            Cell cell = row.getCell(indexCell);
            if(! applyAssociationOnCell(listIdFHeader, mapRequiredHeaders, currentMapping, cell)){
                listIdFHeader.add("");
            }
            addCellInResponseFile(headerRow, indexCell, cell);
        }
    }

    private void sendRequiredException(List<Map.Entry<String, Boolean>> requiredHeaderAbsent) {
        StringBuilder stb = new StringBuilder();
        for(Map.Entry<String, Boolean> e : requiredHeaderAbsent){
            stb.append(e.getKey()).append(", ");
        }
        stb.setLength(stb.length() - 2);
        throw new ImportRequiredHeaderNotFoundException(stb.toString());
    }

    /**
     * This function is used to instanciate a hashmap used to check if the import file has all the required column present and mapped
     * @return the map
     */
    private Map<String, Boolean> createMapRequiredHeaders() {
        Map<String, Boolean> mapRequiredHeaders = new HashMap<>();
        mapRequiredHeaders.put(Attribut.idFAttributIdF, Boolean.FALSE);
        mapRequiredHeaders.put(Attribut.idFAttributNom, Boolean.FALSE);
        mapRequiredHeaders.put(Attribut.idFAttributDescription, Boolean.FALSE);
        mapRequiredHeaders.put(Attribut.idFAttributFamille, Boolean.FALSE);
        mapRequiredHeaders.put(Attribut.idFAttributCategorie, Boolean.FALSE);
        return mapRequiredHeaders;
    }


    /**
     * Read the workbook received and save the products and attribute value and write the lines that could not be saved
     * @param customer
     * @param currentMapping
     * @param sheetReceived
     * @param xssfSheetToSend
     */
    @Override
    public void parseImportFile(Customer customer, Mapping currentMapping, Sheet sheetReceived, XSSFSheet xssfSheetToSend) {
        Row headerRow = xssfSheetToSend.createRow(0);
        List<String> listIdFHeader = new ArrayList<>();
        Map<String, Boolean> mapRequiredHeaders = createMapRequiredHeaders();
        int totalSize = 0;
        boolean isFirstRow = true;
        List<AttributValue> lstAttrValue = new ArrayList<>();
        List<Product> lstProduct = new ArrayList<>();
        List<ProductsWithAttributes> lstProductsWithAttributes = new ArrayList<>();
        for(Row row : sheetReceived){
            if(isFirstRow){
                totalSize = row.getLastCellNum();
                parseHeaderLine(headerRow, listIdFHeader, mapRequiredHeaders, currentMapping, row);
                List<Map.Entry<String, Boolean>> requiredHeaderAbsent = mapRequiredHeaders.entrySet().stream().filter(e -> e.getValue().equals(Boolean.FALSE)).collect(Collectors.toList());
                if(!requiredHeaderAbsent.isEmpty()){
                    sendRequiredException(requiredHeaderAbsent);
                }
                isFirstRow = false;
            } else {
                if (row.getFirstCellNum() < 0) {
                    continue;
                }
                Product newProduct = new Product();
                List<AttributValue> attributValuesProduct = new ArrayList<>();
                try{
                    for (int index = 0; index < totalSize; index++) {
                        Cell cell = row.getCell(index);
                        if (cell == null || cell.getCellType() == CellType.BLANK) {
                            checkIfEmptyCellIsRequired(listIdFHeader, index, mapRequiredHeaders);
                        }
                        Optional<AttributValue> attributValue =  getAttribut(listIdFHeader, newProduct, index, cell, currentMapping.getSeparator(), customer);
                        attributValue.ifPresent(attributValuesProduct::add);
                    }
                    newProduct.setCustomer(customer);
                    if(newProduct.getCategories().size() == 0){
                        Optional<Category> categoryRoot = categoryRepository.findById(Long.valueOf(customer.getCategoryRoot()));
                        categoryRoot.ifPresent(newProduct::addCategory);
                    }
                    lstProduct.add(newProduct);
                    attributValuesProduct.forEach(av -> av.setProduct(newProduct));
                    attributValuesProduct.forEach(lstAttrValue::add);
                    lstProductsWithAttributes.add(new ProductsWithAttributes().setProduct(newProduct).setAttributValues(new HashSet<>(attributValuesProduct)));
                } catch(IllegalStateException e) {
                    String err = e.getMessage();
                    createErrorLineInResponseFile(xssfSheetToSend, row, err);
                }
            }
        }
        List<Product> savedProducts = productRepository.saveAll(lstProduct);
        lstAttrValue.forEach(attrValue -> attributValueRepository.findByAttributIdAndProductId(attrValue.getAttribut().getId(), attrValue.getProduct().getId()).ifPresent(found -> attrValue.setId(found.getId())));
        attributValueRepository.saveAll(lstAttrValue);

        writeEverythingWentFine(xssfSheetToSend);

        if (!savedProducts.isEmpty()){
            lstProductsWithAttributes.forEach(ProductsWithAttributes::initId);
            productSearchRepository.saveAll(lstProductsWithAttributes);
        }
    }

    private void writeEverythingWentFine(XSSFSheet xssfSheetToSend) {
        if (xssfSheetToSend.getLastRowNum() == 0) {
            CellStyle ct = xssfSheetToSend.getWorkbook().createCellStyle();
            ct.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            ct.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            ct.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            XSSFRow row = xssfSheetToSend.createRow(1);
            row.setRowStyle(ct);
            row.createCell(0).setCellValue(EVERYTHING_WENT_FINE);
        }
    }

    private void checkIfEmptyCellIsRequired(List<String> listIdFHeader, int index, Map<String, Boolean> mapRequiredHeaders) {
        String attr = listIdFHeader.get(index);
        if (mapRequiredHeaders.containsKey(attr) && !attr.equals(Attribut.idFAttributCategorie)) {
            throw new IllegalStateException("A required cell is empty");
        }
    }

    private void createErrorLineInResponseFile(XSSFSheet xssfSheetToSend, Row row, String err) {
        int column = xssfSheetToSend.getLastRowNum();
        Row newRow = xssfSheetToSend.createRow(column + 1);
        int totalSize = row.getLastCellNum();
        for (int index = 0; index < totalSize; index++) {
            Cell cell = row.getCell(index);
            addCellInResponseFile(newRow, index, cell);
        }
        newRow.createCell(totalSize + 1).setCellValue(err);
    }
}
