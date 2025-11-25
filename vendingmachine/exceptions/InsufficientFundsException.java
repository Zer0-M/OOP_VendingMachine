// vendingmachine/exceptions/InsufficientFundsException.java
package vendingmachine.exceptions;

/**
 * Exception ที่จะ "โยน" (throw) เมื่อเงินที่ผู้ใช้ใส่มาไม่เพียงพอ
 */
public class InsufficientFundsException extends VendingMachineException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}