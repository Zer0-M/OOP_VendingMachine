package vendingmachine.products;

import vendingmachine.exceptions.OutOfStockException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class InventoryManager {
    
    private static InventoryManager instance;
    private Map<String, ItemSlot> slots;
    private Map<ItemSlot, Integer> cart; 
    
    // 🔥 แก้ตรงนี้: เปลี่ยนจาก 000 (String format) เป็น int ธรรมดา
    private int nextProductId = 1;

    private InventoryManager() {
        slots = new TreeMap<>(); 
        cart = new HashMap<>();
        initializeInventory();
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    private void initializeInventory() {
        // 🔥 ตอนเรียก nextId() มันจะได้ int แล้ว ไม่แดงแน่นอน
        addSlot("A1", new Snack(nextId(), "Lays", 20.0, 330), 10);
        addSlot("A2", new Snack(nextId(), "Testo", 15.0, 300), 5);

        addSlot("B1", new Drink(nextId(), "Coke", 25.0, 500), 10);
        addSlot("B2", new Drink(nextId(), "Water", 10.0, 600), 20);
    }

    private void addSlot(String code, Product product, int quantity) {
        ItemSlot slot = new ItemSlot(code, product, quantity);
        this.slots.put(code, slot);
    }

    // 🔥 แก้ตรงนี้: เปลี่ยน Return Type เป็น int
    private int nextId() {
        return nextProductId++;
    }

    public Map<String, ItemSlot> getSlots() {
        return slots;
    }

    public Map<ItemSlot, Integer> getCart() {
        return cart;
    }

    public void clearCart() {
        cart.clear();
    }

    public double calculateTotal() {
        double total = 0;
        for (Map.Entry<ItemSlot, Integer> entry : cart.entrySet()) {
            total += entry.getKey().getProduct().getPrice() * entry.getValue();
        }
        return total;
    }

    public void addToCart(String slotCode) throws Exception {
        ItemSlot slot = findSlotByCode(slotCode);
        int currentInCart = cart.getOrDefault(slot, 0);
        checkStock(slot, currentInCart); 
        cart.put(slot, currentInCart + 1);
    }

    public ItemSlot findSlotByCode(String slotCode) throws Exception {
        ItemSlot slot = slots.get(slotCode.toUpperCase());
        if (slot == null) {
            throw new Exception("Invalid slot code: " + slotCode);
        }
        return slot;
    }

    public void checkStock(ItemSlot slot, int pendingInCart) throws OutOfStockException {
        int availableToPick = slot.getQuantity() - pendingInCart;
        if (availableToPick <= 0) {
            throw new OutOfStockException("Not enough stock for " + slot.getProduct().getName());
        }
    }

    public void dispenseCart(Map<ItemSlot, Integer> shoppingCart) {
        System.out.println("--- DISPENSING ITEMS ---");
        for (Map.Entry<ItemSlot, Integer> entry : shoppingCart.entrySet()) {
            ItemSlot slot = entry.getKey();
            int quantityToDispense = entry.getValue();
            for (int i = 0; i < quantityToDispense; i++) {
                if (!slot.isEmpty()) {
                    slot.dispense();
                    System.out.println("Dropped: " + slot.getProduct().getName());
                }
            }
        }
        System.out.println("------------------------");
    }
    
    public void restockSlot(String slotCode, int quantity) throws Exception {
        if (!slots.containsKey(slotCode)) throw new Exception("Slot not found");
        slots.get(slotCode).restock(quantity);
    }

    public void updatePrice(String slotCode, double newPrice) throws Exception {
        if (!slots.containsKey(slotCode)) throw new Exception("Slot not found");
        slots.get(slotCode).getProduct().setPrice(newPrice);
    }
    
    public String getProductDisplay() {
       StringBuilder sb = new StringBuilder();
       for (ItemSlot slot : slots.values()) {
           sb.append(String.format("[%s] %-15s Price: %.2f\n", slot.getSlotCode(), slot.getProduct().getName(), slot.getProduct().getPrice()));
       }
       return sb.toString();
    }

    // 🔥 เมธอดใหม่: ลบรายการออกจากตะกร้า (Remove Item from Cart)
    public void removeItemFromCart(String slotCode) {
        try {
            // 1. ค้นหา ItemSlot จากรหัส (เช่น "A1")
            ItemSlot slot = findSlotByCode(slotCode);
        
            // 2. ถ้าเจอตะกร้าที่มีสินค้านี้อยู่
            if (cart.containsKey(slot)) {
                // 3. ลบ Key นี้ออกจาก Map ทันที (ทำให้รายการหายไปทั้งบรรทัด)
                cart.remove(slot); 
            
                // Note: สต็อกจริง (slot.quantity) จะยังเท่าเดิม ไม่ถูกแตะต้องในขั้นตอนนี้
            }
        } catch (Exception e) {
            System.out.println("Error removing item: " + e.getMessage());
        }
    }  
}