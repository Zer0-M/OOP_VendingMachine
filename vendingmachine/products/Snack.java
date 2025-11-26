package vendingmachine.products;

public class Snack extends Product {
    private double weight; // น้ำหนัก (g)

    public Snack(int productID, String name, double price, double weight) {
        super(productID, name, price);
        this.weight = weight;
    }

    @Override
    public String getInfo() {
        return getName() + " (" + weight + " g)";
    }

    // [NEW] Getter สำหรับดึงน้ำหนักไปบันทึกไฟล์
    public double getWeight() {
        return this.weight;
    }
}
