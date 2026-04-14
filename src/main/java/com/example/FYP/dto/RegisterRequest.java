package com.example.FYP.dto;

import com.example.FYP.model.UserRole;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private UserRole role;
    
    // Profile fields
    private String firstName;
    private String lastName;
    private String fullName; // helper field
    private String contactNumber;
    private String address; // for parent
    private String specialization; // for therapist
}
