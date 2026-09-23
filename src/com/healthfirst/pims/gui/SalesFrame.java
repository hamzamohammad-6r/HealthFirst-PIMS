package com.healthfirst.pims.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import com.healthfirst.pims.database.DatabaseConnection;

public class SalesFrame extends JFrame {

    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public SalesFrame() {

        setTitle("Sales Management");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(245, 247, 250));

        // TITLE
        JLabel lblTitle = new JLabel("Sales Management");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setBounds(30, 20, 350, 40);
        mainPanel.add(lblTitle);

        // SEARCH LABEL
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSearch.setBounds(30, 80, 70, 30);
        mainPanel.add(lblSearch);

        // SEARCH FIELD
        txtSearch = new JTextField();
        txtSearch.setBounds(90, 80, 250, 30);
        mainPanel.add(txtSearch);

        // SEARCH BUTTON
        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(350, 80, 100, 30);
        btnSearch.setFocusPainted(false);
        mainPanel.add(btnSearch);

        // REFRESH BUTTON
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(460, 80, 100, 30);
        btnRefresh.setFocusPainted(false);
        mainPanel.add(btnRefresh);

        // NEW SALE BUTTON
        JButton btnNewSale = new JButton("New Sale");
        btnNewSale.setBounds(570, 80, 120, 30);
        btnNewSale.setBackground(new Color(46, 125, 50));
        btnNewSale.setForeground(Color.WHITE);
        btnNewSale.setFocusPainted(false);
        mainPanel.add(btnNewSale);

        // TABLE
        tableModel = new DefaultTableModel(
                new String[]{
                        "Sale ID",
                        "Sale Date",
                        "Total Amount",
                        "User ID"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(28);
        salesTable.setFont(new Font("Arial", Font.PLAIN, 13));

        salesTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBounds(30, 130, 890, 300);
        mainPanel.add(scrollPane);

        // VIEW ITEMS
        JButton btnViewItems = new JButton("View Sale Items");
        btnViewItems.setBounds(500, 460, 140, 40);
        btnViewItems.setFocusPainted(false);
        mainPanel.add(btnViewItems);

        // DELETE
        JButton btnDelete = new JButton("Delete Sale");
        btnDelete.setBounds(650, 460, 120, 40);
        btnDelete.setBackground(new Color(198, 40, 40));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        mainPanel.add(btnDelete);

        // CLOSE
        JButton btnClose = new JButton("Close");
        btnClose.setBounds(780, 460, 120, 40);
        btnClose.setFocusPainted(false);
        mainPanel.add(btnClose);

        // BUTTON ACTIONS

        btnSearch.addActionListener(e -> searchSales());

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadSales();
        });

        btnNewSale.addActionListener(e -> {

            NewSaleFrame newSaleFrame =
                    new NewSaleFrame();

            newSaleFrame.setVisible(true);
        });

        btnViewItems.addActionListener(e -> viewSaleItems());

        btnDelete.addActionListener(e -> deleteSale());

        btnClose.addActionListener(e -> dispose());

        // LOAD SALES
        loadSales();

        add(mainPanel);
    }

    // =========================================================
    // LOAD ALL SALES
    // =========================================================

    private void loadSales() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT sale_id, sale_date, total_amount, user_id " +
                "FROM sales " +
                "ORDER BY sale_id DESC";

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
                        rs.getInt("sale_id"),
                        rs.getTimestamp("sale_date"),
                        rs.getBigDecimal("total_amount"),
                        rs.getObject("user_id")
                });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading sales:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // SEARCH SALES
    // =========================================================

    private void searchSales() {

        String search =
                txtSearch.getText().trim();

        if (search.isEmpty()) {

            loadSales();
            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT sale_id, sale_date, total_amount, user_id " +
                "FROM sales " +
                "WHERE CAST(sale_id AS CHAR) LIKE ? " +
                "OR CAST(user_id AS CHAR) LIKE ? " +
                "ORDER BY sale_id DESC";

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

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                tableModel.addRow(new Object[]{
                        rs.getInt("sale_id"),
                        rs.getTimestamp("sale_date"),
                        rs.getBigDecimal("total_amount"),
                        rs.getObject("user_id")
                });
            }

            rs.close();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error searching sales:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // VIEW SALE ITEMS
    // =========================================================

    private void viewSaleItems() {

        int selectedRow =
                salesTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a sale first.",
                    "No Sale Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int saleId =
                Integer.parseInt(
                        salesTable
                                .getValueAt(
                                        selectedRow,
                                        0
                                )
                                .toString()
                );

        String sql =
                "SELECT si.sale_item_id, " +
                "m.name, " +
                "si.quantity_sold, " +
                "si.price_at_sale, " +
                "(si.quantity_sold * si.price_at_sale) AS subtotal " +
                "FROM sale_items si " +
                "INNER JOIN medicines m " +
                "ON si.medicine_id = m.medicine_id " +
                "WHERE si.sale_id = ?";

        DefaultTableModel itemModel =
                new DefaultTableModel(
                        new String[]{
                                "Item ID",
                                "Medicine",
                                "Quantity",
                                "Price",
                                "Subtotal"
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

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            pst.setInt(1, saleId);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                itemModel.addRow(
                        new Object[]{
                                rs.getInt("sale_item_id"),
                                rs.getString("name"),
                                rs.getInt("quantity_sold"),
                                rs.getBigDecimal("price_at_sale"),
                                rs.getBigDecimal("subtotal")
                        }
                );
            }

            rs.close();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading sale items:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();

            return;
        }

        JTable itemTable =
                new JTable(itemModel);

        itemTable.setRowHeight(28);

        JScrollPane itemScrollPane =
                new JScrollPane(itemTable);

        itemScrollPane.setPreferredSize(
                new Dimension(650, 250)
        );

        JOptionPane.showMessageDialog(
                this,
                itemScrollPane,
                "Sale #" + saleId + " Items",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // DELETE SALE
    // =========================================================

    private void deleteSale() {

        int selectedRow =
                salesTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a sale to delete.",
                    "No Sale Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int saleId =
                Integer.parseInt(
                        salesTable
                                .getValueAt(
                                        selectedRow,
                                        0
                                )
                                .toString()
                );

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete Sale #"
                                + saleId
                                + "?\n\n"
                                + "This will also delete its sale items.",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        Connection con = null;

        try {

            con =
                    DatabaseConnection.getConnection();

            con.setAutoCommit(false);

            // Delete sale items first
            String deleteItems =
                    "DELETE FROM sale_items " +
                    "WHERE sale_id = ?";

            try (PreparedStatement pst =
                         con.prepareStatement(deleteItems)) {

                pst.setInt(1, saleId);
                pst.executeUpdate();
            }

            // Delete sale
            String deleteSale =
                    "DELETE FROM sales " +
                    "WHERE sale_id = ?";

            try (PreparedStatement pst =
                         con.prepareStatement(deleteSale)) {

                pst.setInt(1, saleId);
                pst.executeUpdate();
            }

            con.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Sale deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadSales();

        } catch (SQLException ex) {

            try {

                if (con != null) {
                    con.rollback();
                }

            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting sale:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();

        } finally {

            try {

                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }

            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new SalesFrame().setVisible(true);

        });
    }
}