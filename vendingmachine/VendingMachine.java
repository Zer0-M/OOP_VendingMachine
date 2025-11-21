package vendingmachine;
import java.util.Scanner;
import vendingmachine.products.ItemSlot;

public class VendingMachine {
    private final VendingMachineController controller;
    private final Scanner scanner;

    public VendingMachine() {
        this.controller = new VendingMachineController();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean isRunning = true;
        System.out.println("\n==================== Vending Mechine =====================");
        System.out.println(controller.getDisplayProducts()); // โชว์สินค้าทั้งหมด
        System.out.println("Press OK to SubmitOrder");

        while (isRunning) {

            if(!controller.getCart().isEmpty()){
                System.out.print("CurrentCart: [");

                for (ItemSlot item : controller.getCart()) {
                    System.out.print(item.getProduct().getName() + " | ");
                }
                System.out.println("]");
            }

            System.out.print("Select item: "); // เลือกสินค้า
            String input = scanner.nextLine().toUpperCase();

            if (controller.hasProductsID(input)) { // เช็คว่ามีของจริงมั้ย?
                String shoppingcart = controller.addItemToCart(input);
                System.out.println(shoppingcart);
            } else if (input.equals("OK")) { // เลือกสินค้าเสร็จไปขั้นตอนถัดไป
                isRunning = false;
                startPaymentProcess();
            } else {
                System.out.println("Invalid Product.");
            }
        }
    }

    private void startPaymentProcess() {
        double total = controller.getCartTotal(); // รับเงินรวมมา
        if (total == 0) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("Total due: " + total + " Baht");

        // 2. เลือกวิธีการจ่ายเงิน
        System.out.println("Payment Option \n[1] QR \n[2] Coin \n[3] Banknote");
        System.out.print("Select payment: ");
        String paymentChoice = scanner.nextLine();

        // 3. จ่ายเงิน
        boolean ispaid = controller.processPayment(total, paymentChoice);

        if (ispaid) {
            // 4. สะสมแต้ม
            System.out.println("Payment Successful!");
            System.out.print("Enter phone number for points (or press Enter to skip): ");
            String phone = scanner.nextLine();
            if (!phone.isEmpty()) { // ถ้ารับแต้ม
                String pointsResult = controller.applyPoints(phone); // ส่งไปเพิ่มคะแนน
                System.out.println(pointsResult); // คะแนนหลังเพิ่ม หรือ Error
            }
            controller.clearCart(); // เคลียร์ตะกร้า
        } else {
            System.out.println("Payment Failed. Please try again."); // ถ้าใส่มั่ว
        }
    }

    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.run(); // สั่งตู้ให้เริ่มทำงาน
    }
}