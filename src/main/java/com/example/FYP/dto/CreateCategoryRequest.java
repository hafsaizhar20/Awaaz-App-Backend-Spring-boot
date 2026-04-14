package com.example.FYP.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    private String iconUrl;
    private Long childId; // Optional: If provided, this category belongs to a specific child
}
