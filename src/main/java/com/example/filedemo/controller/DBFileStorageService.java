package com.example.filedemo.controller;

import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class DBFileStorageService {

    @Autowired
    private DBFileRepository dbFileRepository;

    public DBFile storeFile(File file, String uniqueFile, String fileName) {

        try {

            DBFile dbFile = new DBFile(uniqueFile, fileName, "docx",  Files.readAllBytes(file.toPath()));

            return dbFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public DBFile getFile(String fileId) {
        return dbFileRepository.findById(fileId)
                .orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
    }
}