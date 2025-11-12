package vendingmachine.payment;

import vendingmachine.exceptions.InsufficientFundsException;

/**
 * Interface "ข้อตกลง" สำหรับช่องทางการชำระเงิน
 * (ตามหลัก Polymorphism ที่คุณวางแผนไว้)
 */
public interface PaymentMethod {
    PaymentResult receivePayment(double totalAmount) throws InsufficientFundsException;
    
    String getPaymentMethodName();
    
    /**
     * ช่องทางนี้เป็นเงินสดหรือไม่ (เพื่อที่ CashRegister จะได้รู้ว่าต้องทอนเงินไหม)
     */
    boolean isCashPayment();
}