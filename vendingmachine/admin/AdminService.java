// vendingmachine/admin/AdminService.java
package vendingmachine.admin;

import vendingmachine.products.InventoryManager;
import vendingmachine.payment.MoneyManager;

// คลาสนี้ "has-a" (Composition) InventoryManager และ CashRegister
// เพื่อเข้าไปจัดการมัน
public class AdminService {
    private InventoryManager inventoryManager;
    private MoneyManager moneyManager;

    public AdminService(InventoryManager inventoryManager, MoneyManager moneyManager) {
        this.inventoryManager = inventoryManager;
        this.moneyManager = moneyManager;
    }

    // เมธอดเติมของ
    public void restockItem(String slotCode, int quantity) {
        // inventory.restock(slotCode, quantity);
        System.out.println("Restocked " + slotCode + " with " + quantity + " items.");
    }

    // เมธอดเก็บเงิน
    public double collectCash() {
        double collected = moneyManager.collectAllCash();
        System.out.println("Collected " + collected + " Baht.");
        return collected;
    }
    
    // เมธอดปรับราคา
    public void setPrice(String slotCode, double newPrice) {
        // inventory.updatePrice(slotCode, newPrice);
        System.out.println("Updated price for " + slotCode + " to " + newPrice);
    }
}