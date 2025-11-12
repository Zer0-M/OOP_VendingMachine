package vendingmachine.payment;
import vendingmachine.exceptions.ChangeNotAvailableException;
import vendingmachine.exceptions.InsufficientFundsException;

public class MoneyManager {
    // (Encapsulation) ซ่อนข้อมูลเงินไว้
    private double internalCash; // เงินสดในลิ้นชักตู้ (สำหรับทอน)

    public MoneyManager(double initialInternalCash) {
        this.internalCash = initialInternalCash;
        System.out.println("(Admin) CashRegister initialized with " + initialInternalCash + " Baht.");
    }

    public boolean processPayment(double totalAmount, PaymentMethod method) 
            throws InsufficientFundsException, ChangeNotAvailableException {
        
        // 1. สั่งให้ช่องทางนั้นๆ รับเงิน (Polymorphism)
        // Logic การรับเงิน (Loop ถามเงิน) จะเกิดในเมธอดนี้
        PaymentResult result = method.receivePayment(totalAmount);

        // 2. ถ้าจ่ายสำเร็จ (result.isSuccess() มักจะ true เสมอ นอกจากโดน Exception)
        if (result.isSuccess()) {
            
            if (method.isCashPayment()) {
                // 3. ถ้าเป็นเงินสด (Coin, Banknote)
                double changeDue = result.getChangeDue();
                
                // 3.1 เช็กว่ามีเงินทอนในตู้พอไหม
                if (changeDue > this.internalCash) {
                    // ถ้าเงินทอนไม่พอ -> โยน Exception!
                    throw new ChangeNotAvailableException("Sorry, machine does not have enough change (" + changeDue + " Baht).");
                }
                
                // 3.2 ถ้าทอนพอ
                this.internalCash -= changeDue; // หักเงินทอนออกจากตู้
                double cashReceived = totalAmount + changeDue; // เงินที่ผู้ใช้ใส่มาทั้งหมด
                this.internalCash += cashReceived; // เติมเงินที่ได้มาเข้าตู้
                
                if (changeDue > 0) {
                    System.out.println("Please collect your change: " + changeDue + " Baht.");
                }
                
            } else {
                // 3. ถ้าเป็น QR Payment (ไม่มีเงินทอน)
                // (ในโลกจริง เงินจะเข้าบัญชี ไม่ได้เข้า internalCash)
                System.out.println("Digital payment received.");
            }
            
            return true; // จ่ายเงินสำเร็จ
        }
        
        return false; // (ปกติจะโดน Exception ไปก่อนถึงบรรทัดนี้)
    }

    // --- เมธอดสำหรับ AdminService ---
    
    public double collectAllCash() {
        double collected = this.internalCash;
        this.internalCash = 0; // สมมติ Admin เอาไปหมด
        System.out.println("Collected " + collected + " Baht from register.");
        return collected;
    }
    
    public double getCurrentInternalCash() {
        return this.internalCash;
    }
}