// vendingmachine/exceptions/OutOfStockException.java
package vendingmachine.exceptions;

// 2. สร้าง Exception ลูก ที่สืบทอดจากคลาสแม่ของเรา
public class OutOfStockException extends VendingMachineException {
    public OutOfStockException(String message) {
        super(message); // ส่งข้อความกลับไปให้คลาสแม่ (Exception)
    }
}