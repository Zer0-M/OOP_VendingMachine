package vendingmachine.payment;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import vendingmachine.exceptions.InsufficientFundsException;

public class CashReceiver implements PaymentMethod {
    @Override public String getPaymentMethodName() {
        return "Cash Payment";
    }

    @Override public boolean isCashPayment() {
        return true;
    }

    @Override
    public PaymentResult receivePayment(double totalAmount) throws InsufficientFundsException {
        // เราสร้าง Scanner ของตัวเองชั่วคราว เพื่อการรับเงิน Encapsulate อยู่ในคลาสนี้
        try (Scanner scanner = new Scanner(System.in)) {
            // เก็บเงินที่ user หยอดมา <Key: ราคา, Value: จำนวน>
            Map<Double, Integer> insertedMoney = new TreeMap<>();

            System.out.println("--- Cash Payment ---");
            System.out.println("Please insert cash. (Accepted: 1, 2, 5, 10, 20, 50, 100, 500, 1000)");
            System.out.println("Type 'C' to cancel payment.");

            double amountInserted = 0.0;
            while (amountInserted < totalAmount) {
                System.out.printf("Required: %.2f | Inserted: %.2f\n", totalAmount, amountInserted);
                System.out.print("Insert cash: ");
                String input = scanner.nextLine().toUpperCase().trim();

                if (input.equals("C")) {
                    throw new InsufficientFundsException("Payment cancelled by user.");
                }

                try {
                    double cash = Double.parseDouble(input);

                    // ตรวจสอบว่าเป็นเหรียญ/แบงค์ที่ตู้รับไหม
                    if (isValidDenomination(cash)) {
                        amountInserted += cash;

                        // เพิ่ม (ถ้ามีอยู่แล้วให้ +1, ถ้าไม่มีให้เริ่มที่ 1)
                        insertedMoney.merge(cash, 1, Integer::sum);
                    } else {
                        System.out.println("Invalid denomination. System accepts standard Thai currency only.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter numbers or 'C'.");
                }
            }

            double changeDue = amountInserted - totalAmount;
            System.out.printf("Payment complete. Inserted: %.2f, Change: %.2f\n", amountInserted, changeDue);

            scanner.close();
            return new PaymentResult(true, changeDue, insertedMoney);
        }

    }

    // Method เช็คค่าเงิน
    private boolean isValidDenomination(double cash) {
        return cash == 1.0 || cash == 2.0 || cash == 5.0 || cash == 10.0 ||
                cash == 20.0 || cash == 50.0 || cash == 100.0 ||
                cash == 500.0 || cash == 1000.0;
    }
}