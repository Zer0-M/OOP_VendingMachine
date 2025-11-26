package vendingmachine.admin;

import vendingmachine.VendingMachineController;
import vendingmachine.products.ItemSlot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class AdminUI extends JFrame {

    private VendingMachineController controller;
    private Runnable onUpdateCallback;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JLabel cashLabel;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField stockField;
    private JLabel selectedItemLabel; // โชว์ว่ากำลังแก้ตัวไหน

    // --- ADMIN THEME ---
    private final Color BG_ADMIN = new Color(240, 242, 245);
    private final Color PANEL_WHITE = Color.WHITE;
    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Color TEXT_DARK = new Color(50, 50, 60);
    private final Color TEXT_GOLD = new Color(255, 172, 51);

    public AdminUI(VendingMachineController controller, Runnable onUpdateCallback) {
        this.controller = controller;
        this.onUpdateCallback = onUpdateCallback;

        setTitle("Admin Dashboard - Vending Machine System");
        setSize(900, 650);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_ADMIN);

        // --- 1. HEADER (Dashboard Stats) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("ADMIN CONTROL PANEL");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // Cash Display Box
        JPanel cashBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cashBox.setOpaque(false);

        JLabel cashIcon = new JLabel("🪙");
        cashIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        cashIcon.setForeground(TEXT_GOLD);
        cashIcon.setPreferredSize(new Dimension(35, 50));

        cashLabel = new JLabel("Total Cash: Loading...");
        cashLabel.setForeground(Color.WHITE);
        cashLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        cashBox.add(cashIcon);
        cashBox.add(cashLabel);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(cashBox, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. TABLE (Inventory List) ---
        String[] columnNames = { "SLOT", "PRODUCT NAME", "PRICE (THB)", "STOCK QTY" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        styleTable(productTable); // เรียกฟังก์ชันแต่งตาราง

        // Selection Logic
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                int row = productTable.getSelectedRow();
                String name = tableModel.getValueAt(row, 1).toString();
                String price = tableModel.getValueAt(row, 2).toString();

                selectedItemLabel.setText("Editing: " + name);
                selectedItemLabel.setForeground(PRIMARY_COLOR);

                nameField.setText(name);
                priceField.setText(price);
                stockField.setText("0"); // Reset stock add input
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. CONTROLS (Bottom Panel) ---
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        bottomPanel.setBackground(BG_ADMIN);
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // LEFT: Edit Form
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBackground(PANEL_WHITE);
        editPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(15, 20, 15, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        selectedItemLabel = new JLabel("Select an item to edit");
        selectedItemLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectedItemLabel.setForeground(Color.GRAY);

        priceField = new JTextField(10);
        stockField = new JTextField(10);

        JButton updateBtn = new JButton("SAVE CHANGES");
        updateBtn.setBackground(PRIMARY_COLOR);
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Form Layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        editPanel.add(selectedItemLabel, gbc);

        // 2. [เพิ่มใหม่] ช่องแก้ชื่อ (Edit Name)
        nameField = new JTextField(15); // สร้าง TextField
        gbc.gridwidth = 1;
        gbc.gridy++; // ขยับลง 1 บรรทัด (เป็นบรรทัดที่ 1)

        gbc.gridx = 0;
        editPanel.add(new JLabel("Edit Name:"), gbc);
        gbc.gridx = 1;
        editPanel.add(nameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        editPanel.add(new JLabel("Set Price:"), gbc);
        gbc.gridx = 1;
        editPanel.add(priceField, gbc);

        gbc.gridy++; // ขยับลง (เป็นบรรทัดที่ 3)
        gbc.gridx = 0;
        editPanel.add(new JLabel("Add Stock (+):"), gbc);
        gbc.gridx = 1;
        editPanel.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2; // บรรทัดที่ 4
        editPanel.add(updateBtn, gbc);

        // RIGHT: Global Actions
        JPanel actionPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        actionPanel.setOpaque(false);

        JButton collectCashBtn = new JButton("📤 COLLECT CASH");
        collectCashBtn.setBackground(new Color(40, 167, 69)); // Green
        collectCashBtn.setForeground(Color.WHITE);
        collectCashBtn.setFont(new Font("Noto Color Emoji", Font.BOLD, 13));

        // [เพิ่มปุ่มใหม่]
        JButton addProductBtn = new JButton("➕ ADD NEW ITEM");
        addProductBtn.setBackground(PRIMARY_COLOR);
        addProductBtn.setForeground(Color.WHITE);
        addProductBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));

        // [NEW BUTTONS] ปุ่มสำหรับจัดการสมาชิก (ให้วางโค้ดนี้ต่อจากตรงนี้)
        JButton viewMemberBtn = new JButton("👥 VIEW MEMBERS");
        viewMemberBtn.setBackground(new Color(60, 60, 70));
        viewMemberBtn.setForeground(Color.WHITE);
        viewMemberBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));

        // JButton saveMemberBtn = new JButton("💾 SAVE MEMBER DATA");
        // saveMemberBtn.setBackground(new Color(255, 172, 51));
        // saveMemberBtn.setForeground(Color.WHITE);
        // saveMemberBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        // -----------------

        // --- [NEW BUTTONS CODE BLOCK] ---
        // ปุ่ม Save Stock
        JButton saveStockBtn = new JButton("💾 SAVE STOCK");
        saveStockBtn.setBackground(new Color(255, 87, 34)); // สีส้มเท่ๆ
        saveStockBtn.setForeground(Color.WHITE);
        saveStockBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        
        saveStockBtn.addActionListener(e -> {
            controller.adminSaveStock();
            JOptionPane.showMessageDialog(this, "Inventory Saved Successfully!", "System", JOptionPane.INFORMATION_MESSAGE);
        });

        // ปุ่ม Load Stock (ถ้าอยากให้โหลดได้ด้วย)
        JButton loadStockBtn = new JButton("📂 LOAD STOCK");
        loadStockBtn.setBackground(new Color(33, 150, 243)); // สีฟ้า
        loadStockBtn.setForeground(Color.WHITE);
        loadStockBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        
        loadStockBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Loading stock will replace current items. Continue?", 
                "Warning", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                controller.adminLoadStock();
                if (onUpdateCallback != null) onUpdateCallback.run(); // รีเฟรชหน้าหลัก
                JOptionPane.showMessageDialog(this, "Inventory Loaded!", "System", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        actionPanel.add(collectCashBtn);
        actionPanel.add(addProductBtn);
        // [เพิ่มปุ่มใหม่ 2 ปุ่มตรงนี้]
        actionPanel.add(viewMemberBtn); 
        // actionPanel.add(saveMemberBtn);
        actionPanel.add(saveStockBtn);
        actionPanel.add(loadStockBtn);
        // -----------------------------

        bottomPanel.add(editPanel, BorderLayout.CENTER);

        actionPanel.add(collectCashBtn);
        actionPanel.add(addProductBtn);

        bottomPanel.add(editPanel, BorderLayout.CENTER);
        bottomPanel.add(actionPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- LOGIC BINDING ---

        updateBtn.addActionListener(e -> updateSelectedItem());

        collectCashBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this,
                    "Current Machine Cash: " + controller.getMachineCurrentCash() + "\n\n" +
                            "Enter amount to collect:",
                    "Withdraw Cash", JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.isEmpty()) {
                try {
                    double amount = Double.parseDouble(input);
                    String result = controller.adminWithdrawCash(amount);
                    JOptionPane.showMessageDialog(this, result);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number!");
                }
            }
        });

        addProductBtn.addActionListener(e -> showAddProductDialog());

        // [NEW LOGIC BINDING] ให้วางโค้ดนี้ต่อจากบรรทัดด้านบนสุด
        viewMemberBtn.addActionListener(e -> {
            String members = controller.getMemberDatabase().getAllMembersDisplay();
            JOptionPane.showMessageDialog(this, members, "Member List", JOptionPane.PLAIN_MESSAGE);
        });

        // saveMemberBtn.addActionListener(e -> {
        //     controller.adminSaveMemberData();
        //     JOptionPane.showMessageDialog(this, "Member data successfully saved to member_data.txt!", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
        // });
        // ----------------------

        // Initial Data Load
        setVisible(true);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(230, 230, 235));
        header.setForeground(TEXT_DARK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 40));

        // Zebra Striping
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                } else {
                    c.setBackground(new Color(220, 240, 255));
                    c.setForeground(Color.BLACK);
                }
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10)); // Padding
                return c;
            }
        });
    }

    private void updateSelectedItem() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the table first.");
            return;
        }

        String code = (String) tableModel.getValueAt(row, 0);

        try {
            String newName = nameField.getText();
            double newPrice = Double.parseDouble(priceField.getText());
            int stockToAdd = Integer.parseInt(stockField.getText());

            if (!newName.trim().isEmpty()) {
                controller.adminSetName(code, newName); // [2] สั่ง Controller เปลี่ยนชื่อ
            }
            controller.adminSetPrice(code, newPrice);
            if (stockToAdd > 0) {
                controller.adminRestockItem(code, stockToAdd);
            }

            JOptionPane.showMessageDialog(this, "Update Success!");
            // [3] เพิ่มส่วนนี้: สั่งให้หน้าหลักอัปเดตด้วย!
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
            // ------------------------------------

            // Clear input
            stockField.setText("0");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Number Format.");
        }
    }

    private void showAddProductDialog() {
        // สร้าง Panel สำหรับกรอกข้อมูล
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        JTextField slotField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField qtyField = new JTextField();

        String[] types = { "Snack", "Drink" };
        JComboBox<String> typeBox = new JComboBox<>(types);

        JTextField sizeField = new JTextField(); // Weight or Volume

        panel.add(new JLabel("Slot Code (e.g. C1):"));
        panel.add(slotField);

        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Price (THB):"));
        panel.add(priceField);

        panel.add(new JLabel("Initial Stock:"));
        panel.add(qtyField);

        panel.add(new JLabel("Type:"));
        panel.add(typeBox);

        panel.add(new JLabel("Size (g / ml):"));
        panel.add(sizeField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add New Product", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String slot = slotField.getText().trim();
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText());
                int qty = Integer.parseInt(qtyField.getText());
                String type = (String) typeBox.getSelectedItem();
                double size = Double.parseDouble(sizeField.getText());

                if (slot.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                    return;
                }

                // เรียก Controller
                controller.adminAddProduct(slot, name, price, qty, type, size);

                JOptionPane.showMessageDialog(this, "Product Added Successfully!");

                // สั่งหน้าหลักอัปเดตด้วย (Callback)
                if (onUpdateCallback != null)
                    onUpdateCallback.run();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Number Format!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}