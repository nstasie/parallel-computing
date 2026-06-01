package com.coworking.booking.service;

import com.coworking.booking.dto.WorkspaceDto;
import com.coworking.booking.exception.ResourceNotFoundException;
import com.coworking.booking.model.Workspace;
import com.coworking.booking.model.enums.WorkspaceType;
import com.coworking.booking.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервісний шар для управління робочими просторами коворкінгу.
 * Реалізує бізнес-логіку CRUD-операцій над сутністю Workspace.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    /**
     * Повертає список усіх робочих просторів незалежно від їхньої доступності.
     */
    @Transactional(readOnly = true)
    public List<WorkspaceDto> getAllWorkspaces() {
        return workspaceRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Повертає робочий простір за унікальним ідентифікатором.
     *
     * @throws ResourceNotFoundException якщо простір з таким ID не існує
     */
    @Transactional(readOnly = true)
    public WorkspaceDto getWorkspaceById(Long id) {
        return toDto(workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Робоче місце з ідентифікатором " + id + " не знайдено")));
    }

    /**
     * Повертає список усіх доступних для бронювання просторів.
     */
    @Transactional(readOnly = true)
    public List<WorkspaceDto> getAvailableWorkspaces() {
        return workspaceRepository.findByIsAvailableTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Повертає список просторів відфільтрованих за типом.
     *
     * @param type тип робочого простору (DESK або MEETING_ROOM)
     */
    @Transactional(readOnly = true)
    public List<WorkspaceDto> getWorkspacesByType(WorkspaceType type) {
        return workspaceRepository.findByType(type).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Створює новий робочий простір у системі.
     */
    public WorkspaceDto createWorkspace(WorkspaceDto dto) {
        Workspace workspace = toEntity(dto);
        return toDto(workspaceRepository.save(workspace));
    }

    /**
     * Оновлює дані існуючого робочого простору.
     *
     * @throws ResourceNotFoundException якщо простір з таким ID не існує
     */
    public WorkspaceDto updateWorkspace(Long id, WorkspaceDto dto) {
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Робоче місце з ідентифікатором " + id + " не знайдено"));
        workspace.setType(dto.getType());
        workspace.setCapacity(dto.getCapacity());
        if (dto.getIsAvailable() != null) {
            workspace.setIsAvailable(dto.getIsAvailable());
        }
        return toDto(workspaceRepository.save(workspace));
    }

    /**
     * Видаляє робочий простір за ідентифікатором.
     *
     * @throws ResourceNotFoundException якщо простір з таким ID не існує
     */
    public void deleteWorkspace(Long id) {
        if (!workspaceRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Робоче місце з ідентифікатором " + id + " не знайдено");
        }
        workspaceRepository.deleteById(id);
    }

    private WorkspaceDto toDto(Workspace workspace) {
        return WorkspaceDto.builder()
                .id(workspace.getId())
                .type(workspace.getType())
                .capacity(workspace.getCapacity())
                .isAvailable(workspace.getIsAvailable())
                .build();
    }

    private Workspace toEntity(WorkspaceDto dto) {
        return Workspace.builder()
                .type(dto.getType())
                .capacity(dto.getCapacity())
                .isAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true)
                .build();
    }
}
