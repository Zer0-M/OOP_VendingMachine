// vendingmachine/payment/QRReceiver.java
package vendingmachine.payment;

import java.util.HashMap;
import vendingmachine.exceptions.InsufficientFundsException;

public class QRReceiver implements PaymentMethod {

    @Override
    public PaymentResult receivePayment(double totalAmount) throws InsufficientFundsException {
        // ... Logic การแสดง QR Code ...
        System.out.println("Generating QR Code for " + totalAmount + " Baht...");
        System.out.println("Payment Success!");
        
        // IMPORTANT: ส่ง Map ว่างกลับไป เพราะ QR ไม่มีเงินสดเข้ามา
        return new PaymentResult(true, 0.0, new HashMap<>());
    }

    @Override
    public String getPaymentMethodName() {
        return "QR Payment";
    }

    @Override
    public boolean isCashPayment() {
        return false; // ไม่ใช่เงินสด
    }
}