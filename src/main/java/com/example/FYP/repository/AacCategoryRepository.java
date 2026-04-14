package com.example.FYP.repository;

import com.example.FYP.model.AacCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AacCategoryRepository extends JpaRepository<AacCategory, Long> {
}
