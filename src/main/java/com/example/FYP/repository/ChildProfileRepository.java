package com.example.FYP.repository;

import com.example.FYP.model.ChildProfile;
import com.example.FYP.model.ParentProfile;
import com.example.FYP.model.TherapistProfile;
import com.example.FYP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildProfileRepository extends JpaRepository<ChildProfile, Long> {
    Optional<ChildProfile> findByUser(User user);
    List<ChildProfile> findByParent(ParentProfile parent);
    List<ChildProfile> findByTherapist(TherapistProfile therapist);
}
