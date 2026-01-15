package com.owiseman.core.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.owiseman.core.domain.Realm;
import com.owiseman.core.exception.ResourceNotFoundException;
import com.owiseman.core.jooq.repository.RealmRepository;
import com.owiseman.core.service.RealmService;
import com.owiseman.core.web.dto.RealmDTO;

/**
 * Implementation of RealmService providing business logic for realm management.
 */
@Service
public class RealmServiceImpl implements RealmService {

    private final RealmRepository realmRepository;

    @Autowired
    public RealmServiceImpl(RealmRepository realmRepository) {
        this.realmRepository = realmRepository;
    }

    @Override
    public RealmDTO createRealm(RealmDTO realmDTO) {
        Realm realm = new Realm();
        realm.setName(realmDTO.getName());
        realm.setEnabled(realmDTO.getEnabled() != null ? realmDTO.getEnabled() : true);
        
        Realm created = realmRepository.create(realm);
        return mapToDTO(created);
    }

    @Override
    public Optional<RealmDTO> findById(UUID id) {
        return realmRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    public Optional<RealmDTO> findByName(String name) {
        return realmRepository.findByName(null, name).map(this::mapToDTO);
    }

    @Override
    public List<RealmDTO> findAll(int page, int size) {
        return realmRepository.findAll(null, page * size, size)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public RealmDTO update(RealmDTO realmDTO) {
        Realm existingRealm = realmRepository.findById(realmDTO.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Realm", realmDTO.getId().toString()));
        
        existingRealm.setName(realmDTO.getName());
        existingRealm.setEnabled(realmDTO.getEnabled());
        
        Realm updated = realmRepository.update(existingRealm);
        return mapToDTO(updated);
    }

    @Override
    public RealmDTO enable(RealmDTO realmDTO) {
        Realm realm = realmRepository.enable(realmDTO.getId());
        return mapToDTO(realm);
    }

    @Override
    public RealmDTO disable(RealmDTO realmDTO) {
        Realm realm = realmRepository.disable(realmDTO.getId());
        return mapToDTO(realm);
    }

    @Override
    public void delete(UUID id) {
        realmRepository.delete(id);
    }

    @Override
    public boolean isActive(UUID id) {
        return realmRepository.findById(id)
            .map(Realm::getEnabled)
            .orElse(false);
    }

    @Override
    public long count() {
        return realmRepository.count();
    }

    /**
     * Map domain entity to DTO.
     */
    private RealmDTO mapToDTO(Realm realm) {
        RealmDTO dto = new RealmDTO();
        dto.setId(realm.getId());
        dto.setName(realm.getName());
        dto.setEnabled(realm.getEnabled());
        dto.setCreatedAt(realm.getCreatedAt());
        return dto;
    }
}
