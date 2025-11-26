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
        while (true) {
            System.out.println("=== Welcome to the Vending Machine ===");
            System.out.println("Type 'OK' to Confirm Order | Type 'OFF' to Shutdown");

            boolean isSelecting = true;

            // ลูกค้าคนปัจจุบันกำลังเลือกของ
            while (isSelecting) {
                controller.getCart();
        
                System.out.print("Select item (or OK): ");
                String input = scanner.nextLine().toUpperCase().trim();

                if (input.equals("OFF")) {
                    System.out.println("Shutting down system...");
                    return;
                }
                
                if (input.equals("OK")) {
                    isSelecting = false;
                    startPaymentProcess();

                } else if (controller.hasProductsID(input)) {
                    String result = controller.addItemToCart(input);
                    System.out.println(result);
                } else {
                    System.out.println("Invalid Product Code. Please try again.");
                }
            }

            System.out.println("\n\n--- Next Customer ---\n");
        }
    }

    private void startPaymentProcess() {
        double total = controller.getCartTotal();

        if (total == 0) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("Total : " + total + " Baht");

        while (true) {
            System.out.println("Payment Option \n[1] QR \n[2] Cash Payment \n[0] Cancel Transaction");
            System.out.print("Select payment: ");
            String paymentChoice = scanner.nextLine();

            if (!paymentChoice.equals("1") && !paymentChoice.equals("2") 
                    && !paymentChoice.equals("0")) {
                System.out.println("Invalid payment option. Please try again.");
                continue;
            }
            if (paymentChoice.equals("0")) {
                System.out.println("Transaction cancelled.");
                return;
            }

            boolean ispaid = controller.processPayment(total, paymentChoice);

            if (ispaid) {
                System.out.println("Payment Successful!");

                while (true) {
                    System.out.print("Enter phone number for points (or press Enter to skip): ");
                    String phone = scanner.nextLine().trim();

                    if (phone.equals("")) {
                        break;
                    }

                    if (controller.isValidThaiMobilePhone(phone)) {
                        String pointsResult = controller.applyPoints(phone);
                        System.out.println(pointsResult);
                        break;
                    } else {
                        System.out.println("Invalid Phone Number Format! Must be a valid 10-digit Thai Mobile (starting with 06, 08, or 09). Example: 0812345678");
                    }
                }

                controller.clearCart();
                System.out.println("Thank you! Please come again.");
                return;
            } else {
                System.out.println("Payment Failed. Transaction Cancelled.");
                controller.clearCart();
                return;
            }
        }
    }

    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.run();
    }
}