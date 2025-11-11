public class MoneyManager {
    private double currentBalance = 0.0;

    public void addFunds(PaymentReceiver paymentMethod) {
        double receivedAmount = paymentMethod.receiveFunds();
        System.out.println("[Debug]receivedAmount: " + receivedAmount);

        //ถ้าเงินลบ ก็ไม่ต้องรับ
        if (receivedAmount < 0){
            System.out.println("Add funds failed.");
            return;
        }
        // รับปกติ
        currentBalance += receivedAmount;
        System.out.println("Your money: " + receivedAmount + " Baht (Allmoney: " + currentBalance + " Baht)");
    }

    public boolean pay(double price) {
        if (currentBalance >= price) {
            currentBalance -= price;
            return true;
        } else {
            System.out.println("Not enough money Please give me more");
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
