package com.example.FYP.repository;

import com.example.FYP.model.AacCategory;
import com.example.FYP.model.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AacCategoryRepository extends JpaRepository<AacCategory, Long> {
    List<AacCategory> findByChildIsNull();
    List<AacCategory> findByChildOrChildIsNull(ChildProfile child);
    boolean existsByNameAndChildIsNull(String name);
    boolean existsByNameAndChild(String name, ChildProfile child);
}
