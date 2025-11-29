package com.bookmanagement.bookmanagementbackend.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    String storeFile(MultipartFile file) throws IOException;
    String getFileUrl(String fileName);
    boolean deleteFile(String fileName);
}
