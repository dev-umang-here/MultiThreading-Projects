// No package; using default to simplify running

/*
 * For now we are booking one sheet for one user will change this later
 */
public class Booking{
    private final int bookingId;
    private final int userId;
    private final int eventId;
    private final int seatId;
    private final String seatNumber;
    private final String bookingDate;
    private String status;


    public Booking(int bookingId, int userId, int eventId, int seatId, String seatNumber, String bookingDate, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getSeatId() {
        return seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}