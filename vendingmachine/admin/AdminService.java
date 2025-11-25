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

    // Admin ต้องได้รับสิทธิ์ (ถูกฉีด Dependencies)
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
}