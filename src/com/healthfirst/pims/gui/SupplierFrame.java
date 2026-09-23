package com.healthfirst.pims.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import com.healthfirst.pims.database.DatabaseConnection;

public class SupplierFrame extends JFrame {

    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public SupplierFrame() {

        setTitle("Supplier Management");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // =========================
        // MAIN PANEL
        // =========================

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(245, 247, 250));

        // =========================
        // TITLE
        // =========================

        JLabel lblTitle = new JLabel("Supplier Management");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setBounds(30, 20, 350, 40);
        mainPanel.add(lblTitle);

        // =========================
        // SEARCH
        // =========================

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

        // =========================
        // REFRESH
        // =========================

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(510, 80, 100, 30);
        btnRefresh.setFocusPainted(false);
        mainPanel.add(btnRefresh);

        // =========================
        // ADD SUPPLIER
        // =========================

        JButton btnAdd = new JButton("Add Supplier");
        btnAdd.setBounds(620, 80, 130, 30);
        btnAdd.setBackground(new Color(46, 125, 50));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        mainPanel.add(btnAdd);

        // =========================
        // TABLE
        // =========================

        tableModel = new DefaultTableModel(
                new String[]{
                        "ID",
                        "Supplier Name",
                        "Contact Person",
                        "Phone",
                        "Email",
                        "Address"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        supplierTable = new JTable(tableModel);

        supplierTable.setRowHeight(28);
        supplierTable.setFont(new Font("Arial", Font.PLAIN, 13));

        supplierTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scrollPane =
                new JScrollPane(supplierTable);

        scrollPane.setBounds(30, 130, 890, 300);
        mainPanel.add(scrollPane);

        // =========================
        // UPDATE BUTTON
        // =========================

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(550, 460, 120, 40);
        btnUpdate.setFocusPainted(false);
        mainPanel.add(btnUpdate);

        // =========================
        // DELETE BUTTON
        // =========================

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(680, 460, 120, 40);
        btnDelete.setBackground(new Color(198, 40, 40));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        mainPanel.add(btnDelete);

        // =========================
        // CLOSE BUTTON
        // =========================

        JButton btnClose = new JButton("Close");
        btnClose.setBounds(810, 460, 110, 40);
        btnClose.setFocusPainted(false);
        mainPanel.add(btnClose);

        // =========================
        // BUTTON ACTIONS
        // =========================

        btnSearch.addActionListener(e ->
                searchSuppliers()
        );

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadSuppliers();
        });

        btnAdd.addActionListener(e -> {

            AddSupplierFrame addSupplierFrame =
                    new AddSupplierFrame();

            addSupplierFrame.setVisible(true);
        });

        btnUpdate.addActionListener(e ->
                updateSupplier()
        );

        btnDelete.addActionListener(e ->
                deleteSupplier()
        );

        btnClose.addActionListener(e ->
                dispose()
        );

        // =========================
        // LOAD SUPPLIERS
        // =========================

        loadSuppliers();

