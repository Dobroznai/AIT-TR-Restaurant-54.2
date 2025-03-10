package ait.de.dao;

import ait.de.model.Booking;
import ait.de.exceptions.BookingConflictException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface for managing booking storage operations.
 */
public interface BookingRepository {

    /**
     * Retrieves all bookings.
     *
     * @return List of all bookings.
     */
    List<Booking> getAllBookings();

    /**
     * Retrieves a booking by its ID.
     *
     * @param bookingId The ID of the booking.
     * @return An Optional containing the booking if found, or empty if not.
     */
    Optional<Booking> getBookingById(int bookingId);

    /**
     * Adds a new booking after checking for conflicts.
     *
     * @param newBooking The booking to be added.
     * @throws BookingConflictException if there is a conflict.
     */
    void addBooking(Booking newBooking);

    /**
     * Removes a booking by its ID.
     *
     * @param bookingId The ID of the booking to be removed.
     */
    void removeBooking(int bookingId);

    /**
     * Checks if a new booking conflicts with existing ones for the same table.
     *
     * @param tableId       The ID of the table.
     * @param startDateTime The start time of the new booking.
     * @param endDateTime   The end time of the new booking.
     * @return true if there is a conflict, false otherwise.
     */
    boolean hasConflict(int tableId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}