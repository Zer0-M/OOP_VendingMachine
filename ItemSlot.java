public class ItemSlot {
    private Product product;
    private int quantity;

    public ItemSlot(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isEmpty() {
        return quantity <= 0; //return true-false
    }

    public boolean dispense() { //จ่ายสินค้าออก
        if (quantity > 0) {
            quantity--;
            return true;
        }
        return false;
    }
}
