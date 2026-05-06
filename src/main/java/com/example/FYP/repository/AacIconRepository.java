package com.example.FYP.repository;

import com.example.FYP.model.AacIcon;
import com.example.FYP.model.AacCategory;
import com.example.FYP.model.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AacIconRepository extends JpaRepository<AacIcon, Long> {
    List<AacIcon> findByCategory(AacCategory category);

    List<AacIcon> findByCategoryAndChildIsNull(AacCategory category);

    @org.springframework.data.jpa.repository.Query("SELECT i FROM AacIcon i WHERE i.category = :category AND (i.child = :child OR i.child IS NULL)")
    List<AacIcon> findByCategoryAndChildOrChildIsNull(
            @org.springframework.data.repository.query.Param("category") AacCategory category,
            @org.springframework.data.repository.query.Param("child") ChildProfile child);

    boolean existsByLabelAndCategoryAndChildIsNull(String label, AacCategory category);

    boolean existsByLabelAndCategoryAndChild(String label, AacCategory category, ChildProfile child);
}
