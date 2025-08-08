import java.math.BigDecimal;
public class Payment {

    private final int paymentId;
    private final int bookingId;
    private final BigDecimal amount;
    private final String paymentDate;
    private String paymentStatus;

    public Payment(int paymentId, int bookingId, BigDecimal amount, String paymentDate, String paymentStatus){
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
    }

    public int getPaymentId() { return paymentId; }
    public int getBookingId() { return bookingId; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentStatus() { return paymentStatus; }

    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    
}
