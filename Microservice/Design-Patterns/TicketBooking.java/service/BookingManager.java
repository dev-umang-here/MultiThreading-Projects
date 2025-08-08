import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.math.BigDecimal;


public class BookingManager {

    private final Map<Integer,User> users = new HashMap<>();
    private final Map<Integer,Event> events = new HashMap<>();
    private final Map<Integer,Booking> bookings = new HashMap<>();
    private final Map<Integer,Payment> payments = new HashMap<>();

    private int bookingIdGenerator = 100;
    private int paymentIdGenerator = 1000;


    public void addUser(User user){
        users.put(user.getUserId(), user);
    }

    public void addEvent(Event event){
        events.put(event.getEventId(), event);
    }
    public void addSeatToEvent(int eventId, Seat seat){
        Event event = events.get(eventId);
        if(event != null){
            event.addSeat(seat);
        }
    }

    public Booking createBooking(int userId, int eventId, int seatId) {
        User user = users.get(userId);
        Event event = events.get(eventId);

        if (user == null || event == null) {
            throw new IllegalArgumentException("Invalid user/event");
        }

        for (Seat currentSeat : event.getSeats()) {
            if (currentSeat.getSeatId() == seatId && !currentSeat.isBooked()) {
                currentSeat.setBooked(true);
                Booking booking = new Booking(++bookingIdGenerator, userId, eventId, seatId,
                        currentSeat.getSeatNumber(), new Date().toString(), "booked");
                bookings.put(booking.getBookingId(), booking);
                return booking;
            }
        }
        return null;
    }

    public Payment processPayment(int bookingId, BigDecimal amount){
        Booking booking = bookings.get(bookingId);
        if(booking == null){
            throw new IllegalArgumentException("Invalid booking");
        }
        Payment payment = new Payment(++paymentIdGenerator, bookingId, amount, new Date().toString(), "paid");
        payments.put(payment.getPaymentId(), payment);
        return payment;
    }
    public List<Event> getAllEvents() { return new ArrayList<>(events.values()); }

    public List<Seat> getSeatsForEvent(int eventId) {
        Event event = events.get(eventId);
        return event != null ? event.getSeats() : Collections.emptyList();
    }

    public List<Booking> getBookingsForUser(int userId) {
        List<Booking> res = new ArrayList<>();
        for (Booking b : bookings.values()) 
            if (b.getUserId() == userId) res.add(b);
        return res;
    }
  

} 

