import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class VendingMachine {
    private MoneyManager moneyManager;
    private LinkedHashMap<String, ItemSlot[]> ItemType;
    private List<ItemSlot> allSlots;
    private List<Integer> selectProduct;

    public VendingMachine() {
        this.ItemType = ItemManager.Initialize();
        this.moneyManager = new MoneyManager();
        this.allSlots = new ArrayList<>();

        for (ItemSlot[] categoryList : ItemType.values()) {
                allSlots.addAll(List.of(categoryList));
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

    public void selectProduct(int productID) {
        // เช็คว่าสินค้านั้นมีอยู่จริงมั้ย
        for (ItemSlot slot : allSlots) {
            if (slot.getProduct().product_code() == productID) {
                //เช็คว่าของมันยังมีอยู่มั้ย
                if (slot.isEmpty()) {
                    System.out.println("Not have!");
                    return;
                }
                selectProduct.add(productID);
                System.out.println("You have selected: " + slot.getProduct().getName());
                
                // double price = slot.getProduct().getPrice();
                // if (moneyManager.pay(price)) {
                //     slot.dispense();
                //     System.out.println("Buy " + slot.getProduct().getName() + " Successfull!");
                // }
                // return;
            }
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
        System.out.println("[1] Pick Product");
        System.out.println("[2] Select Payment Method");
        System.out.println("[3] Insert Money");
        System.out.println("[4] Receive Change");
        System.out.println("[5] Log Out\n");

        // เลือกสินค้า
        System.out.print("Select ID of Product Code: ");
        
        int productID = sc.nextInt();
        vm.selectProduct(productID);

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
