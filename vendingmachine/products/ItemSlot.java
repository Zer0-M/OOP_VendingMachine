package vendingmachine.products; // 👈 บรรทัดนี้ต้องมี!

public class ItemSlot implements Comparable<ItemSlot> {
    private String slotCode;
    private Product product;
    private int quantity;

    public ItemSlot(String slotCode, Product product, int quantity) {
        this.slotCode = slotCode;
        this.product = product;
        this.quantity = quantity;
    }

    public String getSlotCode() { return slotCode; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    public boolean isEmpty() { return quantity <= 0; }

    public void dispense() {
        if (quantity > 0) quantity--;
    }
    
    public void dispense(int amount) {
        if (quantity >= amount) quantity -= amount;
    }

    public void restock(int amount) {
        this.quantity += amount;
    }

    @Override
    public int compareTo(ItemSlot other) {
        return this.slotCode.compareTo(other.slotCode);
    }
}