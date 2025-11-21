package vendingmachine;
import java.util.Scanner;

public class VendingMachine {
    private final VendingMachineController controller;
    private final Scanner scanner;

    public VendingMachine() {
        this.controller = new VendingMachineController();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        // Loop ใหญ่: ตู้เปิดทำงานตลอดเวลา (Machine On)
        while (true) {
            System.out.println("\n==================== Vending Machine =====================");
            System.out.println(controller.getDisplayProducts()); // โชว์สินค้าใหม่ทุกรอบ (เผื่อสต็อกลด)
            System.out.println("Type 'OK' to Confirm Order | Type 'OFF' to Shutdown");

            boolean isSelecting = true; // ตัวแปรสำหรับ Loop การเลือกของ (Session)

            // Loop เล็ก: ลูกค้าคนปัจจุบันกำลังเลือกของ
            while (isSelecting) {
                controller.getCart(); // แสดงตะกร้าปัจจุบัน

                System.out.print("Select item (or OK): ");
                String input = scanner.nextLine().toUpperCase().trim();

                if (input.equals("OFF")) {
                    System.out.println("Shutting down system...");
                    return; // จบโปรแกรมทันที
                }

                if (input.equals("OK")) {
                    // จบการเลือกของ -> ไปจ่ายเงิน
                    isSelecting = false; 
                    startPaymentProcess();
                    // พอจ่ายเงินเสร็จ (หรือจ่ายไม่สำเร็จ) มันจะหลุด Loop นี้ 
                    // แล้วไปเริ่ม Loop ใหญ่ใหม่ (ต้อนรับลูกค้าคนต่อไป)
                } 
                else if (controller.hasProductsID(input)) {
                    // เลือกสินค้า
                    String result = controller.addItemToCart(input);
                    System.out.println(result);
                } 
                else {
                    System.out.println("Invalid Product Code. Please try again.");
                }
            }
            
            // (Optional) อาจจะใส่ Delay หรือเคลียร์หน้าจอนิดหน่อยเพื่อให้รู้ว่าจบรายการแล้ว
            System.out.println("\n\n--- Next Customer ---\n");
        }
    }

    private void startPaymentProcess() {
        double total = controller.getCartTotal();
        
        if (total == 0) {
            System.out.println("Your cart is empty. Transaction cancelled.");
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
            System.out.println("Payment Successful!");
            
            // 4. สะสมแต้ม
            System.out.print("Enter phone number for points (or press Enter to skip): ");
            String phone = scanner.nextLine();
            if (!phone.isEmpty()) {
                String pointsResult = controller.applyPoints(phone);
                System.out.println(pointsResult);
            }
            
            // *** สำคัญ: ต้องเคลียร์ตะกร้าเสมอหลังจ่ายเสร็จ ***
            controller.clearCart(); 
            System.out.println("Thank you! Please come again.");
        } else {
            System.out.println("Payment Failed. Transaction Cancelled.");
            // กรณีจ่ายไม่ผ่าน ก็ควรเคลียร์ตะกร้าเพื่อเริ่มใหม่ (หรือจะเก็บไว้ก็ได้ แล้วแต่นโยบาย)
            // แต่ปกติถ้าจบ Transaction แล้วควรเคลียร์
            controller.clearCart(); 
        }
    }

    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.run();
    }
}