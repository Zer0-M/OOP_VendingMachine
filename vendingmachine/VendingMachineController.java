package vendingmachine;

import vendingmachine.products.*;
import vendingmachine.payment.*;
import vendingmachine.users.*;
import vendingmachine.admin.AdminService;
import vendingmachine.exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendingMachineController {

    private final InventoryManager inventoryManager;
    private final MoneyManager moneyManager;
    private final MemberDatabase memberDatabase;
    private final AdminService adminService;
    private final HashMap<ItemSlot, Integer> shoppingCart;

    // Constructor
    public VendingMachineController() {
        this.inventoryManager = new InventoryManager();
        this.inventoryManager.loadInventoryFromFile();
        this.moneyManager = new MoneyManager();
        this.memberDatabase = new MemberDatabase();
        this.shoppingCart = new HashMap<>();
        this.adminService = new AdminService(this.inventoryManager, this.moneyManager, this.memberDatabase);
    }

    // [New] เมธอดสำหรับส่ง AdminService ให้ UI
    public AdminService getAdminService() {
        return adminService;
    }

    // เช็กว่ารหัสสินค้านี้มีอยู่จริงในตู้หรือไม่
    public boolean hasProductsID(String slotCode) {
        try {
            inventoryManager.findSlotByCode(slotCode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String addItemToCart(String slotCode) {
        try {
            ItemSlot slot = inventoryManager.findSlotByCode(slotCode);
            int currentQtyInCart = shoppingCart.getOrDefault(slot, 0);

            inventoryManager.checkStock(slot, currentQtyInCart);

            shoppingCart.put(slot, currentQtyInCart + 1);

            return "Added: " + slot.getProduct().getName()
                    + " | Current Total: " + getCartTotal() + " Baht";

        } catch (OutOfStockException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error: Invalid slot code.";
        }
    }

    public HashMap<ItemSlot, Integer> getCart() {
        System.out.print("CurrentCart: ");

        if (shoppingCart.isEmpty()) {
            System.out.println("[ Empty ]");
        } else {
            List<String> items = new ArrayList<>();

            for (Map.Entry<ItemSlot, Integer> entry : shoppingCart.entrySet()) {
                String name = entry.getKey().getProduct().getName();
                int qty = entry.getValue();
                items.add(name + " (x" + qty + ")");
            }

            // แสดงผล: [ Coke (x2), Lays (x1) ]
            System.out.println("[ " + String.join(", ", items) + " ]");
        }

        return shoppingCart;
    }

    public double getCartTotal() {
        double total = 0.0;
        for (HashMap.Entry<ItemSlot, Integer> entry : shoppingCart.entrySet()) {
            ItemSlot slot = entry.getKey();
            int quantity = entry.getValue();
            total += slot.getProduct().getPrice() * quantity;
        }
        return total;
    }

    public boolean processPayment(double totalAmount, String paymentChoice) {
        try {
            boolean success = moneyManager.processPayment(totalAmount, paymentChoice);

            if (success) {
                inventoryManager.dispenseCart(shoppingCart);
            }
            return success;

        } catch (InsufficientFundsException | ChangeNotAvailableException e) {
            System.out.println("Payment Failed: " + e.getMessage());
            return false;
        }
    }

    public String applyPoints(String phoneNumber) {
        // สมมติ 1 บาท 1 แต้ม
        int points = (int) getCartTotal();
        return memberDatabase.addPointsToMember(phoneNumber, points);
    }

    public void clearCart() {
        shoppingCart.clear();
    }

    public Map<String, ItemSlot> getProductList() {
        return inventoryManager.getActiveSlots();
    }

    public void removeProductFromCart(String slotCode) {
        try {
            ItemSlot slot = inventoryManager.findSlotByCode(slotCode);
            if (shoppingCart.containsKey(slot)) {
                shoppingCart.remove(slot);
            }
        } catch (Exception e) {
            System.out.println("Error removing item: " + e.getMessage());
        }
    }

    public boolean isValidThaiMobilePhone(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        String thaiMobilePattern = "^0[689][0-9]{8}$";
        return phoneNumber.matches(thaiMobilePattern);
    }

    public MemberDatabase getMemberDatabase() {
        return memberDatabase;
    }
}