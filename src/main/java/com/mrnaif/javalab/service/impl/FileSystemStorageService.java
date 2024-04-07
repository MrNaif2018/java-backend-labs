package com.mrnaif.javalab.service.impl;

import com.mrnaif.javalab.config.StorageProperties;
import com.mrnaif.javalab.exception.StorageException;
import com.mrnaif.javalab.exception.StorageFileNotFoundException;
import com.mrnaif.javalab.service.StorageService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service
public class FileSystemStorageService implements StorageService {

  private final Path rootLocation;

  @Autowired
  public FileSystemStorageService(StorageProperties properties) {

    if (properties.getLocation().trim().length() == 0) {
      throw new StorageException("File upload location can not be Empty");
    }

    this.rootLocation = Paths.get(properties.getLocation());
  }

  @Override
  public String store(String filename, byte[] data) {
    try {
      if (data.length == 0) {
        throw new StorageException("Failed to store empty file");
      }
      Path destinationFile =
          this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();
      if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
        throw new StorageException("Cannot store file outside current directory.");
      }
      try (InputStream inputStream = new java.io.ByteArrayInputStream(data)) {
        Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        return destinationFile.toString();
      }
    } catch (IOException e) {
      throw new StorageException("Failed to store file", e);
    }
  }

  @Override
  public byte[] load(String filename) {
    try {
      Path file = rootLocation.resolve(filename);
      Resource resource = new UrlResource(file.toUri());
      if (!resource.exists() || !resource.isReadable()) {
        throw new StorageFileNotFoundException("File not found: " + filename);
      }
      return Files.readAllBytes(file);
    } catch (IOException e) {
      throw new StorageException("Failed to read file", e);
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  @Override
  public void init() {
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new StorageException("Could not initialize storage", e);
    }
  }
}