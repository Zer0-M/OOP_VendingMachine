package vendingmachine;

import vendingmachine.products.*;
import vendingmachine.payment.*;
import vendingmachine.users.*;
import vendingmachine.admin.AdminService;
import vendingmachine.exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendingMachineController {
    private final InventoryManager inventoryManager; // ตัวจัดการคลังสินค้า
    private final MoneyManager moneyManager; // ตัวจัดการเงินต่างๆ
    private final MemberDatabase memberDatabase; // ส่วนจัดการผู้ใช้
    private final AdminService adminService;
    // private final List<ItemSlot> shoppingCart;
    private final HashMap<ItemSlot, Integer> shoppingCart; // ตะกล้าสินค้า

    public VendingMachineController() {
        this.inventoryManager = new InventoryManager();
        this.moneyManager = new MoneyManager(500); // ใส่เงินทอนเริ่มต้นในเครื่อง
        this.memberDatabase = new MemberDatabase();

        // สร้างตะกร้าเปล่า
        this.shoppingCart = new HashMap<>();

        // 3. (สำคัญมาก) "ประกอบร่าง" AdminService
        // โดย "ฉีด" (inject) inventory และ cashRegister เข้าไป
        this.adminService = new AdminService(this.inventoryManager, this.moneyManager);
    }

    public String getDisplayProducts() {
        return inventoryManager.getProductDisplay();
    }

    /**
     * (สำหรับ View) เช็กว่ารหัสสินค้านี้มีอยู่จริงในตู้หรือไม่
     */
    public boolean hasProductsID(String slotCode) {
        try {
            inventoryManager.findSlotByCode(slotCode); // ลองค้นหา
            return true; // ถ้าเจอ
        } catch (Exception e) {
            return false; // ถ้าไม่เจอ (โยน Exception)
        }
    }

    public String addItemToCart(String slotCode) {
        try {
            ItemSlot slot = inventoryManager.findSlotByCode(slotCode); // 1. หาช่องสินค้า

            // [เพิ่มใหม่] ดูว่าตอนนี้ในตะกร้ามีสินค้านี้อยู่แล้วกี่ชิ้น
            int currentQtyInCart = shoppingCart.getOrDefault(slot, 0);

            // 2. เช็กสต็อก (ส่งจำนวนที่อยู่ในตะกร้าไปคำนวณด้วย)
            // ถ้าของจริงมี 5, ในตะกร้ามี 5 -> จะโยน Exception ทันทีตรงนี้เลย
            inventoryManager.checkStock(slot, currentQtyInCart);

            // 3. ถ้าผ่าน ก็เพิ่มจำนวนลงตะกร้า (+1)
            shoppingCart.put(slot, currentQtyInCart + 1);

            // 4. ส่งข้อความสถานะกลับไป
            return "Added: " + slot.getProduct().getName()
                    + " | Current Total: " + getCartTotal() + " Baht";

        } catch (OutOfStockException e) {
            return "Error: " + e.getMessage(); // แจ้งเตือนทันทีว่าของหมด/ไม่พอ
        } catch (Exception e) {
            return "Error: Invalid slot code.";
        }
    }

    public HashMap<ItemSlot, Integer> getCart() {
        System.out.print("CurrentCart: ");

        if (shoppingCart.isEmpty()) {
            System.out.println("[ Empty ]");
        } else {
            List<String> items = new ArrayList<>();

            // วนลูปดึงข้อมูลมาเก็บใน List ก่อน
            for (Map.Entry<ItemSlot, Integer> entry : shoppingCart.entrySet()) {
                String name = entry.getKey().getProduct().getName();
                int qty = entry.getValue();
                items.add(name + " (x" + qty + ")");
            }

            // แสดงผล: [ Coke (x2), Lays (x1) ]
            System.out.println("[ " + String.join(", ", items) + " ]");
        }

        return shoppingCart;
    }

    public double getCartTotal() {
        double total = 0.0;
        // (แก้ไขใหม่) วนลูปผ่าน Entry ของ HashMap
        for (HashMap.Entry<ItemSlot, Integer> entry : shoppingCart.entrySet()) {
            ItemSlot slot = entry.getKey();
            int quantity = entry.getValue();
            // ราคาของสินค้า x จำนวนชิ้น
            total += slot.getProduct().getPrice() * quantity;
        }
        return total;
    }

    public boolean processPayment(double totalAmount, String paymentChoice) {

        try {
            // 2. สั่ง MoneyManager จัดการ (Encapsulation)
            // (Logic การรับเงิน/ทอนเงิน/เช็กเงินทอน เกิดในนี้ทั้งหมด)
            boolean success = moneyManager.processPayment(totalAmount, paymentChoice);

            if (success) {
                // 3. จ่ายเงินสำเร็จ -> สั่ง InventoryManager "จ่ายของ" (ตัดสต็อก)
                inventoryManager.dispenseCart(shoppingCart);
            }
            return success;

        } catch (InsufficientFundsException | ChangeNotAvailableException e) {
            // 4. จัดการ Error การเงิน
            System.out.println("Payment Failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * (สำหรับ View) สะสมแต้ม
     */
    public String applyPoints(String phoneNumber) {
        // สะสมแต้ม (Encapsulation)
        int points = (int) getCartTotal(); // สมมติ 1 บาท 1 แต้ม
        return memberDatabase.addPointsToMember(phoneNumber, points);
    }

    /**
     * (สำหรับ View) เคลียร์ตะกร้า (เมื่อจ่ายเงินเสร็จ)
     */
    public void clearCart() {
        shoppingCart.clear();
    }

    // เป็นสะพานเชื่อมให้ GUI ดึงข้อมูลสินค้า
    public java.util.Map<String, vendingmachine.products.ItemSlot> getProductList() {
        return inventoryManager.getSlots();
    }

    // --- 4. (ใหม่) สร้างเมธอด "ส่งต่อ" สำหรับ Admin ---
    // VendingMachine (View) จะเรียกเมธอดนี้
    // Controller จะ "ส่งต่อ" (Delegate) งานไปให้ AdminService

    public void adminRestockItem(String slotCode, int quantity) {
        // (เราอาจจะเช็ก Password ก่อนตรงนี้ก็ได้)
        adminService.restockItem(slotCode, quantity);
    }

    public void adminCollectCash() {
        adminService.collectCash();
    }

    public void adminSetPrice(String slotCode, double newPrice) {
        adminService.setPrice(slotCode, newPrice);
    }

    // [ใหม่] ลบสินค้าออกจากตะกร้าทีละ 1 ชิ้น
    public void removeOneItemFromCart(String slotCode) {
        ItemSlot slot = null;
        try {
            slot = inventoryManager.findSlotByCode(slotCode);
        } catch (Exception e) {
            System.out.println("Error: Invalid slot code.");
            return;
        }
        if (slot != null && shoppingCart.containsKey(slot)) {
            int currentQty = shoppingCart.get(slot);
            if (currentQty > 1) {
                shoppingCart.put(slot, currentQty - 1); // ลดจำนวนลง 1
            } else {
                shoppingCart.remove(slot); // ถ้าเหลือ 1 ให้ลบทิ้งเลย
            }
        }
    }

    // [ใหม่] ขอข้อมูลเงินสดที่มีในตู้ (สำหรับ Admin)
    public double getMachineCurrentCash() {
        return moneyManager.getCurrentInternalCash();
    }

    // [NEW] ลบสินค้าออกจากตะกร้าทั้ง Slot (ไม่สนจำนวน)
    public void removeProductFromCart(String slotCode) {
        try {
            ItemSlot slot = inventoryManager.findSlotByCode(slotCode);
            if (shoppingCart.containsKey(slot)) {
                shoppingCart.remove(slot); // ลบทิ้งทั้ง Key เลย
            }
        } catch (Exception e) {
            System.out.println("Error removing item: " + e.getMessage());
        }
    }

    // [UPDATED] ถอนเงินและสร้างข้อความสรุปรายการ
    public String adminWithdrawCash(double amount) {
        Map<Double, Integer> result = adminService.withdrawCash(amount);
        
        if (result == null) {
            return "Error: Cannot withdraw that amount (Insufficient funds or no suitable change).";
        }

        // สร้างข้อความสรุป (String Builder)
        StringBuilder sb = new StringBuilder();
        sb.append("Withdraw Success! Total: ").append(amount).append(" THB\n");
        sb.append("--------------------------------\n");
        
        // เรียงลำดับจากแบงค์ใหญ่ไปเล็ก (TreeMap) เพื่อความสวยงาม
        Map<Double, Integer> sortedResult = new java.util.TreeMap<>(java.util.Collections.reverseOrder());
        sortedResult.putAll(result);

        for (Map.Entry<Double, Integer> entry : sortedResult.entrySet()) {
            double val = entry.getKey();
            int count = entry.getValue();
            String type = (val >= 20) ? "Bank" : "Coin"; // แยกแบงค์/เหรียญ
            
            // Format: 1000.0 (Bank) : 5 units
            sb.append(String.format("%-6.0f (%s) : x%d\n", val, type, count));
        }
        sb.append("--------------------------------");
        
        return sb.toString();
    }
}