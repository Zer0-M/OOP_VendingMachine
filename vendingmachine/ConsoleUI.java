package vendingmachine;

public class ConsoleUI {
    // สีพื้นฐาน
    public static final String RESET = "\033[0m";
    public static final String GREEN = "\033[0;32m";
    public static final String RED = "\033[0;31m";
    public static final String YELLOW = "\033[0;33m";
    public static final String CYAN = "\033[0;36m";
    public static final String BLUE = "\033[0;34m";
    public static final String WHITE_BOLD = "\033[1;37m";

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}