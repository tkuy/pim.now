package fr.fastmarketeam.pimnow.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;
public interface StorageService {

    void init();

    File store(MultipartFile file) throws IOException;

    Stream<Path> loadAll() throws IOException;

    Path load(String filename);

    File store(MultipartFile file, String newName) throws IOException;

}
