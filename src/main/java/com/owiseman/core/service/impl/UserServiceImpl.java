package com.owiseman.core.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.owiseman.core.domain.User;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.UserRepository;
import com.owiseman.core.service.UserService;
import com.owiseman.core.web.dto.UserDTO;

/**
 * Implementation of UserService providing business logic for user management.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setRealmId(userDTO.getRealmId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEnabled(userDTO.getEnabled() != null ? userDTO.getEnabled() : true);
        
        User created = userRepository.create(user);
        return mapToDTO(created);
    }

    @Override
    public Optional<UserDTO> findById(UUID id) {
        return userRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(null, username).map(this::mapToDTO);
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(null, email).map(this::mapToDTO);
    }

    @Override
    public List<UserDTO> findByRealm(UUID realmId, int page, int size) {
        return userRepository.findAll(realmId, page * size, size)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findAll(int page, int size) {
        return userRepository.findAll(null, page * size, size)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public UserDTO update(UserDTO userDTO) {
        User existingUser = userRepository.findById(userDTO.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", userDTO.getId().toString()));
        
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEnabled(userDTO.getEnabled());
        
        User updated = userRepository.update(existingUser);
        return mapToDTO(updated);
    }

    @Override
    public UserDTO enable(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", userDTO.getId().toString()));
        user.setEnabled(true);
        User updated = userRepository.update(user);
        return mapToDTO(updated);
    }

    @Override
    public UserDTO disable(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", userDTO.getId().toString()));
        user.setEnabled(false);
        User updated = userRepository.update(user);
        return mapToDTO(updated);
    }

    @Override
    public void delete(UUID id) {
        userRepository.delete(id);
    }

    @Override
    public void assignRole(UUID userId, UUID roleId) {
        userRepository.assignRole(userId, roleId);
    }

    @Override
    public void removeRole(UUID userId, UUID roleId) {
        userRepository.removeRole(userId, roleId);
    }

    @Override
    public List<String> findUserRoles(UUID userId) {
        return userRepository.findUserRoles(userId);
    }

    @Override
    public List<String> findPermissions(UUID userId) {
        return userRepository.findPermissions(userId);
    }

    @Override
    public boolean hasPermission(UUID userId, String permission) {
        return findPermissions(userId).contains(permission);
    }

    @Override
    public boolean isActive(UUID userId) {
        return userRepository.findById(userId)
            .map(user -> user.getEnabled() != null && user.getEnabled())
            .orElse(false);
    }

    @Override
    public long countByRealm(UUID realmId) {
        return userRepository.countByRealm(realmId);
    }

    /**
     * Map domain entity to DTO.
     */
    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setRealmId(user.getRealmId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
