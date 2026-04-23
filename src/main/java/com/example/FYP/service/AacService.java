package com.example.FYP.service;

import com.example.FYP.dto.*;
import com.example.FYP.model.*;
import com.example.FYP.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AacService {

    private final AacCategoryRepository categoryRepository;
    private final AacIconRepository iconRepository;
    private final AacUsageLogRepository usageLogRepository;
    private final ChildProfileRepository childProfileRepository;
    private final UserRepository userRepository;
    private final ParentProfileRepository parentProfileRepository;
    private final StorageService storageService;

    public AacService(AacCategoryRepository categoryRepository, 
                      AacIconRepository iconRepository,
                      AacUsageLogRepository usageLogRepository,
                      ChildProfileRepository childProfileRepository,
                      UserRepository userRepository,
                      ParentProfileRepository parentProfileRepository,
                      StorageService storageService) {
        this.categoryRepository = categoryRepository;
        this.iconRepository = iconRepository;
        this.usageLogRepository = usageLogRepository;
        this.childProfileRepository = childProfileRepository;
        this.userRepository = userRepository;
        this.parentProfileRepository = parentProfileRepository;
        this.storageService = storageService;
    }

    public List<AacCategory> getAllCategories(Long childId) {
        if (childId != null) {
            ChildProfile child = childProfileRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Child not found"));
            return categoryRepository.findByChildOrChildIsNull(child);
        }
        return categoryRepository.findByChildIsNull();
    }

    public List<AacIcon> getIconsByCategory(Long categoryId, Long childId) {
        AacCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        if (childId != null) {
            ChildProfile child = childProfileRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Child not found"));
            return iconRepository.findByCategoryAndChildOrChildIsNull(category, child);
        }
        return iconRepository.findByCategoryAndChildIsNull(category);
    }

    @Transactional
    public AacCategory createCategory(String email, CreateCategoryRequest request, MultipartFile file) throws IOException {
        ChildProfile child = null;
        if (request.getChildId() != null) {
            child = verifyAndGetChild(email, request.getChildId());
            if (categoryRepository.existsByNameAndChild(request.getName(), child)) {
                throw new RuntimeException("Category '" + request.getName() + "' already exists for this child.");
            }
        } else {
            if (categoryRepository.existsByNameAndChildIsNull(request.getName())) {
                throw new RuntimeException("Standard category '" + request.getName() + "' already exists.");
            }
        }

        String iconUrl = request.getIconUrl();
        if (file != null && !file.isEmpty()) {
            iconUrl = storageService.uploadImage(file);
        }

        AacCategory category = AacCategory.builder()
                .name(request.getName())
                .iconUrl(iconUrl)
                .child(child)
                .build();
        return categoryRepository.save(category);
    }

    @Transactional
    public AacIcon createIcon(String email, CreateIconRequest request, MultipartFile file) throws IOException {
        AacCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ChildProfile child = null;
        if (request.getChildId() != null) {
            child = verifyAndGetChild(email, request.getChildId());
            if (iconRepository.existsByLabelAndCategoryAndChild(request.getLabel(), category, child)) {
                throw new RuntimeException("Icon '" + request.getLabel() + "' already exists in this category for this child.");
            }
        } else {
            if (iconRepository.existsByLabelAndCategoryAndChildIsNull(request.getLabel(), category)) {
                throw new RuntimeException("Standard icon '" + request.getLabel() + "' already exists in this category.");
            }
        }

        String imageUrl = request.getImageUrl();
        if (file != null && !file.isEmpty()) {
            imageUrl = storageService.uploadImage(file);
        }

        AacIcon icon = AacIcon.builder()
                .label(request.getLabel())
                .imageUrl(imageUrl)
                .speechText(request.getSpeechText() != null ? request.getSpeechText() : request.getLabel())
                .category(category)
                .child(child)
                .build();
        return iconRepository.save(icon);
    }

    private ChildProfile verifyAndGetChild(String email, Long childId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        if (user.getRole() == UserRole.PARENT) {
            ParentProfile parent = parentProfileRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Parent profile not found"));
            if (!child.getParent().getId().equals(parent.getId())) {
                throw new RuntimeException("Unauthorized: This child does not belong to you");
            }
        } else if (user.getRole() == UserRole.ADMIN) {
            // Admin can do anything
        } else {
            throw new RuntimeException("Unauthorized: Only parents or admins can create custom AAC items");
        }
        return child;
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
