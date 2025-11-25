package vendingmachine.payment;

import java.util.Map;
/**
 * คลาสผู้ช่วยสำหรับเก็บ "ผลลัพธ์" ของการจ่ายเงิน
 * (เนื่องจาก Java method คืนค่าได้เพียงค่าเดียว)
 */
public class PaymentResult {
    private boolean success;
    private double changeDue; // เงินทอนที่เกิดจากช่องทางนี้ (เช่น ใส่แบงค์ 100 ซื้อของ 80)

    // ส่วนที่เพิ่มเข้ามา: เก็บรายละเอียดเงินที่หยอดมา (เพื่อให้ MoneyManager เอาไปเติมตู้)
    private Map<Double, Integer> insertedMoney; 

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