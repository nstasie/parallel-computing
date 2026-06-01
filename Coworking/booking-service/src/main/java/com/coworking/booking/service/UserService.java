package com.coworking.booking.service;

import com.coworking.booking.dto.UserDto;
import com.coworking.booking.exception.ResourceNotFoundException;
import com.coworking.booking.model.User;
import com.coworking.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервісний шар для управління користувачами системи бронювання.
 * Реалізує бізнес-логіку CRUD-операцій над сутністю User
 * та забезпечує перетворення між сутностями та DTO.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Повертає список усіх зареєстрованих користувачів системи.
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Повертає користувача за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор користувача
     * @throws ResourceNotFoundException якщо користувача з таким ID не існує
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Користувача з ідентифікатором " + id + " не знайдено"));
        return toDto(user);
    }

    /**
     * Створює нового користувача у системі.
     * Перевіряє унікальність електронної адреси перед збереженням.
     *
     * @param dto дані нового користувача
     * @throws IllegalArgumentException якщо email вже використовується іншим користувачем
     */
    public UserDto createUser(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "Користувач з електронною адресою " + dto.getEmail() + " вже існує в системі");
        }
        User user = toEntity(dto);
        return toDto(userRepository.save(user));
    }

    /**
     * Оновлює дані існуючого користувача.
     *
     * @param id  ідентифікатор користувача, якого потрібно оновити
     * @param dto нові дані користувача
     * @throws ResourceNotFoundException якщо користувача з таким ID не існує
     */
    public UserDto updateUser(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Користувача з ідентифікатором " + id + " не знайдено"));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        return toDto(userRepository.save(user));
    }

    /**
     * Видаляє користувача за ідентифікатором.
     *
     * @param id ідентифікатор користувача для видалення
     * @throws ResourceNotFoundException якщо користувача з таким ID не існує
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Користувача з ідентифікатором " + id + " не знайдено");
        }
        userRepository.deleteById(id);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    private User toEntity(UserDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();
    }
}
