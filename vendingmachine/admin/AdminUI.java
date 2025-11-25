package vendingmachine.admin;
import vendingmachine.products.ItemSlot;

import vendingmachine.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class AdminUI extends JFrame {

    private VendingMachineController controller;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JLabel cashLabel;
    private JTextField priceField;
    private JTextField stockField;

    public AdminUI(VendingMachineController controller) {
        this.controller = controller;

        setTitle("ADMIN CONTROL PANEL");
        setSize(850, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // กลางจอ
        
        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 40, 40));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("SYSTEM ADMINISTRATION");
        title.setForeground(Color.ORANGE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        cashLabel = new JLabel("Machine Cash: Loading...");
        cashLabel.setForeground(Color.CYAN);
        cashLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(cashLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Table (Center) ---
        String[] columnNames = {"Slot Code", "Product Name", "Price (THB)", "Stock Qty"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // ห้ามแก้ในตารางตรงๆ
            }
        };
        productTable = new JTable(tableModel);
        productTable.setRowHeight(30);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Event: คลิกแถวแล้วดึงข้อมูลมาใส่ช่อง Input
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                int row = productTable.getSelectedRow();
                priceField.setText(tableModel.getValueAt(row, 2).toString());
                stockField.setText("0"); // รีเซ็ตช่องเติมของเป็น 0
            }
        });

        add(new JScrollPane(productTable), BorderLayout.CENTER);

        // --- Controls (Bottom) ---
        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        
        // Row 1: Edit Inputs
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        editPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        priceField = new JTextField(8);
        stockField = new JTextField(8);
        JButton updateBtn = new JButton("UPDATE ITEM");
        updateBtn.setBackground(new Color(0, 150, 200));
        updateBtn.setForeground(Color.WHITE);

        editPanel.add(new JLabel("Set Price:"));
        editPanel.add(priceField);
        editPanel.add(new JLabel("Add Stock (+):")); 
        editPanel.add(stockField);
        editPanel.add(updateBtn);
        
        // Row 2: Global Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton collectCashBtn = new JButton("COLLECT ALL CASH");
        collectCashBtn.setBackground(new Color(200, 50, 50));
        collectCashBtn.setForeground(Color.WHITE);
        
        JButton refreshBtn = new JButton("Refresh Data");
        
        actionPanel.add(collectCashBtn);
        actionPanel.add(refreshBtn);

        controlPanel.add(editPanel);
        controlPanel.add(actionPanel);
        add(controlPanel, BorderLayout.SOUTH);

        // --- Actions Logic ---
        
        updateBtn.addActionListener(e -> updateSelectedItem());
        
        collectCashBtn.addActionListener(e -> {
            controller.adminCollectCash();
            JOptionPane.showMessageDialog(this, "Cash collected from machine!");
            refreshData();
        });
        
        refreshBtn.addActionListener(e -> refreshData());

        // Load Initial Data
        refreshData();
        setVisible(true);
    }

    private void refreshData() {
        // 1. อัปเดตเงินสด
        double currentCash = controller.getMachineCurrentCash();
        cashLabel.setText(String.format("Machine Cash: %.2f THB", currentCash));

        // 2. อัปเดตตาราง
        tableModel.setRowCount(0);
        Map<String, ItemSlot> slots = new TreeMap<>(controller.getProductList());
        
        for (ItemSlot slot : slots.values()) {
            tableModel.addRow(new Object[]{
                slot.getSlotCode(),
                slot.getProduct().getName(),
                slot.getProduct().getPrice(),
                slot.getQuantity()
            });
        }
    }

    private void updateSelectedItem() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item first.");
            return;
        }

        String code = (String) tableModel.getValueAt(row, 0);
        
        try {
            double newPrice = Double.parseDouble(priceField.getText());
            int stockToAdd = Integer.parseInt(stockField.getText());

            // 1. แก้ราคา
            controller.adminSetPrice(code, newPrice);
            
            // 2. เติมของ (ถ้ากรอกตัวเลขมากกว่า 0)
            if (stockToAdd > 0) {
                 controller.adminRestockItem(code, stockToAdd);
            }

            JOptionPane.showMessageDialog(this, "Update Success!");
            refreshData();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Number Format. Please enter valid numbers.");
        }
    }
}