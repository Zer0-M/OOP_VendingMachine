package vendingmachine.payment;

import java.util.Scanner;

import vendingmachine.exceptions.InsufficientFundsException;

public class CoinReceiver implements PaymentReceiver {
    @Override
    public PaymentResult receivePayment(double totalAmount) throws InsufficientFundsException {
        // (สำคัญ) เราสร้าง Scanner ของตัวเองชั่วคราว
        // เพื่อให้ Logic การรับเงิน Encapsulate อยู่ในคลาสนี้
        Scanner scanner = new Scanner(System.in);
        double amountInserted = 0.0;
        
        System.out.println("--- Coin Payment ---");
        System.out.println("Please insert coins. (Accepted: 1, 2, 5, 10)");
        System.out.println("Type 'C' to cancel payment.");

        while (amountInserted < totalAmount) {
            System.out.printf("Required: %.2f | Inserted: %.2f\n", totalAmount, amountInserted);
            System.out.print("Insert coin: ");
            String input = scanner.nextLine().toUpperCase();
            
            switch (input) {
                case "1": amountInserted += 1.0; break;
                case "2": amountInserted += 2.0; break;
                case "5": amountInserted += 5.0; break;
                case "10": amountInserted += 10.0; break;
                case "C":
                    throw new InsufficientFundsException("Payment cancelled by user.");
                default:
                    System.out.println("Invalid coin. Please try again.");
            }
        }
        
        double changeDue = amountInserted - totalAmount;
        System.out.printf("Payment complete. Inserted: %.2f, Change: %.2f\n", amountInserted, changeDue);
        
        return new PaymentResult(true, changeDue);
    }

    @Override
    public String getPaymentMethodName() {
        return "Coin Payment";
    }

    @Override
    public boolean isCashPayment() {
        return true; // เป็นเงินสด
    }
}