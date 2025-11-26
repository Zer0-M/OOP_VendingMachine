package vendingmachine.admin;

import vendingmachine.products.InventoryManager;
import vendingmachine.payment.MoneyManager;

/**
 * คลาสสำหรับ "โหมดแอดมิน" (Encapsulation)
 * ห่อหุ้มเมธอดที่อันตราย (เช่น เติมของ, เก็บเงิน) ไว้ที่นี่
 * VendingMachine (View) จะเข้าถึงเมธอดเหล่านี้ไม่ได้
 */
public class AdminService {
    private InventoryManager inventory;
    private MoneyManager moneyManager;
    private static final String ADMIN_PASSWORD = ""; // รหัสผ่านแอดมิน

    public AdminService(InventoryManager inventory, MoneyManager moneyManager) {
        this.inventory = inventory;
        this.moneyManager = moneyManager;
    }

    public void restockItem(String slotCode, int quantity) {
        try {
            inventory.restockSlot(slotCode, quantity);
            System.out.println("Admin: Restocked " + slotCode + " with " + quantity + " items.");
        } catch (Exception e) {
            System.out.println("Admin Error: " + e.getMessage());
        }
    }

    public double collectCash() {
        double collected = moneyManager.collectAllCash();
        System.out.println("Admin: Collected " + collected + " Baht.");
        return collected;
    }

    public void setPrice(String slotCode, double newPrice) {
        try {
            inventory.updatePrice(slotCode, newPrice);
            System.out.println("Admin: Updated price for " + slotCode + " to " + newPrice);
        } catch (Exception e) {
            System.out.println("Admin Error: " + e.getMessage());
        }
    }

    public void setName(String slotCode, String newName) {
        try {
            inventory.updateName(slotCode, newName);
            System.out.println("Admin: Updated name for " + slotCode + " to " + newName);
        } catch (Exception e) {
            System.out.println("Admin Error: " + e.getMessage());
        }
    }

    public java.util.Map<Double, Integer> withdrawCash(double amount) {
        return moneyManager.withdrawSpecificCash(amount);
    }

    public static boolean authenticate(String password) {
        return ADMIN_PASSWORD.equals(password);
    }

    // เพิ่มเมธอดนี้ใน AdminService.java
    public void addProduct(String slotCode, String name, double price, int quantity, String type, double size)
            throws Exception {
        inventory.addNewProduct(slotCode, name, price, quantity, type, size);
        System.out.println("Admin: Added new product " + name + " to slot " + slotCode);
    }

    // [EXTENSION]
    public void saveStock() {
        inventory.saveInventoryToFile();
    }
    
    public void loadStock() {
        inventory.loadInventoryFromFile();
    }
}