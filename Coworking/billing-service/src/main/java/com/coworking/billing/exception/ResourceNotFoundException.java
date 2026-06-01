package com.coworking.billing.exception;

/**
 * Виняток, що виникає при спробі звернення до ресурсу, якого не існує в системі.
 * Відповідає HTTP-статусу 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
