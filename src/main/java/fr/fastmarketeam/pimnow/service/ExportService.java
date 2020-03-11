package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Mapping;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;

public interface ExportService {
    void exportProducts(Customer customer, Mapping currentMapping, String[] listOfIdFProducts, XSSFSheet xssfSheetToSend);
}
