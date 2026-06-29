package com.example.FYP.controller;

import com.example.FYP.dto.*;
import com.example.FYP.model.AacCategory;
import com.example.FYP.model.AacIcon;
import com.example.FYP.service.AacService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final AacService aacService;

    public AacController(AacService aacService) {
        this.aacService = aacService;
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(@RequestParam(required = false) Long childId) {
        return ResponseEntity.ok(ApiResponse.success(aacService.getAllCategories(childId), 200, "Categories fetched successfully"));
    }

    @GetMapping("/categories/{categoryId}/icons")
    public ResponseEntity<ApiResponse<List<IconResponse>>> getIconsByCategory(@PathVariable Long categoryId, @RequestParam(required = false) Long childId) {
        return ResponseEntity.ok(ApiResponse.success(aacService.getIconsByCategory(categoryId, childId), 200, "Icons fetched successfully"));
    }

    @PostMapping(value = "/categories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PARENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestPart("category") @Valid CreateCategoryRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) throws IOException {
        return ResponseEntity.status(201).body(ApiResponse.success(aacService.createCategory(principal.getName(), request, file), 201, "Category created successfully"));
    }

    @PutMapping(value = "/categories/{categoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PARENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long categoryId,
            @RequestPart(value = "category", required = false) UpdateCategoryRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) throws IOException {
        return ResponseEntity.ok(ApiResponse.success(aacService.updateCategory(principal.getName(), categoryId, request, file), 200, "Category updated successfully"));
    }

    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasAnyRole('PARENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long categoryId,
            Principal principal) {
        aacService.deleteCategory(principal.getName(), categoryId);
        return ResponseEntity.ok(ApiResponse.success(null, 200, "Category deleted successfully"));
    }

    @PostMapping(value = "/icons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PARENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<IconResponse>> createIcon(
            @RequestPart("icon") @Valid CreateIconRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) throws IOException {
        return ResponseEntity.status(201).body(ApiResponse.success(aacService.createIcon(principal.getName(), request, file), 201, "Icon created successfully"));
    }

    @PutMapping(value = "/icons/{iconId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PARENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<IconResponse>> updateIcon(
            @PathVariable Long iconId,
            @RequestPart(value = "icon", required = false) UpdateIconRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) throws IOException {
        return ResponseEntity.ok(ApiResponse.success(aacService.updateIcon(principal.getName(), iconId, request, file), 200, "Icon updated successfully"));
    }

    @DeleteMapping("/icons/{iconId}")
    @PreAuthorize("hasAnyRole('PARENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteIcon(
            @PathVariable Long iconId,
            Principal principal) {
        aacService.deleteIcon(principal.getName(), iconId);
        return ResponseEntity.ok(ApiResponse.success(null, 200, "Icon deleted successfully"));
    }

    @PostMapping("/log")
    @PreAuthorize("hasRole('CHILD')")
    public ResponseEntity<ApiResponse<Void>> logUsage(@Valid @RequestBody LogUsageRequest request, Principal principal) {
        aacService.logUsage(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(null, 200, "Usage logged successfully"));
    }

    @GetMapping("/analytics/{childId}")
    @PreAuthorize("hasAnyRole('PARENT', 'THERAPIST', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAnalytics(@PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.success(aacService.getUsageAnalytics(childId), 200, "Analytics fetched successfully"));
    }
}
