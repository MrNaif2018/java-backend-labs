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

class FileSystemStorageServiceTests {

    private StorageProperties properties = new StorageProperties();
    private FileSystemStorageService service;

    @BeforeEach
    public void init() {
        properties.setLocation("target/files/" + Math.abs(new Random().nextLong()));
        service = new FileSystemStorageService(properties);
        service.init();
    }

    @Test
    void emptyUploadLocation() {
        service = null;
        properties.setLocation("");
        assertThrows(StorageException.class, () -> {
            service = new FileSystemStorageService(properties);
        });
    }

    @Test
    void loadNonExistent() {
        assertThrows(StorageFileNotFoundException.class, () -> {
            service.load("foo.txt");
        });
    }

    @Test
    void saveAndLoad() {
        service.store("foo.txt", "Hello, World".getBytes());
        assertDoesNotThrow(() -> {
            service.load("foo.txt");
        });
    }

    @Test
    void saveRelativePathNotPermitted() {
        byte[] data = "Hello, World".getBytes();
        assertThrows(StorageException.class, () -> {
            service.store("../foo.txt", data);
        });
    }

    @Test
    void saveAbsolutePathNotPermitted() {
        byte[] data = "Hello, World".getBytes();
        assertThrows(StorageException.class, () -> {
            service.store("/etc/passwd", data);
        });
    }

    @Test
    @EnabledOnOs({ OS.LINUX })
    void saveAbsolutePathInFilenamePermitted() {
        String fileName = "\\etc\\passwd";
        service.store(fileName, "Hello, World".getBytes());
        assertTrue(Files.exists(
                Paths.get(properties.getLocation()).resolve(Paths.get(fileName))));
    }

    @Test
    void savePermitted() {
        assertDoesNotThrow(() -> {
            service.store("bar/../foo.txt", "Hello, World".getBytes());
        });
    }

}