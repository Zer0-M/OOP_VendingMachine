public class Snack extends Product {
    private double weight; // น้ำหนัก (g)

    public Snack(String name, double price, double weight) {
        super(name, price);
        this.weight = weight;
    }

    @Override
    public String getInfo() {
        return getName() + " (" + weight + " g) - " + getPrice() + " Baht";
    }
}
