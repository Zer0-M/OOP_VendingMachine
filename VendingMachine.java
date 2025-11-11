import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class VendingMachine {
    private MoneyManager moneyManager;
    private LinkedHashMap<String, ItemSlot[]> ItemType;
    private ItemSlot[] allSlots;

    public VendingMachine() {
        this.ItemType = ItemManager.Initialize();
        this.moneyManager = new MoneyManager();
        this.allSlots = new ItemSlot[];

        for (List<ItemSlot> categoryList : ItemType.values()) {
                allSlots.addAll(categoryList);
        }
    }

    public void displayProducts() {
        System.out.println("=== Our Product ===");

        ItemType.forEach((key, value) -> {
            System.out.println(key + ":");
            for (int j = 0; j < value.length; j++) {
                System.out.println(value[j].getProduct().getInfo() +
                        " | Remain: " + value[j].getQuantity());
            }
        });
    }

    public void selectProduct(int index) {
        // เช็คว่าสินค้านั้นมีอยู่จริงมั้ย
        ItemType.forEach((key, value) -> {
            
        });

        // ดึงสินค้าตัวนั้นทาเก็บใน slot
        // ItemSlot slot = slots[index - 1]; //ต้องมาแก้แน่ๆ

        // //เช็คว่าของมันยังมีอยู่มั้ย
        // if (slot.isEmpty()) {
        // System.out.println("Not have!");
        // return;
        // }

        // double price = slot.getProduct().getPrice();
        // if (moneyManager.pay(price)) {
        // slot.dispense();
        // System.out.println("Buy " + slot.getProduct().getName() + " Successfull!");
        // }
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
        System.out.println("[1] Pick Product");
        System.out.println("[2] Select Payment Method");
        System.out.println("[3] Insert Money");
        System.out.println("[4] Receive Change");
        System.out.println("[5] Log Out\n");

        // เลือกสินค้า
        System.out.print("Select num of Product: ");
        int index = sc.nextInt();
        vm.selectProduct(index);

        // รับเงิน
        System.out.println("Please select payment method to add funds:");
        System.out.println("  [1] Coins");
        System.out.println("  [2] Banknote");
        System.out.println("  [3] QR Scan (Top-up)");
        System.out.print("Select method: ");
        int choice = sc.nextInt();

        PaymentReceiver selectedPaymentMethod; // สร้างตัวแปร Interface

        if (choice == 1) {
            selectedPaymentMethod = new CoinReceiver();
        } else if (choice == 2) {
            selectedPaymentMethod = new BanknoteReceiver();
        } else if (choice == 3) {
            selectedPaymentMethod = new QRPaymentReceiver();
        } else {
            System.out.println("Invalid method.");
            sc.close();
            return;
        }
        vm.addFunds(selectedPaymentMethod);

        vm.returnChange();

        System.out.println("Thank you!");
        sc.close();
        return;
    }

    public void addFunds(PaymentReceiver method) {
        moneyManager.addFunds(method);
    }
}
