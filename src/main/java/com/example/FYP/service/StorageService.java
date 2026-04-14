package com.example.FYP.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket:aac-media}")
    private String bucketName;

    private final RestClient restClient;

    public StorageService() {
        this.restClient = RestClient.builder().build();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        restClient.post()
                .uri(uploadUrl)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getBytes())
                .retrieve()
                .toBodilessEntity();

        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
    }
}
