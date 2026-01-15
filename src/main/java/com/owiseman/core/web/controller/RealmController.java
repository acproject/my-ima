package com.owiseman.core.web.controller;

import com.owiseman.core.service.RealmService;
import com.owiseman.core.web.dto.RealmDTO;
import com.owiseman.core.web.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Realm management.
 */
@RestController
@RequestMapping("/api/realms")
public class RealmController {

    private final RealmService realmService;

    public RealmController(RealmService realmService) {
        this.realmService = realmService;
    }

    /**
     * Create a new realm.
     * POST /api/realms
     */
    @PostMapping
    public ResponseEntity<RealmDTO> createRealm(@Valid @RequestBody RealmDTO realmDTO) {
        RealmDTO created = realmService.createRealm(realmDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get a realm by ID.
     * GET /api/realms/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RealmDTO> getRealm(@PathVariable UUID id) {
        RealmDTO realm = realmService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Realm", id.toString()));
        return ResponseEntity.ok(realm);
    }

    /**
     * Get a realm by name.
     * GET /api/realms/name/{name}
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<RealmDTO> getRealmByName(@PathVariable String name) {
        RealmDTO realm = realmService.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Realm", name));
        return ResponseEntity.ok(realm);
    }

    /**
     * Get all realms.
     * GET /api/realms
     */
    @GetMapping
    public ResponseEntity<List<RealmDTO>> getAllRealms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<RealmDTO> realms = realmService.findAll(page, size);
        return ResponseEntity.ok(realms);
    }

    /**
     * Update a realm.
     * PUT /api/realms/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<RealmDTO> updateRealm(
            @PathVariable UUID id, 
            @Valid @RequestBody RealmDTO realmDTO) {
        if (!id.equals(realmDTO.getId())) {
            throw new IllegalArgumentException("ID in path must match ID in request body");
        }
        RealmDTO updated = realmService.update(realmDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Enable a realm.
     * POST /api/realms/{id}/enable
     */
    @PostMapping("/{id}/enable")
    public ResponseEntity<RealmDTO> enableRealm(@PathVariable UUID id) {
        RealmDTO realm = realmService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Realm", id.toString()));
        RealmDTO enabled = realmService.enable(realm);
        return ResponseEntity.ok(enabled);
    }

    /**
     * Disable a realm.
     * POST /api/realms/{id}/disable
     */
    @PostMapping("/{id}/disable")
    public ResponseEntity<RealmDTO> disableRealm(@PathVariable UUID id) {
        RealmDTO realm = realmService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Realm", id.toString()));
        RealmDTO disabled = realmService.disable(realm);
        return ResponseEntity.ok(disabled);
    }

    /**
     * Delete a realm.
     * DELETE /api/realms/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRealm(@PathVariable UUID id) {
        realmService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Realm", id.toString()));
        realmService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Count realms.
     * GET /api/realms/count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRealms() {
        long count = realmService.count();
        return ResponseEntity.ok(count);
    }
}
