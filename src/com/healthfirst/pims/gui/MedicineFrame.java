package com.healthfirst.pims.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import com.healthfirst.pims.database.DatabaseConnection;

public class MedicineFrame extends JFrame {

    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public MedicineFrame() {

        setTitle("Medicine Management");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(245, 247, 250));

        // =====================================================
        // TITLE
        // =====================================================

        JLabel lblTitle = new JLabel("Medicine Management");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setBounds(30, 20, 400, 40);
        mainPanel.add(lblTitle);

        // =====================================================
        // SEARCH
        // =====================================================

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSearch.setBounds(30, 80, 70, 30);
        mainPanel.add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(90, 80, 300, 30);
        mainPanel.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(400, 80, 100, 30);
        btnSearch.setFocusPainted(false);
        mainPanel.add(btnSearch);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(510, 80, 100, 30);
        btnRefresh.setFocusPainted(false);
        mainPanel.add(btnRefresh);

        JButton btnAdd = new JButton("Add Medicine");
        btnAdd.setBounds(620, 80, 130, 30);
        btnAdd.setBackground(new Color(46, 125, 50));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        mainPanel.add(btnAdd);

        // =====================================================
        // TABLE
        // =====================================================

        tableModel = new DefaultTableModel(
                new String[]{
                        "ID",
                        "Medicine",
                        "Company",
                        "Type",
                        "Price",
                        "Stock",
                        "Reorder",
                        "Expiry Date",
                        "Supplier"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        medicineTable = new JTable(tableModel);

        medicineTable.setRowHeight(28);
        medicineTable.setFont(
                new Font("Arial", Font.PLAIN, 12)
        );

        medicineTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        JScrollPane scrollPane =
                new JScrollPane(medicineTable);

        scrollPane.setBounds(
                30,
                130,
                1030,
                330
        );

        mainPanel.add(scrollPane);

        // =====================================================
        // BUTTONS
        // =====================================================

        JButton btnUpdate =
                new JButton("Update");

        btnUpdate.setBounds(
                560,
                490,
                120,
                40
        );

        btnUpdate.setFocusPainted(false);
        mainPanel.add(btnUpdate);

        JButton btnDelete =
                new JButton("Delete");

        btnDelete.setBounds(
                690,
                490,
                120,
                40
        );

        btnDelete.setBackground(
                new Color(198, 40, 40)
        );

        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        mainPanel.add(btnDelete);

        JButton btnClose =
                new JButton("Close");

        btnClose.setBounds(
                820,
                490,
                120,
                40
        );

        btnClose.setFocusPainted(false);
        mainPanel.add(btnClose);

        // =====================================================
        // BUTTON EVENTS
        // =====================================================

        btnSearch.addActionListener(
                e -> searchMedicines()
        );

        btnRefresh.addActionListener(e -> {

            txtSearch.setText("");

            loadMedicines();
        });

        btnAdd.addActionListener(e -> {

            AddMedicineFrame frame =
                    new AddMedicineFrame();

            frame.setVisible(true);
        });

        btnUpdate.addActionListener(
                e -> updateMedicine()
        );

        btnDelete.addActionListener(
                e -> deleteMedicine()
        );

        btnClose.addActionListener(
                e -> dispose()
        );

        // =====================================================
        // LOAD DATA
        // =====================================================

        loadMedicines();

        add(mainPanel);
    }

    // =========================================================
    // LOAD MEDICINES
    // =========================================================

    private void loadMedicines() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT m.medicine_id, " +
                "m.name, " +
                "m.company, " +
                "m.medicine_type, " +
                "m.price, " +
                "m.quantity_in_stock, " +
                "m.reorder_level, " +
                "m.expiry_date, " +
                "s.name AS supplier_name " +
                "FROM medicines m " +
                "LEFT JOIN suppliers s " +
                "ON m.supplier_id = s.supplier_id " +
                "ORDER BY m.medicine_id DESC";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery()
        ) {

            while (rs.next()) {

                tableModel.addRow(
                        new Object[]{
                                rs.getInt("medicine_id"),
                                rs.getString("name"),
                                rs.getString("company"),
                                rs.getString("medicine_type"),
                                rs.getBigDecimal("price"),
                                rs.getInt("quantity_in_stock"),
                                rs.getInt("reorder_level"),
                                rs.getDate("expiry_date"),
                                rs.getString("supplier_name")
                        }
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading medicines:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void searchMedicines() {

        String search =
                txtSearch.getText().trim();

        if (search.isEmpty()) {

            loadMedicines();

            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT m.medicine_id, " +
                "m.name, " +
                "m.company, " +
                "m.medicine_type, " +
                "m.price, " +
                "m.quantity_in_stock, " +
                "m.reorder_level, " +
                "m.expiry_date, " +
                "s.name AS supplier_name " +
                "FROM medicines m " +
                "LEFT JOIN suppliers s " +
                "ON m.supplier_id = s.supplier_id " +
                "WHERE m.name LIKE ? " +
                "OR m.company LIKE ? " +
                "OR m.medicine_type LIKE ? " +
                "OR s.name LIKE ? " +
                "ORDER BY m.medicine_id DESC";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            String keyword =
                    "%" + search + "%";

            pst.setString(1, keyword);
            pst.setString(2, keyword);
            pst.setString(3, keyword);
            pst.setString(4, keyword);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                tableModel.addRow(
                        new Object[]{
                                rs.getInt("medicine_id"),
                                rs.getString("name"),
                                rs.getString("company"),
                                rs.getString("medicine_type"),
                                rs.getBigDecimal("price"),
                                rs.getInt("quantity_in_stock"),
                                rs.getInt("reorder_level"),
                                rs.getDate("expiry_date"),
                                rs.getString("supplier_name")
                        }
                );
            }

            rs.close();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error searching medicines:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // UPDATE MEDICINE
    // =========================================================

    private void updateMedicine() {

        int selectedRow =
                medicineTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine to update.",
                    "No Medicine Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int medicineId =
                Integer.parseInt(
                        medicineTable
                                .getValueAt(
                                        selectedRow,
                                        0
                                )
                                .toString()
                );

        String name =
                String.valueOf(
                        medicineTable.getValueAt(
                                selectedRow,
                                1
                        )
                );

        String company =
                String.valueOf(
                        medicineTable.getValueAt(
                                selectedRow,
                                2
                        )
                );

        String type =
                String.valueOf(
                        medicineTable.getValueAt(
                                selectedRow,
                                3
                        )
                );

        String price =
                String.valueOf(
                        medicineTable.getValueAt(
                                selectedRow,
                                4
                        )
                );

        String stock =
                String.valueOf(
                        medicineTable.getValueAt(
                                selectedRow,
                                5
                        )
                );

        String reorder =
                String.valueOf(
                        medicineTable.getValueAt(
                                selectedRow,
                                6
                        )
                );

        String expiry =
                String.valueOf(
                        medicineTable.getValueAt(
                                selectedRow,
                                7
                        )
                );

        JTextField txtName =
                new JTextField(name);

        JTextField txtCompany =
                new JTextField(company);

        JTextField txtType =
                new JTextField(type);

        JTextField txtPrice =
                new JTextField(price);

        JTextField txtStock =
                new JTextField(stock);

        JTextField txtReorder =
                new JTextField(reorder);

        JTextField txtExpiry =
                new JTextField(expiry);

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                7,
                                2,
                                10,
                                10
                        )
                );

        panel.add(
                new JLabel("Medicine Name:")
        );

        panel.add(txtName);

        panel.add(
                new JLabel("Company:")
        );

        panel.add(txtCompany);

        panel.add(
                new JLabel("Medicine Type:")
        );

        panel.add(txtType);

        panel.add(
                new JLabel("Price:")
        );

        panel.add(txtPrice);

        panel.add(
                new JLabel("Stock:")
        );

        panel.add(txtStock);

        panel.add(
                new JLabel("Reorder Level:")
        );

        panel.add(txtReorder);

        panel.add(
                new JLabel("Expiry Date (YYYY-MM-DD):")
        );

        panel.add(txtExpiry);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Update Medicine",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String newName =
                    txtName.getText().trim();

            String newCompany =
                    txtCompany.getText().trim();

            String newType =
                    txtType.getText().trim();

            double newPrice =
                    Double.parseDouble(
                            txtPrice.getText().trim()
                    );

            int newStock =
                    Integer.parseInt(
                            txtStock.getText().trim()
                    );

            int newReorder =
                    Integer.parseInt(
                            txtReorder.getText().trim()
                    );

            Date newExpiry =
                    Date.valueOf(
                            txtExpiry.getText().trim()
                    );

            if (newName.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Medicine name is required.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (newPrice < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Price cannot be negative.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (newStock < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Stock cannot be negative.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (newReorder < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Reorder level cannot be negative.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            String sql =
                    "UPDATE medicines SET " +
                    "name = ?, " +
                    "company = ?, " +
                    "medicine_type = ?, " +
                    "price = ?, " +
                    "quantity_in_stock = ?, " +
                    "reorder_level = ?, " +
                    "expiry_date = ? " +
                    "WHERE medicine_id = ?";

            try (
                    Connection con =
                            DatabaseConnection.getConnection();

                    PreparedStatement pst =
                            con.prepareStatement(sql)
            ) {

                pst.setString(1, newName);
                pst.setString(2, newCompany);
                pst.setString(3, newType);
                pst.setDouble(4, newPrice);
                pst.setInt(5, newStock);
                pst.setInt(6, newReorder);
                pst.setDate(7, newExpiry);
                pst.setInt(8, medicineId);

                pst.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Medicine updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadMedicines();
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers for price, stock and reorder level.",
                    "Invalid Data",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Expiry date must use YYYY-MM-DD format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating medicine:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // DELETE MEDICINE
    // =========================================================

    private void deleteMedicine() {

        int selectedRow =
                medicineTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine to delete.",
                    "No Medicine Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int medicineId =
                Integer.parseInt(
                        medicineTable
                                .getValueAt(
                                        selectedRow,
                                        0
                                )
                                .toString()
                );

        String medicineName =
                medicineTable
                        .getValueAt(
                                selectedRow,
                                1
                        )
                        .toString();

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete:\n\n"
                                + medicineName
                                + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM medicines " +
                "WHERE medicine_id = ?";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            pst.setInt(1, medicineId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Medicine deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadMedicines();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting medicine:\n"
                            + ex.getMessage()
                            + "\n\n"
                            + "If this medicine has already been used in a sale, "
                            + "the database may prevent deletion.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new MedicineFrame().setVisible(true);

        });
    }
}