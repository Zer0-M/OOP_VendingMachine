public class Snack extends Product {
    private double weight; // น้ำหนัก (g)

    public Snack(int product_code, String name, double price, double weight) {
        super(product_code, name, price);
        this.weight = weight;
    }

    @Override
    public String getInfo() {
        return getName() + " (" + weight + " g) - " + getPrice() + " Baht";
    }
}
