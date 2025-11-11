
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private InventoryService inventoryService;
    private SalesService salesService;
    private ReportService reportService;
    
    private JTabbedPane tabbedPane;
    private InventoryPanel inventoryPanel;
    private SalesPanel salesPanel;
    private ReportsPanel reportsPanel;
    
    // Modern color palette
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Blue
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);  // Light Blue
    private final Color ACCENT_COLOR = new Color(46, 204, 113);     // Green
    private final Color WARNING_COLOR = new Color(231, 76, 60);     // Red
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light Gray
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(51, 51, 51);
    
    public MainFrame() {
        initializeServices();
        initializeUI();
        setupFrame();
    }
    
    private void initializeServices() {
        inventoryService = new InventoryService();
        salesService = new SalesService(inventoryService);
        reportService = new ReportService(inventoryService, salesService);
    }
    
    private void initializeUI() {
        setTitle("Medicine Shop Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 800));
        
        // Set modern background
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create styled tabbed pane
        tabbedPane = createStyledTabbedPane();
        
        // Create panels
        inventoryPanel = new InventoryPanel(inventoryService, PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, WARNING_COLOR, BACKGROUND_COLOR, CARD_COLOR, TEXT_COLOR);
        salesPanel = new SalesPanel(inventoryService, salesService, PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, WARNING_COLOR, BACKGROUND_COLOR, CARD_COLOR, TEXT_COLOR);
        reportsPanel = new ReportsPanel(reportService, inventoryService, PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, WARNING_COLOR, BACKGROUND_COLOR, CARD_COLOR, TEXT_COLOR);
        
        // Add tabs
        tabbedPane.addTab("Inventory Management", inventoryPanel);
        tabbedPane.addTab("Sales Processing", salesPanel);
        tabbedPane.addTab("Reports & Analytics", reportsPanel);
        
        add(tabbedPane);
    }
    
    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane pane = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        pane.setBackground(BACKGROUND_COLOR);
        pane.setForeground(TEXT_COLOR);
        pane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Custom UI for tabs
        UIManager.put("TabbedPane.selected", SECONDARY_COLOR);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND_COLOR);
        UIManager.put("TabbedPane.background", BACKGROUND_COLOR);
        
        return pane;
    }
    
    private void setupFrame() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}