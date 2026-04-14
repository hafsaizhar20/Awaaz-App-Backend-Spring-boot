package com.example.FYP.repository;

import com.example.FYP.model.AacIcon;
import com.example.FYP.model.AacCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AacIconRepository extends JpaRepository<AacIcon, Long> {
    List<AacIcon> findByCategory(AacCategory category);
}
