// vendingmachine/exceptions/OutOfStockException.java
package vendingmachine.exceptions;

/**
 * Exception ที่จะ "โยน" (throw) เมื่อสินค้าในช่องนั้นๆ หมด
 */
public class OutOfStockException extends VendingMachineException {
    public OutOfStockException(String message) {
        super(message);
    }
}