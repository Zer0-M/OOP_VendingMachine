package vendingmachine.payment;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import vendingmachine.exceptions.ChangeNotAvailableException;
import vendingmachine.exceptions.InsufficientFundsException;

public class MoneyManager {
    private final TreeMap<Double, Integer> machineMoney;

    // Constructor
    public MoneyManager() {
        this.machineMoney = new TreeMap<>(Collections.reverseOrder());
        initializeDefaultMoney();
        System.out.println("[Admin] CashRegister initialized.");
    }

    // เติมเงินเริ่มต้นในตู้
    private void initializeDefaultMoney() {
        machineMoney.put(1000.0, 2);
        machineMoney.put(500.0, 4);
        machineMoney.put(100.0, 6);
        machineMoney.put(50.0, 8);
        machineMoney.put(20.0, 10);
        machineMoney.put(10.0, 10);
        machineMoney.put(5.0, 10);
        machineMoney.put(1.0, 20);
    }

    public boolean processPayment(double totalAmount, String choice)
            throws InsufficientFundsException, ChangeNotAvailableException {

        PaymentMethod paymentMethod;
        // (Polymorphism) เลือกช่องทางจ่ายเงิน
        switch (choice) {
            case "1" -> paymentMethod = new QRReceiver();
            case "2" -> paymentMethod = new CashReceiver();
            default -> {
                System.out.println("Invalid payment method.");
                return false;
            }
        }
        // 1. รับเงินจากผู้ใช้
        PaymentResult result = paymentMethod.receivePayment(totalAmount);

        // 2. ถ้าจ่ายสำเร็จ (result.isSuccess() มักจะ true เสมอ นอกจากโดน Exception)
        if (result.isSuccess()) {

            if (paymentMethod.isCashPayment()) {
                Map<Double, Integer> userInserted = result.getInsertedMoney();
                double changeDue = result.getChangeDue();

                // Step 1: เอาเงินลูกค้ามารวมกับเงินตู้ก่อน
                addMoneyToMachine(userInserted);

                // Step 2: คำนวณเงินทอน (Greedy Algorithm)
                Map<Double, Integer> changeToReturn = calculateChange(changeDue);

                // Step 3: เช็กผลลัพธ์การทอน
                if (changeToReturn == null) { // ทอนไม่สำเร็จ
                    removeMoneyFromMachine(userInserted);
                    // showReturnedChange(userInserted);
                    throw new ChangeNotAvailableException(
                            "Not enough cash in this machine " + changeDue + " baht (change: " + totalAmount + ")");
                }
                // Step 4: ทอนสำเร็จ (ตัดเงินทอนออกจากตู้จริงๆ)
                removeMoneyFromMachine(changeToReturn);

                System.out.println("Payment Complete!");
                showReturnedChange(changeToReturn);
            } else {
                // QR Payment (ไม่มีเงินทอน)
                System.out.println("Digital payment received.");
            }
            return true;
        }
        return false; // (ปกติจะโดน Exception ไปก่อนถึงบรรทัดนี้)
    }

    private void showReturnedChange(Map<Double, Integer> changeToReturn) {
        System.out.println("Change details:");
        for (Map.Entry<Double, Integer> entry : changeToReturn.entrySet()) {
            System.out.println("- " + entry.getKey() + " Baht: " + entry.getValue() + " units");
        }
    }

    private void addMoneyToMachine(Map<Double, Integer> money) {
        if (money == null)
            return;
        for (Map.Entry<Double, Integer> entry : money.entrySet()) {
            machineMoney.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    private void removeMoneyFromMachine(Map<Double, Integer> money) {
        if (money == null)
            return;
        for (Map.Entry<Double, Integer> entry : money.entrySet()) {
            if (machineMoney.containsKey(entry.getKey())) {
                int current = machineMoney.get(entry.getKey());
                int remove = entry.getValue();
                // ป้องกันค่าติดลบ
                machineMoney.put(entry.getKey(), Math.max(0, current - remove));
            }
        }
    }

    // ฟังก์ชันคำนวณเงินทอน (Greedy Algorithm)
    private Map<Double, Integer> calculateChange(double targetChange) {
        if (targetChange == 0)
            return new TreeMap<>(); // ไม่ต้องทอน

        Map<Double, Integer> changePlan = new TreeMap<>();
        // สร้าง Temp Inventory จำลอง (Copy มาจากของจริง)
        Map<Double, Integer> tempStock = new TreeMap<>(machineMoney);

        // วนลูปจากมากไปน้อย (1000 -> 1)
        for (Double denomination : machineMoney.keySet()) {
            if (targetChange <= 0.9)
                break;

            int countNeeded = (int) (targetChange / denomination);
            int countAvailable = tempStock.getOrDefault(denomination, 0);

            // หยิบเท่าที่หยิบได้
            int countToTake = Math.min(countNeeded, countAvailable);

            if (countToTake > 0) {
                changePlan.put(denomination, countToTake);
                targetChange -= (denomination * countToTake);
            }
        }
        if (targetChange > 0) return null; // ทอนไม่สำเร็จ (เงินขาด)

        return changePlan;
    }

    // --- Admin & Utils ---
    public double getCurrentInternalCash() {
        double total = 0;
        for (Map.Entry<Double, Integer> entry : machineMoney.entrySet()) {
            total += entry.getKey() * entry.getValue();
        }
        return total;
    }

    // ฟังก์ชันถอนเงินแบบแจกแจงรายละเอียด
    public Map<Double, Integer> withdrawSpecificCash(double amount) {
        if (amount <= 0) return null;

        double currentCash = getCurrentInternalCash();
        if (amount > currentCash) return null; // เงินไม่พอ

        Map<Double, Integer> toWithdraw = calculateChange(amount);
        if (toWithdraw == null) return null;

        // ตัดเงินออกจากตู้จริง
        removeMoneyFromMachine(toWithdraw);
        return toWithdraw; // ส่งรายการที่ถอนกลับไป
    }
}
