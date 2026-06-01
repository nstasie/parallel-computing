package com.coworking.billing.service;

import com.coworking.billing.dto.CreatePenaltyRequest;
import com.coworking.billing.dto.PenaltyDto;
import com.coworking.billing.exception.ResourceNotFoundException;
import com.coworking.billing.model.Penalty;
import com.coworking.billing.repository.PenaltyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервісний шар для управління штрафами білінгової системи.
 * Нараховує фіксований штраф при порушенні умов скасування бронювання.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PenaltyService {

    /**
     * Фіксована сума штрафу у гривнях за скасування бронювання
     * менш ніж за 2 години до його початку.
     */
    private static final BigDecimal LATE_CANCELLATION_PENALTY = BigDecimal.valueOf(200.00);

    private final PenaltyRepository penaltyRepository;

    /**
     * Повертає список усіх нарахованих штрафів.
     */
    @Transactional(readOnly = true)
    public List<PenaltyDto> getAllPenalties() {
        return penaltyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Повертає штраф за унікальним ідентифікатором.
     *
     * @throws ResourceNotFoundException якщо штраф не знайдено
     */
    @Transactional(readOnly = true)
    public PenaltyDto getPenaltyById(Long id) {
        return toDto(penaltyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Штраф з ідентифікатором " + id + " не знайдено")));
    }

    /**
     * Повертає всі штрафи пов'язані з конкретним бронюванням.
     *
     * @param bookingId ідентифікатор бронювання у Booking Service
     */
    @Transactional(readOnly = true)
    public List<PenaltyDto> getPenaltiesByBookingId(Long bookingId) {
        return penaltyRepository.findByBookingId(bookingId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Нараховує штраф на основі запиту від Booking Service.
     * Сума штрафу є фіксованою і визначається константою LATE_CANCELLATION_PENALTY.
     *
     * @param request дані для нарахування штрафу
     */
    public PenaltyDto createPenalty(CreatePenaltyRequest request) {
        Penalty penalty = Penalty.builder()
                .bookingId(request.getBookingId())
                .reason(request.getReason())
                .penaltyAmount(LATE_CANCELLATION_PENALTY)
                .build();

        Penalty saved = penaltyRepository.save(penalty);
        log.info("Штраф {} нараховано для бронювання {} на суму {} грн. Причина: {}",
                saved.getId(), request.getBookingId(), LATE_CANCELLATION_PENALTY, request.getReason());
        return toDto(saved);
    }

    /**
     * Видаляє штраф за ідентифікатором.
     *
     * @throws ResourceNotFoundException якщо штраф не знайдено
     */
    public void deletePenalty(Long id) {
        if (!penaltyRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Штраф з ідентифікатором " + id + " не знайдено");
        }
        penaltyRepository.deleteById(id);
    }

    private PenaltyDto toDto(Penalty penalty) {
        return PenaltyDto.builder()
                .id(penalty.getId())
                .bookingId(penalty.getBookingId())
                .reason(penalty.getReason())
                .penaltyAmount(penalty.getPenaltyAmount())
                .build();
    }
}
