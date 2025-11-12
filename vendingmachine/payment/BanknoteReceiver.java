package vendingmachine.payment;

public class BanknoteReceiver implements PaymentReceiver {
    @Override
    public boolean receivePayment(double amount) {
        System.out.println("Validating coins... " + amount + " Baht.");
        // (Logic การตรวจสอบเหรียญจริง)
        return true; // สมมติว่าสำเร็จ
    }

    @Override
    public String getPaymentMethodName() {
        return "Coins";
    }
}
