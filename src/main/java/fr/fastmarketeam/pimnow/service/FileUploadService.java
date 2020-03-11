package fr.fastmarketeam.pimnow.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileUploadService {
    void transferFile(MultipartFile multipartFile) throws IOException;
    void transferFile(MultipartFile multipartFile, String newName) throws IOException;
    void createFile(File file);
}
