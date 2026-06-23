package com.example.FYP.controller;

import com.example.FYP.dto.*;
import com.example.FYP.service.ParentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parents")
@PreAuthorize("hasRole('PARENT')")
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @PostMapping("/children")
    public ResponseEntity<ApiResponse<ChildResponse>> addChild(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddChildRequest addChildRequest) {
        return ResponseEntity.status(201).body(ApiResponse.success(parentService.addChild(userDetails.getUsername(), addChildRequest), 201, "Child added successfully"));
    }

    @GetMapping("/children")
    public ResponseEntity<ApiResponse<List<ChildResponse>>> getMyChildren(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(parentService.getMyChildren(userDetails.getUsername()), 200, "Children fetched successfully"));
    }

    @PutMapping("/children/{childId}")
    public ResponseEntity<ApiResponse<ChildResponse>> updateChild(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long childId,
            @Valid @RequestBody UpdateChildRequest updateChildRequest) {
        return ResponseEntity.ok(ApiResponse.success(parentService.updateChild(userDetails.getUsername(), childId, updateChildRequest), 200, "Child updated successfully"));
    }

    @DeleteMapping("/children/{childId}")
    public ResponseEntity<ApiResponse<Void>> deleteChild(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long childId) {
        parentService.deleteChild(userDetails.getUsername(), childId);
        return ResponseEntity.ok(ApiResponse.success(null, 200, "Child deleted successfully"));
    }

    @PostMapping("/children/{childId}/therapist")
    public ResponseEntity<ApiResponse<Void>> assignTherapist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long childId,
            @Valid @RequestBody AssignTherapistRequest request) {
        parentService.assignTherapist(userDetails.getUsername(), childId, request.getTherapistEmail());
        return ResponseEntity.ok(ApiResponse.success(null, 200, "Therapist assigned successfully"));
    }

    @DeleteMapping("/children/{childId}/therapist")
    public ResponseEntity<ApiResponse<Void>> unassignTherapist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long childId) {
        parentService.unassignTherapist(userDetails.getUsername(), childId);
        return ResponseEntity.ok(ApiResponse.success(null, 200, "Therapist removed successfully"));
    }

    @GetMapping("/therapists")
    public ResponseEntity<ApiResponse<List<TherapistResponse>>> getAllTherapists() {
        return ResponseEntity.ok(ApiResponse.success(parentService.getAllTherapists(), 200, "Therapists fetched successfully"));
    }
}
