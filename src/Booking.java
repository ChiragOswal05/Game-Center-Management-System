
public class Booking {
    int bookingId;
    int customerId;
    int gameId;
    int hoursBooked;
    double totalCost;

    public Booking(int bookingId, int customerId, int gameId, int hoursBooked, double totalCost) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.gameId = gameId;
        this.hoursBooked = hoursBooked;
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId + ", Customer ID: " + customerId + ", Game ID: " + gameId
                + ", Hours: " + hoursBooked + ", Total Cost: " + totalCost;
    }
}
