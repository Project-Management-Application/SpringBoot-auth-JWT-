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
        System.out.println("Supabase URL: " + supabaseUrl);
        System.out.println("Service Key: " + serviceKey);
        System.out.println("Bucket Name: " + bucketName);
    }

    public String uploadImage(MultipartFile file, String fileName) throws IOException {
        byte[] fileBytes = file.getBytes();
        String uploadPath = bucketName + "/" + fileName;
        System.out.println("Uploading to: " + uploadPath);
        System.out.println("File size: " + fileBytes.length + " bytes");
        System.out.println("Content type: " + file.getContentType());

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
}