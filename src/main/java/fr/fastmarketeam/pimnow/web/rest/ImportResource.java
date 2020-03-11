package fr.fastmarketeam.pimnow.web.rest;

import com.google.common.net.HttpHeaders;
import com.monitorjbl.xlsx.StreamingReader;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Mapping;
import fr.fastmarketeam.pimnow.security.AuthoritiesConstants;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.ImportService;
import fr.fastmarketeam.pimnow.service.MappingService;
import fr.fastmarketeam.pimnow.service.UserExtraService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * REST controller for managing imports.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
public class ImportResource {

    private final Logger log = LoggerFactory.getLogger(ImportResource.class);
    private final UserExtraService userExtraService;
    private final MappingService mappingService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ImportService importService;

    public ImportResource(ImportService importService, UserExtraService userExtraService, MappingService mappingService) {
        this.importService = importService;
        this.userExtraService = userExtraService;
        this.mappingService = mappingService;
    }

    private String generateDate(){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return formater.format(new Date());
    }

    @PostMapping("/import")
    @Transactional
    public ResponseEntity<Resource> importProducts(@RequestParam(value = "idMapping", required = true) Long idMapping, @RequestParam(value = "file", required = true) MultipartFile file) throws IOException {
        Customer customer = userExtraService.getCustomer(SecurityUtils.getCurrentUserLogin());
        Mapping currentMapping = mappingService.getMapping(idMapping, customer.getId());

        Workbook workbookReceived = StreamingReader.builder().bufferSize(4096).rowCacheSize(100).open(file.getInputStream());
        Sheet sheetReceived = workbookReceived.getSheetAt(0);

        // The excel file containing informations on the import that we will send to the user
        XSSFWorkbook workbookToSend = new XSSFWorkbook();
        XSSFSheet xssfSheetToSend = workbookToSend.createSheet();

        importService.parseImportFile(customer, currentMapping, sheetReceived, xssfSheetToSend);

        String fileToSendName = "response-result-import-" + generateDate() + ".xlsx";
        File fileOut = getOutputFile(workbookToSend, fileToSendName);
        Resource resource = new InputStreamResource(new FileInputStream(fileOut));
        try {
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileToSendName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .contentLength(fileOut.length())
                .body(resource);
        } finally {
            workbookToSend.close();
            fileOut.delete();
        }

    }

    /**
     * Create the output file and write the content of the workbook in it
     * @param workbookToSend
     * @param fileToSendName
     * @return
     * @throws IOException
     */
    private File getOutputFile(XSSFWorkbook workbookToSend, String fileToSendName) throws IOException {
        File fileOut = new File("/tmp/" + fileToSendName);
        FileOutputStream streamOut = new FileOutputStream(fileOut);
        workbookToSend.write(streamOut);
        workbookToSend.close();
        streamOut.close();
        return fileOut;
    }
}
