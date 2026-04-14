package com.example.FYP.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddChildRequest {
    private String email; // for child login
    private String password; // for child login
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String diagnosisDetails;
    private Long therapistId; // optional
}
