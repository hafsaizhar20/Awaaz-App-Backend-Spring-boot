package com.example.FYP.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String iconUrl;
    private Long childId;
    private List<IconResponse> icons;
}
