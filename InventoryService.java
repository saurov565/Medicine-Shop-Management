

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryService {
    private List<Medicine> medicines;
    private int nextId;
    
    public InventoryService() {
        this.medicines = new ArrayList<>();
        this.nextId = 1;
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        addMedicine(new Medicine(nextId++, "Paracetamol", "ABC Pharma", 5.99, 100, 
                LocalDate.of(2025, 12, 31), "Pain Relief"));
        addMedicine(new Medicine(nextId++, "Amoxicillin", "XYZ Meds", 12.50, 50, 
                LocalDate.of(2024, 8, 15), "Antibiotic"));
        addMedicine(new Medicine(nextId++, "Vitamin C", "Health Plus", 8.75, 200, 
                LocalDate.of(2026, 3, 20), "Supplement"));
        addMedicine(new Medicine(nextId++, "Ibuprofen", "MediCorp", 7.25, 75, 
                LocalDate.of(2025, 6, 30), "Pain Relief"));
        addMedicine(new Medicine(nextId++, "Cetirizine", "Allergy Free", 4.99, 150, 
                LocalDate.of(2024, 12, 15), "Allergy"));
    }
    
    public void addMedicine(Medicine medicine) {
        medicine = new Medicine(nextId++, medicine.getName(), medicine.getManufacturer(), 
                medicine.getPrice(), medicine.getQuantity(), medicine.getExpiryDate(), medicine.getCategory());
        medicines.add(medicine);
    }
    
    public boolean updateMedicine(Medicine updatedMedicine) {
        for (int i = 0; i < medicines.size(); i++) {
            if (medicines.get(i).getId() == updatedMedicine.getId()) {
                medicines.set(i, updatedMedicine);
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteMedicine(int id) {
        return medicines.removeIf(medicine -> medicine.getId() == id);
    }
    
    public Medicine findMedicineById(int id) {
        return medicines.stream()
                .filter(medicine -> medicine.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public List<Medicine> findMedicineByName(String name) {
        return medicines.stream()
                .filter(medicine -> medicine.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Medicine> getLowStockMedicines(int threshold) {
        return medicines.stream()
                .filter(medicine -> medicine.getQuantity() <= threshold)
                .collect(Collectors.toList());
    }
    
    public List<Medicine> getExpiredMedicines() {
        return medicines.stream()
                .filter(medicine -> medicine.getExpiryDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }
    
    public List<Medicine> getAllMedicines() {
        return new ArrayList<>(medicines);
    }
    
    public boolean sellMedicine(int medicineId, int quantity) {
        Medicine medicine = findMedicineById(medicineId);
        if (medicine != null && medicine.getQuantity() >= quantity) {
            medicine.setQuantity(medicine.getQuantity() - quantity);
            return true;
        }
        return false;
    }
    
    public int getNextId() {
        return nextId;
    }
} 
    

