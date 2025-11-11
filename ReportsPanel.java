
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel {
    private InventoryService inventoryService;
    private JTextArea reportArea;
    
    // Modern colors
    private final Color PRIMARY_COLOR;
    private final Color SECONDARY_COLOR;
    private final Color WARNING_COLOR;
    private final Color BACKGROUND_COLOR;
    private final Color CARD_COLOR;
    private final Color TEXT_COLOR;
    
    public ReportsPanel(ReportService reportService, InventoryService inventoryService,
                       Color primary, Color secondary, Color accent, Color warning,
                       Color background, Color card, Color text) {
        this.inventoryService = inventoryService;
        this.PRIMARY_COLOR = primary;
        this.SECONDARY_COLOR = secondary;
        this.WARNING_COLOR = warning;
        this.BACKGROUND_COLOR = background;
        this.CARD_COLOR = card;
        this.TEXT_COLOR = text;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Button panel
        add(createButtonPanel(), BorderLayout.NORTH);
        
        // Report display area
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setBackground(CARD_COLOR);
        reportArea.setForeground(TEXT_COLOR);
        reportArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            "Reports Dashboard",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            TEXT_COLOR
        ));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JButton inventoryReportBtn = createStyledButton("Inventory Report", PRIMARY_COLOR);
        JButton salesReportBtn = createStyledButton("Sales Report", SECONDARY_COLOR);
        JButton stockAlertBtn = createStyledButton("Stock Alerts", WARNING_COLOR);
        JButton expiredBtn = createStyledButton("Expired Medicines", new Color(230, 126, 34));
        JButton lowStockBtn = createStyledButton("Low Stock", new Color(155, 89, 182));
        
        inventoryReportBtn.addActionListener(e -> generateInventoryReport());
        salesReportBtn.addActionListener(e -> generateSalesReport());
        stockAlertBtn.addActionListener(e -> generateStockAlerts());
        expiredBtn.addActionListener(e -> showExpiredMedicines());
        lowStockBtn.addActionListener(e -> showLowStock());
        
        panel.add(inventoryReportBtn);
        panel.add(salesReportBtn);
        panel.add(stockAlertBtn);
        panel.add(expiredBtn);
        panel.add(lowStockBtn);
        
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

    private void generateInventoryReport() {
        List<Medicine> medicines = inventoryService.getAllMedicines();
        double totalValue = medicines.stream()
                .mapToDouble(m -> m.getPrice() * m.getQuantity())
                .sum();
        
        StringBuilder report = new StringBuilder();
        report.append("=== INVENTORY REPORT ===\n\n");
        report.append(String.format("Total Medicines: %d\n", medicines.size()));
        report.append(String.format("Total Inventory Value: $%.2f\n\n", totalValue));
        report.append("Medicine Details:\n");
        report.append("-----------------------------------------------------------------------------\n");
        
        for (Medicine medicine : medicines) {
            report.append(String.format("ID: %-3d | %-20s | Stock: %-4d | Price: $%-6.2f | Value: $%-8.2f\n",
                    medicine.getId(), medicine.getName(), medicine.getQuantity(), 
                    medicine.getPrice(), medicine.getPrice() * medicine.getQuantity()));
        }
        
        reportArea.setText(report.toString());
    }
    
    private void generateSalesReport() {
        // For now, show a simple sales summary
        // In a real application, you'd get this from SalesService
        StringBuilder report = new StringBuilder();
        report.append("=== SALES REPORT ===\n\n");
        report.append("Sales reporting feature will be implemented with database integration.\n");
        report.append("Currently using in-memory data storage.\n\n");
        report.append("Features available with database:\n");
        report.append("- Daily/Weekly/Monthly sales reports\n");
        report.append("- Revenue analysis\n");
        report.append("- Customer purchase history\n");
        report.append("- Best-selling products\n");
        
        reportArea.setText(report.toString());
    }
    
    private void generateStockAlerts() {
        List<Medicine> lowStock = inventoryService.getLowStockMedicines(10);
        List<Medicine> expired = inventoryService.getExpiredMedicines();
        
        StringBuilder report = new StringBuilder();
        report.append("=== STOCK ALERTS ===\n\n");
        
        if (lowStock.isEmpty() && expired.isEmpty()) {
            report.append("No stock alerts at this time.\n");
            reportArea.setText(report.toString());
            return;
        }
        
        if (!lowStock.isEmpty()) {
            report.append("LOW STOCK ALERTS (Quantity < 10):\n");
            report.append("----------------------------------------\n");
            for (Medicine medicine : lowStock) {
                report.append(String.format("! %s - Only %d left in stock\n", 
                    medicine.getName(), medicine.getQuantity()));
            }
            report.append("\n");
        }
        
        if (!expired.isEmpty()) {
            report.append("EXPIRED MEDICINES:\n");
            report.append("----------------------------------------\n");
            for (Medicine medicine : expired) {
                report.append(String.format("X %s - Expired on %s\n", 
                    medicine.getName(), medicine.getExpiryDate()));
            }
        }
        
        reportArea.setText(report.toString());
    }
    
    private void showExpiredMedicines() {
        List<Medicine> expired = inventoryService.getExpiredMedicines();
        
        StringBuilder report = new StringBuilder();
        report.append("=== EXPIRED MEDICINES ===\n\n");
        
        if (expired.isEmpty()) {
            report.append("No expired medicines found.\n");
        } else {
            report.append("Expired Medicines List:\n");
            report.append("----------------------------------------\n");
            for (Medicine medicine : expired) {
                report.append(String.format("Name: %s\n", medicine.getName()));
                report.append(String.format("Expiry Date: %s\n", medicine.getExpiryDate()));
                report.append(String.format("Quantity: %d\n", medicine.getQuantity()));
                report.append("----------------------------------------\n");
            }
        }
        
        reportArea.setText(report.toString());
    }
    
    private void showLowStock() {
        List<Medicine> lowStock = inventoryService.getLowStockMedicines(10);
        
        StringBuilder report = new StringBuilder();
        report.append("=== LOW STOCK MEDICINES ===\n\n");
        
        if (lowStock.isEmpty()) {
            report.append("No low stock medicines found.\n");
        } else {
            report.append("Low Stock Medicines (Quantity < 10):\n");
            report.append("----------------------------------------\n");
            for (Medicine medicine : lowStock) {
                report.append(String.format("Name: %s\n", medicine.getName()));
                report.append(String.format("Current Stock: %d\n", medicine.getQuantity()));
                report.append(String.format("Price: $%.2f\n", medicine.getPrice()));
                report.append("----------------------------------------\n");
            }
        }
        
        reportArea.setText(report.toString());
    }
}
