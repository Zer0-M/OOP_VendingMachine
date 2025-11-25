package vendingmachine;

import vendingmachine.products.ItemSlot;
import vendingmachine.products.Product;
import vendingmachine.admin.AdminUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class ModernVendingUI extends JFrame {

    private VendingMachineController controller;
    private JPanel productGridPanel;
    private DefaultListModel<String> cartListModel;
    private JList<String> cartList; // ประกาศเป็น field เพื่อให้ปุ่ม Remove เรียกใช้ได้
    private JLabel totalLabel;
    private JLabel statusLabel;

    // --- THEME COLORS ---
    private final Color BG_DARK = new Color(20, 20, 25);
    private final Color BG_PANEL = new Color(35, 35, 40);
    private final Color ACCENT_CYAN = new Color(0, 220, 220);
    private final Color ACCENT_RED = new Color(255, 60, 60);
    private final Color ACCENT_GREEN = new Color(0, 200, 100);
    private final Color TEXT_WHITE = new Color(240, 240, 240);

    public ModernVendingUI() {
        controller = new VendingMachineController();

        // Setup Main Window
        setTitle("Vending Machine PRO");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);

        // --- 1. HEADER (Top Bar) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        JLabel title = new JLabel("VENDING OS 3.0");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ACCENT_CYAN);
        
        // ปุ่ม Admin
        JButton adminBtn = new JButton("⚙ ADMIN PANEL");
        adminBtn.setBackground(new Color(50, 50, 50));
        adminBtn.setForeground(Color.ORANGE);
        adminBtn.setFocusPainted(false);
        adminBtn.setBorder(new LineBorder(Color.ORANGE, 1));
        adminBtn.addActionListener(e -> new AdminUI(controller)); // เปิดหน้า Admin

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeader.setBackground(BG_DARK);
        
        statusLabel = new JLabel("SYSTEM READY   ", SwingConstants.RIGHT);
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        statusLabel.setForeground(ACCENT_GREEN);
        
        rightHeader.add(statusLabel);
        rightHeader.add(adminBtn);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTER (Product Grid) ---
        productGridPanel = new JPanel();
        productGridPanel.setBackground(BG_DARK);
        productGridPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(productGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. RIGHT SIDEBAR (Cart & Controls) ---
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(360, 0));
        sidebar.setBackground(BG_PANEL);
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Cart Section
        JLabel cartTitle = new JLabel("SHOPPING CART");
        cartTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cartTitle.setForeground(TEXT_WHITE);
        cartTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        cartList.setBackground(new Color(25, 25, 30));
        cartList.setForeground(TEXT_WHITE);
        cartList.setFont(new Font("Consolas", Font.PLAIN, 14));
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // ปุ่มลบสินค้า (Remove)
        JButton removeBtn = createStyledButton("REMOVE ITEM!", new Color(100, 40, 40), TEXT_WHITE);
        removeBtn.addActionListener(e -> handleRemoveItem());

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(BG_PANEL);
        cartPanel.add(cartTitle, BorderLayout.NORTH);
        cartPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);
        cartPanel.add(removeBtn, BorderLayout.SOUTH);
        
        sidebar.add(cartPanel, BorderLayout.CENTER);

        // Bottom Controls
        JPanel checkoutPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        checkoutPanel.setBackground(BG_PANEL);
        checkoutPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        totalLabel = new JLabel("TOTAL: 0.00 THB", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        totalLabel.setForeground(ACCENT_GREEN);

        JButton payBtn = createStyledButton("CHECKOUT & PAY", ACCENT_GREEN, Color.BLACK);
        payBtn.addActionListener(e -> handleCheckout());

        JButton clearBtn = createStyledButton("CLEAR CART", new Color(60, 60, 60), TEXT_WHITE);
        clearBtn.addActionListener(e -> {
            controller.clearCart();
            refreshUI();
            showStatus("Cart Cleared", false);
        });

        checkoutPanel.add(totalLabel);
        checkoutPanel.add(payBtn);
        checkoutPanel.add(clearBtn);
        
        sidebar.add(checkoutPanel, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.EAST);

        // Initial Load
        refreshUI();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- LOGIC ---

    private void refreshUI() {
        // Refresh Grid
        productGridPanel.removeAll();
        Map<String, ItemSlot> slots = new TreeMap<>(controller.getProductList());

        for (ItemSlot slot : slots.values()) {
            productGridPanel.add(createProductCard(slot));
        }

        // Refresh Cart (Format: [A1] Name xQty)
        cartListModel.clear();
        Map<ItemSlot, Integer> cart = controller.getCart();
        for (Map.Entry<ItemSlot, Integer> entry : cart.entrySet()) {
            String code = entry.getKey().getSlotCode();
            String name = entry.getKey().getProduct().getName();
            int qty = entry.getValue();
            cartListModel.addElement(String.format("[%s] %-14s x%d", code, name, qty));
        }

        totalLabel.setText(String.format("TOTAL: %.2f ฿", controller.getCartTotal()));
        
        productGridPanel.revalidate();
        productGridPanel.repaint();
    }
    
    // ฟังก์ชันลบสินค้า
    // [MODIFIED] แก้ให้เรียกใช้ removeProductFromCart แทน removeOneItemFromCart
    private void handleRemoveItem() {
        String selected = cartList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.");
            return;
        }
        try {
            int start = selected.indexOf("[") + 1;
            int end = selected.indexOf("]");
            String slotCode = selected.substring(start, end);

            // เรียกใช้ Method ใหม่ที่เพิ่งสร้างใน Controller
            controller.removeProductFromCart(slotCode); 
            
            refreshUI();
        } catch (Exception e) {
            showStatus("Error removing item", true);
        }
    }

    private JPanel createProductCard(ItemSlot slot) {
        Product p = slot.getProduct();
        boolean isOutOfStock = slot.getQuantity() <= 0;

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(200, 250));
        card.setBackground(BG_PANEL);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(60, 60, 70), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel codeLbl = new JLabel(slot.getSlotCode());
        codeLbl.setForeground(ACCENT_CYAN);
        codeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLbl = new JLabel(isOutOfStock ? "❌" : "🥤");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLbl = new JLabel(p.getName());
        nameLbl.setForeground(TEXT_WHITE);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLbl = new JLabel(p.getPrice() + " THB");
        priceLbl.setForeground(Color.LIGHT_GRAY);
        priceLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel stockLbl = new JLabel("Stock: " + slot.getQuantity());
        stockLbl.setForeground(isOutOfStock ? ACCENT_RED : Color.GRAY);
        stockLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        stockLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addBtn = createStyledButton(isOutOfStock ? "OUT OF STOCK" : "ADD", 
                isOutOfStock ? new Color(50, 20, 20) : ACCENT_CYAN, 
                isOutOfStock ? Color.GRAY : Color.BLACK);
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.setEnabled(!isOutOfStock);
        
        addBtn.addActionListener(e -> {
            String result = controller.addItemToCart(slot.getSlotCode());
            if (result.startsWith("Error")) {
                showStatus(result, true);
            } else {
                showStatus("Added: " + p.getName(), false);
            }
            refreshUI();
        });

        card.add(codeLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(iconLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLbl);
        card.add(priceLbl);
        card.add(stockLbl);
        card.add(Box.createVerticalGlue());
        card.add(addBtn);

        return card;
    }

    private void handleCheckout() {
        double total = controller.getCartTotal();
        if (total <= 0) {
            showStatus("Error: Cart is empty", true);
            return;
        }

        String[] options = {"Scan QR Code", "Cash (Coins/Bank notes)"};
        int choice = JOptionPane.showOptionDialog(this, "Total: " + total + " THB\nSelect Payment Method:", 
                "Payment", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == 0) { 
            showQRCodeDialog(total);
        } else if (choice == 1) {
            simulateLoading("Processing Cash...", () -> processPaymentResult("2"));
        }
    }

    private void showQRCodeDialog(double amount) {
        JDialog dialog = new JDialog(this, "Scan QR to Pay", true);
        dialog.setSize(350, 450);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel qrPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);
                int size = 10;
                Random rand = new Random();
                for (int y = 50; y < 300; y += size) {
                    for (int x = 50; x < 300; x += size) {
                        boolean isCorner = (x<120 && y<120) || (x>230 && y<120) || (x<120 && y>230);
                        if (isCorner || rand.nextBoolean()) g.fillRect(x, y, size, size);
                    }
                }
                g.clearRect(60, 60, 50, 50); g.drawRect(60, 60, 50, 50); g.fillRect(70, 70, 30, 30);
                g.clearRect(240, 60, 50, 50); g.drawRect(240, 60, 50, 50); g.fillRect(250, 70, 30, 30);
                g.clearRect(60, 240, 50, 50); g.drawRect(60, 240, 50, 50); g.fillRect(70, 250, 30, 30);
            }
        };

        JLabel info = new JLabel("Scan to pay " + amount + " THB", SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.BOLD, 16));
        info.setBorder(new EmptyBorder(10,0,10,0));

        JButton confirmBtn = new JButton("SIMULATE SCAN SUCCESS");
        confirmBtn.setBackground(ACCENT_GREEN);
        confirmBtn.setForeground(Color.BLACK);
        confirmBtn.addActionListener(e -> {
            dialog.dispose();
            simulateLoading("Verifying Payment...", () -> processPaymentResult("1"));
        });

        JButton cancelBtn = new JButton("CANCEL");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new GridLayout(1, 2));
        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);

        dialog.add(info, BorderLayout.NORTH);
        dialog.add(qrPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void simulateLoading(String msg, Runnable onComplete) {
        JDialog loadingDialog = new JDialog(this, "Processing", true);
        loadingDialog.setUndecorated(true);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new LineBorder(ACCENT_CYAN, 2));
        
        JLabel lbl = new JLabel(msg, SwingConstants.CENTER);
        lbl.setForeground(TEXT_WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(BG_DARK);
        progressBar.setForeground(ACCENT_CYAN);
        
        panel.add(lbl, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        loadingDialog.add(panel);

        new Timer(2000, e -> {
            ((Timer)e.getSource()).stop();
            loadingDialog.dispose();
            onComplete.run();
        }).start();

        loadingDialog.setVisible(true);
    }

    private void processPaymentResult(String method) {
        double total = controller.getCartTotal();
        boolean success = controller.processPayment(total, method);
        if (success) {
            JOptionPane.showMessageDialog(this, "Payment Successful!\nDispensing items...");
            controller.clearCart();
            refreshUI();
            showStatus("Transaction Complete", false);
        } else {
            showStatus("Error: Payment Failed", true);
        }
    }

    private void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg.toUpperCase());
        statusLabel.setForeground(isError ? ACCENT_RED : ACCENT_GREEN);
        if (isError) {
            Timer blink = new Timer(200, null);
            blink.addActionListener(e -> {
                statusLabel.setVisible(!statusLabel.isVisible());
                if(blink.getDelay() > 1000) {
                    statusLabel.setVisible(true);
                    blink.stop();
                }
                blink.setDelay(blink.getDelay() + 200);
            });
            blink.start();
        }
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }

    // MAIN METHOD
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        SwingUtilities.invokeLater(() -> new ModernVendingUI());
    }
}