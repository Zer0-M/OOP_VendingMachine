package vendingmachine.products;

import vendingmachine.exceptions.OutOfStockException;
import java.util.HashMap;
import java.util.Map; // ใช้ Map แทน List ในการรับพารามิเตอร์
import java.io.*;

/**
 * คลาส "ผู้จัดการสต็อก" (Encapsulation)
 * Controller จะคุยกับคลาสนี้คลาสเดียวเรื่องสินค้าและสต็อก
 * ใช้ HashMap เก็บช่องสินค้า (Key=A1, Value=ItemSlot)
 */
public class InventoryManager {
    private Map<String, ItemSlot> slots = new HashMap<>();
    private int nextProductId = 000;
    private static final String INVENTORY_FILE = "vendingmachine/products/inventory_data.txt";

    public void saveInventoryToFile() {
        try (java.io.FileWriter writer = new java.io.FileWriter(INVENTORY_FILE)) {
            // Format: SlotCode,Type,ID,Name,Price,Quantity,Size
            for (ItemSlot slot : slots.values()) {
                Product p = slot.getProduct();
                String type = (p instanceof Snack) ? "Snack" : "Drink";
                double size = 0.0;
                
                if (p instanceof Snack) size = ((Snack) p).getWeight();
                else if (p instanceof Drink) size = ((Drink) p).getVolume();

                String line = String.format("%s,%s,%d,%s,%.2f,%d,%.2f",
                        slot.getSlotCode(), type, p.getProductId(), p.getName(), 
                        p.getPrice(), slot.getQuantity(), size);
                
                writer.write(line + "\n");
            }
            System.out.println("✅ Inventory saved to " + INVENTORY_FILE);
        } catch (Exception e) {
            System.out.println("❌ Error saving inventory: " + e.getMessage());
        }
    }

    public void loadInventoryFromFile() {
        java.io.File file = new java.io.File(INVENTORY_FILE);
        if (!file.exists()) {
            System.out.println("⚠️ No saved inventory file found. Using default.");
            return;
        }

        try (java.util.Scanner scanner = new java.util.Scanner(file)) {
            // เคลียร์ของเก่าก่อนโหลดใหม่ (หรือจะใช้ method นี้แทน initializeInventory ก็ได้)
            slots.clear(); 
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");
                // Format: Code[0], Type[1], ID[2], Name[3], Price[4], Qty[5], Size[6]
                
                if (data.length == 7) {
                    String code = data[0];
                    String type = data[1];
                    int id = Integer.parseInt(data[2]); // ถ้าอยากใช้ ID เดิมก็แก้ constructor Product ให้รับ ID ได้
                    String name = data[3];
                    double price = Double.parseDouble(data[4]);
                    int qty = Integer.parseInt(data[5]);
                    double size = Double.parseDouble(data[6]);

                    Product p;
                    // *หมายเหตุ: ต้องแน่ใจว่า Constructor ของ Product/Snack รองรับการรับ ID 
                    // (ในโค้ดเดิม nextId() มัน Auto run อาจจะต้องปรับนิดหน่อยถ้าซีเรียสเรื่อง ID เดิม)
                    // แต่ในที่นี้เราสร้างใหม่ไปเลยเพื่อความง่าย
                    if (type.equals("Snack")) {
                        p = new Snack(nextId(), name, price, size); 
                    } else {
                        p = new Drink(nextId(), name, price, size);
                    }
                    
                    addSlot(code, p, qty);
                }
            }
            System.out.println("✅ Loaded inventory from file.");
        } catch (Exception e) {
            System.out.println("❌ Error loading inventory: " + e.getMessage());
        }
    }
    
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

    public void updateName(String slotCode, String newName) throws Exception {
        ItemSlot slot = findSlotByCode(slotCode);
        slot.getProduct().setName(newName);
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
        sb.append("┌──────┬──────────────────────────────┬─────────────┬───────┐\n");
        sb.append(String.format("│ %-4s │ %-28s │ %-11s │ %-5s │\n", "CODE", "PRODUCT NAME", "PRICE", "STOCK"));
        sb.append("├──────┼──────────────────────────────┼─────────────┼───────┤\n");
        // (ในโลกจริงควรจัดกลุ่ม แต่ตอนนี้เรียงตามรหัสไปก่อน)
        for (ItemSlot slot : slots.values()) {
            sb.append(String.format("│ [%s] │ %-28s │ %-11.2f │ %-5d │\n",
                    slot.getSlotCode(),
                    slot.getProduct().getInfo(),
                    slot.getProduct().getPrice(),
                    slot.getQuantity()));
        }
        sb.append("└──────┴──────────────────────────────┴─────────────┴───────┘\n");
        return sb.toString();
    }

    // ส่ง Map สินค้าออกไปให้ GUI วาดรูป
    public java.util.Map<String, ItemSlot> getSlots() {
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

    // เพิ่มเมธอดนี้ใน InventoryManager.java
    public void addNewProduct(String slotCode, String name, double price, int quantity, String type, double size)
            throws Exception {
        slotCode = slotCode.toUpperCase();
        if (slots.containsKey(slotCode)) {
            throw new Exception("Slot " + slotCode + " already exists!");
        }

        Product product;
        if (type.equalsIgnoreCase("Snack")) {
            product = new Snack(nextId(), name, price, size);
        } else if (type.equalsIgnoreCase("Drink")) {
            product = new Drink(nextId(), name, price, size);
        } else {
            throw new Exception("Invalid product type");
        }

        addSlot(slotCode, product, quantity);
    }
}