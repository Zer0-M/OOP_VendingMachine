public abstract class Product {
    private int product_code;
    private String name;
    private double price;

    public Product(int product_code, String name, double price) {
        this.product_code = product_code;
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int product_code() { return product_code; }

    public abstract String getInfo();
}
