public class MoneyManager {
    private double currentBalance = 0.0;

    public void addFunds(PaymentReceiver paymentMethod) {
        double receivedAmount = paymentMethod.receiveFunds();

        if (receivedAmount > 0) {
            currentBalance += receivedAmount;
            System.out.println("Your money: " + receivedAmount + " Baht (Allmoney: " + currentBalance + " Baht)");
        } else {
            System.out.println("Add funds failed.");
        }
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
