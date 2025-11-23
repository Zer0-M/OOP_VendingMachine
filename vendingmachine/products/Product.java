package vendingmachine.products; // 👈 ต้องมี

public abstract class Product {
    private int id; // แก้เป็น int ตาม InventoryManager
    private String name;
    private double price;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public abstract String getInfo(); // Abstract method
}