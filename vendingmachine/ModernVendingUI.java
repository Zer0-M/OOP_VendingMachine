package vendingmachine;

import vendingmachine.products.ItemSlot;
import vendingmachine.products.Product;
import vendingmachine.admin.AdminUI;
import vendingmachine.admin.AdminService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class ModernVendingUI extends JFrame {

    private VendingMachineController controller;
    private JPanel productGridPanel;
    private DefaultListModel<String> cartListModel;
    private JList<String> cartList;
    private JLabel totalLabel;
    private JLabel statusLabel;

    // --- MODERN PALETTE ---
    private final Color BG_MAIN = new Color(18, 18, 24);       // พื้นหลังหลัก
    private final Color BG_SIDEBAR = new Color(28, 28, 36);    // พื้นหลัง Sidebar
    private final Color CARD_BG = new Color(35, 35, 45);       // สีการ์ดสินค้า
    private final Color ACCENT_PRIMARY = new Color(88, 101, 242); // สีม่วงฟ้า (Add/Primary)
    private final Color ACCENT_SUCCESS = new Color(59, 165, 93);  // สีเขียว (Pay)
    private final Color ACCENT_DANGER = new Color(237, 66, 69);   // สีแดง (Remove/Error)
    private final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private final Color TEXT_SECONDARY = new Color(185, 187, 190);

    public ModernVendingUI() {
        controller = new VendingMachineController();

        // Setup Main Window
        setTitle("Vending Machine PRO 2024");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        // --- 1. HEADER (Top Bar) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_SIDEBAR);
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
        // เพิ่มเงาใต้ Header เล็กน้อย
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 45, 55)),
                new EmptyBorder(15, 30, 15, 30)
        ));

        JLabel title = new JLabel("VENDING OS 3.0");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ACCENT_PRIMARY);
        title.setIcon(new TextIcon("💎", 28)); // ใส่ไอคอนเก๋ๆ

        // ปุ่ม Admin แบบ Minimal
        JButton adminBtn = new JButton("⚙ ADMIN");
        styleGhostButton(adminBtn);
        adminBtn.addActionListener(e -> openAdminPanel());

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        rightHeader.setOpaque(false);
        
        statusLabel = new JLabel("SYSTEM ONLINE ●");
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        statusLabel.setForeground(ACCENT_SUCCESS);
        
        rightHeader.add(statusLabel);
        rightHeader.add(adminBtn);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTER (Product Grid) ---
        productGridPanel = new JPanel();
        productGridPanel.setBackground(BG_MAIN);
        productGridPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 25, 25)); // Grid สวยๆ
        
        JScrollPane scrollPane = new JScrollPane(productGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BG_MAIN);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. RIGHT SIDEBAR (Cart & Controls) ---
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(380, 0));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Cart Section
        JPanel cartHeaderPanel = new JPanel(new BorderLayout());
        cartHeaderPanel.setOpaque(false);
        JLabel cartTitle = new JLabel("YOUR CART");
        cartTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cartTitle.setForeground(TEXT_PRIMARY);
        cartHeaderPanel.add(cartTitle, BorderLayout.WEST);
        cartHeaderPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        cartList.setBackground(new Color(22, 22, 28));
        cartList.setForeground(TEXT_PRIMARY);
        cartList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartList.setFixedCellHeight(35);
        cartList.setBorder(new LineBorder(new Color(45, 45, 55), 1));
        
        // Custom Scrollbar for Cart
        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setBorder(null);

        JPanel cartContainer = new JPanel(new BorderLayout());
        cartContainer.setOpaque(false);
        cartContainer.add(cartHeaderPanel, BorderLayout.NORTH);
        cartContainer.add(cartScroll, BorderLayout.CENTER);
        
        JButton removeBtn = createModernButton("REMOVE SELECTED", new Color(60, 40, 40), ACCENT_DANGER);
        removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        removeBtn.setPreferredSize(new Dimension(0, 40));
        removeBtn.addActionListener(e -> handleRemoveItem());
        
        JPanel cartActionPanel = new JPanel(new BorderLayout());
        cartActionPanel.setOpaque(false);
        cartActionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        cartActionPanel.add(removeBtn, BorderLayout.CENTER);
        
        cartContainer.add(cartActionPanel, BorderLayout.SOUTH);

        sidebar.add(cartContainer, BorderLayout.CENTER);

        // Bottom Checkout Section
        JPanel checkoutPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        checkoutPanel.setOpaque(false);
        checkoutPanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        // Divider
        checkoutPanel.add(new JSeparator(JSeparator.HORIZONTAL));

        totalLabel = new JLabel("0.00 ฿", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        totalLabel.setForeground(ACCENT_SUCCESS);

        JButton payBtn = createModernButton("CHECKOUT & PAY", ACCENT_SUCCESS, Color.WHITE);
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        payBtn.addActionListener(e -> handleCheckout());

        JButton clearBtn = createModernButton("CLEAR ALL", new Color(45, 45, 50), TEXT_SECONDARY);
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

    // --- LOGIC (ยังคงเดิม 100%) ---

    private void openAdminPanel() {
        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter Admin Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            String password = new String(pf.getPassword());
            if (AdminService.authenticate(password)) {
                new AdminUI(controller); 
            } else {
                JOptionPane.showMessageDialog(this, "Wrong Password! Access Denied.", "Security Alert", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshUI() {
        productGridPanel.removeAll();
        Map<String, ItemSlot> slots = new TreeMap<>(controller.getProductList());

        for (ItemSlot slot : slots.values()) {
            productGridPanel.add(createProductCard(slot));
        }

        cartListModel.clear();
        Map<ItemSlot, Integer> cart = controller.getCart();
        for (Map.Entry<ItemSlot, Integer> entry : cart.entrySet()) {
            String code = entry.getKey().getSlotCode();
            String name = entry.getKey().getProduct().getName();
            int qty = entry.getValue();
            // Format ต้องคงไว้ให้มี [CODE] เพื่อให้ฟังก์ชัน remove ทำงานได้
            cartListModel.addElement(String.format(" [%s] %-15s x%d", code, name, qty));
        }

        totalLabel.setText(String.format("%.2f ฿", controller.getCartTotal()));
        
        productGridPanel.revalidate();
        productGridPanel.repaint();
    }
    
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
            controller.removeProductFromCart(slotCode); 
            refreshUI();
        } catch (Exception e) {
            showStatus("Error removing item", true);
        }
    }

    private JPanel createProductCard(ItemSlot slot) {
        Product p = slot.getProduct();
        boolean isOutOfStock = slot.getQuantity() <= 0;

        // Card Container
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(210, 280));
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        // Rounded border styling
        card.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 60), 1));

        // 1. Image/Icon Area
        JLabel iconLbl = new JLabel(isOutOfStock ? "❌" : "🥤", SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        iconLbl.setPreferredSize(new Dimension(210, 140));
        
        // Code Badge (Overlay trick using layout not easy in Swing without layers, using North for now)
        JLabel codeLbl = new JLabel(" " + slot.getSlotCode() + " ");
        codeLbl.setOpaque(true);
        codeLbl.setBackground(ACCENT_PRIMARY);
        codeLbl.setForeground(Color.WHITE);
        codeLbl.setFont(new Font("Consolas", Font.BOLD, 14));
        codeLbl.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(CARD_BG);
        topPanel.add(codeLbl, BorderLayout.WEST);
        topPanel.add(iconLbl, BorderLayout.CENTER);

        // 2. Info Area
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_BG);
        infoPanel.setBorder(new EmptyBorder(0, 15, 15, 15));

        JLabel nameLbl = new JLabel(p.getName());
        nameLbl.setForeground(TEXT_PRIMARY);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLbl = new JLabel(p.getPrice() + " THB");
        priceLbl.setForeground(ACCENT_SUCCESS);
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel stockLbl = new JLabel("In Stock: " + slot.getQuantity());
        stockLbl.setForeground(isOutOfStock ? ACCENT_DANGER : TEXT_SECONDARY);
        stockLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stockLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(nameLbl);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLbl);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(stockLbl);

        // 3. Button
        JButton addBtn = createModernButton(isOutOfStock ? "SOLD OUT" : "ADD TO CART", 
                isOutOfStock ? new Color(60, 30, 30) : ACCENT_PRIMARY, 
                Color.WHITE);
        addBtn.setPreferredSize(new Dimension(210, 40));
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

        card.add(topPanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(addBtn, BorderLayout.SOUTH);

        return card;
    }

    private void handleCheckout() {
        double total = controller.getCartTotal();
        if (total <= 0) {
            showStatus("Error: Cart is empty", true);
            return;
        }

        String[] options = {"Scan QR Code", "Cash Payment"};
        int choice = JOptionPane.showOptionDialog(this, "Amount Due: " + total + " THB\nChoose payment method:", 
                "Checkout", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == 0) { 
            showQRCodeDialog(total);
        } else if (choice == 1) {
            simulateLoading("Processing Cash...", () -> processPaymentResult("2"));
        }
    }

    // (Code ส่วน QR และ Loading ยังเหมือนเดิม แต่ปรับ UI เล็กน้อย)
    private void showQRCodeDialog(double amount) {
        JDialog dialog = new JDialog(this, "Scan QR to Pay", true);
        dialog.setSize(350, 480);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        dialog.getRootPane().setBorder(new LineBorder(new Color(60,60,60), 2));

        JPanel qrPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);
                int size = 12; // ใหญ่ขึ้น
                Random rand = new Random();
                for (int y = 50; y < 280; y += size) {
                    for (int x = 50; x < 280; x += size) {
                        if(rand.nextBoolean()) g.fillRect(x, y, size, size);
                    }
                }
                // Corners
                g.fillRect(50, 50, 40, 40);
                g.fillRect(240, 50, 40, 40);
                g.fillRect(50, 240, 40, 40);
            }
        };
        qrPanel.setBackground(Color.WHITE);

        JLabel info = new JLabel("SCAN TO PAY: " + amount + " ฿", SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.BOLD, 20));
        info.setOpaque(true);
        info.setBackground(BG_SIDEBAR);
        info.setForeground(TEXT_PRIMARY);
        info.setBorder(new EmptyBorder(20,0,20,0));

        JButton confirmBtn = createModernButton("CONFIRM PAYMENT", ACCENT_SUCCESS, Color.WHITE);
        confirmBtn.addActionListener(e -> {
            dialog.dispose();
            simulateLoading("Verifying Payment...", () -> processPaymentResult("1"));
        });

        JButton cancelBtn = createModernButton("CANCEL", BG_SIDEBAR, TEXT_SECONDARY);
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
        loadingDialog.setSize(350, 120);
        loadingDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_SIDEBAR);
        panel.setBorder(new LineBorder(ACCENT_PRIMARY, 1));
        
        JLabel lbl = new JLabel(msg, SwingConstants.CENTER);
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(new EmptyBorder(20,0,10,0));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(new Color(40,40,50));
        progressBar.setForeground(ACCENT_PRIMARY);
        progressBar.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        panel.add(lbl, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        loadingDialog.add(panel);

        new Timer(1500, e -> {
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
        statusLabel.setForeground(isError ? ACCENT_DANGER : ACCENT_SUCCESS);
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

    // --- HELPER UI METHODS ---
    
    private JButton createModernButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(bg.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private void styleGhostButton(JButton btn) {
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new LineBorder(TEXT_SECONDARY));
        btn.setForeground(TEXT_SECONDARY);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBorder(new LineBorder(ACCENT_PRIMARY));
                btn.setForeground(ACCENT_PRIMARY);
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBorder(new LineBorder(TEXT_SECONDARY));
                btn.setForeground(TEXT_SECONDARY);
            }
        });
    }

    // ไอคอนหลอกๆ (ใช้ Text paint เอา)
    private static class TextIcon implements Icon {
        private String text;
        private int size;
        public TextIcon(String text, int size) { this.text = text; this.size = size; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
            g.drawString(text, x, y + size - 5);
        }
        public int getIconWidth() { return size; }
        public int getIconHeight() { return size; }
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        SwingUtilities.invokeLater(() -> new ModernVendingUI());
    }
}