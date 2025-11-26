package vendingmachine;

import vendingmachine.products.*;
import vendingmachine.payment.*;
import vendingmachine.users.*;
import vendingmachine.admin.AdminService;
import vendingmachine.exceptions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        this.adminService = new AdminService(this.inventoryManager, this.moneyManager);
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


    // ---------------- Admin Operations ----------------
    public void adminRestockItem(String slotCode, int quantity) {
        adminService.restockItem(slotCode, quantity);
    }

    public void adminSetName(String slotCode, String newName) {
        adminService.setName(slotCode, newName);
    }

    public void adminSetPrice(String slotCode, double newPrice) {
        adminService.setPrice(slotCode, newPrice);
    }

    public double getMachineCurrentCash() {
        return moneyManager.getCurrentInternalCash();
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

    // ดึงเงินสดออกจากตู้ (Admin)
    public String adminWithdrawCash(double amount) {
        Map<Double, Integer> result = adminService.collectCashAmount(amount);

        if (result == null)
            return "Error: Cannot withdraw that amount (Insufficient funds or no suitable change).";

        StringBuilder sb = new StringBuilder();
        sb.append("Withdraw Success! Total: ").append(amount).append(" THB\n");
        sb.append("--------------------------------\n");

        Map<Double, Integer> sortedResult = new TreeMap<>(Collections.reverseOrder());
        sortedResult.putAll(result);

        for (Map.Entry<Double, Integer> entry : sortedResult.entrySet()) {
            double value = entry.getKey();
            int count = entry.getValue();
            String type = (value >= 20) ? "Bank" : "Coin";

            // Format: 1000.0 (Bank) : 5 units
            sb.append(String.format("%-6.0f (%s) : x%d\n", value, type, count));
        }
        sb.append("--------------------------------");

        return sb.toString();
    }

    public void adminAddProduct(String slotCode, String name, double price, int quantity, String type, double size)
            throws Exception {
        adminService.addProduct(slotCode, name, price, quantity, type, size);
    }

    public boolean isValidThaiMobilePhone(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        
        String thaiMobilePattern = "^0[689][0-9]{8}$";
        return phoneNumber.matches(thaiMobilePattern);
    }

    public void adminSaveStock() {
        adminService.saveStock();
    }

    public void adminLoadStock() {
        adminService.loadStock();
    }

    public MemberDatabase getMemberDatabase() {
        return memberDatabase;
    }
}
