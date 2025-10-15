import java.util.Scanner;

public class VendingMachine {
    private ItemSlot[] slots;
    private MoneyManager moneyManager;

    public VendingMachine() {
        moneyManager = new MoneyManager();
        slots = new ItemSlot[4]; // ตัวอย่างมี 4 ช่อง

        // ตั้งค่าสินค้า
        slots[0] = new ItemSlot(new Drink("Coke", 15.0, 330), 5);
        slots[1] = new ItemSlot(new Drink("Water", 10.0, 500), 5);
        slots[2] = new ItemSlot(new Snack("Chips", 20.0, 100), 3);
        slots[3] = new ItemSlot(new Snack("Cookie", 12.0, 50), 4);
    }

    public void displayProducts() {
        System.out.println("=== Our Product ===");
        for (int i = 0; i < slots.length; i++) {
            ItemSlot slot = slots[i];
            System.out.println((i + 1) + ". " + slot.getProduct().getInfo() +
                    " | Remain: " + slot.getQuantity());
        }
    }

    public void insertCoin(double amount) {
        moneyManager.insertCoin(amount);
    }

    public void selectProduct(int index) {
        if (index < 1 || index > slots.length) {
            System.out.println("Fault Product");
            return;
        }

        ItemSlot slot = slots[index - 1];

        if (slot.isEmpty()) {
            System.out.println("Not have!");
            return;
        }

        double price = slot.getProduct().getPrice();

        if (moneyManager.pay(price)) {
            slot.dispense();
            System.out.println("Buy " + slot.getProduct().getName() + " Successfull!");
        }
    }

    public void returnChange() {
        double change = moneyManager.returnChange();
        if (change > 0) {
            System.out.println("Receive Change: " + change + " Baht");
        } else {
            System.out.println("No money left.");
        }
    }

    public static void main(String[] args) {
    VendingMachine vm = new VendingMachine();
    Scanner sc = new Scanner(System.in);
        
    System.out.println("\n=== Vending Mechine ===");
    vm.displayProducts();
    System.out.println("\n=== Step for Using Mechine ===");
    System.out.println("[1] Insert Coins");
    System.out.println("[2] Pick Product");
    System.out.println("[3] Receive Change");
    System.out.println("[0] Log Out\n");

    System.out.print("Insert Coins: ");
    double coin = sc.nextDouble();
    vm.insertCoin(coin);


    System.out.print("Select num of Product: ");
    int index = sc.nextInt();
    vm.selectProduct(index);

    vm.returnChange();

    System.out.println("Thank you!");
    sc.close();
    return;
    }
}
