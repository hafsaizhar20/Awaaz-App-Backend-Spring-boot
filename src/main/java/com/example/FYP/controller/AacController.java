package com.example.FYP.controller;

import com.example.FYP.dto.*;
import com.example.FYP.model.AacCategory;
import com.example.FYP.model.AacIcon;
import com.example.FYP.service.AacService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<List<AacCategory>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(aacService.getAllCategories(), 200));
    }

    @GetMapping("/categories/{categoryId}/icons")
    public ResponseEntity<ApiResponse<List<AacIcon>>> getIconsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(aacService.getIconsByCategory(categoryId), 200));
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('PARENT', 'THERAPIST', 'ADMIN')")
    public ResponseEntity<ApiResponse<AacCategory>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(aacService.createCategory(request), 201));
    }

    @PostMapping("/icons")
    @PreAuthorize("hasAnyRole('PARENT', 'THERAPIST', 'ADMIN')")
    public ResponseEntity<ApiResponse<AacIcon>> createIcon(@Valid @RequestBody CreateIconRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(aacService.createIcon(request), 201));
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
