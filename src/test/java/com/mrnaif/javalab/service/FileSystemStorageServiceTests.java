package com.mrnaif.javalab.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import com.mrnaif.javalab.config.StorageProperties;
import com.mrnaif.javalab.exception.StorageException;
import com.mrnaif.javalab.exception.StorageFileNotFoundException;
import com.mrnaif.javalab.serviceimpl.FileSystemStorageService;

public class FileSystemStorageServiceTests {

    private StorageProperties properties = new StorageProperties();
    private FileSystemStorageService service;

    @BeforeEach
    public void init() {
        properties.setLocation("target/files/" + Math.abs(new Random().nextLong()));
        service = new FileSystemStorageService(properties);
        service.init();
    }

    @Test
    public void emptyUploadLocation() {
        service = null;
        properties.setLocation("");
        assertThrows(StorageException.class, () -> {
            service = new FileSystemStorageService(properties);
        });
    }

    @Test
    public void loadNonExistent() {
        assertThrows(StorageFileNotFoundException.class, () -> {
            service.load("foo.txt");
        });
    }

    @Test
    public void saveAndLoad() {
        service.store("foo.txt", "Hello, World".getBytes());
        assertDoesNotThrow(() -> {
            service.load("foo.txt");
        });
    }

    @Test
    public void saveRelativePathNotPermitted() {
        assertThrows(StorageException.class, () -> {
            service.store("../foo.txt", "Hello, World".getBytes());
        });
    }

    @Test
    public void saveAbsolutePathNotPermitted() {
        assertThrows(StorageException.class, () -> {
            service.store("/etc/passwd", "Hello, World".getBytes());
        });
    }

    @Test
    @EnabledOnOs({ OS.LINUX })
    public void saveAbsolutePathInFilenamePermitted() {
        String fileName = "\\etc\\passwd";
        service.store(fileName, "Hello, World".getBytes());
        assertTrue(Files.exists(
                Paths.get(properties.getLocation()).resolve(Paths.get(fileName))));
    }

    @Test
    public void savePermitted() {
        service.store("bar/../foo.txt", "Hello, World".getBytes());
    }

}