package com.mrnaif.javalab.service;

public interface StorageService {

    void init();

    String store(String filename, byte[] data);

    byte[] load(String filename);

    void deleteAll();

}