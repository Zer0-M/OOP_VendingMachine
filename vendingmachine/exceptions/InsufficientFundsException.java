package vendingmachine.exceptions;

import java.util.Map;

import vendingmachine.payment.PaymentResult;

// Exception ที่จะ "โยน" (throw) เมื่อเงินที่ผู้ใช้ใส่มาไม่เพียงพอ
public class InsufficientFundsException extends VendingMachineException {
    public InsufficientFundsException(String message, Map<Double, Integer> insertedMoney) {
        super(message);
        returnFault(insertedMoney);
    }
    private PaymentResult returnFault(Map<Double, Integer> insertedMoney) {
        return new PaymentResult(false, 0, insertedMoney);
    }
}