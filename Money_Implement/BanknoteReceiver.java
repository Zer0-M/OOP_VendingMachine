import java.util.Scanner;

public class BanknoteReceiver implements PaymentReceiver {
    private Scanner sc = new Scanner(System.in);

    @Override
    public double receiveFunds() {
        System.out.print("Insert Banknote (e.g., 20, 50, 100): ");
        double amount = sc.nextDouble();

        // เราสามารถเพิ่ม Logic ตรวจสอบแบงก์ปลอม หรือรับแค่บางชนิดได้
        if (amount == 20 || amount == 50 || amount == 100 || amount == 500 || amount == 1000) {
            return amount;
        }
        System.out.println("Invalid Banknote!");
        return 0.0;
    }

    public String getMethodName() {
        return "Banknote";
    }
}