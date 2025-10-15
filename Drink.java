public class Drink extends Product {
    private double volume; // ปริมาตร (ml)

    public Drink(String name, double price, double volume) {
        super(name, price);
        this.volume = volume;
    }

    @Override
    public String getInfo() {
        return getName() + " (" + volume + " ml) - " + getPrice() + " Baht";
    }
}
