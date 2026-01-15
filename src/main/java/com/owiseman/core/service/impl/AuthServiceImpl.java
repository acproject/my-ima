package com.owiseman.core.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.owiseman.core.domain.User;
import com.owiseman.core.jooq.repository.UserRepository;
import com.owiseman.core.security.JwtTokenProvider;
import com.owiseman.core.service.AuthService;
import com.owiseman.core.web.dto.AuthResponse;
import com.owiseman.core.web.dto.LoginRequest;
import com.owiseman.core.web.dto.RegisterRequest;
import com.owiseman.core.web.dto.UserDTO;

/**
 * Implementation of AuthService for authentication operations.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // Find user by username
        Optional<User> userOpt = userRepository.findByUsername(null, loginRequest.getUsername());
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }
        
        User user = userOpt.get();
        
        // Verify password using BCrypt
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        // Check if user is enabled
        if (user.getEnabled() == null || !user.getEnabled()) {
            throw new RuntimeException("User account is disabled");
        }
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getUsername());
        
        // Get user roles
        List<String> roles = userRepository.findUserRoles(user.getId());
        
        return new AuthResponse(
            token,
            jwtExpiration,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            roles
        );
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if username already exists
        Optional<User> existingUser = userRepository.findByUsername(null, registerRequest.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        Optional<User> existingEmail = userRepository.findByEmail(null, registerRequest.getEmail());
        if (existingEmail.isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword())); // Encode password
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setRealmId(registerRequest.getRealmId());
        user.setEnabled(true);
        
        User createdUser = userRepository.create(user);
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(createdUser.getUsername());
        
        return new AuthResponse(
            token,
            jwtExpiration,
            createdUser.getId(),
            createdUser.getUsername(),
            createdUser.getEmail(),
            List.of("USER") // Default role
        );
    }

    @Override
    public AuthResponse refreshToken(String username) {
        Optional<User> userOpt = userRepository.findByUsername(null, username);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        // Generate new JWT token
        String token = jwtTokenProvider.generateToken(user.getUsername());
        
        // Get user roles
        List<String> roles = userRepository.findUserRoles(user.getId());
        
        return new AuthResponse(
            token,
            jwtExpiration,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            roles
        );
    }
}
