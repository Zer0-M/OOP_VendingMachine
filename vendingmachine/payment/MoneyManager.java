package vendingmachine.payment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import vendingmachine.exceptions.ChangeNotAvailableException;
import vendingmachine.exceptions.InsufficientFundsException;

public class MoneyManager {

    // (Encapsulation) เปลี่ยนจาก double เป็น Map เพื่อเก็บสต็อกเหรียญ/แบงค์แต่ละชนิด
    // ใช้ TreeMap + reverseOrder เพื่อให้วนลูปจาก แบงค์ 1000 -> เหรียญ 1 บาท เสมอ (สำคัญมากสำหรับ Greedy Algo)
    private TreeMap<Double, Integer> machineMoney; 

    public MoneyManager(double initialInternalCash) {
        this.machineMoney = new TreeMap<>(Collections.reverseOrder());
        initializeDefaultMoney(); // สมมติว่าใส่เงินทอนเริ่มต้นให้ตู้
        System.out.println("(Admin) CashRegister initialized.");
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

        // 1. สั่งให้ช่องทางนั้นๆ รับเงิน (Polymorphism)
        // Logic การรับเงิน (Loop ถามเงิน) จะเกิดในเมธอดนี้
        PaymentResult result = paymentMethod.receivePayment(totalAmount);

        // 2. ถ้าจ่ายสำเร็จ (result.isSuccess() มักจะ true เสมอ นอกจากโดน Exception)
        if (result.isSuccess()) {

            if (paymentMethod.isCashPayment()) {
                // --- LOGIC ใหม่เริ่มตรงนี้ ---
            
                Map<Double, Integer> userInserted = result.getInsertedMoney();
                double changeDue = result.getChangeDue();

                // Step A: เอาเงินลูกค้ามารวมกับเงินตู้ก่อน (เพื่อให้มีแบงค์ย่อยทอน)
                addMoneyToMachine(userInserted); 

                // Step B: คำนวณเงินทอน (Greedy Algorithm)
                Map<Double, Integer> changeToReturn = calculateChange(changeDue);

                // Step C: เช็กผลลัพธ์การทอน
                if (changeToReturn == null) {
                    // ทอนไม่ได้! (เงินไม่พอ หรือ ไม่มีเศษเหรียญที่แมตช์)
                    // IMPORTANT: ต้อง Rollback เอาเงินลูกค้าออก เพราะ Transaction ล้มเหลว
                    removeMoneyFromMachine(userInserted); 
                
                    throw new ChangeNotAvailableException(
                        "Not enough cash in this machine " + changeDue + " baht (change: " + totalAmount + ")"
                    );
                }
                // Step D: ทอนสำเร็จ (ตัดเงินทอนออกจากตู้จริงๆ)
                removeMoneyFromMachine(changeToReturn);
            
                System.out.println("Payment Complete! Change details:");
                changeToReturn.forEach((val, count) -> {
                    if(count > 0) System.out.println("- " + val + " Baht: " + count + " units");
                });

            } else {
                // QR Payment (ไม่มีเงินทอน)
                System.out.println("Digital payment received.");
            }
            return true;
        }

        return false; // (ปกติจะโดน Exception ไปก่อนถึงบรรทัดนี้)
    }

    private void addMoneyToMachine(Map<Double, Integer> money) {
        if (money == null) return;
        for (Map.Entry<Double, Integer> entry : money.entrySet()) {
            machineMoney.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    private void removeMoneyFromMachine(Map<Double, Integer> money) {
        if (money == null) return;
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
        if (targetChange == 0) return new HashMap<>(); // ไม่ต้องทอน

        Map<Double, Integer> changePlan = new HashMap<>();
        // สร้าง Temp Inventory จำลอง (Copy มาจากของจริง)
        Map<Double, Integer> tempStock = new HashMap<>(machineMoney);

        // วนลูปจากมากไปน้อย (1000 -> 1)
        for (Double denomination : machineMoney.keySet()) {
            if (targetChange <= 0.001) break;

            int countNeeded = (int) (targetChange / denomination);
            int countAvailable = tempStock.getOrDefault(denomination, 0);

            // หยิบเท่าที่หยิบได้
            int countToTake = Math.min(countNeeded, countAvailable);

            if (countToTake > 0) {
                changePlan.put(denomination, countToTake);
                targetChange -= (denomination * countToTake);
                // แก้ปัญหาทศนิยม (Floating point precision)
                targetChange = Math.round(targetChange * 100.0) / 100.0;
            }
        }

        if (targetChange > 0) {
            return null; // ทอนไม่สำเร็จ (เงินขาด)
        }
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
    public double collectAllCash() {
        double total = 0;
        for (Map.Entry<Double, Integer> entry : machineMoney.entrySet()) {
            total += entry.getKey() * entry.getValue();
        }
        machineMoney.clear(); // ล้างตู้
        initializeDefaultMoney(); // (Optional) อาจจะเหลือเหรียญตั้งต้นไว้หน่อยก็ได้
        System.out.println("Collected total: " + total + " Baht.");
        return total;
    }

    private void initializeDefaultMoney() {
        // ใส่เงินตั้งต้นไว้ทอนหน่อย
        machineMoney.put(10.0, 10); // เหรียญสิบ 10 เหรียญ
        machineMoney.put(5.0, 10);
        machineMoney.put(1.0, 20);
    }
    
    private String formatMoney(double val) {
        return (val >= 20) ? (int)val + " Bank" : (int)val + " Coin";
    }

    // [NEW] ฟังก์ชันถอนเงินออกจากตู้แบบระบุจำนวน (สำหรับ Admin)
    public double withdrawSpecificCash(double amount) {
        if (amount <= 0) return 0;
        double currentCash = getCurrentInternalCash();
        
        if (amount > currentCash) {
            return -1; // เงินในตู้ไม่พอ
        }

        // ใช้ Greedy Algorithm คำนวณว่าจะดึงแบงค์ไหนออกไปบ้าง
        Map<Double, Integer> toWithdraw = new HashMap<>();
        double remaining = amount;

        for (Double denom : machineMoney.keySet()) {
            if (remaining < denom) continue;
            int countAvailable = machineMoney.get(denom);
            int countNeeded = (int) (remaining / denom);
            int countToTake = Math.min(countAvailable, countNeeded);

            if (countToTake > 0) {
                toWithdraw.put(denom, countToTake);
                remaining -= (countToTake * denom);
                remaining = Math.round(remaining * 100.0) / 100.0;
            }
        }

        if (remaining > 0) {
            return -2; // มีเศษย่อยไม่พอให้ถอน (เช่น มีแต่แบงค์พัน จะถอน 500)
        }

        // ตัดเงินออกจากตู้จริง
        removeMoneyFromMachine(toWithdraw);
        System.out.println("Admin Withdrawn: " + amount);
        return amount;
    }
}
