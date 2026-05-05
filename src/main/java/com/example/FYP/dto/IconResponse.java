package com.example.FYP.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IconResponse {
    private Long id;
    private String label;
    private String imageUrl;
    private String speechText;
    private Long categoryId;
    private Long childId;
}
