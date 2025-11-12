package vendingmachine.items;
public class ItemSlot {
    private Product product;
    private int quantity;
    private int product_code;

    public ItemSlot(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public void addquantity(int num) {
        quantity += num;
    }

    public int getProduct_code() {
        return product_code;
    }


    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isEmpty() {
        return quantity <= 0; // return true-false
    }

    public boolean dispense() { // จ่ายสินค้าออก
        if (quantity > 0) {
            quantity--;
            return true;
        }
        return false;
    }
}