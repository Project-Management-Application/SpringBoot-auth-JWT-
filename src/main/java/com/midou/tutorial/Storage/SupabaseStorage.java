package com.midou.tutorial.Storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

@Service
public class SupabaseStorage {
    private final WebClient webClient;
    private final String bucketName;
    private final String supabaseUrl;

    public SupabaseStorage(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-key}") String serviceKey, // Use service key for write operations
            @Value("${supabase.bucket.name}") String bucketName) {
        this.supabaseUrl = supabaseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader("Authorization", "Bearer " + serviceKey) // Use service key
                .build();
        this.bucketName = bucketName;

    }

    public String uploadImage(MultipartFile file, String fileName) throws IOException {
        byte[] fileBytes = file.getBytes();
        String uploadPath = bucketName + "/" + fileName;

        try {
            webClient.post()
                    .uri("/storage/v1/object/" + uploadPath)
                    .contentType(MediaType.parseMediaType(file.getContentType())) // Set the correct Content-Type
                    .body(BodyInserters.fromResource(new ByteArrayResource(fileBytes)))
                    .retrieve()
                    .bodyToMono(String.class) // Expect a response body for better error handling
                    .block();
        } catch (Exception e) {
            System.err.println("Supabase Error: " + e.getMessage());
            throw new IOException("Failed to upload image to Supabase: " + e.getMessage(), e);
        }

        String url = String.format("%s/storage/v1/object/public/%s", supabaseUrl, uploadPath);
        System.out.println("Generated URL: " + url);
        return url;
    }

    public List<String> listBackgroundImages() {
        String folderPath = "Background_Images";

        // Create the request body
        ListRequestBody requestBody = new ListRequestBody();
        requestBody.setPrefix(folderPath + "/");

        // Use Supabase Storage API to list files in the Background_Images folder
        FileObject[] files = webClient.post()
                .uri("/storage/v1/object/list/" + bucketName)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(FileObject[].class)
                .block();

        if (files == null) {
            throw new RuntimeException("Failed to fetch background images: No files returned");
        }

        // Generate public URLs for each file
        return List.of(files).stream()
                .filter(file -> !file.getName().isEmpty()) // Filter out folder entries
                .map(file -> String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucketName, folderPath + "/" + file.getName()))
                .toList();
    }

    // Helper classes to parse the Supabase request and response
    @lombok.Data
    private static class ListRequestBody {
        private String prefix;
    }

    @lombok.Data
    private static class FileObject {
        private String name;
        private String id;
        private String created_at;
        private String updated_at;
        private String last_accessed_at;
        private Object metadata;
    }

    public String uploadFileToSupabase(MultipartFile file, String fileName) throws IOException {
        // Check if the file is empty or null
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Convert the file to a byte array
        byte[] fileBytes = file.getBytes();

        // Define the upload path (e.g., include a folder structure in the path)
        String uploadPath = bucketName + "/" + fileName;

        try {
            // Make the request to Supabase to upload the file
            webClient.post()
                    .uri("/storage/v1/object/" + uploadPath)
                    .contentType(MediaType.parseMediaType(file.getContentType())) // Set the correct Content-Type
                    .body(BodyInserters.fromResource(new ByteArrayResource(fileBytes))) // Attach file as body
                    .retrieve()
                    .bodyToMono(String.class) // Expect a response body for better error handling
                    .block();  // Block until the upload completes
        } catch (Exception e) {
            // Log the error and rethrow an exception if something goes wrong
            System.err.println("Supabase Error: " + e.getMessage());
            throw new IOException("Failed to upload file to Supabase: " + e.getMessage(), e);
        }

        // Construct the public URL for the uploaded file
        String fileUrl = String.format("%s/storage/v1/object/public/%s", supabaseUrl, uploadPath);
        System.out.println("File uploaded successfully. URL: " + fileUrl);

        // Return the generated URL
        return fileUrl;
    }

    public void deleteFileFromSupabase(String fileUrl) throws IOException {
        // Extract the relative path from the file URL
        String filePath = fileUrl.replaceFirst("https?://[^/]+", ""); // Remove the domain part of the URL
        filePath = filePath.replace("/storage/v1/object/public", ""); // Remove the public path

        try {
            // Make the DELETE request to Supabase to remove the file
            webClient.delete()
                    .uri("/storage/v1/object/" + filePath)  // Use the relative file path
                    .retrieve()
                    .bodyToMono(Void.class) // No body is expected on successful deletion
                    .block(); // Block until the operation completes
            System.out.println("File deleted successfully from Supabase: " + filePath);
        } catch (Exception e) {
            // Log the error and rethrow an exception if something goes wrong
            System.err.println("Supabase Error: " + e.getMessage());
            throw new IOException("Failed to delete file from Supabase: " + e.getMessage(), e);
        }
    }

}