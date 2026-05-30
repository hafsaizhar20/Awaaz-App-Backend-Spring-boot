package com.example.FYP.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String address;
}
