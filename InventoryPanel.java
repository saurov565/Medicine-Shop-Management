import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class InventoryPanel extends JPanel {
    private InventoryService inventoryService;
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    // Modern colors
    private final Color PRIMARY_COLOR;
    private final Color SECONDARY_COLOR;
    private final Color ACCENT_COLOR;
    private final Color WARNING_COLOR;
    private final Color BACKGROUND_COLOR;
    private final Color CARD_COLOR;
    private final Color TEXT_COLOR;
    
    public InventoryPanel(InventoryService inventoryService, Color primary, Color secondary, 
                         Color accent, Color warning, Color background, Color card, Color text) {
        this.inventoryService = inventoryService;
        this.PRIMARY_COLOR = primary;
        this.SECONDARY_COLOR = secondary;
        this.ACCENT_COLOR = accent;
        this.WARNING_COLOR = warning;
        this.BACKGROUND_COLOR = background;
        this.CARD_COLOR = card;
        this.TEXT_COLOR = text;
        
        initializeUI();
        loadMedicineData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel with buttons and search
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Table for displaying medicines
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JButton addButton = createStyledButton("Add Medicine", ACCENT_COLOR);
        JButton editButton = createStyledButton("Edit Medicine", SECONDARY_COLOR);
        JButton deleteButton = createStyledButton("Delete Medicine", WARNING_COLOR);
        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR);
        
        // Search components
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLabel.setForeground(TEXT_COLOR);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddMedicineDialog());
        editButton.addActionListener(e -> showEditMedicineDialog());
        deleteButton.addActionListener(e -> deleteSelectedMedicine());
        refreshButton.addActionListener(e -> loadMedicineData());
        searchButton.addActionListener(e -> searchMedicines());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchButton);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(backgroundColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(backgroundColor.brighter());
                } else {
                    g2.setColor(backgroundColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        
        return button;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Medicine Inventory",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Table model
        String[] columns = {"ID", "Name", "Manufacturer", "Price", "Quantity", "Expiry Date", "Category"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        medicineTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Highlight low stock in red
                int quantity = (int) getValueAt(row, 4);
                if (quantity <= 10) {
                    c.setBackground(new Color(255, 230, 230)); // Light red
                } else {
                    c.setBackground(row % 2 == 0 ? 
                        new Color(250, 250, 250) : 
                        new Color(240, 245, 255)); // Alternating colors
                }
                
                // Highlight expired medicines
                LocalDate expiryDate = (LocalDate) getValueAt(row, 5);
                if (expiryDate.isBefore(LocalDate.now())) {
                    c.setBackground(new Color(255, 200, 200)); // Darker red for expired
                }
                
                return c;
            }
        };
        
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineTable.getTableHeader().setReorderingAllowed(false);
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicineTable.setRowHeight(25);
        medicineTable.setShowGrid(true);
        medicineTable.setGridColor(new Color(240, 240, 240));
        
        // Style table header
        JTableHeader header = medicineTable.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(medicineTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadMedicineData() {
        tableModel.setRowCount(0);
        List<Medicine> medicines = inventoryService.getAllMedicines();
        for (Medicine medicine : medicines) {
            tableModel.addRow(medicine.toTableRow());
        }
    }
    
    private void searchMedicines() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadMedicineData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Medicine> medicines = inventoryService.findMedicineByName(searchText);
        for (Medicine medicine : medicines) {
            tableModel.addRow(medicine.toTableRow());
        }
    }
    
    private void showAddMedicineDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Medicine", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel headerLabel = new JLabel("Add New Medicine");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        dialog.add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 15, 15));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form fields with modern styling
        JTextField nameField = createStyledTextField();
        JTextField manufacturerField = createStyledTextField();
        JTextField priceField = createStyledTextField();
        JTextField quantityField = createStyledTextField();
        JTextField expiryField = createStyledTextField();
        JTextField categoryField = createStyledTextField();
        
        // Set placeholder text for expiry date
        expiryField.setText("YYYY-MM-DD");
        
        addFormField(formPanel, "Name:", nameField);
        addFormField(formPanel, "Manufacturer:", manufacturerField);
        addFormField(formPanel, "Price:", priceField);
        addFormField(formPanel, "Quantity:", quantityField);
        addFormField(formPanel, "Expiry Date:", expiryField);
        addFormField(formPanel, "Category:", categoryField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_COLOR);
        JButton saveButton = createStyledButton("Save", ACCENT_COLOR);
        JButton cancelButton = createStyledButton("Cancel", new Color(120, 120, 120));
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        formPanel.add(new JLabel()); // Empty cell
        formPanel.add(buttonPanel);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String manufacturer = manufacturerField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                LocalDate expiryDate = LocalDate.parse(expiryField.getText());
                String category = categoryField.getText();
                
                if (name.isEmpty() || manufacturer.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields");
                    return;
                }
                
                Medicine medicine = new Medicine(0, name, manufacturer, price, quantity, expiryDate, category);
                inventoryService.addMedicine(medicine);
                loadMedicineData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Medicine added successfully!");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage());
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
    
    private void addFormField(JPanel panel, String label, JTextField field) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel.setForeground(TEXT_COLOR);
        panel.add(jLabel);
        panel.add(field);
    }
    
    private void showEditMedicineDialog() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to edit");
            return;
        }
        
        int medicineId = (int) tableModel.getValueAt(selectedRow, 0);
        Medicine medicine = inventoryService.findMedicineById(medicineId);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Medicine", true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        // Form fields with current values
        JTextField nameField = new JTextField(medicine.getName());
        JTextField manufacturerField = new JTextField(medicine.getManufacturer());
        JTextField priceField = new JTextField(String.valueOf(medicine.getPrice()));
        JTextField quantityField = new JTextField(String.valueOf(medicine.getQuantity()));
        JTextField expiryField = new JTextField(medicine.getExpiryDate().toString());
        JTextField categoryField = new JTextField(medicine.getCategory());
        
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Manufacturer:"));
        dialog.add(manufacturerField);
        dialog.add(new JLabel("Price:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Quantity:"));
        dialog.add(quantityField);
        dialog.add(new JLabel("Expiry Date:"));
        dialog.add(expiryField);
        dialog.add(new JLabel("Category:"));
        dialog.add(categoryField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(new JLabel());
        dialog.add(buttonPanel);
        
        saveButton.addActionListener(e -> {
            try {
                medicine.setName(nameField.getText());
                medicine.setManufacturer(manufacturerField.getText());
                medicine.setPrice(Double.parseDouble(priceField.getText()));
                medicine.setQuantity(Integer.parseInt(quantityField.getText()));
                medicine.setExpiryDate(LocalDate.parse(expiryField.getText()));
                medicine.setCategory(categoryField.getText());
                
                inventoryService.updateMedicine(medicine);
                loadMedicineData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Medicine updated successfully!");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage());
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    
    private void deleteSelectedMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to delete");
            return;
        }
        
        int medicineId = (int) tableModel.getValueAt(selectedRow, 0);
        String medicineName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete " + medicineName + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            inventoryService.deleteMedicine(medicineId);
            loadMedicineData();
            JOptionPane.showMessageDialog(this, "Medicine deleted successfully!");
        }
    }
}