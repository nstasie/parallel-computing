package com.coworking.booking.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Об'єкт передачі даних (DTO) для сутності User.
 * Використовується для отримання вхідних даних від клієнта та
 * відправки відповіді через REST API, не розкриваючи деталей реалізації сутності.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    /** Ідентифікатор користувача. Не заповнюється при створенні (POST). */
    private Long id;

    @NotBlank(message = "Ім'я користувача не може бути порожнім")
    @Size(min = 2, max = 100, message = "Ім'я повинно містити від 2 до 100 символів")
    private String name;

    @NotBlank(message = "Електронна адреса не може бути порожньою")
    @Email(message = "Некоректний формат електронної адреси")
    private String email;

    @NotBlank(message = "Номер телефону не може бути порожнім")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некоректний формат номера телефону")
    private String phone;
}
