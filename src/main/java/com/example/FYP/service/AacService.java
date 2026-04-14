package com.example.FYP.service;

import com.example.FYP.dto.*;
import com.example.FYP.model.*;
import com.example.FYP.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AacService {

    private final AacCategoryRepository categoryRepository;
    private final AacIconRepository iconRepository;
    private final AacUsageLogRepository usageLogRepository;
    private final ChildProfileRepository childProfileRepository;
    private final UserRepository userRepository;

    public AacService(AacCategoryRepository categoryRepository, 
                      AacIconRepository iconRepository,
                      AacUsageLogRepository usageLogRepository,
                      ChildProfileRepository childProfileRepository,
                      UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.iconRepository = iconRepository;
        this.usageLogRepository = usageLogRepository;
        this.childProfileRepository = childProfileRepository;
        this.userRepository = userRepository;
    }

    public List<AacCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<AacIcon> getIconsByCategory(Long categoryId) {
        AacCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return iconRepository.findByCategory(category);
    }

    @Transactional
    public AacCategory createCategory(CreateCategoryRequest request) {
        AacCategory category = AacCategory.builder()
                .name(request.getName())
                .iconUrl(request.getIconUrl())
                .build();
        return categoryRepository.save(category);
    }

    @Transactional
    public AacIcon createIcon(CreateIconRequest request) {
        AacCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        AacIcon icon = AacIcon.builder()
                .label(request.getLabel())
                .imageUrl(request.getImageUrl())
                .speechText(request.getSpeechText() != null ? request.getSpeechText() : request.getLabel())
                .category(category)
                .build();
        return iconRepository.save(icon);
    }

    @Transactional
    public void logUsage(String email, LogUsageRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ChildProfile child = childProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Child profile not found for this user"));

        AacIcon icon = iconRepository.findById(request.getIconId())
                .orElseThrow(() -> new RuntimeException("Icon not found"));

        AacUsageLog log = AacUsageLog.builder()
                .child(child)
                .icon(icon)
                .build();
        
        usageLogRepository.save(log);
    }

    public List<Map<String, Object>> getUsageAnalytics(Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        
        return usageLogRepository.getUsageStatsByChild(child);
    }
}
