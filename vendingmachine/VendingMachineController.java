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
}