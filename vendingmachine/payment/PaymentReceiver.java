// vendingmachine/payment/PaymentReceiver.java
package vendingmachine.payment;

public interface PaymentReceiver {
    // เมธอดสำหรับรับเงิน
    // คืนค่า true ถ้าชำระสำเร็จ
    boolean receivePayment(double amount); 
    
    // ชื่อของช่องทางการชำระเงิน
    String getPaymentMethodName(); 
}