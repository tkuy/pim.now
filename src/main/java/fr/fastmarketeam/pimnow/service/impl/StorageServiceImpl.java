package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;


@Service
@Transactional
public class StorageServiceImpl implements StorageService {
    private static final String SEPARATOR = "/";
    private static final String STORAGE_FOLDER = "/tmp";

    @Override
    public void init() {

    }

    @Override
    public File store(MultipartFile file) throws IOException {
        return store(file, file.getOriginalFilename());
    }

    @Override
    public File store(MultipartFile file, String newName) throws IOException {
        File folder = new File(STORAGE_FOLDER);
        if(!folder.exists()) {
            folder.mkdir();
        }
        String path = STORAGE_FOLDER + SEPARATOR + newName;
        File convFile = new File(path);
        try(InputStream is = file.getInputStream()) {
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    @Override
    public Stream<Path> loadAll() throws IOException {
        File storageFolder = new File(STORAGE_FOLDER + SEPARATOR);
        if(storageFolder.isDirectory()) {
            return Arrays.stream(storageFolder.listFiles()).map(File::toPath);
        } else {
            throw new IOException("Not a folder");
        }
    }

    @Override
    public Path load(String filename) {
        return Paths.get(STORAGE_FOLDER + SEPARATOR + filename);
    }
}
