package com.coworking.billing.controller;

import com.coworking.billing.dto.CreatePenaltyRequest;
import com.coworking.billing.dto.PenaltyDto;
import com.coworking.billing.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST-контролер для управління штрафами Billing Service.
 * Базовий шлях: /api/penalties
 */
@RestController
@RequestMapping("/api/penalties")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;

    @GetMapping
    public ResponseEntity<List<PenaltyDto>> getAllPenalties() {
        return ResponseEntity.ok(penaltyService.getAllPenalties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PenaltyDto> getPenaltyById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(penaltyService.getPenaltyById(id));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PenaltyDto>> getPenaltiesByBookingId(@PathVariable("bookingId") Long bookingId) {
        return ResponseEntity.ok(penaltyService.getPenaltiesByBookingId(bookingId));
    }

    @PostMapping("/create")
    public ResponseEntity<PenaltyDto> createPenalty(@RequestBody CreatePenaltyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(penaltyService.createPenalty(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePenalty(@PathVariable("id") Long id) {
        penaltyService.deletePenalty(id);
        return ResponseEntity.noContent().build();
    }
}
