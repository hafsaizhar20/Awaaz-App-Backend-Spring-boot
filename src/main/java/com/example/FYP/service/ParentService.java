package com.example.FYP.service;

import com.example.FYP.dto.*;
import com.example.FYP.model.*;
import com.example.FYP.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParentService {

    private final UserRepository userRepository;
    private final ParentProfileRepository parentProfileRepository;
    private final TherapistProfileRepository therapistProfileRepository;
    private final ChildProfileRepository childProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public ParentService(UserRepository userRepository, 
                         ParentProfileRepository parentProfileRepository, 
                         TherapistProfileRepository therapistProfileRepository, 
                         ChildProfileRepository childProfileRepository, 
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.parentProfileRepository = parentProfileRepository;
        this.therapistProfileRepository = therapistProfileRepository;
        this.childProfileRepository = childProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ChildResponse addChild(String parentEmail, AddChildRequest request) {
        User parentUser = userRepository.findByEmail(parentEmail).orElseThrow(() -> new RuntimeException("Parent not found"));
        ParentProfile parentProfile = parentProfileRepository.findByUser(parentUser).orElseThrow(() -> new RuntimeException("Parent profile not found"));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Child email already exists");
        }

        User childUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CHILD)
                .build();
        userRepository.save(childUser);

        TherapistProfile therapist = null;
        if (request.getTherapistId() != null) {
            therapist = therapistProfileRepository.findById(request.getTherapistId()).orElse(null);
        }

        ChildProfile childProfile = ChildProfile.builder()
                .user(childUser)
                .parent(parentProfile)
                .therapist(therapist)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .diagnosisDetails(request.getDiagnosisDetails())
                .build();

        childProfileRepository.save(childProfile);

        return mapToResponse(childProfile);
    }

    public List<ChildResponse> getMyChildren(String parentEmail) {
        User parentUser = userRepository.findByEmail(parentEmail).orElseThrow(() -> new RuntimeException("Parent not found"));
        ParentProfile parentProfile = parentProfileRepository.findByUser(parentUser).orElseThrow(() -> new RuntimeException("Parent profile not found"));
        
        return childProfileRepository.findByParent(parentProfile).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TherapistResponse> getAllTherapists() {
        return therapistProfileRepository.findAll().stream()
                .map(profile -> TherapistResponse.builder()
                        .id(profile.getId())
                        .firstName(profile.getFirstName())
                        .lastName(profile.getLastName())
                        .specialization(profile.getSpecialization())
                        .email(profile.getUser().getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignTherapist(String parentEmail, Long childId, String therapistEmail) {
        User parentUser = userRepository.findByEmail(parentEmail).orElseThrow(() -> new RuntimeException("Parent not found"));
        ParentProfile parentProfile = parentProfileRepository.findByUser(parentUser).orElseThrow(() -> new RuntimeException("Parent profile not found"));

        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        if (!child.getParent().getId().equals(parentProfile.getId())) {
            throw new RuntimeException("Unauthorized: This child does not belong to you");
        }

        User therapistUser = userRepository.findByEmail(therapistEmail)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        
        if (therapistUser.getRole() != UserRole.THERAPIST) {
            throw new RuntimeException("The email provided does not belong to a therapist");
        }

        TherapistProfile therapistProfile = therapistProfileRepository.findByUser(therapistUser)
                .orElseThrow(() -> new RuntimeException("Therapist profile not found"));

        child.setTherapist(therapistProfile);
        childProfileRepository.save(child);
    }

    private ChildResponse mapToResponse(ChildProfile profile) {
        return ChildResponse.builder()
                .id(profile.getId())
                .email(profile.getUser().getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .dateOfBirth(profile.getDateOfBirth())
                .diagnosisDetails(profile.getDiagnosisDetails())
                .therapistName(profile.getTherapist() != null ? 
                        profile.getTherapist().getFirstName() + " " + profile.getTherapist().getLastName() : null)
                .build();
    }
}
