import java.util.Scanner;

public class CoinReceiver implements PaymentReceiver {
    private Scanner sc = new Scanner(System.in);

    @Override
    public double receiveFunds() {
        System.out.print("Insert Coins (e.g., 10, 5): ");
        // ในอนาคต ตรงนี้อาจต่อกับ Hardware จริง
        double amount = sc.nextDouble();
        
        if (amount > 0) {
            return amount;
        }
        System.out.println("Fault value!");
        return 0.0;
    }
}