        add(mainPanel);
    }

    // =========================================================
    // LOAD SUPPLIERS
    // =========================================================

    private void loadSuppliers() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT supplier_id, name, contact_person, " +
                "phone, email, address " +
                "FROM suppliers " +
                "ORDER BY supplier_id DESC";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery()
        ) {

            while (rs.next()) {

                tableModel.addRow(new Object[]{

                        rs.getInt("supplier_id"),

                        rs.getString("name"),

                        rs.getString("contact_person"),

                        rs.getString("phone"),

                        rs.getString("email"),

                        rs.getString("address")
                });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading suppliers:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // SEARCH SUPPLIERS
    // =========================================================

    private void searchSuppliers() {

        String search =
                txtSearch.getText().trim();

        if (search.isEmpty()) {

            loadSuppliers();
            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT supplier_id, name, contact_person, " +
                "phone, email, address " +
                "FROM suppliers " +
                "WHERE name LIKE ? " +
                "OR contact_person LIKE ? " +
                "OR phone LIKE ? " +
                "OR email LIKE ? " +
                "OR address LIKE ? " +
                "ORDER BY supplier_id DESC";

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
            pst.setString(5, keyword);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                tableModel.addRow(new Object[]{

                        rs.getInt("supplier_id"),

                        rs.getString("name"),

                        rs.getString("contact_person"),

                        rs.getString("phone"),

                        rs.getString("email"),

                        rs.getString("address")
                });
            }

            rs.close();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error searching suppliers:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // UPDATE SUPPLIER
    // =========================================================

    private void updateSupplier() {

        int selectedRow =
                supplierTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier to update.",
                    "No Supplier Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int supplierId =
                Integer.parseInt(
                        supplierTable
                                .getValueAt(selectedRow, 0)
                                .toString()
                );

        String name =
                supplierTable
                        .getValueAt(selectedRow, 1)
                        .toString();

        String contactPerson =
                supplierTable
                        .getValueAt(selectedRow, 2)
                        .toString();

        String phone =
                supplierTable
                        .getValueAt(selectedRow, 3)
                        .toString();

        String email =
                supplierTable
                        .getValueAt(selectedRow, 4)
                        .toString();

        String address =
                supplierTable
                        .getValueAt(selectedRow, 5)
                        .toString();

        // =========================
        // UPDATE FIELDS
        // =========================

        JTextField txtName =
                new JTextField(name);

        JTextField txtContactPerson =
                new JTextField(contactPerson);

        JTextField txtPhone =
                new JTextField(phone);

        JTextField txtEmail =
                new JTextField(email);

        JTextField txtAddress =
                new JTextField(address);

        JPanel panel =
                new JPanel(
                        new GridLayout(5, 2, 10, 10)
                );

        panel.add(
                new JLabel("Supplier Name:")
        );
        panel.add(txtName);

        panel.add(
                new JLabel("Contact Person:")
        );
        panel.add(txtContactPerson);

        panel.add(
                new JLabel("Phone:")
        );
        panel.add(txtPhone);

        panel.add(
                new JLabel("Email:")
        );
        panel.add(txtEmail);

        panel.add(
                new JLabel("Address:")
        );
        panel.add(txtAddress);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Update Supplier",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newName =
                txtName.getText().trim();

        String newContactPerson =
                txtContactPerson.getText().trim();

        String newPhone =
                txtPhone.getText().trim();

        String newEmail =
                txtEmail.getText().trim();

        String newAddress =
                txtAddress.getText().trim();

        // =========================
        // VALIDATION
        // =========================

        if (newName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier Name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // =========================
        // UPDATE SQL
        // =========================

        String sql =
                "UPDATE suppliers SET " +
                "name = ?, " +
                "contact_person = ?, " +
                "phone = ?, " +
                "email = ?, " +
                "address = ? " +
                "WHERE supplier_id = ?";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            pst.setString(1, newName);
            pst.setString(2, newContactPerson);
            pst.setString(3, newPhone);
            pst.setString(4, newEmail);
            pst.setString(5, newAddress);
            pst.setInt(6, supplierId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadSuppliers();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating supplier:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // DELETE SUPPLIER
    // =========================================================

    private void deleteSupplier() {

        int selectedRow =
                supplierTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier to delete.",
                    "No Supplier Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int supplierId =
                Integer.parseInt(
                        supplierTable
                                .getValueAt(selectedRow, 0)
                                .toString()
                );

        String supplierName =
                supplierTable
                        .getValueAt(selectedRow, 1)
                        .toString();

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete:\n\n"
                                + supplierName + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM suppliers " +
                "WHERE supplier_id = ?";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            pst.setInt(1, supplierId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadSuppliers();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting supplier:\n"
                            + ex.getMessage(),
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

            new SupplierFrame().setVisible(true);

        });
    }
}