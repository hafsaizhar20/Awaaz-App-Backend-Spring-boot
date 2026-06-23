package com.example.FYP.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChildRequest {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String diagnosisDetails;
    private Long therapistId; // Optional: therapist ID to assign (or null/-1 to unassign)
}
