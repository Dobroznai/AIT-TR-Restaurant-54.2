package ait.de.model;

import ait.de.utilities.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingTest {
    private Booking booking;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @BeforeEach
    void setUp() {
        // Инициализация начальных данных для каждого теста
        startDateTime = LocalDateTime.of(2025, 3, 12, 12, 0); // Пример начала бронирования
        endDateTime = startDateTime.plusHours(2); // Пример конца бронирования
        // Сбросить значение nextId перед каждым тестом
        Booking.resetNextId();
    }

    @Test
    void testShouldCreateBookingWithValidData() {
        // Создаем бронирование с корректными данными
        booking = new Booking(1, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED);

        // Проверка, что бронирование успешно создано
        assertNotNull(booking, "Booking should not be null");
        assertEquals(1, booking.getTableId(), "Table ID should be 1");
        assertEquals("John Doe", booking.getCustomerName(), "Customer name should be 'John Doe'");
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus(), "Booking status should be CONFIRMED");
    }

    @Test
    void testShouldThrowExceptionIfStartDateTimeIsNull() {
        // Проверка, что если startDateTime равен null, будет выброшено исключение
        assertThrows(IllegalArgumentException.class, () -> new Booking(1, null, endDateTime, "John Doe", BookingStatus.CONFIRMED));
    }

    @Test
    void testShouldThrowExceptionIfEndDateTimeBeforeStartDateTime() {
        // Проверка, что если endDateTime до startDateTime, будет выброшено исключение
        LocalDateTime invalidEndDateTime = startDateTime.minusHours(1);
        assertThrows(IllegalArgumentException.class, () -> new Booking(1, startDateTime, invalidEndDateTime, "John Doe", BookingStatus.CONFIRMED));
    }

    @Test
    void testShouldThrowExceptionIfInvalidTableId() {
        // Проверка, что если tableId некорректен, будет выброшено исключение
        assertThrows(IllegalArgumentException.class, () -> new Booking(-1, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED));
    }

    @Test
    void testShouldThrowExceptionIfCustomerNameIsEmpty() {
        // Проверка, что если customerName пустой, будет выброшено исключение
        assertThrows(IllegalArgumentException.class, () -> new Booking(1, startDateTime, endDateTime, "", BookingStatus.CONFIRMED));
    }

    @Test
    void testShouldThrowExceptionIfStatusIsNull() {
        // Проверка, что если статус null, будет выброшено исключение
        assertThrows(IllegalArgumentException.class, () -> new Booking(1, startDateTime, endDateTime, "John Doe", null));
    }

    @Test
    void testShouldReturnCorrectStringRepresentation() {
        // Проверка правильности строкового представления для первого объекта
        booking = new Booking(1, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED);
        String expected = "Booking{id=1, tableId=1, startDateTime=12.03.2025, endDateTime=12.03.2025, customerName='John Doe', status=CONFIRMED}";
        assertEquals(expected, booking.toString(), "The string representation of the booking is incorrect");

        // Создадим второй объект, чтобы проверить инкремент ID
        Booking booking2 = new Booking(1, startDateTime, endDateTime, "Jane Doe", BookingStatus.PENDING);
        expected = "Booking{id=2, tableId=1, startDateTime=12.03.2025, endDateTime=12.03.2025, customerName='Jane Doe', status=PENDING}";
        assertEquals(expected, booking2.toString(), "The string representation of the second booking is incorrect");
    }

    @Test
    void testShouldHaveUniqueIdForEachBooking() {
        // Создание нескольких бронирований и проверка уникальных ID
        Booking booking1 = new Booking(1, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED);
        Booking booking2 = new Booking(2, startDateTime.plusHours(1), endDateTime.plusHours(1), "Jane Doe", BookingStatus.PENDING);

        assertNotEquals(booking1.getId(), booking2.getId(), "Booking IDs should be unique");
    }
}