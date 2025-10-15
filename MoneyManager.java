public class MoneyManager {
    private double currentBalance = 0.0;

    public void insertCoin(double amount) {
        if (amount <= 0) {
            System.out.println("จำนวนเงินไม่ถูกต้อง");
            return;
        }
        currentBalance += amount;
        System.out.println("คุณใส่เงิน: " + amount + "฿ (รวม: " + currentBalance + "฿)");
    }

    public boolean pay(double price) {
        if (currentBalance >= price) {
            currentBalance -= price;
            return true;
        } else {
            System.out.println("เงินไม่พอ! กรุณาเติมเงินเพิ่ม");
            return false;
        }
    }

    public double returnChange() {
        double change = currentBalance;
        currentBalance = 0;
        return change;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }
}
