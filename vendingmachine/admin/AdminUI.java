package vendingmachine.admin;

import vendingmachine.ModernVendingUI;
import vendingmachine.VendingMachineController;
import vendingmachine.products.ItemSlot;
import vendingmachine.products.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class AdminUI extends JFrame {

    private VendingMachineController controller;
    private ModernVendingUI mainUI;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JLabel totalMoneyLabel; // <--- พระเอกของเรา: ป้ายแสดงยอดเงิน

    // Theme Colors (ใช้ชุดสีเดียวกับ ModernVendingUI)
    private final Color BG_DARK = new Color(20, 20, 25);
    private final Color BG_PANEL = new Color(35, 35, 40);
    private final Color ACCENT_CYAN = new Color(0, 220, 220);
    private final Color TEXT_WHITE = new Color(240, 240, 240);

    public AdminUI(VendingMachineController controller, ModernVendingUI mainUI) {
        this.controller = controller;
        this.mainUI = mainUI;

        setTitle("Admin Control Panel");
        setSize(900, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);
        setLocationRelativeTo(null); // จัดให้อยู่กลางจอ

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("STOCK & PRICE MANAGER");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ACCENT_CYAN);
        header.add(title, BorderLayout.WEST);
        
        add(header, BorderLayout.NORTH);

        // --- CENTER: PRODUCT TABLE ---
        // ใช้ Table เพื่อให้คลิกเลือก item ได้ง่ายๆ
        String[] columns = {"Code", "Name", "Price (THB)", "Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // ห้ามแก้ในตารางตรงๆ ต้องกดปุ่มเอา
            }
        };
        
        productTable = new JTable(tableModel);
        productTable.setBackground(BG_PANEL);
        productTable.setForeground(TEXT_WHITE);
        productTable.setFont(new Font("Consolas", Font.PLAIN, 14));
        productTable.setRowHeight(30);
        productTable.getTableHeader().setBackground(new Color(60,60,70));
        productTable.getTableHeader().setForeground(TEXT_WHITE);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // --- RIGHT SIDEBAR: ACTIONS ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_PANEL);
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        sidebar.setPreferredSize(new Dimension(280, 0));

        JLabel actionLbl = new JLabel("ACTIONS");
        actionLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        actionLbl.setForeground(Color.WHITE);
        actionLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- NEW FEATURE: SHOW TOTAL MONEY (ส่วนที่เพิ่มใหม่) ---
        JPanel moneyPanel = new JPanel(new BorderLayout());
        moneyPanel.setBackground(new Color(50, 50, 60));
        moneyPanel.setBorder(new LineBorder(ACCENT_CYAN, 1, true));
        moneyPanel.setMaximumSize(new Dimension(240, 80));
        
        JLabel moneyTitle = new JLabel("CURRENT BALANCE", SwingConstants.CENTER);
        moneyTitle.setForeground(Color.GRAY);
        moneyTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        moneyTitle.setBorder(new EmptyBorder(5,0,0,0));
        
        // ตรงนี้จะโชว์ยอดเงิน
        totalMoneyLabel = new JLabel("0.00 THB", SwingConstants.CENTER);
        totalMoneyLabel.setForeground(new Color(0, 255, 128)); // สีเขียวสะท้อนแสง สวยๆ
        totalMoneyLabel.setFont(new Font("Consolas", Font.BOLD, 28));
        
        moneyPanel.add(moneyTitle, BorderLayout.NORTH);
        moneyPanel.add(totalMoneyLabel, BorderLayout.CENTER);
        
        // --- BUTTONS ---
        JButton restockBtn = createButton("Restock Selected", new Color(0, 150, 100));
        restockBtn.addActionListener(e -> handleRestock());

        JButton priceBtn = createButton("Set Price Selected", new Color(0, 100, 200));
        priceBtn.addActionListener(e -> handleSetPrice());

        JButton collectBtn = createButton("Collect Cash", new Color(220, 150, 0));
        collectBtn.addActionListener(e -> handleCollectCash());

        sidebar.add(actionLbl);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(moneyPanel); // แปะป้ายแสดงเงินตรงนี้
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(restockBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(priceBtn);
        sidebar.add(Box.createVerticalGlue()); // ดันปุ่ม Collect ไปล่างสุด
        sidebar.add(collectBtn);
        
        add(sidebar, BorderLayout.EAST);

        refreshData(); // โหลดข้อมูลครั้งแรก
        setVisible(true);
    }
    
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 45));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        return btn;
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        // 1. ดึงรายการสินค้ามาแสดง
        Map<String, ItemSlot> slots = controller.getProductList(); 
        
        for (ItemSlot slot : slots.values()) {
            Product p = slot.getProduct();
            tableModel.addRow(new Object[]{
                slot.getSlotCode(),
                p.getName(),
                p.getPrice(),
                slot.getQuantity()
            });
        }
        
        // 2. --- UPDATE MONEY LABEL ---
        // TODO: เช็ค Controller ของนายว่ามี method getCollectedCash() ไหม?
        double currentCash = 0.0;
        try {
             // สมมติชื่อ method ว่า getCollectedCash()
             // ถ้านายเก็บเงินไว้ในตัวแปรชื่ออะไร ก็เขียน getter มาดึงค่านั้น
             currentCash = controller.getCollectedCash(); 
        } catch (Exception e) {
             // ถ้าไม่มี method นี้ มันจะ error เงียบๆ หรือแจ้งเตือนใน console
             System.err.println("Error: หา method getCollectedCash() ใน Controller ไม่เจอ หรือยังไม่ได้สร้าง");
        }
        totalMoneyLabel.setText(String.format("%.2f THB", currentCash));
    }

    private void handleRestock() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item first!");
            return;
        }
        String code = (String) tableModel.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this, "Add quantity for " + code + ":");
        if (input != null && !input.isEmpty()) {
            try {
                int qty = Integer.parseInt(input);
                controller.restock(code, qty); // เรียก method เติมของใน Controller
                refreshData();
                mainUI.externalRefresh();
                JOptionPane.showMessageDialog(this, "Restocked successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number!");
            }
        }
    }

    private void handleSetPrice() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item first!");
            return;
        }
        String code = (String) tableModel.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this, "Set new price for " + code + ":");
        if (input != null && !input.isEmpty()) {
            try {
                double price = Double.parseDouble(input);
                controller.setPrice(code, price); // เรียก method แก้ราคา
                refreshData();
                mainUI.externalRefresh();
                JOptionPane.showMessageDialog(this, "Price updated!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price!");
            }
        }
    }

    private void handleCollectCash() {
        // กดปุ่มแล้วค่อยถอนเงินออก
        double amount = controller.collectCash(); 
        JOptionPane.showMessageDialog(this, "Collected " + amount + " THB from machine.");
        refreshData(); // รีเฟรชหน้าจอ (ตัวเลขควรกลายเป็น 0.00)
    }
}