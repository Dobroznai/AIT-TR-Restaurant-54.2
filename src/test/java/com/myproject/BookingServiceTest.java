package com.myproject;

import com.myproject.exception.BookingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingServiceTest {
    private BookingService bookingService;
    private List<Table> tables;

    @BeforeEach
    void setUp() {
        tables = new ArrayList<>();
        tables.add(new Table(1, 4, false));
        tables.add(new Table(2, 2, false));
        bookingService = new BookingService(tables);
    }

    @Test
    void testCreateBookingWithValidTime_ShouldCreateBookingSuccessfully() throws BookingException {
        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);

        bookingService.createBooking(newBooking);
        assertEquals(1, bookingService.getAllBookings().size());
        assertTrue(bookingService.getAllBookings().contains(newBooking));
    }

    @Test
    void testCreateBookingWithInvalidTime_ShouldThrowBookingException() {
        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 21, 0),
                LocalDateTime.of(2025, 3, 7, 23, 0), "Customer1", BookingStatus.CONFIRMED);

        assertThrows(BookingException.class, () -> bookingService.createBooking(newBooking));
    }

    @Test
    void testCreateBookingWithConflictingTime_ShouldThrowBookingExceptionForTimeConflict() throws BookingException {
        Booking existingBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);
        bookingService.createBooking(existingBooking);

        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 11, 0),
                LocalDateTime.of(2025, 3, 7, 13, 0), "Customer2", BookingStatus.PENDING);

        assertThrows(BookingException.class, () -> bookingService.createBooking(newBooking));
    }

    @Test
    void testCancelBookingWithValidData_ShouldCancelBookingSuccessfully() throws BookingException {
        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);
        bookingService.createBooking(newBooking);

        boolean result = bookingService.cancelBooking(1, "Customer1");
        assertTrue(result);
        assertEquals(0, bookingService.getAllBookings().size());
    }

    @Test
    void testCancelBookingWithInvalidData_ShouldReturnFalseForNonExistingCustomer() throws BookingException {
        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);
        bookingService.createBooking(newBooking);

        boolean result = bookingService.cancelBooking(1, "NonExistingCustomer");
        assertFalse(result);
    }

    @Test
    void testCancelBooking_WhenBookingsListIsEmpty_ShouldReturnFalse() {
        boolean result = bookingService.cancelBooking(1, "Customer1");
        assertFalse(result, "Бронирование не должно быть отменено, так как список пуст");
    }

    @Test
    void testCancelBooking_WhenBookingExists_ShouldReturnTrue() throws BookingException {
        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);
        bookingService.createBooking(newBooking);

        boolean result = bookingService.cancelBooking(1, "Customer1");
        assertTrue(result, "Бронирование должно быть отменено для стола 1 и клиента Customer1");
        assertEquals(0, bookingService.getAllBookings().size(), "Список бронирований должен быть пуст");
    }

    @Test
    void testCancelBooking_WhenBookingDoesNotExist_ShouldReturnFalse() throws BookingException {
        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);
        bookingService.createBooking(newBooking);

        boolean result = bookingService.cancelBooking(1, "NonExistingCustomer");
        assertFalse(result, "Бронирование не должно быть отменено, так как клиента NonExistingCustomer нет в списке");
    }

    @Test
    void testCancelBooking_WhenInvalidTableId_ShouldReturnFalse() throws BookingException {
        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);
        bookingService.createBooking(newBooking);

        boolean result = bookingService.cancelBooking(999, "Customer1");
        assertFalse(result, "Бронирование не должно быть отменено, так как стол с ID 999 не существует");
    }

    @Test
    void testCancelBooking_WhenCustomerNameWithDifferentCase_ShouldReturnTrue() throws BookingException {
        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);
        bookingService.createBooking(newBooking);

        boolean result = bookingService.cancelBooking(1, "customer1"); // Разный регистр
        assertTrue(result, "Бронирование должно быть отменено, несмотря на разный регистр имени клиента");
        assertEquals(0, bookingService.getAllBookings().size(), "Список бронирований должен быть пуст");
    }

    @Test
    void testSaveBookingsToFile_ShouldSaveBookingsToFileSuccessfully() throws IOException {
        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);
        bookingService.createBooking(newBooking);

        bookingService.saveBookingsToFile("bookings_test.txt");

        // Let's load the file and verify the contents
        BookingService newBookingService = new BookingService(tables);
        newBookingService.loadBookingsFromFile("bookings_test.txt");

        assertEquals(1, newBookingService.getAllBookings().size());
        assertTrue(newBookingService.getAllBookings().contains(newBooking));
    }

    @Test
    void testLoadBookingsFromFile_ShouldLoadBookingsFromFileSuccessfully() throws IOException {
        // Simulating loading data by directly adding a booking
        Booking newBooking = new Booking(1, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);

        // Save the booking to the service (adding the booking to the list)
        bookingService.createBooking(newBooking);

        // Save the booking to a file
        bookingService.saveBookingsToFile("bookings_test.txt");

        // Create a new BookingService instance and load from the file
        BookingService newBookingService = new BookingService(tables);
        newBookingService.loadBookingsFromFile("bookings_test.txt");

        assertEquals(1, newBookingService.getAllBookings().size());
        assertTrue(newBookingService.getAllBookings().contains(newBooking));
    }

    @Test
    void testCreateBookingWithInvalidTableId_ShouldThrowBookingExceptionForInvalidTableId() {
        Booking newBooking = new Booking(999, LocalDateTime.of(2025, 3, 7, 10, 0),
                LocalDateTime.of(2025, 3, 7, 12, 0), "Customer1", BookingStatus.CONFIRMED);

        assertThrows(BookingException.class, () -> bookingService.createBooking(newBooking));
    }

    @Test
    void testCancelBookingWithEmptyList_ShouldReturnFalseWhenNoBookingsExist() {
        bookingService = new BookingService(new ArrayList<>());
        boolean result = bookingService.cancelBooking(1, "Customer1");
        assertFalse(result);
    }
}