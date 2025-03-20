public class Customer {
    int customerId;
    String customerName;

    public Customer(int customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return "Customer ID: " + customerId + ", Name: " + customerName;
    }
}
