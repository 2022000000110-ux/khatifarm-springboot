package com.example.khatifarm.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    private final String uploadDir =
            "src/main/resources/static/uploads/products";

    public String uploadImage(MultipartFile file) throws IOException {

        // No file selected
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Get original file name
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IOException("Invalid file name.");
        }

        // Check file extension
        String extension = "";

        int dotIndex = originalFileName.lastIndexOf(".");

        if (dotIndex > 0) {
            extension = originalFileName
                    .substring(dotIndex)
                    .toLowerCase();
        }

        // Allow only image files
        if (!extension.equals(".jpg")
                && !extension.equals(".jpeg")
                && !extension.equals(".png")
                && !extension.equals(".webp")) {

            throw new IOException(
                    "Only JPG, JPEG, PNG and WEBP images are allowed."
            );
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique file name
        String fileName = UUID.randomUUID() + extension;

        Path filePath = uploadPath.resolve(fileName);

        // Save image
        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        // This path will be stored in MongoDB
        return "/uploads/products/" + fileName;
    }
}