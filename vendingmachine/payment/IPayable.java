package vendingmachine.payment;
public interface IPayable {
    double pay(double amount);
    String getMethodName();
    
}
