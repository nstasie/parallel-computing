package com.coworking.billing.controller;

import com.coworking.billing.dto.TariffDto;
import com.coworking.billing.model.enums.WorkspaceType;
import com.coworking.billing.service.TariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST-контролер для управління тарифами Billing Service.
 * Базовий шлях: /api/tariffs
 */
@RestController
@RequestMapping("/api/tariffs")
@RequiredArgsConstructor
public class TariffController {

    private final TariffService tariffService;

    @GetMapping
    public ResponseEntity<List<TariffDto>> getAllTariffs() {
        return ResponseEntity.ok(tariffService.getAllTariffs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TariffDto> getTariffById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tariffService.getTariffById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<TariffDto> getTariffByWorkspaceType(@PathVariable("type") WorkspaceType type) {
        return ResponseEntity.ok(tariffService.getTariffByWorkspaceType(type));
    }

    @PostMapping
    public ResponseEntity<TariffDto> createTariff(@Valid @RequestBody TariffDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tariffService.createTariff(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TariffDto> updateTariff(@PathVariable("id") Long id, @Valid @RequestBody TariffDto dto) {
        return ResponseEntity.ok(tariffService.updateTariff(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariff(@PathVariable("id") Long id) {
        tariffService.deleteTariff(id);
        return ResponseEntity.noContent().build();
    }
}
