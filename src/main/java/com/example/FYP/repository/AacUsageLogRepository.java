package com.example.FYP.repository;

import com.example.FYP.model.AacUsageLog;
import com.example.FYP.model.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AacUsageLogRepository extends JpaRepository<AacUsageLog, Long> {
    
    List<AacUsageLog> findByChild(ChildProfile child);

    @Query("SELECT l.icon.label as label, COUNT(l) as count " +
           "FROM AacUsageLog l " +
           "WHERE l.child = :child " +
           "GROUP BY l.icon.label " +
           "ORDER BY COUNT(l) DESC")
    List<Map<String, Object>> getUsageStatsByChild(ChildProfile child);
}
