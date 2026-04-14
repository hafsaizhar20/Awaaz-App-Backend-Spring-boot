package com.example.FYP.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "aac_usage_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AacUsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildProfile child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_id", nullable = false)
    private AacIcon icon;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}
