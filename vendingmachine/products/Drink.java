package vendingmachine.products;

public class Drink extends Product {
    private double volume; // ปริมาตร (ml)

    public Drink(int product_code, String name, double price, double volume) {
        super(product_code, name, price);
        this.volume = volume;
    }

    @Override
    public String getInfo() {
        return getName() + " (" + volume + " ml)";
    }

    // [NEW] Getter สำหรับดึงปริมาตรไปบันทึกไฟล์
    public double getVolume() {
        return this.volume;
    }
}
