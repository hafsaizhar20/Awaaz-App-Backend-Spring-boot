package com.example.FYP.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TherapistResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String email;
}
