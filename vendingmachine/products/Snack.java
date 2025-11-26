package vendingmachine.products;

public class Snack extends Product {
    private final double weight; // น้ำหนัก (g)

    public Snack(int productID, String name, double price, double weight) {
        super(productID, name, price);
        this.weight = weight;
    }

    @Override
    public String getInfo() {
        return getName() + " (" + weight + " g)";
    }

    public double getWeight() {
        return this.weight;
    }
}
