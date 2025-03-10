package ait.de.dao;

import ait.de.model.Booking;
import ait.de.utilities.BookingStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages persistent storage of bookings in a CSV file.
 */
@Slf4j
public class FileStorage {
    private static final String DIRECTORY = "src/main/java/ait/de/files";  // Storage location `
    private static final String FILE_NAME = DIRECTORY + "/bookings.csv";  // File path
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Ensures the storage directory exists.
     */
    private static void ensureDirectoryExists() {
        Path path = Paths.get(DIRECTORY);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                log.info("Created missing storage directory: {}", DIRECTORY);
            } catch (IOException e) {
                log.error("Failed to create storage directory: {}", DIRECTORY, e);
            }
        }
    }

    /**
     * Saves a list of bookings to a CSV file.
     *
     * @param bookings List of bookings to be saved.
     */
    public static void saveToFile(List<Booking> bookings) {
        ensureDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Booking booking : bookings) {
                writer.write(formatBooking(booking));
                writer.newLine();
            }
            log.info("Bookings successfully saved to file: {}", FILE_NAME);
        } catch (IOException e) {
            log.error("Error writing to file: {}", FILE_NAME, e);
        }
    }

    /**
     * Loads bookings from a CSV file.
     *
     * @return List of loaded bookings.
     */
    public static List<Booking> loadFromFile() {
        ensureDirectoryExists();
        List<Booking> bookings = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            log.warn("Booking file does not exist. A new file will be created when saving.");
            return bookings;  // Return empty list, no file yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Booking booking = parseBooking(line);
                if (booking != null) {
                    bookings.add(booking);
                }
            }
            log.info("Bookings successfully loaded from file: {}", FILE_NAME);
        } catch (IOException e) {
            log.error("Error reading file: {}", FILE_NAME, e);
        }
        return bookings;
    }

    /**
     * Formats a booking object into a CSV-compatible string.
     */
    private static String formatBooking(Booking booking) {
        return booking.getTableId() + "," +
                booking.getStartDateTime().format(FORMATTER) + "," +
                booking.getEndDateTime().format(FORMATTER) + "," +
                booking.getCustomerName() + "," +
                booking.getStatus();
    }

    /**
     * Parses a booking entry from a CSV line.
     */
    private static Booking parseBooking(String line) {
        String[] parts = line.split(",");
        if (parts.length == 5) {
            try {
                int tableId = Integer.parseInt(parts[0]);
                LocalDateTime startDateTime = LocalDateTime.parse(parts[1], FORMATTER);
                LocalDateTime endDateTime = LocalDateTime.parse(parts[2], FORMATTER);
                String customerName = parts[3];
                BookingStatus status = BookingStatus.valueOf(parts[4]);
                return new Booking(tableId, startDateTime, endDateTime, customerName, status);
            } catch (Exception e) {
                log.error("Error parsing booking entry: {}", line, e);
            }
        } else {
            log.warn("Invalid booking entry format: {}", line);
        }
        return null;
    }
}