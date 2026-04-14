package com.example.FYP.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignTherapistRequest {
    @NotBlank(message = "Therapist email is required")
    @Email(message = "Invalid email format")
    private String therapistEmail;
}
