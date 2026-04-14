package com.example.FYP.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateIconRequest {
    @NotBlank(message = "Label is required")
    private String label;
    private String imageUrl;
    private String speechText;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
