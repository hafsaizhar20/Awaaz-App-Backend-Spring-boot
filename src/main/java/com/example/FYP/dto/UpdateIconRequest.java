package com.example.FYP.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIconRequest {
    private String label;
    private String labelUr;
    private String speechText;
    private String speechTextUr;
    private Long categoryId;
    private String imageUrl;
}
