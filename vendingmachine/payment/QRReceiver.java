package vendingmachine.payment;

import java.util.HashMap;
import vendingmachine.exceptions.InsufficientFundsException;

public class QRReceiver implements PaymentMethod {
    @Override public String getPaymentMethodName() {
        return "QR Payment";
    }

    @Override public boolean isCashPayment() {
        return false;
    }

    @Override
    public PaymentResult receivePayment(double totalAmount) throws InsufficientFundsException {
        System.out.println("Generating QR Code for " + totalAmount + " Baht...");
        System.out.println("Payment Success!");
        return new PaymentResult(true, 0.0, new HashMap<>());
    }
}