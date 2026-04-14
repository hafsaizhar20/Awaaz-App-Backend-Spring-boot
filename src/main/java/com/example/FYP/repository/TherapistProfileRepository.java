package com.example.FYP.repository;

import com.example.FYP.model.TherapistProfile;
import com.example.FYP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TherapistProfileRepository extends JpaRepository<TherapistProfile, Long> {
    Optional<TherapistProfile> findByUser(User user);
}
