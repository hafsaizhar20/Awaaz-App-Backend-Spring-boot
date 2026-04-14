package com.example.FYP.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parent_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String firstName;
    private String lastName;
    private String contactNumber;

    @Column(nullable = false)
    private String address;
}
