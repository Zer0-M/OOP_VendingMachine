package vendingmachine.products;

import vendingmachine.exceptions.OutOfStockException;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

// คลาส "ผู้จัดการสต็อก" (Encapsulation)
// Controller จะคุยกับคลาสนี้คลาสเดียวเรื่องสินค้าและสต็อก
public class InventoryManager {
    private static final String INVENTORY_FILE = "vendingmachine/products/inventory_data.txt";
    // เก็บช่องสินค้า <Key=A1, Value=ItemSlot>
    private final Map<String, ItemSlot> active_slots = new TreeMap<>();
    private int nextProductId = 0;

    public void updateName(String slotCode, String newName) throws Exception {
        ItemSlot slot = findSlotByCode(slotCode);
        slot.getProduct().setName(newName);
    }

    // หา ItemSlot จากรหัสช่องสินค้า
    public ItemSlot findSlotByCode(String slotCode) throws Exception {
        ItemSlot slot = active_slots.get(slotCode.toUpperCase());
        if (slot == null) {
            throw new Exception("Invalid slot code: " + slotCode);
        }
        return slot;
    }

    // ตรวจสอบสต็อกโดยคำนึงถึงของในตะกร้าด้วย
    // pendingInCart = จำนวนที่ลูกค้ารายนี้หยิบใส่ตะกร้าไปแล้ว
    public void checkStock(ItemSlot slot, int pendingInCart) throws OutOfStockException {
        // สต็อกที่เหลือให้หยิบ = สต็อกจริง - ของที่คาอยู่ในตะกร้า
        int availableToPick = slot.getQuantity() - pendingInCart;

        if (availableToPick <= 0) {
            throw new OutOfStockException("Not enough stock for " + slot.getProduct().getName()
                    + " (Stock: " + slot.getQuantity() + ")");
        }
    }

    // จ่ายของในตะกร้า (Controller เรียกหลังจ่ายเงินสำเร็จ)
    public void dispenseCart(Map<ItemSlot, Integer> shoppingCart) {
        System.out.println("--- DISPENSING ITEMS ---");

        // วนลูปสินค้าแต่ละชนิดใน Map
        for (Map.Entry<ItemSlot, Integer> entry : shoppingCart.entrySet()) {
            ItemSlot slot = entry.getKey();
            int quantityToDispense = entry.getValue(); // จำนวนที่ลูกค้าซื้อ

            // วนลูปจ่ายของตามจำนวนชิ้น
            for (int i = 0; i < quantityToDispense; i++) {
                if (!slot.isEmpty()) {
                    slot.dispense(); // ตัดสต็อกจริงใน ItemSlot
                    System.out.println("[System] Dropped: " + slot.getProduct().getName());
                    // จำลองเวลาจ่ายของ
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    // (เผื่อกรณี Error แปลกๆ ที่สต็อกหมดกลางคัน)
                    System.out.println(
                            "[System] Error: " + slot.getProduct().getName() + " is out of stock during dispense.");
                }
            }
        }
        System.out.println("------------------------");
    }

    // ------------------------ AdminService ----------------------------
    public void restockSlot(String slotCode, int quantity) throws Exception {
        ItemSlot slot = findSlotByCode(slotCode);
        slot.restock(quantity);
    }

    public void updatePrice(String slotCode, double newPrice) throws Exception {
        ItemSlot slot = findSlotByCode(slotCode);
        slot.getProduct().setPrice(newPrice);
    }

    private int nextId() {
        return nextProductId++;
    }

    public void addNewProduct(String slotCode, String name, double price, int quantity, String type, double size)
            throws Exception {
        slotCode = slotCode.toUpperCase();

        // ตรวจสอบว่าช่องสินค้านี้มีอยู่แล้วหรือไม่
        if (active_slots.containsKey(slotCode)) {
            throw new Exception("[System] Slot " + slotCode + " already exists!");
        }

        // สร้างสินค้าใหม่ตามประเภท
        Product product = null;
        if (type.equalsIgnoreCase("Snack")) {
            product = new Snack(nextId(), name, price, size);
        } else if (type.equalsIgnoreCase("Drink")) {
            product = new Drink(nextId(), name, price, size);
        }

        // เพิ่มช่องสินค้าใหม่
        addSlot(slotCode, product, quantity);
    }

    private void addSlot(String code, Product product, int quantity) {
        ItemSlot slot = new ItemSlot(code, product, quantity);
        this.active_slots.put(code, slot);
    }

    // ------------------------ Save/Load Inventory ------------------------
    public void saveInventoryToFile() {
        try (FileWriter writer = new FileWriter(INVENTORY_FILE)) {
            for (ItemSlot slot : active_slots.values()) {
                Product product = slot.getProduct(); // ดึงสินค้าออกมา
                String type = (product instanceof Snack) ? "Snack" : "Drink"; // ตรวจสอบประเภทสินค้า
                double size = 0.0;

                if (product instanceof Snack)
                    size = ((Snack) product).getWeight();
                else if (product instanceof Drink)
                    size = ((Drink) product).getVolume();

                // Format: CodeSlot[0],ProductID[1],Name[2],Size[3],Price[4],Type[5],Qty[6]
                String line = String.format("%s,%d,%s,%.2f,%.2f,%s,%d",
                        slot.getSlotCode(), product.getProductId(), product.getName(), size, product.getPrice(), type,
                        slot.getQuantity());
                writer.write(line + "\n");
            }
            System.out.println("[System] Inventory saved to " + INVENTORY_FILE);
        } catch (Exception e) {
            System.out.println("[System] Error saving inventory: " + e.getMessage());
        }
    }

    public void loadInventoryFromFile() {
        File file = new File(INVENTORY_FILE);
        if (!file.exists()) {
            System.out.println("[System] No saved inventory file found. Using default.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            active_slots.clear();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");
                
                // Format: CodeSlot[0],ProductID[1],Name[2],Size[3],Price[4],Type[5],Qty[6]
                if (data.length == 7) {
                    String code = data[0];
                    int productId = Integer.parseInt(data[1]);
                    String name = data[2];
                    double size = Double.parseDouble(data[3]);
                    double price = Double.parseDouble(data[4]);
                    String type = data[5];
                    int qty = Integer.parseInt(data[6]);

                    Product product;
                    if (type.equals("Snack")) {
                        product = new Snack(productId, name, price, size);
                    } else {
                        product = new Drink(productId, name, price, size);
                    }

                    addSlot(code, product, qty);
                }
            }
            System.out.println("[System] Loaded inventory from file.");
        } catch (Exception e) {
            System.out.println("[System] Error loading inventory: " + e.getMessage());
        }
    }

    public Map<String, ItemSlot> getActiveSlots() {
        return this.active_slots;
    }
}