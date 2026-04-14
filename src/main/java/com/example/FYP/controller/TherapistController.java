package com.example.FYP.controller;

import com.example.FYP.dto.ApiResponse;
import com.example.FYP.dto.ChildResponse;
import com.example.FYP.service.TherapistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/therapists")
@PreAuthorize("hasRole('THERAPIST')")
public class TherapistController {

    private final TherapistService therapistService;

    public TherapistController(TherapistService therapistService) {
        this.therapistService = therapistService;
    }

    @GetMapping("/children")
    public ResponseEntity<ApiResponse<List<ChildResponse>>> getMyAssignedChildren(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                therapistService.getAssignedChildren(userDetails.getUsername()), 
                200
        ));
    }
}
