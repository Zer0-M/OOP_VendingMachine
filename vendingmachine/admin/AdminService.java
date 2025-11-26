package vendingmachine.admin;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import vendingmachine.products.InventoryManager;
import vendingmachine.products.ItemSlot;
import vendingmachine.payment.MoneyManager;
import vendingmachine.users.MemberDatabase;
import vendingmachine.exceptions.*;

/**
 * คลาสสำหรับ "โหมดแอดมิน" (Encapsulation)
 * ห่อหุ้มเมธอดที่อันตราย (เช่น เติมของ, เก็บเงิน) ไว้ที่นี่
 * AdminUI (View) จะเข้าถึงเมธอดเหล่านี้ไม่ได้
 */
public class AdminService {
    private final InventoryManager inventory;
    private final MoneyManager moneyManager;
    private final MemberDatabase memberDatabase;
    private static final String ADMIN_PASSWORD = "1234"; // รหัสผ่านแอดมิน

    // Constructor
    public AdminService(InventoryManager inventory, MoneyManager moneyManager, MemberDatabase memberDatabase) {
        this.inventory = inventory;
        this.moneyManager = moneyManager;
        this.memberDatabase = memberDatabase;
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
        } catch (VendingMachineException e) {
            System.out.println("Admin Error: " + e.getMessage());
        }
    }

    // เปลี่ยนราคาสินค้าในช่องที่ระบุ
    public void setPrice(String slotCode, double newPrice) {
        try {
            inventory.updatePrice(slotCode, newPrice);
            System.out.println("Admin: Updated price for " + slotCode + " to " + newPrice);
        } catch (VendingMachineException e) {
            System.out.println("Admin Error: " + e.getMessage());
        }
    }

    // เปลี่ยนชื่อสินค้่าในช่องที่ระบุ
    public void setName(String slotCode, String newName) {
        try {
            inventory.updateName(slotCode, newName);
            System.out.println("Admin: Updated name for " + slotCode + " to " + newName);
        } catch (VendingMachineException e) {
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
            throws VendingMachineException {
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

    // --- Data Access for AdminUI (Proxy Methods) ---
    // [New] เมธอดเหล่านี้ช่วยให้ AdminUI ดึงข้อมูลได้โดยตรงผ่าน Service

    public Map<String, ItemSlot> getProductList() {
        return inventory.getActiveSlots();
    }

    public double getMachineCurrentCash() {
        return moneyManager.getCurrentInternalCash();
    }

    public String getAllMembersDisplay() {
        return memberDatabase.getAllMembersDisplay();
    }

    // --- Complex Logic (ย้ายมาจาก Controller) ---

    public String withdrawCash(double amount) {
        // เรียกใช้เมธอดจาก MoneyManager
        Map<Double, Integer> result = moneyManager.withdrawSpecificCash(amount);

        if (result == null)
            return "Error: Cannot withdraw that amount (Insufficient funds or no suitable change).";

        StringBuilder sb = new StringBuilder();
        sb.append("Withdraw Success! Total: ").append(amount).append(" THB\n");
        sb.append("--------------------------------\n");

        Map<Double, Integer> sortedResult = new TreeMap<>(Collections.reverseOrder());
        sortedResult.putAll(result);

        for (Map.Entry<Double, Integer> entry : sortedResult.entrySet()) {
            double value = entry.getKey();
            int count = entry.getValue();
            String type = (value >= 20) ? "Bank" : "Coin";
            sb.append(String.format("%-6.0f (%s) : x%d\n", value, type, count));
        }
        sb.append("--------------------------------");
        return sb.toString();
    }
}