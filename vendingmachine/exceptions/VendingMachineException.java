package vendingmachine.exceptions;

// คลาสแม่สำหรับ Custom Exceptions ทั้งหมดของเรา
public class VendingMachineException extends Exception {
    public VendingMachineException(String message) {
        super(message);
    }
}