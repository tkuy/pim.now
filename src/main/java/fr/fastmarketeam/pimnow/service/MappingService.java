package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Mapping;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public interface MappingService {
    Mapping getMapping(Long idMapping, Long customerId);
}
