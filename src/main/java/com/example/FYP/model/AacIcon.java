package com.example.FYP.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aac_icons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AacIcon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;

    private String imageUrl;

    private String speechText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private AacCategory category;
}
