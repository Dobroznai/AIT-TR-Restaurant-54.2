package ait.de.exceptions;
/**
 * Exception thrown when a booking conflict is detected.
 */
public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}
