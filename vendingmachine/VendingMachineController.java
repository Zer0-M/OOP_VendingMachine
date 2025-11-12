package vendingmachine;

import vendingmachine.products.*;
import vendingmachine.payment.*;
import vendingmachine.users.*;
import vendingmachine.exceptions.*;

import java.util.ArrayList;
import java.util.List;

public class VendingMachineController {
    private final InventoryManager inventoryManager;
    private final MoneyManager cashRegister;
    private final MemberDatabase memberDatabase;

    private final List<Product> shoppingCart;

    public VendingMachineController() {
        this.inventoryManager = new InventoryManager();
        this.cashRegister = new MoneyManager(500);
        this.memberDatabase = new MemberDatabase();
        this.shoppingCart = new ArrayList<>();
    }

    public String addItemToCart(String slotCode) {
        try {
            Product product = inventoryManager.findProductBySlot(slotCode); // 1. หาของ
            inventoryManager.checkStock(slotCode); // 2. เช็กสต็อก (โยน OutOfStockException ถ้าหมด)

            shoppingCart.add(product); // 3. เพิ่มลงตะกร้า
            return "Added: " + product.getName();
        } catch (OutOfStockException e) {
            return "Error: " + e.getMessage(); // ส่ง Error กลับไปให้ VendingMachine (View)
        } catch (Exception e) {
            return "Error: Invalid slot code.";
        }
    }

    public double getCartTotal() {
        double total = 0.0;
        for (Product p : shoppingCart) {
            total += p.getPrice();
        }
        return total;
    }

    public boolean processPayment(double total, String paymentChoice) {
        PaymentReceiver method;
        if (paymentChoice.equals("1")) {
            method = new QRReceiver();
        } else {
            method = new CoinReceiver();
        }

        // สั่ง CashRegister จัดการ (Encapsulation)
        boolean success = cashRegister.processPayment(total, method);

        if (success) {
            // จ่ายเงินสำเร็จ ค่อยตัดสต็อก!
            inventoryManager.dispenseCart(shoppingCart);
        }
        return success;
    }

    public String applyPoints(String phoneNumber) {
        // สะสมแต้ม (Encapsulation)
        int points = (int) getCartTotal(); // สมมติ 1 บาท 1 แต้ม
        return memberDatabase.addPointsToMember(phoneNumber, points);
    }

    public void clearCart() {
        shoppingCart.clear();
    }

    // (เมธอดสำหรับให้ VendingMachine (View) ดึงไปโชว์)
    // public String getDisplayProducts() { ... }
    public String getDisplayProducts() {
        System.out.println("                   === Our Product ===                    ");
        System.out.println("==========================================================");

        ItemType.forEach((categoryName, slots) -> {
            System.out.println("--- " + categoryName.toUpperCase() + " ---");
            System.out.printf("    %-5s | %-38s | %s\n", "Code", "Product Details", "In Stock");
            System.out.println("    ------|----------------------------------------|--------");

            for (ItemSlot slot : slots) {
                String slotCode = "[" + "]";
                String productInfo = slot.getProduct().getInfo();
                String stockInfo = "Remain: " + slot.getQuantity();

                System.out.printf("    %-5s | %-38s | %s\n", slotCode, productInfo, stockInfo);
            }
        });
        System.out.println("==========================================================");
    }
}