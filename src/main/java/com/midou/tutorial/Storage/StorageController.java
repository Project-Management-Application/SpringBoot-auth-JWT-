package com.midou.tutorial.Storage;




import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@CrossOrigin(origins = "${frontend.url}")
@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    private final SupabaseStorage supabaseStorage;

    @GetMapping("/backgrounds")
    public ResponseEntity<List<String>> getBackgroundImages() {
        try {
            List<String> backgroundImages = supabaseStorage.listBackgroundImages();
            return ResponseEntity.ok(backgroundImages);
        } catch (Exception e) {
            System.err.println("Error fetching background images: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}