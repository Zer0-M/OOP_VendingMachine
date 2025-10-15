public class MoneyManager {
    private double currentBalance = 0.0;

    public void insertCoin(double amount) {
        if (amount <= 0) {
            System.out.println("Fault value!");
            return;
        }
        currentBalance += amount;
        System.out.println("Your money: " + amount + " Baht (Almoney: " + currentBalance + " Baht)");
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
