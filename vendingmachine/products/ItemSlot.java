package vendingmachine.products;

/**
 * คลาส "ช่องเก็บของ" (เช่น A1, A2)
 * ห่อหุ้ม (Encapsulate) สินค้า (Product) และ จำนวนคงเหลือ (Quantity)
 */
public class ItemSlot {
    private String slotCode;
    private Product product;
    private int quantity;

    public ItemSlot(String slotCode, Product product, int quantity) {
        this.slotCode = slotCode;
        this.product = product;
        this.quantity = quantity;
    }

    // --- Getters ---
    public String getSlotCode() {
        return slotCode;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isEmpty() {
        return quantity == 0;
    }

    /**
     * เมธอดสำหรับ "จ่ายของ" (ลดสต็อก)
     * (Encapsulation: Controller สั่งจ่ายของ แต่การลดสต็อกจริงเกิดขึ้นที่นี่)
     */
    public void dispense() {
        if (!isEmpty()) {
            this.quantity--;
        }
    }
    
    /**
     * เมธอดสำหรับ Admin (เติมของ)
     */
    public void restock(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }
}