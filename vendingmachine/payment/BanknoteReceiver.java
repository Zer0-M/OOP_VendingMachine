package vendingmachine.payment;

import java.util.Scanner;
import vendingmachine.exceptions.InsufficientFundsException;

public class BanknoteReceiver implements PaymentMethod {

    @Override
    public PaymentResult receivePayment(double totalAmount) throws InsufficientFundsException {
        Scanner scanner = new Scanner(System.in);
        double amountInserted = 0.0;
        
        System.out.println("--- Banknote Payment ---");
        System.out.println("Please insert banknotes. (Accepted: 20, 50, 100, 500, 1000)");
        System.out.println("Type 'C' to cancel payment.");

        while (amountInserted < totalAmount) {
            System.out.printf("Required: %.2f | Inserted: %.2f\n", totalAmount, amountInserted);
            System.out.print("Insert banknote: ");
            String input = scanner.nextLine().toUpperCase();
            
            switch (input) {
                case "20": amountInserted += 20.0; break;
                case "50": amountInserted += 50.0; break;
                case "100": amountInserted += 100.0; break;
                case "500": amountInserted += 500.0; break;
                case "1000": amountInserted += 1000.0; break;
                case "C":
                    throw new InsufficientFundsException("Payment cancelled by user.");
                default:
                    System.out.println("Invalid banknote. Please try again.");
            }
        }

        double changeDue = amountInserted - totalAmount;
        System.out.printf("Payment complete. Inserted: %.2f, Change: %.2f\n", amountInserted, changeDue);
        
        return new PaymentResult(true, changeDue);
    }

    @Override
    public String getPaymentMethodName() {
        return "Banknote Payment";
    }

    @Override
    public boolean isCashPayment() {
        return true; // เป็นเงินสด
    }
}