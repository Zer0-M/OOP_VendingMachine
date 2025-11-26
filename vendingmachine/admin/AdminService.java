package vendingmachine.admin;

import java.util.Map;
import vendingmachine.products.InventoryManager;
import vendingmachine.payment.MoneyManager;

/**
 * คลาสสำหรับ "โหมดแอดมิน" (Encapsulation)
 * ห่อหุ้มเมธอดที่อันตราย (เช่น เติมของ, เก็บเงิน) ไว้ที่นี่
 * AdminUI (View) จะเข้าถึงเมธอดเหล่านี้ไม่ได้
 */
public class AdminService {
    private final InventoryManager inventory;
    private final MoneyManager moneyManager;
    private static final String ADMIN_PASSWORD = "1234"; // รหัสผ่านแอดมิน

    // Constructor
    public AdminService(InventoryManager inventory, MoneyManager moneyManager) {
        this.inventory = inventory;
        this.moneyManager = moneyManager;
    }

    // เช็ครหัสผ่านแอดมิน
    public static boolean authenticate(String password) {
        return ADMIN_PASSWORD.equals(password);
    }

    // เติมสินค้าในช่องที่ระบุ
    public void restockItem(String slotCode, int quantity) {
        try {
            inventory.restockSlot(slotCode, quantity);
            System.out.println("Admin: Restocked " + slotCode + " with " + quantity + " items.");
        } catch (Exception e) {
            System.out.println("Admin Error: " + e.getMessage());
        }
    }

    // เปลี่ยนราคาสินค้าในช่องที่ระบุ
    public void setPrice(String slotCode, double newPrice) {
        try {
            inventory.updatePrice(slotCode, newPrice);
            System.out.println("Admin: Updated price for " + slotCode + " to " + newPrice);
        } catch (Exception e) {
            System.out.println("Admin Error: " + e.getMessage());
        }
    }

    // เปลี่ยนชื่อสินค้่าในช่องที่ระบุ
    public void setName(String slotCode, String newName) {
        try {
            inventory.updateName(slotCode, newName);
            System.out.println("Admin: Updated name for " + slotCode + " to " + newName);
        } catch (Exception e) {
            System.out.println("Admin Error: " + e.getMessage());
        }
    }

    // ถอนเงินจำนวนเฉพาะเจาะจง
    public Map<Double, Integer> collectCashAmount(double amount) {
        System.out.println("Admin: Collected " + amount + " Baht.");
        return moneyManager.withdrawSpecificCash(amount);
    }

    // เพิ่มสินค้าใหม่ลงในตู้
    public void addProduct(String slotCode, String name, double price, int quantity, String type, double size)
            throws Exception {
        inventory.addNewProduct(slotCode, name, price, quantity, type, size);
        System.out.println("Admin: Added new product " + name + " to slot " + slotCode);
    }
    
    // บันทึกสต็อกสินค้าลงไฟล์
    public void saveStock() {
        inventory.saveInventoryToFile();
    }

    public void loadStock() {
        inventory.loadInventoryFromFile();
    }
}