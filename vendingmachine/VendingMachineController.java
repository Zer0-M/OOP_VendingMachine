package vendingmachine;

import vendingmachine.admin.AdminService;
import vendingmachine.products.InventoryManager;
import vendingmachine.products.ItemSlot;
import vendingmachine.payment.MoneyManager;
import vendingmachine.exceptions.InsufficientFundsException;
import vendingmachine.exceptions.ChangeNotAvailableException;

import java.util.Map;

public class VendingMachineController {
    private InventoryManager inventoryManager;
    private MoneyManager moneyManager;
    private AdminService adminService;

    // Constructor
    public VendingMachineController() {
        this.inventoryManager = InventoryManager.getInstance();
        this.moneyManager = new MoneyManager(100.0); // เงินทอนเริ่มต้น
        this.adminService = new AdminService(inventoryManager, moneyManager);
    }

    // --- 🟢 Methods for GUI & General Logic ---
    
    public Map<String, ItemSlot> getProductList() {
        return inventoryManager.getSlots();
    }

    public Map<ItemSlot, Integer> getCart() {
        return inventoryManager.getCart();
    }

    public double getCartTotal() {
        return inventoryManager.calculateTotal();
    }

    public String addItemToCart(String slotCode) {
        try {
            inventoryManager.addToCart(slotCode);
            return "Success: Added to cart.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public void clearCart() {
        inventoryManager.clearCart();
    }

    public boolean processPayment(double total, String methodChoice) {
        try {
            return moneyManager.processPayment(total, methodChoice);
        } catch (InsufficientFundsException | ChangeNotAvailableException e) {
            System.out.println("Payment Error: " + e.getMessage());
            return false;
        }
    }

    public AdminService getAdminService() {
        return this.adminService;
    }

    /**
     * สร้าง String แสดงรายการสินค้าสำหรับ Console
     */
    public String getDisplayProducts() {
        StringBuilder sb = new StringBuilder();
        Map<String, ItemSlot> slots = inventoryManager.getSlots();
        sb.append("-----------------------------------------\n");
        sb.append(" Slot | Product    | Price   | Stock \n");
        sb.append("-----------------------------------------\n");
        for (ItemSlot slot : slots.values()) {
             sb.append(String.format(" [%-2s] | %-10s | %-6.2f | %d \n", 
                slot.getSlotCode(), 
                slot.getProduct().getName(), 
                slot.getProduct().getPrice(), 
                slot.getQuantity()));
        }
        sb.append("-----------------------------------------");
        return sb.toString();
    }

    /**
     * เช็คว่ามีรหัสช่องสินค้านี้ไหม (สำหรับ Console Input)
     */
    public boolean hasProductsID(String slotCode) {
        return inventoryManager.getSlots().containsKey(slotCode);
    }

    /**
     * ระบบสะสมแต้ม (Mock Logic)
     */
    public String applyPoints(String phoneNumber) {
        // ในโปรเจกต์จริงอาจจะเรียก MemberDatabase
        // แต่นี่ใส่ Logic เบื้องต้นไว้ก่อนกัน Error
        return ">> Points added to " + phoneNumber + ". Current Points: 10"; 
    }

    // 🔥 เมธอดใหม่: รับคำสั่งลบจาก UI ส่งต่อให้ Inventory
    public void removeItem(String slotCode) {
        inventoryManager.removeItemFromCart(slotCode);
    }
}