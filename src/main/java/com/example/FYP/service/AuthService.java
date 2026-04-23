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
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
            ParentProfileRepository parentProfileRepository,
            TherapistProfileRepository therapistProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.parentProfileRepository = parentProfileRepository;
        this.therapistProfileRepository = therapistProfileRepository;
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
                    .specialization(request.getSpecialization())
                    .build();
            therapistProfileRepository.save(therapistProfile);
        }

        String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwt)
                .email(user.getEmail())
                .role(user.getRole().name())
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

        return AuthResponse.builder()
                .token(jwt)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
