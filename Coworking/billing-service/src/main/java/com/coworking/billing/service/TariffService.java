package com.coworking.billing.service;

import com.coworking.billing.dto.TariffDto;
import com.coworking.billing.exception.ResourceNotFoundException;
import com.coworking.billing.model.Tariff;
import com.coworking.billing.model.enums.WorkspaceType;
import com.coworking.billing.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервісний шар для управління тарифами білінгової системи.
 * Тариф визначає вартість оренди за одну годину для кожного типу простору.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TariffService {

    private final TariffRepository tariffRepository;

    /**
     * Повертає список усіх тарифів у системі.
     */
    @Transactional(readOnly = true)
    public List<TariffDto> getAllTariffs() {
        return tariffRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Повертає тариф за унікальним ідентифікатором.
     *
     * @throws ResourceNotFoundException якщо тариф з таким ID не існує
     */
    @Transactional(readOnly = true)
    public TariffDto getTariffById(Long id) {
        return toDto(tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Тариф з ідентифікатором " + id + " не знайдено")));
    }

    /**
     * Повертає тариф за типом робочого простору.
     *
     * @param type тип робочого простору
     * @throws ResourceNotFoundException якщо тариф для вказаного типу не існує
     */
    @Transactional(readOnly = true)
    public TariffDto getTariffByWorkspaceType(WorkspaceType type) {
        return toDto(tariffRepository.findByWorkspaceType(type)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Тариф для типу робочого простору " + type + " не знайдено")));
    }

    /**
     * Створює новий тариф. Для кожного типу простору може існувати лише один тариф.
     *
     * @throws IllegalArgumentException якщо тариф для вказаного типу вже існує
     */
    public TariffDto createTariff(TariffDto dto) {
        if (tariffRepository.existsByWorkspaceType(dto.getWorkspaceType())) {
            throw new IllegalArgumentException(
                    "Тариф для типу простору " + dto.getWorkspaceType() + " вже існує в системі");
        }
        return toDto(tariffRepository.save(toEntity(dto)));
    }

    /**
     * Оновлює існуючий тариф.
     *
     * @throws ResourceNotFoundException якщо тариф з таким ID не існує
     */
    public TariffDto updateTariff(Long id, TariffDto dto) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Тариф з ідентифікатором " + id + " не знайдено"));
        tariff.setWorkspaceType(dto.getWorkspaceType());
        tariff.setPricePerHour(dto.getPricePerHour());
        return toDto(tariffRepository.save(tariff));
    }

    /**
     * Видаляє тариф за ідентифікатором.
     *
     * @throws ResourceNotFoundException якщо тариф з таким ID не існує
     */
    public void deleteTariff(Long id) {
        if (!tariffRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Тариф з ідентифікатором " + id + " не знайдено");
        }
        tariffRepository.deleteById(id);
    }

    private TariffDto toDto(Tariff tariff) {
        return TariffDto.builder()
                .id(tariff.getId())
                .workspaceType(tariff.getWorkspaceType())
                .pricePerHour(tariff.getPricePerHour())
                .build();
    }

    private Tariff toEntity(TariffDto dto) {
        return Tariff.builder()
                .workspaceType(dto.getWorkspaceType())
                .pricePerHour(dto.getPricePerHour())
                .build();
    }
}
