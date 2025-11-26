package vendingmachine.payment;

import vendingmachine.exceptions.InsufficientFundsException;

// Interface "ข้อตกลง" สำหรับช่องทางการชำระเงิน
// (ตามหลัก Polymorphism ที่คุณวางแผนไว้)
public interface PaymentMethod {
    String getPaymentMethodName();
    boolean isCashPayment(); // ไว้ระบุว่าช่องทางนี้เป็นเงินสดหรือไม่ (จะได้รู้ว่าต้องทอนเงินไหม)
    PaymentResult receivePayment(double totalAmount) throws InsufficientFundsException;
}