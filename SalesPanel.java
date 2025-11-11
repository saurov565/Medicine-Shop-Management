import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SalesPanel extends JPanel {
    private InventoryService inventoryService;
    private SalesService salesService;
    private Sale currentSale;
    private List<SaleItem> cartItems;
    
    private JTextField customerField;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel totalLabel;
    private JComboBox<Medicine> medicineComboBox;
    
    // Modern colors
    private final Color PRIMARY_COLOR;
    private final Color SECONDARY_COLOR;
    private final Color ACCENT_COLOR;
    private final Color WARNING_COLOR;
    private final Color BACKGROUND_COLOR;
    private final Color CARD_COLOR;
    private final Color TEXT_COLOR;
    
    public SalesPanel(InventoryService inventoryService, SalesService salesService, 
                     Color primary, Color secondary, Color accent, Color warning, 
                     Color background, Color card, Color text) {
        this.inventoryService = inventoryService;
        this.salesService = salesService;
        this.PRIMARY_COLOR = primary;
        this.SECONDARY_COLOR = secondary;
        this.ACCENT_COLOR = accent;
        this.WARNING_COLOR = warning;
        this.BACKGROUND_COLOR = background;
        this.CARD_COLOR = card;
        this.TEXT_COLOR = text;
        this.cartItems = new ArrayList<>();
        
        initializeUI();
        startNewSale();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel - Customer info and medicine selection
        add(createTopPanel(), BorderLayout.NORTH);
        
        // Center panel - Cart items
        add(createCartPanel(), BorderLayout.CENTER);
        
        // Bottom panel - Total and buttons
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Customer information
        JLabel customerLabel = new JLabel("Customer Name:");
        customerLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        customerLabel.setForeground(TEXT_COLOR);
        panel.add(customerLabel);
        
        customerField = new JTextField();
        customerField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.add(customerField);
        
        // Medicine selection
        JLabel medicineLabel = new JLabel("Select Medicine:");
        medicineLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        medicineLabel.setForeground(TEXT_COLOR);
        panel.add(medicineLabel);
        
        medicineComboBox = new JComboBox<>();
        medicineComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicineComboBox.setBackground(Color.WHITE);
        updateMedicineComboBox();
        panel.add(medicineComboBox);
        
        return panel;
    }
    
    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Sale Items",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        String[] columns = {"Medicine", "Quantity", "Price", "Total"};
        cartModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        cartTable = new JTable(cartModel);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.setRowHeight(25);
        cartTable.getTableHeader().setBackground(SECONDARY_COLOR);
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add/Remove buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton addButton = createStyledButton("Add to Cart", ACCENT_COLOR);
        JButton removeButton = createStyledButton("Remove Selected", WARNING_COLOR);
        JButton clearButton = createStyledButton("Clear Cart", new Color(120, 120, 120));
        
        addButton.addActionListener(e -> addItemToCart());
        removeButton.addActionListener(e -> removeSelectedItem());
        clearButton.addActionListener(e -> clearCart());
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        // Total display
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(CARD_COLOR);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel totalTextLabel = new JLabel("Total Amount:");
        totalTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalTextLabel.setForeground(TEXT_COLOR);
        totalPanel.add(totalTextLabel);
        
        totalLabel = new JLabel("$0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(ACCENT_COLOR);
        totalPanel.add(totalLabel);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JButton newSaleButton = createStyledButton("New Sale", PRIMARY_COLOR);
        JButton processButton = createStyledButton("Process Sale", ACCENT_COLOR);
        
        newSaleButton.addActionListener(e -> startNewSale());
        processButton.addActionListener(e -> processSale());
        
        buttonPanel.add(newSaleButton);
        buttonPanel.add(processButton);
        
        panel.add(totalPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
    
    private void updateMedicineComboBox() {
        medicineComboBox.removeAllItems();
        List<Medicine> medicines = inventoryService.getAllMedicines();
        for (Medicine medicine : medicines) {
            if (medicine.getQuantity() > 0) {
                medicineComboBox.addItem(medicine);
            }
        }
    }
    
    private void addItemToCart() {
        Medicine selectedMedicine = (Medicine) medicineComboBox.getSelectedItem();
        if (selectedMedicine == null) {
            JOptionPane.showMessageDialog(this, "Please select a medicine");
            return;
        }
        
        String quantityStr = JOptionPane.showInputDialog(this, 
            "Enter quantity for " + selectedMedicine.getName() + ":\nAvailable: " + selectedMedicine.getQuantity(), 
            "1");
        
        if (quantityStr == null) return;
        
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive");
                return;
            }
            
            if (quantity > selectedMedicine.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Not enough stock available");
                return;
            }
            
            // Add to cart
            SaleItem item = new SaleItem(selectedMedicine, quantity);
            cartItems.add(item);
            updateCartDisplay();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity");
        }
    }
    
    private void updateCartDisplay() {
        cartModel.setRowCount(0);
        double total = 0;
        
        for (SaleItem item : cartItems) {
            Medicine medicine = item.getMedicine();
            cartModel.addRow(new Object[]{
                medicine.getName(),
                item.getQuantity(),
                String.format("$%.2f", medicine.getPrice()),
                String.format("$%.2f", item.getTotalPrice())
            });
            total += item.getTotalPrice();
        }
        
        totalLabel.setText(String.format("$%.2f", total));
    }
    
    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1) {
            cartItems.remove(selectedRow);
            updateCartDisplay();
        }
    }
    
    private void clearCart() {
        cartItems.clear();
        updateCartDisplay();
    }
    
    private void startNewSale() {
        customerField.setText("");
        cartItems.clear();
        updateCartDisplay();
        updateMedicineComboBox();
        currentSale = null;
    }
    
    private void processSale() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty");
            return;
        }
        
        String customerName = customerField.getText().trim();
        if (customerName.isEmpty()) {
            customerName = "Walk-in Customer";
        }
        
        // Create and process sale
        currentSale = salesService.createSale(customerName);
        
        for (SaleItem item : cartItems) {
            salesService.addItemToSale(currentSale, item.getMedicine().getId(), item.getQuantity());
        }
        
        salesService.completeSale(currentSale);
        
        // Show receipt
        showReceipt();
        startNewSale();
    }
    
    private void showReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== MEDICINE SHOP RECEIPT ===\n");
        receipt.append("Sale ID: ").append(currentSale.getSaleId()).append("\n");
        receipt.append("Customer: ").append(currentSale.getCustomerName()).append("\n");
        receipt.append("Date: ").append(currentSale.getSaleDate()).append("\n\n");
        receipt.append("Items:\n");
        
        for (SaleItem item : currentSale.getItems()) {
            receipt.append(String.format("  %s x %d = $%.2f\n", 
                item.getMedicine().getName(), 
                item.getQuantity(), 
                item.getTotalPrice()));
        }
        
        receipt.append("\nTotal: $").append(String.format("%.2f", currentSale.getTotalAmount()));
        receipt.append("\n\nThank you for your purchase!");
        
        JTextArea textArea = new JTextArea(receipt.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Sale Completed", JOptionPane.INFORMATION_MESSAGE);
    }
} 
    

