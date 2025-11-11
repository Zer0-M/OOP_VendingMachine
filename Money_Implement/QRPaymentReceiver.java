import java.util.Scanner;

public class QRPaymentReceiver implements PaymentReceiver {
    private Scanner sc = new Scanner(System.in);

    @Override
    public double receiveFunds() {
        // ในโลกจริง:
        // 1. ระบบจะ generate QR Code + แสดงบนจอ
        // 2. ระบบจะรอ "Callback" จาก Payment Gateway (เช่น Bank API)

        // ใน Console App (เราจำลองเอาง่ายๆ):
        System.out.println("[SIMULATION] QR Code Generated for Top-up...");
        System.out.println("[SIMULATION] Please scan and enter the amount you paid.");
        System.out.print("Enter amount paid via QR: ");

        double amount = sc.nextDouble();

        if (amount > 0) {
            System.out.println("...Payment confirmation received for " + amount + " Baht!");
            return amount;
        }
        System.out.println("Payment failed or cancelled.");
        return 0.0;

    }

    public String getMethodName() {
        return "QR Scan";
    }
}