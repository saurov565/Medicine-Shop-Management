

import java.time.LocalDate;

public class Medicine {
    private int id;
    private String name;
    private String manufacturer;
    private double price;
    private int quantity;
    private LocalDate expiryDate;
    private String category;
    
    public Medicine(int id, String name, String manufacturer, double price, 
                   int quantity, LocalDate expiryDate, String category) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.category = category;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getManufacturer() { return manufacturer; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getCategory() { return category; }
    
    public void setName(String name) { this.name = name; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setCategory(String category) { this.category = category; }
    
    public Object[] toTableRow() {
        return new Object[]{
            id, name, manufacturer, price, quantity, expiryDate, category
        };
    }
    
    @Override
    public String toString() {
        return String.format("%s (ID: %d) - $%.2f", name, id, price);
    }
}