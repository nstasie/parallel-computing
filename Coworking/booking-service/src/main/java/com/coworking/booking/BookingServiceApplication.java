package com.coworking.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Головний клас запуску мікросервісу бронювань.
 * Анотація @EnableFeignClients активує підтримку Feign-клієнтів
 * для міжсервісної комунікації з Billing Service.
 */
@SpringBootApplication
@EnableFeignClients
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
