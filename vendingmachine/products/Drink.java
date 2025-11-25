package vendingmachine.products;

public class Drink extends Product {
    private int volume;

    public Drink(int id, String name, double price, int volume) {
        super(id, name, price);
        this.volume = volume;
    }

    @Override
    public String getInfo() {
        return getName() + " (" + volume + "ml)";
    }
}