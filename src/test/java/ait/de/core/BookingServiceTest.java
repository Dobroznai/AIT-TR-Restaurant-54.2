package ait.de.core;

import ait.de.dao.BookingRepository;
import ait.de.dao.InMemoryStorage;
import ait.de.exceptions.BookingConflictException;
import ait.de.exceptions.BookingException;
import ait.de.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ait.de.utilities.BookingStatus;

public class BookingServiceTest {

    private BookingService bookingService;
    private FakeBookingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FakeBookingRepository(); // Используем фейковую реализацию
        bookingService = new BookingService(repository);
        Booking.resetNextId();
        repository.removeAllBookings(); // Очистка перед каждым тестом

    }

    @Test
    void testCreateBookingSuccessfully() throws BookingException {
        // Arrange
        repository.removeAllBookings(); // Очищаем репозиторий перед тестом
        LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 12, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 12, 12, 0);
        Booking newBooking = new Booking(1, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED);

        // Act
        bookingService.createBooking(newBooking);

        // Assert
        assertEquals(1, repository.getAllBookings().size());  // Проверяем, что бронирование добавлено в репозиторий
    }

    @Test
    void testCreateBookingWithInvalidTableId() {
        // Arrange
        LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 12, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 12, 12, 0);
        Booking newBooking = new Booking(11, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED);

        // Act & Assert
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.createBooking(newBooking));
        assertEquals("Table number must be between 1 and 10!", exception.getMessage());
    }

    @Test
    void testCreateBookingWithTimeConflict() {
        // Arrange
        LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 12, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 12, 12, 0);
        Booking newBooking = new Booking(1, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED);

        repository.addBooking(newBooking); // Добавляем бронь в репозиторий

        // Act & Assert
        BookingConflictException exception = assertThrows(BookingConflictException.class, () -> bookingService.createBooking(newBooking));
        assertEquals("Booking time conflicts with an existing reservation!", exception.getMessage());
    }

    @Test
    void testCancelBookingSuccessfully() {
        // Arrange
        repository.removeAllBookings(); // Очищаем репозиторий перед тестом
        Booking booking = new Booking(1, LocalDateTime.of(2025, 3, 12, 10, 0), LocalDateTime.of(2025, 3, 12, 12, 0), "John Doe", BookingStatus.CONFIRMED);
        repository.addBooking(booking); // Добавляем бронь
//Проверяем, что бронь действительно добавлена
        System.out.println("All bookings after adding: " + repository.getAllBookings());

        // Act
        boolean result = bookingService.cancelBooking(1);

        // Assert
        assertTrue(result);  // Проверяем, что бронь была отменена
        assertTrue(repository.getAllBookings().isEmpty());  // Репозиторий должен быть пустым после отмены
    }

    @Test
    void testCancelBookingWithNonExistentBooking() {
        // Act
        boolean result = bookingService.cancelBooking(99);

        // Assert
        assertFalse(result);  // Бронь не существует, должна вернуть false
    }

    @Test
    void testGetAllBookings() {
        // Arrange
        Booking booking1 = new Booking(1, LocalDateTime.of(2025, 3, 12, 10, 0), LocalDateTime.of(2025, 3, 12, 12, 0), "John Doe", BookingStatus.CONFIRMED);
        Booking booking2 = new Booking(2, LocalDateTime.of(2025, 3, 12, 14, 0), LocalDateTime.of(2025, 3, 12, 16, 0), "Jane Doe", BookingStatus.PENDING);
        repository.addBooking(booking1);
        repository.addBooking(booking2);

        // Act
        List<Booking> result = bookingService.getAllBookings();

        // Assert
        assertEquals(2, result.size());  // Проверяем, что количество бронирований равно 2
        assertTrue(result.contains(booking1));  // Проверяем, что booking1 присутствует в списке
        assertTrue(result.contains(booking2));  // Проверяем, что booking2 присутствует в списке
    }

    @Test
    void testCreateBookingOutsideBusinessHours() {
        // Arrange
        LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 12, 22, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 12, 23, 30); // Предположим, что заведение закрывается в 23:00
        Booking newBooking = new Booking(1, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED);

        // Act & Assert
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.createBooking(newBooking));
        assertEquals("Booking must end at least one hour before closing time!", exception.getMessage());
    }

    @Test
    void testCreateBookingWithInvalidTime() {
        // Arrange
        LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 12, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 12, 10, 0); // Ошибка: конец совпадает с началом
        Booking newBooking = new Booking(1, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED);

        // Act & Assert
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.createBooking(newBooking));
        assertEquals("End time must be later than start time.", exception.getMessage());
    }

    @Test
    void testCreateBookingWithTableIdLessThanOne() {
        // Arrange
        LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 12, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 12, 12, 0);
        // ID стола < 1
        Booking newBooking = new Booking(0, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED);

        // Act & Assert
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.createBooking(newBooking));
        assertEquals("Table number must be between 1 and 10!", exception.getMessage());
    }

    @Test
    void testCreateBookingWithTableIdGreaterThanTen() {
        // Arrange
        LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 12, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 12, 12, 0);
        // ID стола > 10
        Booking newBooking = new Booking(11, startDateTime, endDateTime, "John Doe", BookingStatus.CONFIRMED);

        // Act & Assert
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.createBooking(newBooking));
        assertEquals("Table number must be between 1 and 10!", exception.getMessage());
    }

    // Фейковая реализация BookingRepository
    private static class FakeBookingRepository implements BookingRepository {
        private final List<Booking> bookings = new ArrayList<>();

        public void removeAllBookings() {
            bookings.clear(); // Очищаем все бронирования
        }
        @Override
        public void addBooking(Booking booking) {
            bookings.add(booking);
        }

        @Override
        public void removeBooking(int bookingId) {
            bookings.removeIf(booking -> booking.getId() == bookingId);
        }

        @Override
        public Optional<Booking> getBookingById(int bookingId) {
            return bookings.stream().filter(booking -> booking.getId() == bookingId).findFirst();
        }

        @Override
        public List<Booking> getAllBookings() {
            return new ArrayList<>(bookings);
        }

        @Override
        public boolean hasConflict(int tableId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
            return bookings.stream().anyMatch(booking ->
                    booking.getTableId() == tableId &&
                            !(booking.getEndDateTime().isBefore(startDateTime) || booking.getStartDateTime().isAfter(endDateTime))
            );
        }
    }
}