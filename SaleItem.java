

public class SaleItem {
    private Medicine medicine;
    private int quantity;
    private double totalPrice;
    
    public SaleItem(Medicine medicine, int quantity) {
        this.medicine = medicine;
        this.quantity = quantity;
        this.totalPrice = medicine.getPrice() * quantity;
    }
    
    // Getters
    public Medicine getMedicine() { return medicine; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
    
    @Override
    public String toString() {
        return String.format("%-20s x %d = $%.2f", medicine.getName(), quantity, totalPrice);
    }
    
}
