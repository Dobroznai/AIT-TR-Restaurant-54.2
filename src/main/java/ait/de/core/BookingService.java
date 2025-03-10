package ait.de.core;

import ait.de.exceptions.BookingException;
import ait.de.model.Booking;
import ait.de.exceptions.BookingConflictException;
import ait.de.dao.BookingRepository;
import ait.de.dao.FileStorage;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
public class BookingService {
    private static final LocalTime OPENING_TIME = LocalTime.of(10, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);

    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
        loadBookings(); // Load bookings from file on startup
    }

    /**
     * Creates a new booking after checking for conflicts.
     */
    public void createBooking(Booking newBooking) throws BookingException {
        if (!isValidBookingTime(newBooking)) {
            log.warn("Invalid booking time: {}", newBooking);
            throw new BookingException("Booking must end at least one hour before closing time!");
        }
        if (repository.hasConflict(newBooking.getTableId(), newBooking.getStartDateTime(), newBooking.getEndDateTime())) {
            log.warn("Booking conflict detected: {}", newBooking);
            throw new BookingConflictException("Booking time conflicts with an existing reservation!");
        }
        repository.addBooking(newBooking);
        log.info("Booking successfully created: {}", newBooking);
        saveBookings(); // Save after booking is created
    }

    /**
     * Validates booking time constraints.
     */
    private boolean isValidBookingTime(Booking booking) {
        LocalDateTime end = booking.getEndDateTime();
        LocalDateTime start = booking.getStartDateTime();
        LocalDateTime closingDeadline = LocalDateTime.of(start.toLocalDate(), CLOSING_TIME.minusHours(1));

        return !start.toLocalTime().isBefore(OPENING_TIME) &&
                !end.isAfter(closingDeadline);
    }

    /**
     * Cancels an existing booking.
     */
    public boolean cancelBooking(int bookingId) {
        if (repository.getBookingById(bookingId).isPresent()) {
            repository.removeBooking(bookingId);
            log.info("Booking ID={} successfully canceled", bookingId);
            saveBookings(); // Save after cancellation
            return true;
        }
        log.warn("Attempt to cancel a non-existing booking ID={}", bookingId);
        return false;
    }

    /**
     * Retrieves all bookings.
     */
    public List<Booking> getAllBookings() {
        return repository.getAllBookings();
    }

    /**
     * Saves current bookings to file.
     */
    private void saveBookings() {
        FileStorage.saveToFile(repository.getAllBookings());
    }

    /**
     * Loads bookings from file on startup.
     */
    private void loadBookings() {
        List<Booking> loadedBookings = FileStorage.loadFromFile();
        for (Booking booking : loadedBookings) {
            repository.addBooking(booking);
        }
    }
}