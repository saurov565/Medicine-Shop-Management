import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SalesService {
    private List<Sale> sales;
    private InventoryService inventoryService;
    private int nextSaleId;
    
    public SalesService(InventoryService inventoryService) {
        this.sales = new ArrayList<>();
        this.inventoryService = inventoryService;
        this.nextSaleId = 1;
    }
    
    public Sale createSale(String customerName) {
        return new Sale(nextSaleId++, customerName);
    }
    
    public boolean addItemToSale(Sale sale, int medicineId, int quantity) {
        Medicine medicine = inventoryService.findMedicineById(medicineId);
        if (medicine != null && medicine.getQuantity() >= quantity) {
            SaleItem item = new SaleItem(medicine, quantity);
            sale.addItem(item);
            inventoryService.sellMedicine(medicineId, quantity);
            return true;
        }
        return false;
    }
    
    public void completeSale(Sale sale) {
        sales.add(sale);
    }
    
    public List<Sale> getAllSales() {
        return new ArrayList<>(sales);
    }
    
    public double getTotalRevenue() {
        return sales.stream()
                .mapToDouble(Sale::getTotalAmount)
                .sum();
    }
    
    public List<Sale> getSalesByCustomer(String customerName) {
        return sales.stream()
                .filter(sale -> sale.getCustomerName().equalsIgnoreCase(customerName))
                .collect(Collectors.toList());
    }
}