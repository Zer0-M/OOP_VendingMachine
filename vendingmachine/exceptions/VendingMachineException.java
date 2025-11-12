// vendingmachine/exceptions/VendingMachineException.java
package vendingmachine.exceptions;

// 1. สร้างคลาสแม่ของเราเองก่อน
public class VendingMachineException extends Exception {
    public VendingMachineException(String message) {
        super(message);
    }
}