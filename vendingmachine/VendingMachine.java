package vendingmachine;
import vendingmachine.payment.*;
import vendingmachine.items.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.List;

public class VendingMachine {
    private MoneyManager moneyManager = new MoneyManager(); // สร้างมาเพื่อจัดการเกี่ยวกับเงิน
    private List<Integer> selectProductID = new ArrayList<>(); // ไว้เก็บสินค้าที่ผู้ใช้เลือก
    private LinkedHashMap<String, ItemSlot[]> ItemType = ItemManager.Initialize(); // เก็บสินค้าเป็นกลุ่มๆ จะได้ง่ายเวลาจะเพิ่มสินค้า
    private List<ItemSlot> allSlots = new ArrayList<>(); // เก็บสินค้าทั้งหมดโดยไม่จัดกลุ่ม จะได้ใช้ง่าย

    public VendingMachine() {
        for (ItemSlot[] categoryList : ItemType.values()) { // นำสิรค้าทั้งหมดมาใส่ใน allSlots เพื่อจะได้ใช้ง่ายๆ
            allSlots.addAll(List.of(categoryList));
        }
    }

    public void displayProducts() {
        System.out.println("                   === Our Product ===                    ");
        System.out.println("==========================================================");

        ItemType.forEach((categoryName, slots) -> {
            System.out.println("--- " + categoryName.toUpperCase() + " ---");

            // v-- ปรับ Format ให้แสดง SlotCode
            System.out.printf("    %-5s | %-38s | %s\n", "Code", "Product Details", "In Stock");
            System.out.println("    ------|----------------------------------------|--------");

            for (ItemSlot slot : slots) {
                String slotCode = "["  + "]";
                String productInfo = slot.getProduct().getInfo();
                String stockInfo = "Remain: " + slot.getQuantity();

                System.out.printf("    %-5s | %-38s | %s\n", slotCode, productInfo, stockInfo);
            }
        });
        System.out.println("==========================================================");
    }

    // public void selectProduct(int productID) {
    //     // เช็คว่าสินค้านั้นมีอยู่จริงมั้ย
    //     for (ItemSlot slot : allSlots) {
    //         if (slot.getProduct().product_code() == productID) {
    //             // เช็คว่าของมันยังมีอยู่มั้ย
    //             if (slot.isEmpty()) {
    //                 System.out.println("Not have!");
    //                 return;
    //             }
    //             selectProductID.add(productID);
    //             System.out.println("You have selected: " + slot.getProduct().getName());

    //             double price = slot.getProduct().getPrice();
    //             if (moneyManager.pay(price)) {
    //             slot.dispense();
    //             System.out.println("Buy " + slot.getProduct().getName() + " Successfull!");
    //             }
    //             return;
    //         }
    //     }
    // }

    // public void returnChange() {
    //     double change = moneyManager.returnChange();
    //     if (change > 0) {
    //         System.out.println("Receive Change: " + change + " Baht");
    //     } else {
    //         System.out.println("No money left.");
    //     }
    // }

    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        Scanner sc = new Scanner(System.in);
        System.out.println("\n==================== Vending Mechine =====================");
        vm.displayProducts();
        System.out.println("\n=== Step for Using Mechine ===");
        System.out.println("[1] Pick Product");
        System.out.println("[2] Select Payment Method");
        System.out.println("[3] Insert Money");
        System.out.println("[4] Receive Change");
        System.out.println("[5] Log Out\n");

        // เลือกสินค้า
        // while (true) {
        //     System.out.print("Select ID of Product Code: ");
        //     int productID = sc.nextInt();
        //     vm.selectProduct(productID);
        //     if(productID == 000) break;
        // }

        // // รับเงิน
        // System.out.println("Please select payment method to add funds:");
        // System.out.println("  [1] Coins");
        // System.out.println("  [2] Banknote");
        // System.out.println("  [3] QR Scan (Top-up)");
        // System.out.print("Select method: ");
        // int choice = sc.nextInt();

        // PaymentReceiver selectedPaymentMethod; // สร้างตัวแปร Interface

        // if (choice == 1) {
        //     selectedPaymentMethod = new CoinReceiver();
        // } else if (choice == 2) {
        //     selectedPaymentMethod = new BanknoteReceiver();
        // } else if (choice == 3) {
        //     selectedPaymentMethod = new QRPaymentReceiver();
        // } else {
        //     System.out.println("Invalid method.");
        //     sc.close();
        //     return;
        // }
        // vm.addFunds(selectedPaymentMethod);

        // vm.returnChange();

        // System.out.println("Thank you!");
        // sc.close();
        // return;
    }

    // public void addFunds(PaymentReceiver method) {
    //     moneyManager.addFunds(method);
    // }
}
