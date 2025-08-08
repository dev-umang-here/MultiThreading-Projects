import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicketBookingDemo {
    
    public static void main(String[] args) {
        BookingManager manager = new BookingManager();

        // Handle user
        User user1 = new User(1,"Alice","alice@example.com");
        User user2 = new User(2,"Bob","bob@example.com");
        User user3 = new User(3,"Charlie","charlie@example.com");
        manager.addUser(user1);
        manager.addUser(user2);
        manager.addUser(user3);

        // Handle event
        Event event = new Event(100,"Concert","2025-01-01","New York");
        Event event2 = new Event(101,"Movie","2025-01-02","Los Angeles");
        manager.addEvent(event);
        manager.addEvent(event2);

        // Handle seat
        Seat seat = new Seat(1001,100,"A1",new BigDecimal(100));
        manager.addSeatToEvent(100,seat);

        // Book a seat
        Booking booking = manager.createBooking(1, 100, 1001);
        if (booking != null) {
            System.out.println("Booking successful: " + booking.getBookingId() + ", Seat: " + booking.getSeatNumber());
            // Process payment
            Payment payment = manager.processPayment(booking.getBookingId(), seat.getPrice());
            System.out.println("Payment processed: " + payment.getPaymentId() + ", Amount: " + payment.getAmount());
        } else {
            System.out.println("Booking failed.");
        }
    }
}
