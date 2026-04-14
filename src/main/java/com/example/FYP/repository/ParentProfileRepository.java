package com.example.FYP.repository;

import com.example.FYP.model.ParentProfile;
import com.example.FYP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentProfileRepository extends JpaRepository<ParentProfile, Long> {
    Optional<ParentProfile> findByUser(User user);
}
