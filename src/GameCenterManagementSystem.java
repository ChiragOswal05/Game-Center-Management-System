import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameCenterManagementSystem extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/GameCenterDB";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Chirag05@Chirag05@";

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
        btnViewBookings.addActionListener(e -> viewBookings());

        add(btnAddGame);
        add(btnViewGames);
        add(btnAddCustomer);
        add(btnViewCustomers);
        add(btnBookGame);
        add(btnViewBookings);

        setVisible(true);
    }


    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // ✅ Updated: Add Game with Duplicate Check
    private void addGame() {
        String gameName = JOptionPane.showInputDialog(this, "Enter Game Name:");
        if (gameName == null || gameName.trim().isEmpty()) return;

        String priceStr = JOptionPane.showInputDialog(this, "Enter Price per Hour:");
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) throw new NumberFormatException();

            // Check if the game already exists
            try (Connection conn = getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM games WHERE game_name = ?")) {
                checkStmt.setString(1, gameName);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Error: Game already exists!");
                    return;
                }
            }

            // Insert new game
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

    // ✅ Updated: Add Customer with Duplicate Check
    private void addCustomer() {
        String customerName = JOptionPane.showInputDialog(this, "Enter Customer Name:");
        if (customerName == null || customerName.trim().isEmpty()) return;

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM customers WHERE customer_name = ?")) {
            checkStmt.setString(1, customerName);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Error: Customer already exists!");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers (customer_name) VALUES (?)")) {
            stmt.setString(1, customerName);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void viewGames() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(20);
        JTextArea textArea = new JTextArea(15, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(searchField, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadGames("", textArea);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateResults(); }
            public void removeUpdate(DocumentEvent e) { updateResults(); }
            public void changedUpdate(DocumentEvent e) { updateResults(); }

            private void updateResults() {
                loadGames(searchField.getText(), textArea);
            }
        });

        JOptionPane.showMessageDialog(this, panel, "View Games", JOptionPane.PLAIN_MESSAGE);
    }

    private void loadGames(String keyword, JTextArea textArea) {
        textArea.setText("");
        String query = "SELECT * FROM games WHERE game_name LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                textArea.append("Game ID: " + rs.getInt("game_id") + ", Name: " + rs.getString("game_name") + ", Price: $" + rs.getDouble("price_per_hour") + "\n");
            }
        } catch (SQLException e) {
            textArea.setText("Error: " + e.getMessage());
        }
    }

    private void viewCustomers() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(20);
        JTextArea textArea = new JTextArea(15, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(searchField, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadCustomers("", textArea);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateResults(); }
            public void removeUpdate(DocumentEvent e) { updateResults(); }
            public void changedUpdate(DocumentEvent e) { updateResults(); }

            private void updateResults() {
                loadCustomers(searchField.getText(), textArea);
            }
        });

        JOptionPane.showMessageDialog(this, panel, "View Customers", JOptionPane.PLAIN_MESSAGE);
    }

    private void loadCustomers(String keyword, JTextArea textArea) {
        textArea.setText("");
        String query = "SELECT * FROM customers WHERE customer_name LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                textArea.append("Customer ID: " + rs.getInt("customer_id") + ", Name: " + rs.getString("customer_name") + "\n");
            }
        } catch (SQLException e) {
            textArea.setText("Error: " + e.getMessage());
        }
    }

    // ✅ Book a Game
    private void bookGame() {
        try (Connection conn = getConnection()) {
            // Get customer ID
            String customerIdStr = JOptionPane.showInputDialog(this, "Enter Customer ID:");
            int customerId = Integer.parseInt(customerIdStr);

            // Get game ID
            String gameIdStr = JOptionPane.showInputDialog(this, "Enter Game ID:");
            int gameId = Integer.parseInt(gameIdStr);

            // Get duration in hours
            String durationStr = JOptionPane.showInputDialog(this, "Enter Duration (in hours):");
            int duration = Integer.parseInt(durationStr);

            // Fetch game price per hour
            String priceQuery = "SELECT price_per_hour FROM games WHERE game_id = ?";
            double pricePerHour = 0;

            try (PreparedStatement priceStmt = conn.prepareStatement(priceQuery)) {
                priceStmt.setInt(1, gameId);
                ResultSet rs = priceStmt.executeQuery();

                if (rs.next()) {
                    pricePerHour = rs.getDouble("price_per_hour");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Game ID!");
                    return;
                }
            }

            // Calculate total price
            double totalPrice = duration * pricePerHour;

            // Insert booking into the database
            String insertQuery = "INSERT INTO bookings (customer_id, game_id, duration, total_price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, customerId);
                insertStmt.setInt(2, gameId);
                insertStmt.setInt(3, duration);
                insertStmt.setDouble(4, totalPrice);
                insertStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Game booked successfully!\nTotal Price: $" + totalPrice);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter numbers only.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }


    // ✅ View Bookings
    private void viewBookings() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea(15, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        String query = """
            SELECT b.booking_id, c.customer_name, g.game_name, b.start_time, b.duration, b.total_price
            FROM bookings b
            JOIN customers c ON b.customer_id = c.customer_id
            JOIN games g ON b.game_id = g.game_id
            ORDER BY b.start_time DESC""";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            textArea.setText("Bookings:\n");
            while (rs.next()) {
                textArea.append("Booking ID: " + rs.getInt("booking_id") + "\n");
                textArea.append("Customer: " + rs.getString("customer_name") + "\n");
                textArea.append("Game: " + rs.getString("game_name") + "\n");
                textArea.append("Start Time: " + rs.getString("start_time") + "\n");
                textArea.append("Duration: " + rs.getInt("duration") + " hours\n");
                textArea.append("Total Price: $" + rs.getDouble("total_price") + "\n");
                textArea.append("---------------------------\n");
            }
        } catch (SQLException e) {
            textArea.setText("Error: " + e.getMessage());
        }

        JOptionPane.showMessageDialog(this, panel, "View Bookings", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        new GameCenterManagementSystem();
    }
}
