package ait.de.model;

import ait.de.utilities.BookingStatus;
import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Represents a table booking in the restaurant.
 */
@Slf4j
@Getter
@EqualsAndHashCode(of = {"tableId", "startDateTime", "endDateTime"})
public class Booking {
    private static int nextId = 1; // Auto-increment ID for each booking

    private final int id; // Unique booking ID
    private final int tableId; // Table being booked
    private final LocalDateTime startDateTime; // Booking start time
    private final LocalDateTime endDateTime; // Booking end time
    private final String customerName; // Name of the person booking
    private final BookingStatus status; // Booking status (CONFIRMED, CANCELED)

    /**
     * Constructor for creating a booking.
     *
     * @param tableId      ID of the table being booked.
     * @param startDateTime Start time of the booking.
     * @param endDateTime   End time of the booking.
     * @param customerName  Name of the customer.
     * @param status        Status of the booking.
     * @throws IllegalArgumentException if the end time is before the start time.
     */
    public Booking(int tableId, LocalDateTime startDateTime, LocalDateTime endDateTime, String customerName, BookingStatus status) {
        if (startDateTime == null) {
            log.error("Start time is null");
            throw new IllegalArgumentException("Start time must not be null.");
        }
        if (endDateTime.isBefore(startDateTime)) {
            log.error("EndDateTime {} is before StartDateTime {}", endDateTime, startDateTime);
            throw new IllegalArgumentException("End time must be after start time.");
        }
        if (tableId <= 0){
            log.error("Invalid or incorrect table ID: {}", tableId);
            throw new IllegalArgumentException("Table ID must be positive.");
        }
        if (customerName == null || customerName.trim().isEmpty()){
            log.error("Customer name is null or empty.");
            throw new IllegalArgumentException("Customer name must not be empty.");
        }
        if (status == null){
            log.error("Booking status is null.");
            throw new IllegalArgumentException("Booking status must not be null.");
        }
        this.id = nextId++;
        this.tableId = tableId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.customerName = customerName;
        this.status = status;
    }

    /**
     * Custom string representation of Booking.
     *
     * @return Formatted string with booking details.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Booking{");
        sb.append("id=").append(id);
        sb.append(", tableId=").append(tableId);
        sb.append(", startDateTime=").append(startDateTime);
        sb.append(", endDateTime=").append(endDateTime);
        sb.append(", customerName='").append(customerName).append('\'');
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}