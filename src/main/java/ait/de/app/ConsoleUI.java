package ait.de.app;

import ait.de.exceptions.BookingException;
import ait.de.model.Booking;
import ait.de.exceptions.BookingConflictException;
import ait.de.dao.BookingRepository;
import ait.de.core.BookingService;
import ait.de.utilities.BookingStatus;
import ait.de.dao.FileStorage;
import ait.de.dao.InMemoryStorage;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class ConsoleUI {
    private static final Scanner scanner = new Scanner(System.in);
    private static BookingRepository repository = new InMemoryStorage();
    private static BookingService bookingService = new BookingService(repository);

    public static void main(String[] args) {
        byte choice;
        boolean run = true;
        while (run) {
            showMenu();
            choice = inputChoice();

            switch (choice) {
                case 1 -> addBooking(bookingService);
                case 2 -> cancelBooking(bookingService);
                case 3 -> printBookingList(bookingService);
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
        scanner.close();
    }

    private static void showMenu() {
        System.out.println("Menu: ");
        System.out.println("1. Create a booking");
        System.out.println("2. Cancel your booking");
        System.out.println("3. View all bookings");
        System.out.println("4. Save bookings to a file");
        System.out.println("5. Load bookings from a file");
        System.out.println("6. Exit");
        System.out.print("Choose an action: ");
    }

    private static byte inputChoice() {
        byte choice = scanner.nextByte();
        scanner.nextLine();
        return choice;
    }

    private static Booking buildBooking() {
        System.out.print("Enter the table number (1 - 10): ");
        int tableId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter the booking start time (dd.MM.yyyy HH:mm, for example, 16.03.2025 13:00): ");
        String startDateTimeString = scanner.nextLine().trim();

        System.out.print("Enter the booking end time (dd.MM.yyyy HH:mm, for example, 16.03.2025 15:00): ");
        String endDateTimeString = scanner.nextLine().trim();

        System.out.print("Enter the client's name (first and last name): ");
        String customerName = scanner.nextLine().trim();

        DateTimeFormatter formatterUser = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeString, formatterUser);
        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString, formatterUser);
        Booking booking = null;
        try {
            booking = new Booking(tableId, startDateTime, endDateTime,
                    customerName, BookingStatus.CONFIRMED);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
            log.error(exception.getMessage());
        }
        return booking;
    }

    private static void addBooking(BookingService bookingService) {
        Booking booking = buildBooking();
        try {
            bookingService.createBooking(booking);
        } catch (BookingException exception) {
            System.out.println(exception.getMessage());
            log.error(exception.getMessage());
        }
    }

    private static void cancelBooking(BookingService bookingService) {
        if (!bookingService.getAllBookings().isEmpty()) {
            System.out.print("Enter the table number (1 - 10): ");
            int tableId = scanner.nextInt();
            scanner.nextLine();

//            System.out.print("Enter the client's name (first and last name): ");
//            String customerName = scanner.nextLine().trim();

            bookingService.cancelBooking(tableId);
        } else {
            System.out.println("No bookings in the list.");
            log.info("No bookings in the list.");
        }
    }

    private static void printBookingList(BookingService bookingService) {
        if (bookingService.getAllBookings().isEmpty()) {
            System.out.println("No bookings in the list.");
            log.info("No bookings in the list.");
        } else {
            System.out.println("Booking list:");
            int count = 1;
            for (Booking booking : bookingService.getAllBookings()) {
                System.out.println("--------------------------------");
                System.out.println(count + ". " + " : " + booking);
            }
        }
    }

    /**
     * Saves bookings to a file.
     */
    private static void saveBookings() {
        FileStorage.saveToFile(bookingService.getAllBookings());
        System.out.println("Bookings successfully saved to file.");
    }

    /**
     * Loads bookings from a file.
     */
    private static void loadBookings() {
        List<Booking> loadedBookings = FileStorage.loadFromFile();

        if (loadedBookings.isEmpty()) {
            System.out.println("No bookings found in the file. You can create new bookings.");
            return;
        }

        int skippedCount = 0; // Missed booking counter

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
        //Shows the user how many reservations could not be loaded.
        System.out.println("Bookings successfully loaded from file.");
        if (skippedCount > 0) {
            System.out.println("Skipped " + skippedCount + " conflicting bookings.");
        }
    }
}