package com.owiseman.core.service;

import com.owiseman.core.web.dto.AuthResponse;
import com.owiseman.core.web.dto.LoginRequest;
import com.owiseman.core.web.dto.RegisterRequest;
import com.owiseman.core.web.dto.UserDTO;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {

    /**
     * Authenticate a user and return JWT token.
     *
     * @param loginRequest the login credentials
     * @return authentication response with JWT token
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * Register a new user and return JWT token.
     *
     * @param registerRequest the registration data
     * @return authentication response with JWT token
     */
    AuthResponse register(RegisterRequest registerRequest);

    /**
     * Refresh JWT token for an authenticated user.
     *
     * @param username the username
     * @return new authentication response with JWT token
     */
    AuthResponse refreshToken(String username);
}
