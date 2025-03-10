package ait.de.app;

import ait.de.core.BookingService;
import ait.de.dao.BookingRepository;
import ait.de.dao.FileStorage;
import ait.de.dao.InMemoryStorage;
import ait.de.exceptions.BookingConflictException;
import ait.de.exceptions.BookingException;
import ait.de.model.Booking;
import ait.de.utilities.BookingStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based UI for managing restaurant table bookings.
 */
@Slf4j
public class ConsoleUI {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final BookingService bookingService;
    private final Scanner sc;

    public ConsoleUI(BookingService bookingService) {
        this.bookingService = bookingService;
        this.sc = new Scanner(System.in);
    }

    /**
     * Displays the main menu and handles user input.
     */
    public void showMenu() {
        boolean run = true;
        while (run) {
            System.out.println("\n==== TABLE BOOKING SYSTEM ====");
            System.out.println("1. Create a booking");
            System.out.println("2. View all bookings");
            System.out.println("3. Cancel a booking");
            System.out.println("4. Save bookings to a file");
            System.out.println("5. Load bookings from a file");
            System.out.println("6. Exit");
            System.out.println("===============================");
            System.out.print("Choose an option: ");


            byte choice = inputChoice();
            switch (choice) {
                case 1 -> createBooking();
                case 2 -> viewBookings();
                case 3 -> cancelBooking();
                case 4 -> saveBookings();
                case 5 -> loadBookings();
                case 6 -> {
                    run = false;
                    System.out.println("Exiting the program.");
                    log.warn("Exiting the program.");
                }
                default -> {
                    System.out.println("Invalid choice. Please try again.");
                    log.warn("Invalid choice. Please try again.");
                }
            }
        }
        sc.close();
    }

    private byte inputChoice() {
        while (true) {
            try {
                return Byte.parseByte(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number:");
            }
        }
    }

    /**
     * Handles booking creation.
     */
    private void createBooking() {
        try {
            System.out.print("Enter Table ID (1-10): ");
            int tableId = Integer.parseInt(sc.nextLine());

            // Check if tableId is within the allowed range (1-10)
            if (tableId < 1 || tableId > 10) {
                System.out.println("Error: Invalid table number. Please enter a number between 1 and 10.");
                return;
            }

            System.out.print("Enter Start Time (dd.MM.yyyy HH:mm): ");
            LocalDateTime startTime = LocalDateTime.parse(sc.nextLine(), FORMATTER);

            System.out.print("Enter End Time (dd.MM.yyyy HH:mm): ");
            LocalDateTime endTime = LocalDateTime.parse(sc.nextLine(), FORMATTER);

            System.out.print("Enter Customer Name: ");
            String customerName = sc.nextLine();

            Booking newBooking = new Booking(tableId, startTime, endTime, customerName, BookingStatus.CONFIRMED);
            bookingService.createBooking(newBooking);
            System.out.println("Booking successfully created!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numeric values where required.");
        } catch (BookingConflictException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (BookingException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid format. Please use dd.MM.yyyy HH:mm.");
        }
    }

    /**
     * Displays all bookings.
     */
    private void viewBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            bookings.forEach(System.out::println);
        }
    }

    /**
     * Handles booking cancellation.
     */
    private void cancelBooking() {
        try {
            System.out.print("Enter Booking ID to Cancel: ");
            int bookingId = Integer.parseInt(sc.nextLine());

            if (bookingService.cancelBooking(bookingId)) {
                System.out.println("Booking successfully canceled.");
            } else {
                System.out.println("Booking ID not found.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric Booking ID.");
        }
    }

    /**
     * Saves bookings to a file.
     */
    private void saveBookings() {
        FileStorage.saveToFile(bookingService.getAllBookings());
        System.out.println("Bookings successfully saved to file.");
    }

    /**
     * Loads bookings from a file.
     */
    private void loadBookings() {
        List<Booking> loadedBookings = FileStorage.loadFromFile();

        if (loadedBookings.isEmpty()) {
            System.out.println("No bookings found in the file. You can create new bookings.");
            return;
        }
        int skippedCount = 0;

        for (Booking booking : loadedBookings) {
            try {
                bookingService.createBooking(booking);
            } catch (BookingConflictException e) {
                System.out.println("Skipping conflicting booking: " + booking);
                skippedCount++;
            } catch (BookingException e) {
                System.out.println("Error loading booking: " + e.getMessage());
            }
        }

        System.out.println("Bookings successfully loaded from file.");
        if (skippedCount > 0) {
            System.out.println("Skipped " + skippedCount + " conflicting bookings.");
        }
    }

    /**
     * Main method to run the console UI.
     */
    public static void main(String[] args) {
        BookingRepository repository = new InMemoryStorage();
        BookingService bookingService = new BookingService(repository);
        ConsoleUI consoleUI = new ConsoleUI(bookingService);
        consoleUI.showMenu();
    }
}