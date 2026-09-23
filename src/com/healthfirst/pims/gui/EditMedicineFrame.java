package com.healthfirst.pims.gui;

import com.healthfirst.pims.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditMedicineFrame extends JFrame {

    private int medicineId;

    private JTextField txtName;
    private JTextField txtCompany;
    private JTextField txtType;
    private JTextField txtPrice;
    private JTextField txtStock;
    private JTextField txtReorder;
    private JTextField txtExpiry;

    private JComboBox<SupplierItem> cmbSupplier;

    private ArrayList<SupplierItem> suppliers = new ArrayList<>();

    public EditMedicineFrame(int medicineId) {

        this.medicineId = medicineId;

        setTitle("Edit Medicine");
        setSize(550, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();

        loadSuppliers();
        loadMedicine();
    }

    private void buildUI() {

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtName = new JTextField();
        txtCompany = new JTextField();
        txtType = new JTextField();
        txtPrice = new JTextField();
        txtStock = new JTextField();
        txtReorder = new JTextField();
        txtExpiry = new JTextField();

        cmbSupplier = new JComboBox<>();

        panel.add(new JLabel("Medicine Name:"));
        panel.add(txtName);

        panel.add(new JLabel("Company:"));
        panel.add(txtCompany);

        panel.add(new JLabel("Medicine Type:"));
        panel.add(txtType);

        panel.add(new JLabel("Price:"));
        panel.add(txtPrice);

        panel.add(new JLabel("Quantity in Stock:"));
        panel.add(txtStock);

        panel.add(new JLabel("Reorder Level:"));
        panel.add(txtReorder);

        panel.add(new JLabel("Expiry Date:"));
        panel.add(txtExpiry);

        panel.add(new JLabel("Supplier:"));
        panel.add(cmbSupplier);

        JButton btnUpdate = new JButton("Update Medicine");
        JButton btnCancel = new JButton("Cancel");

        panel.add(btnUpdate);
        panel.add(btnCancel);

        add(panel);

        btnUpdate.addActionListener(e -> updateMedicine());
        btnCancel.addActionListener(e -> dispose());
    }

    // =========================
    // LOAD SUPPLIERS
    // =========================

    private void loadSuppliers() {

        cmbSupplier.removeAllItems();
        suppliers.clear();

        String sql =
                "SELECT supplier_id, name " +
                "FROM suppliers " +
                "ORDER BY name";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                SupplierItem supplier = new SupplierItem(
                        rs.getInt("supplier_id"),
                        rs.getString("name")
                );

                suppliers.add(supplier);
                cmbSupplier.addItem(supplier);
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading suppliers:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // LOAD MEDICINE
    // =========================

    private void loadMedicine() {

        String sql =
                "SELECT medicine_id, name, company, medicine_type, " +
                "price, quantity_in_stock, reorder_level, expiry_date, supplier_id " +
                "FROM medicines " +
                "WHERE medicine_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, medicineId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    txtName.setText(rs.getString("name"));
                    txtCompany.setText(rs.getString("company"));
                    txtType.setText(rs.getString("medicine_type"));

                    txtPrice.setText(
                            String.valueOf(rs.getBigDecimal("price"))
                    );

                    txtStock.setText(
                            String.valueOf(rs.getInt("quantity_in_stock"))
                    );

                    txtReorder.setText(
                            String.valueOf(rs.getInt("reorder_level"))
                    );

                    Date expiryDate = rs.getDate("expiry_date");

                    if (expiryDate != null) {

                        SimpleDateFormat sdf =
                                new SimpleDateFormat("yyyy-MM-dd");

                        txtExpiry.setText(
                                sdf.format(expiryDate)
                        );
                    }

                    int supplierId = rs.getInt("supplier_id");

                    if (!rs.wasNull()) {

                        for (int i = 0; i < cmbSupplier.getItemCount(); i++) {

                            SupplierItem item =
                                    cmbSupplier.getItemAt(i);

                            if (item.getId() == supplierId) {

                                cmbSupplier.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                }
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading medicine:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // UPDATE MEDICINE
    // =========================

    private void updateMedicine() {

        String name = txtName.getText().trim();
        String company = txtCompany.getText().trim();
        String type = txtType.getText().trim();
        String priceText = txtPrice.getText().trim();
        String stockText = txtStock.getText().trim();
        String reorderText = txtReorder.getText().trim();
        String expiryText = txtExpiry.getText().trim();

        if (name.isEmpty() ||
            priceText.isEmpty() ||
            stockText.isEmpty() ||
            reorderText.isEmpty() ||
            expiryText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);
            int reorder = Integer.parseInt(reorderText);

            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd");

            sdf.setLenient(false);

            Date expiryDate =
                    sdf.parse(expiryText);

            SupplierItem selectedSupplier =
                    (SupplierItem) cmbSupplier.getSelectedItem();

            Integer supplierId = null;

            if (selectedSupplier != null) {
                supplierId = selectedSupplier.getId();
            }

            String sql =
                    "UPDATE medicines SET " +
                    "name = ?, " +
                    "company = ?, " +
                    "medicine_type = ?, " +
                    "price = ?, " +
                    "quantity_in_stock = ?, " +
                    "reorder_level = ?, " +
                    "expiry_date = ?, " +
                    "supplier_id = ? " +
                    "WHERE medicine_id = ?";

            try (Connection con =
                         DatabaseConnection.getConnection();
                 PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, company);
                ps.setString(3, type);
                ps.setDouble(4, price);
                ps.setInt(5, stock);
                ps.setInt(6, reorder);

                ps.setDate(
                        7,
                        new java.sql.Date(expiryDate.getTime())
                );

                if (supplierId == null) {
                    ps.setNull(8, Types.INTEGER);
                } else {
                    ps.setInt(8, supplierId);
                }

                ps.setInt(9, medicineId);

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Medicine updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    dispose();
                }
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Price, stock and reorder level must contain valid numbers.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (java.text.ParseException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Expiry date must use this format:\nYYYY-MM-DD\n\nExample: 2027-12-31",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating medicine:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // SUPPLIER OBJECT
    // =========================

    private static class SupplierItem {

        private int id;
        private String name;

        public SupplierItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}