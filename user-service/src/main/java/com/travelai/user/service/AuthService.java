package com.travelai.user.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.travelai.user.dto.UserDtos.AuthResponse;
import com.travelai.user.dto.UserDtos.LoginRequest;
import com.travelai.user.dto.UserDtos.RegisterRequest;
import com.travelai.user.dto.UserDtos.UpdateProfileRequest;
import com.travelai.user.dto.UserDtos.UserResponse;
import com.travelai.user.entity.Role;
import com.travelai.user.entity.RoleName;
import com.travelai.user.entity.User;
import com.travelai.user.repository.RoleRepository;
import com.travelai.user.repository.UserRepository;
import com.travelai.user.security.CustomUserDetailsService;
import com.travelai.user.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().toLowerCase().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        Role userRole = roleRepository.findByName(RoleName.USER)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role missing"));

        User user = new User();
        user.setEmail(request.email().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName().trim());
        user.getRoles().add(userRole);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email().toLowerCase().trim(), request.password())
        );

        User user = userRepository.findByEmail(request.email().toLowerCase().trim())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        return buildAuthResponse(user);
    }

    public UserResponse currentUser() {
        return toUserResponse(getCurrentUserEntity());
    }

    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUserEntity();
        user.setFullName(request.fullName().trim());
        userRepository.save(user);
        return toUserResponse(user);
    }

    public User getCurrentUserEntity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated"));
    }

    private AuthResponse buildAuthResponse(User user) {
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails, user.getId());
        return new AuthResponse(token, toUserResponse(user));
    }

    public static UserResponse toUserResponse(User user) {
        Set<String> roles = user.getRoles().stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toSet());
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), roles);
    }
}
