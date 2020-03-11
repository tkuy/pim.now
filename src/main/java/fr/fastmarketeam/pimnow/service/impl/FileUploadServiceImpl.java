package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.config.SftpConfig.UploadGateway;
import fr.fastmarketeam.pimnow.service.FileUploadService;
import fr.fastmarketeam.pimnow.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class FileUploadServiceImpl implements FileUploadService {

    private final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    private StorageService storageService;

    private UploadGateway gateway;

    public FileUploadServiceImpl(StorageService storageService, ApplicationContext context) {
        this.storageService = storageService;
        this.gateway = context.getBean(UploadGateway.class);
    }

    @Override
    public void transferFile(MultipartFile multipartFile) throws IOException {
        transferFile(multipartFile, multipartFile.getOriginalFilename());
    }

    @Override
    public void transferFile(MultipartFile multipartFile, String newName) throws IOException {
        File storedFile = null;
        try {
            storedFile = storageService.store(multipartFile, newName);
            gateway.upload(storedFile);
        } finally {
            if(storedFile!=null) {
                boolean isDeleted = storedFile.delete();
                if (!isDeleted) throw new IllegalStateException("File is not present");
            }
        }
    }

    @Override
    public void createFile(File file) {
        this.gateway.upload(file);
    }
}
