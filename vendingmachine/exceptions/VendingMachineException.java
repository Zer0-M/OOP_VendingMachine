// vendingmachine/exceptions/VendingMachineException.java
package vendingmachine.exceptions;

/**
 * คลาสแม่สำหรับ Custom Exceptions ทั้งหมดของเรา
 * (ตามหลัก Inheritance ที่คุณวางแผนไว้)
 */
public class VendingMachineException extends Exception {
    public VendingMachineException(String message) {
        super(message);
    }
}