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
        System.out.println("=== สินค้าที่มีในตู้ ===");
        for (int i = 0; i < slots.length; i++) {
            ItemSlot slot = slots[i];
            System.out.println((i + 1) + ". " + slot.getProduct().getInfo() +
                    " | คงเหลือ: " + slot.getQuantity());
        }
    }

    public void insertCoin(double amount) {
        moneyManager.insertCoin(amount);
    }

    public void selectProduct(int index) {
        if (index < 1 || index > slots.length) {
            System.out.println("ช่องสินค้าที่เลือกไม่ถูกต้อง");
            return;
        }

        ItemSlot slot = slots[index - 1];

        if (slot.isEmpty()) {
            System.out.println("สินค้าหมด!");
            return;
        }

        double price = slot.getProduct().getPrice();

        if (moneyManager.pay(price)) {
            slot.dispense();
            System.out.println("✅ ซื้อ " + slot.getProduct().getName() + " สำเร็จ!");
        }
    }

    public void returnChange() {
        double change = moneyManager.returnChange();
        if (change > 0) {
            System.out.println("💸 รับเงินทอน: " + change + "฿");
        } else {
            System.out.println("ไม่มีเงินคงเหลือ");
        }
    }

    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== ตู้ขายของอัตโนมัติ ===");
            vm.displayProducts();
            System.out.println("[1] ใส่เหรียญ");
            System.out.println("[2] เลือกสินค้า");
            System.out.println("[3] รับเงินทอน");
            System.out.println("[0] ออกจากระบบ");
            System.out.print("เลือกเมนู: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("ใส่จำนวนเงิน: ");
                    double coin = sc.nextDouble();
                    vm.insertCoin(coin);
                    break;
                case 2:
                    System.out.print("เลือกสินค้าหมายเลข: ");
                    int index = sc.nextInt();
                    vm.selectProduct(index);
                    break;
                case 3:
                    vm.returnChange();
                    break;
                case 0:
                    System.out.println("ขอบคุณที่ใช้บริการ!");
                    sc.close();
                    return;
                default:
                    System.out.println("เมนูไม่ถูกต้อง");
            }
        }
    }
}
