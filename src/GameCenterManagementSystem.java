import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class GameCenterManagementSystem extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/GameCenterDB";
    private static final String DB_USER = "root";  // Change if needed
    private static final String DB_PASSWORD = "Chirag05@Chirag05@";  // Change if you have a password

    public GameCenterManagementSystem() {
        setTitle("Game Center Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        JButton btnAddGame = new JButton("Add Game");
        JButton btnViewGames = new JButton("View Games");
        JButton btnAddCustomer = new JButton("Add Customer");
        JButton btnViewCustomers = new JButton("View Customers");
        JButton btnBookGame = new JButton("Book Game");
        JButton btnViewBookings = new JButton("View Bookings");

        btnAddGame.addActionListener(e -> addGame());
        btnViewGames.addActionListener(e -> viewGames());
        btnAddCustomer.addActionListener(e -> addCustomer());
        btnViewCustomers.addActionListener(e -> viewCustomers());
        btnBookGame.addActionListener(e -> bookGame());
        btnViewBookings.addActionListener(e -> viewBookings()); // Added action listener

        add(btnAddGame);
        add(btnViewGames);
        add(btnAddCustomer);
        add(btnViewCustomers);
        add(btnBookGame);
        add(btnViewBookings);

        setVisible(true);
    }

    // Connect to the database
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Add a new game
    private void addGame() {
        String gameName = JOptionPane.showInputDialog(this, "Enter Game Name:");
        if (gameName == null || gameName.isEmpty()) return;

        String priceStr = JOptionPane.showInputDialog(this, "Enter Price per Hour:");
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) throw new NumberFormatException();

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO games (game_name, price_per_hour) VALUES (?, ?)")) {
                stmt.setString(1, gameName);
                stmt.setDouble(2, price);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Game added successfully!");
            }
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // View games
    private void viewGames() {
        StringBuilder result = new StringBuilder("Games Available:\n");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM games")) {

            while (rs.next()) {
                result.append("Game ID: ").append(rs.getInt("game_id"))
                        .append(", Name: ").append(rs.getString("game_name"))
                        .append(", Price: ").append(rs.getDouble("price_per_hour"))
                        .append("\n");
            }
        } catch (SQLException e) {
            result.append("Error: ").append(e.getMessage());
        }
        JOptionPane.showMessageDialog(this, result.toString());
    }

    // Add a new customer
    private void addCustomer() {
        String customerName = JOptionPane.showInputDialog(this, "Enter Customer Name:");
        if (customerName == null || customerName.isEmpty()) return;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers (customer_name) VALUES (?)")) {
            stmt.setString(1, customerName);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // View customers
    private void viewCustomers() {
        StringBuilder result = new StringBuilder("Registered Customers:\n");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {

            while (rs.next()) {
                result.append("Customer ID: ").append(rs.getInt("customer_id"))
                        .append(", Name: ").append(rs.getString("customer_name"))
                        .append("\n");
            }
        } catch (SQLException e) {
            result.append("Error: ").append(e.getMessage());
        }
        JOptionPane.showMessageDialog(this, result.toString());
    }

    // Book a game
    private void bookGame() {
        String customerIdStr = JOptionPane.showInputDialog(this, "Enter Customer ID:");
        String gameIdStr = JOptionPane.showInputDialog(this, "Enter Game ID:");
        String hoursStr = JOptionPane.showInputDialog(this, "Enter Number of Hours:");

        try {
            int customerId = Integer.parseInt(customerIdStr);
            int gameId = Integer.parseInt(gameIdStr);
            int hours = Integer.parseInt(hoursStr);

            // Validate Customer ID
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customers WHERE customer_id = ?")) {
                stmt.setInt(1, customerId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Error: Customer ID not found!");
                    return;
                }
            }

            // Validate Game ID and get price
            double pricePerHour;
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT price_per_hour FROM games WHERE game_id = ?")) {
                stmt.setInt(1, gameId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Error: Game ID not found!");
                    return;
                }
                pricePerHour = rs.getDouble("price_per_hour");
            }

            // Calculate total cost
            double totalCost = pricePerHour * hours;

            // Insert into bookings
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO bookings (customer_id, game_id, hours_booked, total_cost) VALUES (?, ?, ?, ?)")) {
                stmt.setInt(1, customerId);
                stmt.setInt(2, gameId);
                stmt.setInt(3, hours);
                stmt.setDouble(4, totalCost);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Booking successful! Total Cost: $" + totalCost);
            }
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // View bookings
    private void viewBookings() {
        StringBuilder result = new StringBuilder("Game Bookings:\n");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT b.booking_id, c.customer_name, g.game_name, b.hours_booked, b.total_cost " +
                             "FROM bookings b " +
                             "JOIN customers c ON b.customer_id = c.customer_id " +
                             "JOIN games g ON b.game_id = g.game_id")) {

            while (rs.next()) {
                result.append("Booking ID: ").append(rs.getInt("booking_id"))
                        .append(", Customer: ").append(rs.getString("customer_name"))
                        .append(", Game: ").append(rs.getString("game_name"))
                        .append(", Hours: ").append(rs.getInt("hours_booked"))
                        .append(", Total Cost: $").append(rs.getDouble("total_cost"))
                        .append("\n");
            }
        } catch (SQLException e) {
            result.append("Error: ").append(e.getMessage());
        }
        JOptionPane.showMessageDialog(this, result.toString());
    }
}
