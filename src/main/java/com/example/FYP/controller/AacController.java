package com.example.FYP.controller;

import com.example.FYP.dto.*;
import com.example.FYP.model.AacCategory;
import com.example.FYP.model.AacIcon;
import com.example.FYP.service.AacService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aac")
public class AacController {

    private final AacService aacService;

    public AacController(AacService aacService) {
        this.aacService = aacService;
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<AacCategory>>> getAllCategories(@RequestParam(required = false) Long childId) {
        return ResponseEntity.ok(ApiResponse.success(aacService.getAllCategories(childId), 200));
    }

    @GetMapping("/categories/{categoryId}/icons")
    public ResponseEntity<ApiResponse<List<AacIcon>>> getIconsByCategory(@PathVariable Long categoryId, @RequestParam(required = false) Long childId) {
        return ResponseEntity.ok(ApiResponse.success(aacService.getIconsByCategory(categoryId, childId), 200));
    }

    @PostMapping(value = "/categories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PARENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<AacCategory>> createCategory(
            @RequestPart("category") @Valid CreateCategoryRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) throws IOException {
        return ResponseEntity.status(201).body(ApiResponse.success(aacService.createCategory(principal.getName(), request, file), 201));
    }

    @PostMapping(value = "/icons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PARENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<AacIcon>> createIcon(
            @RequestPart("icon") @Valid CreateIconRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) throws IOException {
        return ResponseEntity.status(201).body(ApiResponse.success(aacService.createIcon(principal.getName(), request, file), 201));
    }

    @PostMapping("/log")
    @PreAuthorize("hasRole('CHILD')")
    public ResponseEntity<ApiResponse<Void>> logUsage(@Valid @RequestBody LogUsageRequest request, Principal principal) {
        aacService.logUsage(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(null, 200));
    }

    @GetMapping("/analytics/{childId}")
    @PreAuthorize("hasAnyRole('PARENT', 'THERAPIST', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAnalytics(@PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.success(aacService.getUsageAnalytics(childId), 200));
    }
}
