package com.coworking.booking.service;

import com.coworking.booking.client.BillingServiceClient;
import com.coworking.booking.dto.*;
import com.coworking.booking.exception.ResourceNotFoundException;
import com.coworking.booking.model.Booking;
import com.coworking.booking.model.Workspace;
import com.coworking.booking.model.enums.BookingStatus;
import com.coworking.booking.repository.BookingRepository;
import com.coworking.booking.repository.UserRepository;
import com.coworking.booking.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервісний шар для управління бронюваннями робочих місць.
 * Реалізує основну бізнес-логіку системи:
 * - перевірку доступності робочого місця на запитаний часовий проміжок;
 * - міжсервісну взаємодію з Billing Service через Feign-клієнт;
 * - нарахування штрафу при пізньому скасуванні бронювання.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingService {

    /** Мінімальна кількість годин до початку бронювання для скасування без штрафу. */
    private static final long MIN_HOURS_FOR_FREE_CANCELLATION = 2;

    private final BookingRepository bookingRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final BillingServiceClient billingServiceClient;

    /**
     * Повертає список усіх бронювань у системі.
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Повертає бронювання за унікальним ідентифікатором.
     *
     * @throws ResourceNotFoundException якщо бронювання з таким ID не існує
     */
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long id) {
        return toDto(bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Бронювання з ідентифікатором " + id + " не знайдено")));
    }

    /**
     * Повертає всі бронювання конкретного користувача.
     *
     * @throws ResourceNotFoundException якщо користувача з таким ID не існує
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "Користувача з ідентифікатором " + userId + " не знайдено");
        }
        return bookingRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Створює нове бронювання з комплексною перевіркою доступності.
     *
     * Алгоритм роботи:
     * 1. Перевірити існування користувача та робочого місця.
     * 2. Валідувати коректність часового проміжку.
     * 3. Перевірити загальну доступність місця (isAvailable).
     * 4. Перевірити відсутність конфліктуючих бронювань на вказаний час.
     * 5. Зберегти бронювання зі статусом ACTIVE.
     * 6. Надіслати запит до Billing Service для розрахунку вартості та створення рахунку.
     *
     * @param dto дані нового бронювання
     * @throws ResourceNotFoundException якщо користувача або місця не знайдено
     * @throws IllegalArgumentException  якщо часовий проміжок некоректний
     * @throws IllegalStateException     якщо місце зайняте або недоступне
     */
    public BookingDto createBooking(BookingDto dto) {
        // Крок 1: Перевірка існування користувача
        if (!userRepository.existsById(dto.getUserId())) {
            throw new ResourceNotFoundException(
                    "Користувача з ідентифікатором " + dto.getUserId() + " не знайдено");
        }

        // Крок 2: Валідація часового проміжку
        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new IllegalArgumentException(
                    "Некоректний час бронювання: час початку має передувати часу завершення");
        }
        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Некоректний час бронювання: неможливо забронювати місце у минулому");
        }

        // Крок 3: Отримання та перевірка доступності місця
        Workspace workspace = workspaceRepository.findById(dto.getWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Робоче місце з ідентифікатором " + dto.getWorkspaceId() + " не знайдено"));

        if (!workspace.getIsAvailable()) {
            throw new IllegalStateException("Місце вже зайняте або тимчасово недоступне");
        }

        // Крок 4: Перевірка конфліктуючих бронювань у заданий часовий проміжок
        boolean hasConflict = bookingRepository.existsConflictingBooking(
                dto.getWorkspaceId(), dto.getStartTime(), dto.getEndTime());
        if (hasConflict) {
            throw new IllegalStateException(
                    "Місце вже зайняте на вказаний часовий проміжок. Будь ласка, оберіть інший час");
        }

        // Крок 5: Збереження бронювання
        Booking booking = toEntity(dto);
        booking.setStatus(BookingStatus.ACTIVE);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Бронювання {} успішно створено для користувача {} на місце {}",
                savedBooking.getId(), dto.getUserId(), dto.getWorkspaceId());

        // Крок 6: Міжсервісна взаємодія — запит до Billing Service для створення рахунку
        try {
            CreateInvoiceRequest invoiceRequest = CreateInvoiceRequest.builder()
                    .bookingId(savedBooking.getId())
                    .workspaceType(workspace.getType())
                    .startTime(savedBooking.getStartTime())
                    .endTime(savedBooking.getEndTime())
                    .build();
            billingServiceClient.createInvoice(invoiceRequest);
            log.info("Рахунок для бронювання {} успішно сформовано у Billing Service",
                    savedBooking.getId());
        } catch (Exception e) {
            // Бронювання залишається активним навіть при недоступності Billing Service.
            // Рахунок може бути сформований повторно пізніше.
            log.error("Помилка при створенні рахунку у Billing Service для бронювання {}: {}",
                    savedBooking.getId(), e.getMessage());
        }

        return toDto(savedBooking);
    }

    /**
     * Скасовує активне бронювання та нараховує штраф за пізнє скасування.
     * Штраф нараховується, якщо від моменту скасування до початку бронювання
     * залишається менш ніж {@value #MIN_HOURS_FOR_FREE_CANCELLATION} години.
     *
     * @param id ідентифікатор бронювання для скасування
     * @throws ResourceNotFoundException якщо бронювання не знайдено
     * @throws IllegalStateException     якщо бронювання не є активним
     */
    public BookingDto cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Бронювання з ідентифікатором " + id + " не знайдено"));

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Неможливо скасувати бронювання зі статусом: " + booking.getStatus().name());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelled = bookingRepository.save(booking);
        log.info("Бронювання {} скасовано", id);

        // Перевірка умови нарахування штрафу
        long hoursUntilStart = ChronoUnit.HOURS.between(LocalDateTime.now(), booking.getStartTime());
        if (hoursUntilStart < MIN_HOURS_FOR_FREE_CANCELLATION && hoursUntilStart >= 0) {
            try {
                CreatePenaltyRequest penaltyRequest = CreatePenaltyRequest.builder()
                        .bookingId(id)
                        .startTime(booking.getStartTime())
                        .reason("Скасування бронювання менш ніж за " +
                                MIN_HOURS_FOR_FREE_CANCELLATION + " години до початку")
                        .build();
                billingServiceClient.createPenalty(penaltyRequest);
                log.info("Штраф за пізнє скасування бронювання {} нараховано у Billing Service", id);
            } catch (Exception e) {
                log.error("Помилка при нарахуванні штрафу у Billing Service для бронювання {}: {}",
                        id, e.getMessage());
            }
        }

        return toDto(cancelled);
    }

    /**
     * Завершує активне бронювання (перехід у статус COMPLETED).
     * Викликається після того, як час бронювання закінчився.
     *
     * @throws IllegalStateException якщо бронювання не є активним
     */
    public BookingDto completeBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Бронювання з ідентифікатором " + id + " не знайдено"));

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Неможливо завершити бронювання зі статусом: " + booking.getStatus().name());
        }

        booking.setStatus(BookingStatus.COMPLETED);
        log.info("Бронювання {} успішно завершено", id);
        return toDto(bookingRepository.save(booking));
    }

    /**
     * Видаляє запис бронювання з бази даних.
     *
     * @throws ResourceNotFoundException якщо бронювання не знайдено
     */
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Бронювання з ідентифікатором " + id + " не знайдено");
        }
        bookingRepository.deleteById(id);
    }

    private BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .workspaceId(booking.getWorkspaceId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .build();
    }

    private Booking toEntity(BookingDto dto) {
        return Booking.builder()
                .userId(dto.getUserId())
                .workspaceId(dto.getWorkspaceId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(BookingStatus.ACTIVE)
                .build();
    }
}
