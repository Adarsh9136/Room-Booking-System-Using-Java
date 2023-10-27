import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class RoomBookingApp extends JFrame {
    private Map<Integer, List<Booking>> roomBookings = new HashMap<>();
    private JLabel statusLabel;
    private JTextArea bookedRoomsTextArea;
    private JLabel totalCollectionLabel;
    private int nextRoomNumber = 101;
    private double totalCollection = 0.0;

    private int bookingId = 1; // Incremental booking ID

    public RoomBookingApp() {
        setTitle("Room Booking System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());

        // Hotel Name
        JLabel hotelNameLabel = new JLabel("Hotel The Taj");
        hotelNameLabel.setFont(new Font("Italic",Font.ITALIC | Font.BOLD, 20));
        hotelNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Room Booking System");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(hotelNameLabel, BorderLayout.PAGE_START);
        topPanel.add(subtitleLabel, BorderLayout.PAGE_END);

        // Main Panel
        JPanel mainPanel = new JPanel(new FlowLayout());

        // User Name
        JLabel nameLabel = new JLabel("User Name:");
        JTextField nameField = new JTextField(10);

        // Start Date
        JLabel startDateLabel = new JLabel("Start Date:");
        JFormattedTextField startDateField = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        startDateField.setColumns(10);

        // End Date
        JLabel endDateLabel = new JLabel("End Date:");
        JFormattedTextField endDateField = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        endDateField.setColumns(10);

        // Book Button
        JButton bookButton = new JButton("Book Room");

        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = nameField.getText();
                Date startDate = (Date) startDateField.getValue();
                Date endDate = (Date) endDateField.getValue();
                Date currentDate = new Date();

                if (startDate == null || endDate == null) {
                    statusLabel.setText("Invalid date range.");
                    return;
                }

                if (endDate.before(startDate) || startDate.before(currentDate)) {
                    statusLabel.setText("Invalid date range or start date should be today or later.");
                    return;
                }

                int roomNumber = allocateRoom(startDate, endDate);
                if (roomNumber > 0) {
                    List<Booking> bookings = roomBookings.computeIfAbsent(roomNumber, k -> new ArrayList<>());
                    double bookingCost = calculateTotalCollection(startDate, endDate);
                    totalCollection += bookingCost;

                    Booking booking = new Booking(bookingId, userName, startDate, endDate, bookingCost);
                    bookings.add(booking);
                    updateTotalCollectionLabel();
                    updateBookedRoomsTextArea(roomNumber, booking);
                    statusLabel.setText("Room " + roomNumber + " booked for " + userName);
                    bookingId++; // Increment the booking ID
                } else {
                    statusLabel.setText("No available room for the selected date range.");
                }
            }
        });

        // Status Label
        statusLabel = new JLabel();

        // Total Collection Label
        totalCollectionLabel = new JLabel("Total Collection: $" + String.format("%.2f", totalCollection));

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(startDateLabel);
        mainPanel.add(startDateField);
        mainPanel.add(endDateLabel);
        mainPanel.add(endDateField);
        mainPanel.add(bookButton);
        mainPanel.add(statusLabel);

        // Room List Panel
        JPanel roomListPanel = new JPanel(new BorderLayout());

        bookedRoomsTextArea = new JTextArea(10, 60);
        bookedRoomsTextArea.setEditable(false);
        roomListPanel.add(new JScrollPane(bookedRoomsTextArea), BorderLayout.CENTER);
        roomListPanel.add(totalCollectionLabel, BorderLayout.PAGE_START);

        add(topPanel, BorderLayout.PAGE_START);
        add(mainPanel, BorderLayout.CENTER);
        add(roomListPanel, BorderLayout.PAGE_END);

        setVisible(true);
    }

    private int allocateRoom(Date startDate, Date endDate) {
        for (int roomNumber = 101; roomNumber <= 110; roomNumber++) {
            boolean isAvailable = true;
            List<Booking> bookings = roomBookings.get(roomNumber);

            if (bookings != null) {
                for (Booking booking : bookings) {
                    if (endDate.before(booking.getStartDate()) || startDate.after(booking.getEndDate())) {
                        continue;
                    } else {
                        isAvailable = false;
                        break;
                    }
                }
            }

            if (isAvailable) {
                return roomNumber;
            }
        }
        return -1;
    }

    private double calculateTotalCollection(Date startDate, Date endDate) {
        long millisecondsInDay = 24 * 60 * 60 * 1000;
        long days = (endDate.getTime() - startDate.getTime()) / millisecondsInDay;
        return days * 100.0;
    }

    private void updateTotalCollectionLabel() {
        totalCollectionLabel.setText("Total Collection: $" + String.format("%.2f", totalCollection));
    }

    private void updateBookedRoomsTextArea(int roomNumber, Booking booking) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        bookedRoomsTextArea.append("Booking ID: " + booking.getBookingId() + "\n");
        bookedRoomsTextArea.append("Room " + roomNumber + " - " + booking.getUserName() + " (" +
                sdf.format(booking.getStartDate()) + " to " + sdf.format(booking.getEndDate()) + ")\n");
        bookedRoomsTextArea.append("Booking Cost: $" + String.format("%.2f", booking.getBookingCost()) + "\n");
        bookedRoomsTextArea.append("\n");
        bookedRoomsTextArea.append("-------------------------------------------------------------------------------------------");
        bookedRoomsTextArea.append("\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoomBookingApp());
    }
}

class Booking {
    private int bookingId;
    private String userName;
    private Date startDate;
    private Date endDate;
    private double bookingCost;

    public Booking(int bookingId, String userName, Date startDate, Date endDate, double bookingCost) {
        this.bookingId = bookingId;
        this.userName = userName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookingCost = bookingCost;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getUserName() {
        return userName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public double getBookingCost() {
        return bookingCost;
    }
}
