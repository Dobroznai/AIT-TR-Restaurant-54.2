package ait.de.dao;

import ait.de.exceptions.BookingConflictException;
import ait.de.model.Booking;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory storage for managing bookings.
 */
@Slf4j
public class InMemoryStorage implements BookingRepository {
    private final List<Booking> bookings = new ArrayList<>();

    /**
     * Retrieves all bookings currently stored in memory.
     *
     * @return List of all stored bookings.
     */
    @Override
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings); // Return a copy to prevent external modifications
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param bookingId The ID of the booking.
     * @return Optional containing the booking if found.
     */
    @Override
    public Optional<Booking> getBookingById(int bookingId) {
        return bookings.stream()
                .filter(booking -> booking.getId() == bookingId)
                .findFirst();
    }

    /**
     * Adds a new booking to the storage after checking for conflicts.
     *
     * @param newBooking The booking to be added.
     * @throws BookingConflictException if a time conflict is detected.
     */
    @Override
    public void addBooking(Booking newBooking) {
        if (hasConflict(newBooking.getTableId(), newBooking.getStartDateTime(), newBooking.getEndDateTime())) {
            log.warn("Booking conflict detected: {}", newBooking);
            throw new BookingConflictException("Booking time conflicts with an existing reservation!");
        }
        bookings.add(newBooking);
        log.info("Booking successfully added: {}", newBooking);
    }

    /**
     * Removes a booking from storage by ID.
     *
     * @param bookingId The ID of the booking to be removed.
     */
    @Override
    public void removeBooking(int bookingId) {
        bookings.removeIf(booking -> booking.getId() == bookingId);
        log.info("Booking ID={} removed from storage.", bookingId);
    }

    /**
     * Checks if a new booking conflicts with any existing bookings for the same table.
     *
     * @param tableId        The ID of the table.
     * @param startDateTime  The start time of the new booking.
     * @param endDateTime    The end time of the new booking.
     * @return true if a conflict exists, false otherwise.
     */
    @Override
    public boolean hasConflict(int tableId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return bookings.stream()
                .filter(booking -> booking.getTableId() == tableId)
                .anyMatch(existingBooking ->
                        startDateTime.isBefore(existingBooking.getEndDateTime()) &&
                                existingBooking.getStartDateTime().isBefore(endDateTime));
    }
}