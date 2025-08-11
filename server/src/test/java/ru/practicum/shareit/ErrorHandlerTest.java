package ru.practicum.shareit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void testHandleValidation() {
        ErrorResponse errorResponse = errorHandler.handleIncorrectParameter(new ValidationException("Test"));

        assertEquals("Ошибка валидации.", errorResponse.getError());
    }

    @Test
    void testHandleInternalServerError() {
        ErrorResponse errorResponse = errorHandler.handleInternalServerError(new InternalServerErrorException("Test"));

        assertEquals("Ошибка сервера.", errorResponse.getError());
    }

    @Test
    void testHandleNotFoundException() {
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(new NotFoundException("Test"));

        assertEquals("Ошибка с входным параметром.", errorResponse.getError());
    }

    @Test
    void testHandleConstraintViolation() {
        ConstraintViolationException exception = new ConstraintViolationException(
                "Test message",
                Collections.emptySet()
        );

        ErrorResponse errorResponse = errorHandler.handleIncorrectParameter(exception);

        assertEquals("Ошибка валидации.", errorResponse.getError());
    }

    @Test
    void testHandleInvocationTarget() {
        Throwable cause = new RuntimeException("Test error message");
        InvocationTargetException exception = new InvocationTargetException(cause);

        ErrorResponse errorResponse = errorHandler.handleIncorrectParameter(exception);

        assertAll(
                () -> assertEquals("Ошибка валидации.", errorResponse.getError())
        );
    }

    @Test
    void testHandleNoSuchElement() {
        ErrorResponse errorResponse = errorHandler.handleIncorrectParameter(new NoSuchElementException("Test"));

        assertEquals("Ошибка с входным параметром.", errorResponse.getError());
    }

    @Test
    void testHandleConflict() {
        ErrorResponse errorResponse = errorHandler.handleConflictParameter(new ConflictException("Test"));

        assertEquals("Ошибка из-за уже существующих параметров.", errorResponse.getError());
    }

    @Test
    void testHandleAccessDenied() {
        ErrorResponse errorResponse = errorHandler.handleAccessDenied(new AccessDeniedException("Test"));

        assertEquals("Ошибка доступа к данным.", errorResponse.getError());
    }
}