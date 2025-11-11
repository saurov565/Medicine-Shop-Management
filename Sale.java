

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private int saleId;
    private LocalDateTime saleDate;
    private List<SaleItem> items;
    private double totalAmount;
    private String customerName;
    
    public Sale(int saleId, String customerName) {
        this.saleId = saleId;
        this.customerName = customerName;
        this.saleDate = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }
    
    public void addItem(SaleItem item) {
        items.add(item);
        totalAmount += item.getTotalPrice();
    }
    
    // Getters
    public int getSaleId() { return saleId; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public List<SaleItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getCustomerName() { return customerName; }
}