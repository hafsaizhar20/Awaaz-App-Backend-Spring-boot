package com.example.FYP.service;

import com.example.FYP.dto.*;
import com.example.FYP.model.*;
import com.example.FYP.repository.*;
import com.example.FYP.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ParentProfileRepository parentProfileRepository;
    private final TherapistProfileRepository therapistProfileRepository;
    private final ChildProfileRepository childProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
            ParentProfileRepository parentProfileRepository,
            TherapistProfileRepository therapistProfileRepository,
            ChildProfileRepository childProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.parentProfileRepository = parentProfileRepository;
        this.therapistProfileRepository = therapistProfileRepository;
        this.childProfileRepository = childProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        Object profileDetails = null;

        if (request.getRole() == UserRole.PARENT) {
            String firstName = request.getFirstName();
            String lastName = request.getLastName();

            if (firstName == null && lastName == null && request.getFullName() != null) {
                String[] parts = request.getFullName().split(" ", 2);
                firstName = parts[0];
                lastName = parts.length > 1 ? parts[1] : "";
            }

            ParentProfile parentProfile = ParentProfile.builder()
                    .user(user)
                    .firstName(firstName != null ? firstName : "")
                    .lastName(lastName != null ? lastName : "")
                    .contactNumber(request.getContactNumber() != null ? request.getContactNumber() : "")
                    .address(request.getAddress())
                    .build();
            parentProfileRepository.save(parentProfile);

            profileDetails = ParentResponse.builder()
                    .id(parentProfile.getId())
                    .email(user.getEmail())
                    .firstName(parentProfile.getFirstName())
                    .lastName(parentProfile.getLastName())
                    .contactNumber(parentProfile.getContactNumber())
                    .address(parentProfile.getAddress())
                    .build();
        } else if (request.getRole() == UserRole.THERAPIST) {
            String firstName = request.getFirstName();
            String lastName = request.getLastName();

            if (firstName == null && lastName == null && request.getFullName() != null) {
                String[] parts = request.getFullName().split(" ", 2);
                firstName = parts[0];
                lastName = parts.length > 1 ? parts[1] : "";
            }

            TherapistProfile therapistProfile = TherapistProfile.builder()
                    .user(user)
                    .firstName(firstName != null ? firstName : "")
                    .lastName(lastName != null ? lastName : "")
                    .contactNumber(request.getContactNumber() != null ? request.getContactNumber() : "")
                    .specialization(request.getSpecialization() != null ? request.getSpecialization() : "")
                    .build();
            therapistProfileRepository.save(therapistProfile);

            profileDetails = TherapistResponse.builder()
                    .id(therapistProfile.getId())
                    .firstName(therapistProfile.getFirstName())
                    .lastName(therapistProfile.getLastName())
                    .specialization(therapistProfile.getSpecialization())
                    .email(user.getEmail())
                    .build();
        }

        String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwt)
                .email(user.getEmail())
                .role(user.getRole().name())
                .profile(profileDetails)
                .build();

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .fullName(request.getFullName())
                .authResponse(authResponse)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        Object profileDetails = null;
        if (user.getRole() == UserRole.PARENT) {
            ParentProfile parentProfile = parentProfileRepository.findByUser(user).orElse(null);
            if (parentProfile != null) {
                profileDetails = ParentResponse.builder()
                        .id(parentProfile.getId())
                        .email(user.getEmail())
                        .firstName(parentProfile.getFirstName())
                        .lastName(parentProfile.getLastName())
                        .contactNumber(parentProfile.getContactNumber())
                        .address(parentProfile.getAddress())
                        .build();
            }
        } else if (user.getRole() == UserRole.THERAPIST) {
            TherapistProfile therapistProfile = therapistProfileRepository.findByUser(user).orElse(null);
            if (therapistProfile != null) {
                profileDetails = TherapistResponse.builder()
                        .id(therapistProfile.getId())
                        .firstName(therapistProfile.getFirstName())
                        .lastName(therapistProfile.getLastName())
                        .specialization(therapistProfile.getSpecialization())
                        .email(user.getEmail())
                        .build();
            }
        } else if (user.getRole() == UserRole.CHILD) {
            ChildProfile childProfile = childProfileRepository.findByUser(user).orElse(null);
            if (childProfile != null) {
                profileDetails = ChildResponse.builder()
                        .id(childProfile.getId())
                        .email(user.getEmail())
                        .firstName(childProfile.getFirstName())
                        .lastName(childProfile.getLastName())
                        .dateOfBirth(childProfile.getDateOfBirth())
                        .diagnosisDetails(childProfile.getDiagnosisDetails())
                        .therapistName(childProfile.getTherapist() != null ? 
                                childProfile.getTherapist().getFirstName() + " " + childProfile.getTherapist().getLastName() : null)
                        .build();
            }
        }

        return AuthResponse.builder()
                .token(jwt)
                .email(user.getEmail())
                .role(user.getRole().name())
                .profile(profileDetails)
                .build();
    }
}
