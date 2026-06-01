package com.coworking.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Сутність користувача системи бронювання коворкінгу.
 * Представляє зареєстрованого клієнта, який може створювати бронювання.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Унікальний ідентифікатор користувача, генерується автоматично базою даних. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Повне ім'я користувача. Обов'язкове поле, від 2 до 100 символів. */
    @NotBlank(message = "Ім'я користувача не може бути порожнім")
    @Size(min = 2, max = 100, message = "Ім'я повинно містити від 2 до 100 символів")
    @Column(nullable = false)
    private String name;

    /** Електронна адреса користувача. Має бути унікальною в системі. */
    @NotBlank(message = "Електронна адреса не може бути порожньою")
    @Email(message = "Некоректний формат електронної адреси")
    @Column(nullable = false, unique = true)
    private String email;

    /** Номер телефону користувача для зворотного зв'язку. */
    @NotBlank(message = "Номер телефону не може бути порожнім")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некоректний формат номера телефону")
    @Column(nullable = false)
    private String phone;
}
