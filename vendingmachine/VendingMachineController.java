package vendingmachine;

import vendingmachine.products.*;
import vendingmachine.payment.*;
import vendingmachine.users.*;
import vendingmachine.exceptions.*;

import java.util.ArrayList;
import java.util.List;

public class VendingMachineController {
    private final InventoryManager inventoryManager; // ตัวจัดการคลังสินค้า
    private final MoneyManager moneyManager; // ตัวจัดการเงินต่างๆ
    private final MemberDatabase memberDatabase; // ส่วนจัดการผู้ใช้

    private final List<ItemSlot> shoppingCart;

    public VendingMachineController() {
        this.inventoryManager = new InventoryManager();
        this.moneyManager = new MoneyManager(500); // ใส่เงินทอนเริ่มต้นในเครื่อง
        this.memberDatabase = new MemberDatabase();

        // สร้างตะกร้าเปล่า
        this.shoppingCart = new ArrayList<>();
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
            inventoryManager.checkStock(slot); // 2. เช็กสต็อก (ถ้าหมด จะโยน OutOfStockException)

            shoppingCart.add(slot); // 3. เพิ่มลงตะกร้า

            // 4. ส่งข้อความสถานะกลับไป
            return "Added: " + slot.getProduct().getName() + " | Current Total: " + getCartTotal() + " Baht";
        } catch (OutOfStockException e) {
            return "Error: " + e.getMessage(); // ส่ง Error กลับไปให้ VendingMachine (View)
        } catch (Exception e) {
            return "Error: Invalid slot code.";
        }
    }

    public double getCartTotal() {
        double total = 0.0;
        for (ItemSlot slot : shoppingCart) {
            total += slot.getProduct().getPrice();
        }
        return total;
    }

    // public boolean processPayment(double totalAmount, String paymentChoice) {
    //     PaymentReceiver paymentMethod;

    //     // (Polymorphism) เลือกช่องทางจ่ายเงิน
    //     switch (paymentChoice) {
    //         case "1":
    //             paymentMethod = new QRReceiver();
    //             break;
    //         case "2":
    //             paymentMethod = new CoinReceiver();
    //             break;
    //         case "3":
    //             paymentMethod = new BanknoteReceiver();
    //             break;
    //         default:
    //             System.out.println("Invalid payment method.");
    //             return false;
    //     }

    //     try {
    //         // 2. สั่ง CashRegister จัดการ (Encapsulation)
    //         // (Logic การรับเงิน/ทอนเงิน/เช็กเงินทอน เกิดในนี้ทั้งหมด)
    //         boolean success = cashRegister.processPayment(totalAmount, paymentMethod);

    //         if (success) {
    //             // 3. จ่ายเงินสำเร็จ -> สั่ง InventoryManager "จ่ายของ" (ตัดสต็อก)
    //             inventoryManager.dispenseCart(shoppingCart);
    //         }
    //         return success;

    //     } catch (InsufficientFundsException | ChangeNotAvailableException e) {
    //         // 4. จัดการ Error การเงิน
    //         System.out.println("Payment Failed: " + e.getMessage());
    //         return false;
    //     }
    // }

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
}