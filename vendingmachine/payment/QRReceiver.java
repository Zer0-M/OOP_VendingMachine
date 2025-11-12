// vendingmachine/payment/QRReceiver.java
package vendingmachine.payment;

public class QRReceiver implements PaymentReceiver {

    @Override
    public PaymentResult receivePayment(double totalAmount) {
        // --- Simulation ---
        System.out.println("Generating QR Code for " + totalAmount + " Baht...");
        System.out.println("[Image of a QR Code]"); // (จำลองการแสดง QR)
        System.out.println("... Waiting for payment confirmation from bank ...");
        
        // (จำลองว่าจ่ายสำเร็จเสมอ)
        System.out.println("... Payment Confirmed!");
        // --- End Simulation ---
        
        // QR Code จะจ่ายพอดีเป๊ะเสมอ เลยไม่มีเงินทอน
        return new PaymentResult(true, 0.0);
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