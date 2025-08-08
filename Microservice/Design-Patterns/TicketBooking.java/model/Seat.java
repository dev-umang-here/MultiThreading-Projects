import java.math.BigDecimal;
public class Seat {
    private final int seatId;
    private final String seatNumber;
    private final BigDecimal price;
    private  boolean isBooked = false;
   private final int eventId; // Event ownership
        
     public Seat(int seatId,int eventId, String seatNumber, BigDecimal price) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.eventId = eventId;
        this.price = price;
    }

    public int getSeatId() { return seatId; }
    public String getSeatNumber() { return seatNumber; }
    public BigDecimal getPrice() { return price; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { this.isBooked = booked; }
    public int getEventId() { return eventId; }
    
}
