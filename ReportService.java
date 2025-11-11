

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {
    private InventoryService inventoryService;
    private SalesService salesService;
    
    public ReportService(InventoryService inventoryService, SalesService salesService) {
        this.inventoryService = inventoryService;
        this.salesService = salesService;
    }
    
    public void generateInventoryReport() {
        System.out.println("\n=== INVENTORY REPORT ===");
        System.out.println("Total Medicines: " + inventoryService.getAllMedicines().size());
        
        List<Medicine> lowStock = inventoryService.getLowStockMedicines(10);
        System.out.println("Low Stock Medicines (<10): " + lowStock.size());
        
        List<Medicine> expired = inventoryService.getExpiredMedicines();
        System.out.println("Expired Medicines: " + expired.size());
        
        double totalInventoryValue = inventoryService.getAllMedicines().stream()
                .mapToDouble(m -> m.getPrice() * m.getQuantity())
                .sum();
        System.out.printf("Total Inventory Value: $%.2f\n", totalInventoryValue);
    }
    
    public void generateSalesReport() {
        System.out.println("\n=== SALES REPORT ===");
        System.out.println("Total Sales: " + salesService.getAllSales().size());
        System.out.printf("Total Revenue: $%.2f\n", salesService.getTotalRevenue());
        
        Map<String, Double> revenueByCustomer = salesService.getAllSales().stream()
                .collect(Collectors.groupingBy(
                    Sale::getCustomerName,
                    Collectors.summingDouble(Sale::getTotalAmount)
                ));
        
        System.out.println("\nRevenue by Customer:");
        revenueByCustomer.forEach((customer, revenue) -> 
            System.out.printf("  %s: $%.2f\n", customer, revenue));
    }
    
    public void generateStockAlertReport() {
        List<Medicine> lowStock = inventoryService.getLowStockMedicines(10);
        if (!lowStock.isEmpty()) {
            System.out.println("\n=== STOCK ALERT ===");
            lowStock.forEach(medicine -> 
                System.out.printf("LOW STOCK: %s (Only %d left)\n", 
                    medicine.getName(), medicine.getQuantity()));
        }
        
        List<Medicine> expired = inventoryService.getExpiredMedicines();
        if (!expired.isEmpty()) {
            System.out.println("\n=== EXPIRED MEDICINES ===");
            expired.forEach(medicine -> 
                System.out.printf("EXPIRED: %s (Expired on %s)\n", 
                    medicine.getName(), medicine.getExpiryDate()));
        }
    }
}