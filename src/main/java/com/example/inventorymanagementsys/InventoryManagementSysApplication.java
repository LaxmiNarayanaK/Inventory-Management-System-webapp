package com.example.inventorymanagementsys;

import com.example.inventorymanagementsys.model.Item;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class InventoryManagementSysApplication extends JFrame {
    private JTextField itemNameField;
    private JTextField quantityField;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;
    private JList<String> inventoryList;
    private DefaultListModel<String> inventoryModel;
    private int itemCount;
    private JLabel itemCountValue;

    private List<Item> inventory;

    private JButton importButton;
    private JButton exportButton;
    private JButton sortByNameButton;
    private JButton sortByQuantityButton;

    public InventoryManagementSysApplication() {
        setTitle("Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240)); // Set background color

        inventory = new ArrayList<>(); // Initialize inventory list

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(new Color(255, 255, 255)); // Set panel background color

        JLabel itemNameLabel = new JLabel("Item Name:");
        itemNameField = new JTextField(15);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityField = new JTextField(5);

        JLabel dateLabel = new JLabel("Date:");

        // Day ComboBox
        dayComboBox = new JComboBox<>();
        for (int day = 1; day <= 31; day++) {
            dayComboBox.addItem(String.valueOf(day));
        }

        // Month ComboBox
        monthComboBox = new JComboBox<>();
        String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        for (String monthName : monthNames) {
            monthComboBox.addItem(monthName);
        }

        // Year ComboBox
        yearComboBox = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 10; year <= currentYear + 10; year++) {
            yearComboBox.addItem(String.valueOf(year));
        }

        JButton addButton = new JButton("Add Item");
        addButton.setBackground(new Color(191, 191, 255)); // Set button background color as light purple
        addButton.setForeground(Color.BLACK); // Set button text color as black
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemName = itemNameField.getText();
                String quantityText = quantityField.getText();

                // Check if quantity is a valid number
                if (!quantityText.matches("\\d+")) {
                    JOptionPane.showMessageDialog(InventoryManagementSysApplication.this,
                            "Invalid quantity. Please enter a valid number.",
                            "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity = Integer.parseInt(quantityText);
                int day = Integer.parseInt(dayComboBox.getSelectedItem().toString());
                int month = monthComboBox.getSelectedIndex() + 1;
                int year = Integer.parseInt(yearComboBox.getSelectedItem().toString());

                if (isValidDate(day, month, year)) {
                    Date date = createDate(day, month, year);
                    addItem(itemName, quantity, date);
                    saveItemToDatabase(itemName, quantity, date);
                } else {
                    JOptionPane.showMessageDialog(InventoryManagementSysApplication.this,
                            "Invalid date. Please enter a valid date.",
                            "Invalid Date", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        inputPanel.add(itemNameLabel);
        inputPanel.add(itemNameField);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);
        inputPanel.add(dateLabel);
        inputPanel.add(dayComboBox);
        inputPanel.add(monthComboBox);
        inputPanel.add(yearComboBox);
        inputPanel.add(addButton);

        // Inventory display area
        inventoryModel = new DefaultListModel<>();
        inventoryList = new JList<>(inventoryModel);
        inventoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryList.setBackground(new Color(255, 255, 255)); // Set list background color
        inventoryList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // Set monospaced font for consistent spacing

        JScrollPane scrollPane = new JScrollPane(inventoryList);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Extra Feature: Show total item count
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel itemCountLabel = new JLabel("Total Items: ");
        itemCountValue = new JLabel("0");
        statusPanel.add(itemCountLabel);
        statusPanel.add(itemCountValue);
        add(statusPanel, BorderLayout.SOUTH);

        // Extra Feature: Delete and Edit Functionality
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = inventoryList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Item item = inventory.get(selectedIndex);
                    deleteItem(item);
                    deleteItemFromDatabase(item);
                    inventoryModel.removeElementAt(selectedIndex);
                }
            }
        });
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = inventoryList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Item item = inventory.get(selectedIndex);
                    editItem(item);
                }
            }
        });

        // Sort by Name Button
        sortByNameButton = new JButton("Sort by Name");
        sortByNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortInventoryByName();
                updateInventoryDisplay();
            }
        });

        // Sort by Quantity Button
        sortByQuantityButton = new JButton("Sort by Quantity");
        sortByQuantityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortInventoryByQuantity();
                updateInventoryDisplay();
            }
        });


        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(sortByNameButton);
        buttonPanel.add(sortByQuantityButton);
        add(buttonPanel, BorderLayout.WEST);

        //connectToDatabase();
        //loadItemsFromDatabase();
        //updateInventoryDisplay();

        // Import and Export Buttons
        importButton = new JButton("Import");
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importInventory();
            }
        });

        exportButton = new JButton("Export");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportInventory();
            }
        });

        JPanel importExportPanel = new JPanel(new FlowLayout());
        importExportPanel.add(importButton);
        importExportPanel.add(exportButton);
        add(importExportPanel, BorderLayout.EAST);

        setVisible(true);
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/inventory";
        String username = "root";
        String password = "1234";
    }

    private void loadItemsFromDatabase() {

    }
    private void saveItemToDatabase(String itemName, int quantity, Date date) {

    }
    private void deleteItemFromDatabase(Item item) {

    }
    private boolean isValidDate(int day, int month, int year) {
        // Validate the date logic based on your requirements
        // For example, you can check if the date is within a valid range
        return true;
    }
    private Date createDate(int day, int month, int year) {
        return Date.from(Instant.now());
    }

    private void addItem(String itemName, int quantity, Date date) {
        // Add the item to the inventory list
        Item item = new Item(itemName, quantity, date);
        inventory.add(item);
    }

    private void deleteItem(Item item) {
        // Delete the item from the inventory list
        inventory.remove(item);
    }

    private void editItem(Item item) {
        // Create a dialog box to edit item details
        JPanel editPanel = new JPanel(new GridLayout(4, 2));

        itemNameField = new JTextField(item.getName(), 15);
        quantityField = new JTextField(String.valueOf(item.getQuantity()), 5);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateText = dateFormat.format(item.getDate());
        String[] dateParts = dateText.split("-");
        int day = Integer.parseInt(dateParts[2]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[0]);

        dayComboBox = new JComboBox<>();
        for (int d = 1; d <= 31; d++) {
            dayComboBox.addItem(String.valueOf(d));
        }
        dayComboBox.setSelectedItem(String.valueOf(day));

        monthComboBox = new JComboBox<>();
        String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        for (int m = 1; m <= 12; m++) {
            monthComboBox.addItem(monthNames[m - 1]);
        }
        monthComboBox.setSelectedItem(monthNames[month - 1]);

        yearComboBox = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 10; y <= currentYear + 10; y++) {
            yearComboBox.addItem(String.valueOf(y));
        }
        yearComboBox.setSelectedItem(String.valueOf(year));

        editPanel.add(new JLabel("Item Name:"));
        editPanel.add(itemNameField);
        editPanel.add(new JLabel("Quantity:"));
        editPanel.add(quantityField);
        editPanel.add(new JLabel("Day:"));
        editPanel.add(dayComboBox);
        editPanel.add(new JLabel("Month:"));
        editPanel.add(monthComboBox);
        editPanel.add(new JLabel("Year:"));
        editPanel.add(yearComboBox);

        int result = JOptionPane.showConfirmDialog(
                this, editPanel, "Edit Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String itemName = itemNameField.getText();
            String quantityText = quantityField.getText();

            if (!quantityText.matches("\\d+")) {
                JOptionPane.showMessageDialog(this,
                        "Invalid quantity. Please enter a valid number.",
                        "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            int selectedDay = Integer.parseInt(dayComboBox.getSelectedItem().toString());
            int selectedMonth = monthComboBox.getSelectedIndex() + 1;
            int selectedYear = Integer.parseInt(yearComboBox.getSelectedItem().toString());

            if (isValidDate(selectedDay, selectedMonth, selectedYear)) {
                Date date = createDate(selectedDay, selectedMonth, selectedYear);
                item.setName(itemName);
                item.setQuantity(quantity);
                item.setDate(date);

                updateItemInDatabase(item);
                updateInventoryDisplay();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid date. Please enter a valid date.",
                        "Invalid Date", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateItemInDatabase(Item item) {

    }

    private void updateInventoryDisplay() {
        inventoryModel.clear();
        for (Item item : inventory) {
            String itemDetails = String.format("Item: %-20s Quantity: %-5d Date: %s",
                    item.getName(), item.getQuantity(), formatDate(item.getDate()));
            inventoryModel.addElement(itemDetails);
        }
    }

    private String formatDate(Date date) {
        // Format the date in a specific format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private void exportInventory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Inventory");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave))) {
                for (Item item : inventory) {
                    String itemName = item.getName();
                    int quantity = item.getQuantity();
                    Date date = item.getDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = dateFormat.format(date);
                    writer.println(itemName + "," + quantity + "," + formattedDate);
                }
                System.out.println("Inventory data exported successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error exporting inventory data.",
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importInventory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Inventory");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToImport = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToImport))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String itemName = parts[0].trim();
                        int quantity = Integer.parseInt(parts[1].trim());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
                        Date date = dateFormat.parse(parts[2].trim()); // Parse the date using the specified format
                        addItem(itemName, quantity, date);
                        saveItemToDatabase(itemName, quantity, date);
                    }
                }
                System.out.println("Inventory data imported successfully.");
                updateInventoryDisplay();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error importing inventory data: " + e.getMessage(),
                        "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sortInventoryByName() {
        inventory.sort(Comparator.comparing(Item::getName));
    }
    private void sortInventoryByQuantity() {
        inventory.sort(Comparator.comparingInt(Item::getQuantity));
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new InventoryManagementSysApplication();
            }
        });
    }
}
