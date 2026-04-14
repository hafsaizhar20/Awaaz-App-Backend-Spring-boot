package com.example.FYP.service;

import com.example.FYP.dto.ChildResponse;
import com.example.FYP.model.*;
import com.example.FYP.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TherapistService {

    private final UserRepository userRepository;
    private final TherapistProfileRepository therapistProfileRepository;
    private final ChildProfileRepository childProfileRepository;

    public TherapistService(UserRepository userRepository, 
                            TherapistProfileRepository therapistProfileRepository, 
                            ChildProfileRepository childProfileRepository) {
        this.userRepository = userRepository;
        this.therapistProfileRepository = therapistProfileRepository;
        this.childProfileRepository = childProfileRepository;
    }

    public List<ChildResponse> getAssignedChildren(String therapistEmail) {
        User therapistUser = userRepository.findByEmail(therapistEmail)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        
        TherapistProfile therapistProfile = therapistProfileRepository.findByUser(therapistUser)
                .orElseThrow(() -> new RuntimeException("Therapist profile not found"));

        return childProfileRepository.findByTherapist(therapistProfile).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ChildResponse mapToResponse(ChildProfile child) {
        return ChildResponse.builder()
                .id(child.getId())
                .email(child.getUser().getEmail())
                .firstName(child.getFirstName())
                .lastName(child.getLastName())
                .dateOfBirth(child.getDateOfBirth())
                .diagnosisDetails(child.getDiagnosisDetails())
                .therapistName(child.getTherapist() != null ? 
                        child.getTherapist().getFirstName() + " " + child.getTherapist().getLastName() : "Not Assigned")
                .build();
    }
}
