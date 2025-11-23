package vendingmachine;

public class ConsoleUI {
    public static final String RESET = "\033[0m";
    public static final String GREEN = "\033[1;32m"; // สีเขียวสว่าง
    public static final String RED = "\033[1;31m";   // สีแดงสว่าง
    public static final String YELLOW = "\033[1;33m"; // สีเหลือง
    public static final String CYAN = "\033[1;36m";   // สีฟ้า
    public static final String WHITE = "\033[1;37m";  // สีขาว
    
    // คำสั่งเคลียร์หน้าจอ (ให้ดูเหมือนโปรแกรมทำงานจริง ไม่ใช่ Log ไหลๆ)
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}