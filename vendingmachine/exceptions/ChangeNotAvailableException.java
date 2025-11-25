// vendingmachine/exceptions/ChangeNotAvailableException.java
package vendingmachine.exceptions;

/**
 * Exception ที่จะ "โยน" (throw) เมื่อตู้ไม่มีเงินทอนเพียงพอ
 */
public class ChangeNotAvailableException extends VendingMachineException {
    public ChangeNotAvailableException(String message) {
        super(message);
    }
}