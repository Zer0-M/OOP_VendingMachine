package vendingmachine.payment;

public class MoneyManager {
    private double userBalance; // เงินที่ User หยอดเข้ามา
    private double internalCash; // เงินสดในลิ้นชักตู้ (สำหรับทอน)

    public MoneyManager(double initialInternalCash) {
        this.userBalance = 0.0;
        this.internalCash = initialInternalCash;
    }

    public void addUserBalance(double amount) {
        this.userBalance += amount;
    }

    public double getUserBalance() {
        return this.userBalance;
    }
    
    // Feature 3: Give Change
    public double returnChange() {
        double changeToReturn = this.userBalance;
        this.userBalance = 0.0;
        // (ในโลกจริง ต้องเช็กด้วยว่า internalCash พอทอนไหม)
        return changeToReturn;
    }

    // เมธอดสำหรับ Admin (Feature 6)
    public double collectAllCash() {
        double collected = this.internalCash;
        this.internalCash = 0.0; // เก็บเงินเกลี้ยงตู้
        return collected;
    }
    
    // (เมธอดจ่ายเงิน, เช็กเงินทอน ฯลฯ)
}