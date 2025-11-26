package vendingmachine.products;

public class ItemSlot {
    private final String slotCode;
    private final Product product;
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

    // --- Other ---
    public void dispense() {
        if (!isEmpty()) {
            this.quantity--;
        }
    }

    public void restock(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }
}