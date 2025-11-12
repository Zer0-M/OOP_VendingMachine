package vendingmachine.products;

import vendingmachine.exceptions.OutOfStockException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * คลาส "ผู้จัดการสต็อก" (Encapsulation)
 * Controller จะคุยกับคลาสนี้คลาสเดียวเรื่องสินค้าและสต็อก
 * ใช้ HashMap เก็บช่องสินค้า (Key=A1, Value=ItemSlot)
 */
public class InventoryManager {
    private Map<String, ItemSlot> slots = new HashMap<>();
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

    /* ตรวจสอบสต็อก (Controller เรียกก่อนเพิ่มลงตะกร้า) */
    public void checkStock(ItemSlot slot) throws OutOfStockException {
        if (slot.isEmpty()) {
            throw new OutOfStockException(slot.getProduct().getName() + " is sold out.");
        }
    }

    /* จ่ายของในตะกร้า (Controller เรียก *หลัง* จ่ายเงินสำเร็จ) */
    public void dispenseCart(List<ItemSlot> shoppingCart) {
        System.out.println("--- DISPENSING ITEMS ---");
        for (ItemSlot slot : shoppingCart) {
            slot.dispense();
            System.out.println("Dropped: " + slot.getProduct().getName());
        }
        System.out.println("------------------------");
    }

    /**
     * (สำหรับ VendingMachine.java)
     * ดึงรายการสินค้าทั้งหมดเพื่อไปแสดงผลที่หน้าจอ
     */
    public String getProductDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("                   === Our Product ===                    \n");
        sb.append("==========================================================\n");
        // (ในโลกจริงควรจัดกลุ่ม แต่ตอนนี้เรียงตามรหัสไปก่อน)
        for (ItemSlot slot : slots.values()) {
            sb.append(String.format("[%s] %-38s (%.2f Baht) [Stock: %d]\n",
                    slot.getSlotCode(),
                    slot.getProduct().getInfo(),
                    slot.getProduct().getPrice(),
                    slot.getQuantity()));
        }
        sb.append("==========================================================");
        return sb.toString();
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