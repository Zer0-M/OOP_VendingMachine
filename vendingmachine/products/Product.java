package vendingmachine.products;

public abstract class Product {
    private final int productID;
    private String productName;
    private double productPrice;

    public Product(int productID, String productName, double productPrice) {
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public void setName(String newName) {
        this.productName = newName;
    }

    public String getName() {
        return productName;
    }

    public double getPrice() {
        return productPrice;
    }

    public int getProductId() {
        return productID;
    }

    public void setPrice(double newPrice) {
        this.productPrice = newPrice;
    }

    public abstract String getInfo();
}
