package vendingmachine.products;

public class Snack extends Product {
    private int weight;

    public Snack(int id, String name, double price, int weight) {
        super(id, name, price);
        this.weight = weight;
    }

    @Override
    public String getInfo() {
        return getName() + " (" + weight + "g)";
    }
}