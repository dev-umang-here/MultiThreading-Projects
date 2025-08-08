import java.util.ArrayList;
import java.util.List;
public class Event {
    private final int eventId;
    private final String name;
    private final String date;
    private final String location;

    private final List<Seat> seats = new ArrayList<>();

    public Event(int eventId, String name, String date, String location){
        this.eventId = eventId;
        this.name = name;
        this.date = date;
        this.location = location;
    }

    // getters,setters and business logic
    public int getEventId() { return eventId; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public List<Seat> getSeats() { return seats; }

    public void addSeat(Seat seat){
        seats.add(seat);
    }

    public void removeSeat(Seat seat){
        seats.remove(seat);
    }
    
}
