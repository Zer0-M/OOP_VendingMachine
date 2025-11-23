package vendingmachine.products;

import vendingmachine.ConsoleUI;
import vendingmachine.exceptions.OutOfStockException;
import java.util.Map; // ใช้ Map แทน List ในการรับพารามิเตอร์
import java.util.TreeMap;

/**
 * คลาส "ผู้จัดการสต็อก" (Encapsulation)
 * Controller จะคุยกับคลาสนี้คลาสเดียวเรื่องสินค้าและสต็อก
 * ใช้ HashMap เก็บช่องสินค้า (Key=A1, Value=ItemSlot)
 */
public class InventoryManager {
    private Map<String, ItemSlot> slots = new TreeMap<>();
    private int nextProductId = 000;

    public InventoryManager() {
        initializeInventory(); // โหลดของเข้าตู้
    }

    private void initializeInventory() {
        // (นี่คือที่ที่เราจะโหลดสินค้าตัวอย่าง)
        // หมวด A (Snacks)
        addSlot("A1", new Snack(nextId(), "Lays", 20.0, 330), 10);
        addSlot("A2", new Snack(nextId(), "Testo", 15.0, 300), 5);

        // หมวด B (Drinks)
        addSlot("B1", new Drink(nextId(), "Coke", 25.0, 500), 10);
        addSlot("B2", new Drink(nextId(), "Water", 10.0, 600), 20);
    }

    // Helper-method
    private void addSlot(String code, Product product, int quantity) {
        ItemSlot slot = new ItemSlot(code, product, quantity);
        this.slots.put(code, slot);
    }

    private int nextId() {
        return nextProductId++;
    }

    // --- เมธอดที่ Controller เรียกใช้ ---
    public ItemSlot findSlotByCode(String slotCode) throws Exception {
        ItemSlot slot = slots.get(slotCode.toUpperCase());
        if (slot == null) {
            throw new Exception("Invalid slot code: " + slotCode);
        }
        return slot;
    }

    /*
     * * [แก้ไขใหม่] ตรวจสอบสต็อกโดยคำนึงถึงของในตะกร้าด้วย
     * int pendingInCart = จำนวนที่ลูกค้ารายนี้หยิบใส่ตะกร้าไปแล้ว
     */
    public void checkStock(ItemSlot slot, int pendingInCart) throws OutOfStockException {
        // สต็อกที่เหลือให้หยิบ = สต็อกจริง - ของที่คาอยู่ในตะกร้า
        int availableToPick = slot.getQuantity() - pendingInCart;

        if (availableToPick <= 0) {
            throw new OutOfStockException("Not enough stock for " + slot.getProduct().getName()
                    + " (Stock: " + slot.getQuantity() + ")");
        }
    }

    /**
     * * [แก้ไขจุดนี้] จ่ายของในตะกร้า (Controller เรียก *หลัง* จ่ายเงินสำเร็จ)
     * รับค่าเป็น Map<ItemSlot, Integer> แทน List
     */
    public void dispenseCart(Map<ItemSlot, Integer> shoppingCart) {
        System.out.println("--- DISPENSING ITEMS ---");

        // วนลูปสินค้าแต่ละชนิดใน Map
        for (Map.Entry<ItemSlot, Integer> entry : shoppingCart.entrySet()) {
            ItemSlot slot = entry.getKey();
            int quantityToDispense = entry.getValue(); // จำนวนที่ลูกค้าซื้อ

            // วนลูปจ่ายของตามจำนวนชิ้น (เช่น ซื้อ Lays 2 ห่อ ก็วนลูป 2 รอบ)
            for (int i = 0; i < quantityToDispense; i++) {
                if (!slot.isEmpty()) {
                    slot.dispense(); // ตัดสต็อกจริงใน ItemSlot
                    System.out.println("Dropped: " + slot.getProduct().getName());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    // (เผื่อกรณี Error แปลกๆ ที่สต็อกหมดกลางคัน)
                    System.out.println("Error: " + slot.getProduct().getName() + " is out of stock during dispense.");
                }
            }
        }
        System.out.println("------------------------");
    }

    /**
     * (สำหรับ VendingMachine.java)
     * ดึงรายการสินค้าทั้งหมดเพื่อไปแสดงผลที่หน้าจอ
     */
    public String getProductDisplay() {
        StringBuilder sb = new StringBuilder();
        // Header ตาราง
        // ส่วนหัวตู้
        sb.append(vendingmachine.ConsoleUI.CYAN);
        sb.append(" ___________________________________________________ \n");
        sb.append("|  VENDING MACHINE [OOP EDITION]              24/7  |\n");
        sb.append("|___________________________________________________|\n");
        sb.append(vendingmachine.ConsoleUI.RESET);

        // วาดชั้นวางสินค้า (Grid Layout)
        int count = 0;
        sb.append(vendingmachine.ConsoleUI.YELLOW + "| " + vendingmachine.ConsoleUI.RESET); // เปิดขอบซ้าย

        for (ItemSlot slot : slots.values()) {
            Product p = slot.getProduct();
            boolean hasStock = slot.getQuantity() > 0;

            // เลือกสี: มีของ=เขียว, หมด=แดง
            String color = hasStock ? vendingmachine.ConsoleUI.GREEN : vendingmachine.ConsoleUI.RED;
            String statusIcon = hasStock ? "[ITEM]" : "[ X  ]";

            // จัด Format สินค้าแต่ละชิ้นให้อยู่ในบล็อกขนาดเท่ากัน
            // รูปแบบ: [ CODE ] ชื่อ.. (ราคา)
            String itemBlock = String.format(
                    "%s%s %-4s %s: %-6s (%2.0fB)%s",
                    color, statusIcon, slot.getSlotCode(), vendingmachine.ConsoleUI.RESET,
                    p.getName(), p.getPrice(), vendingmachine.ConsoleUI.RESET);

            sb.append(itemBlock).append(" | "); // คั่นช่อง

            count++;
            // สมมติว่าตู้กว้าง 2 ช่อง (ถ้าครบ 2 ชิ้น ให้ขึ้นบรรทัดใหม่)
            if (count % 2 == 0) {
                sb.append("\n"); // จบบรรทัด
                sb.append(vendingmachine.ConsoleUI.CYAN + "|---------------------------------------------------|\n"
                        + vendingmachine.ConsoleUI.RESET);
                if (count < slots.size())
                    sb.append(vendingmachine.ConsoleUI.YELLOW + "| " + vendingmachine.ConsoleUI.RESET); // เปิดขอบบรรทัดถัดไป
            }
        }

        // ส่วนปิดท้ายตู้ (ช่องรับของ)
        sb.append(vendingmachine.ConsoleUI.CYAN);
        sb.append("|      [  PUSH  ]            [  INSERT COIN  ]      |\n");
        sb.append("|___________________________________________________|\n");
        sb.append(vendingmachine.ConsoleUI.RESET);

        return sb.toString();
    }

    // ใน InventoryManager.java
    public Map<String, ItemSlot> getSlots() {
        return this.slots;
    }

    // --- เมธอดสำหรับ AdminService ---
    public void restockSlot(String slotCode, int quantity) throws Exception {
        ItemSlot slot = findSlotByCode(slotCode); // ใช้เมธอดเดิมหา
        slot.restock(quantity);
    }

    public void updatePrice(String slotCode, double newPrice) throws Exception {
        ItemSlot slot = findSlotByCode(slotCode);
        slot.getProduct().setPrice(newPrice);
    }
}