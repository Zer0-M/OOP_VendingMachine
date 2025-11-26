package vendingmachine.payment;

import java.util.Map;

// คลาส Format เก็บ "ผลลัพธ์" ของการจ่ายเงิน
public class PaymentResult {
    private final boolean success;
    private final double changeDue; // เงินทอนที่ต้องคืน
    // เช่น <1000, 2> หมายถึง ใส่แบงค์ 1000 จำนวน 2 ใบ
    private final Map<Double, Integer> insertedMoney;
    
    // Constructor
    public PaymentResult(boolean success, double changeDue, Map<Double, Integer> insertedMoney) {
        this.success = success;
        this.changeDue = changeDue;
        this.insertedMoney = insertedMoney;
    }

    public boolean isSuccess() {
        return success;
    }

    public double getChangeDue() {
        return changeDue;
    }

    public Map<Double, Integer> getInsertedMoney() {
        return insertedMoney;
    }
}