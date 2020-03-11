package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Mapping;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.Optional;

public interface ImportService {
    void parseImportFile(Customer customer, Mapping currentMapping, Sheet sheetReceived, XSSFSheet xssfSheetToSend);
}
