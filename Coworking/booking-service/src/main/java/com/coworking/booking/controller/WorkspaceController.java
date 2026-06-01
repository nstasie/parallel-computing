package com.coworking.booking.controller;

import com.coworking.booking.dto.WorkspaceDto;
import com.coworking.booking.model.enums.WorkspaceType;
import com.coworking.booking.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST-контролер для управління робочими просторами коворкінгу.
 * Базовий шлях: /api/workspaces
 */
@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @GetMapping
    public ResponseEntity<List<WorkspaceDto>> getAllWorkspaces() {
        return ResponseEntity.ok(workspaceService.getAllWorkspaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceDto> getWorkspaceById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(workspaceService.getWorkspaceById(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<WorkspaceDto>> getAvailableWorkspaces() {
        return ResponseEntity.ok(workspaceService.getAvailableWorkspaces());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<WorkspaceDto>> getWorkspacesByType(@PathVariable("type") WorkspaceType type) {
        return ResponseEntity.ok(workspaceService.getWorkspacesByType(type));
    }

    @PostMapping
    public ResponseEntity<WorkspaceDto> createWorkspace(@Valid @RequestBody WorkspaceDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceService.createWorkspace(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkspaceDto> updateWorkspace(@PathVariable("id") Long id, @Valid @RequestBody WorkspaceDto dto) {
        return ResponseEntity.ok(workspaceService.updateWorkspace(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable("id") Long id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }
}
