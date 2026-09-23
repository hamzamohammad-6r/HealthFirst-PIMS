package com.healthfirst.pims.gui;

import com.healthfirst.pims.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StockCheckFrame extends JFrame {

    private JTextField txtSearch;
    private JTable medicineTable;
    private DefaultTableModel tableModel;

    public StockCheckFrame() {

        setTitle("HealthFirst PIMS - Medicine Stock Check");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        buildUI();
        loadMedicines("");
    }

    // =========================================================
    // BUILD UI
    // =========================================================

    private void buildUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout(10, 10));

        mainPanel.setBackground(
                new Color(245, 247, 250)
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        // =====================================================
        // HEADER
        // =====================================================

        JPanel headerPanel =
                new JPanel(new BorderLayout());

        headerPanel.setBackground(
                new Color(35, 45, 65)
        );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 15, 12, 15
                )
        );

        JLabel titleLabel =
                new JLabel("Medicine Stock Check");

        titleLabel.setForeground(Color.WHITE);

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        headerPanel.add(
                titleLabel,
                BorderLayout.WEST
        );

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        // =====================================================
        // SEARCH PANEL
        // =====================================================

        JPanel searchPanel =
                new JPanel(new BorderLayout(10, 10));

        searchPanel.setBackground(
                new Color(245, 247, 250)
        );

        JLabel searchLabel =
                new JLabel("Search Medicine:");

        searchLabel.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        txtSearch =
                new JTextField();

        JButton btnSearch =
                new JButton("Search");

        JButton btnRefresh =
                new JButton("Refresh");

        styleButton(btnSearch);
        styleButton(btnRefresh);

        searchPanel.add(
                searchLabel,
                BorderLayout.WEST
        );

        searchPanel.add(
                txtSearch,
                BorderLayout.CENTER
        );

        JPanel searchButtons =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT,
                        5,
                        0
                ));

        searchButtons.setBackground(
                new Color(245, 247, 250)
        );

        searchButtons.add(btnSearch);
        searchButtons.add(btnRefresh);

        searchPanel.add(
                searchButtons,
                BorderLayout.EAST
        );

        // =====================================================
        // TABLE
        // =====================================================

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Medicine",
                                "Company",
                                "Type",
                                "Price",
                                "Available Stock",
                                "Status"
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

        medicineTable =
                new JTable(tableModel);

        medicineTable.setRowHeight(28);

        medicineTable.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        medicineTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );

        medicineTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(medicineTable);

        // =====================================================
        // CENTER
        // =====================================================

        JPanel centerPanel =
                new JPanel(new BorderLayout(10, 10));

        centerPanel.setBackground(
                new Color(245, 247, 250)
        );

        centerPanel.add(
                searchPanel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );

        // =====================================================
        // FOOTER
        // =====================================================

        JLabel footerLabel =
                new JLabel(
                        "Cashier Stock Check - View only",
                        SwingConstants.CENTER
                );

        footerLabel.setFont(
                new Font(
                        "Arial",
                        Font.ITALIC,
                        12
                )
        );

        mainPanel.add(
                footerLabel,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        // =====================================================
        // BUTTON EVENTS
        // =====================================================

        btnSearch.addActionListener(e ->
                loadMedicines(
                        txtSearch.getText().trim()
                )
        );

        btnRefresh.addActionListener(e -> {

            txtSearch.setText("");

            loadMedicines("");
        });

        txtSearch.addActionListener(e ->
                loadMedicines(
                        txtSearch.getText().trim()
                )
        );
    }

    // =========================================================
    // LOAD MEDICINES
    // =========================================================

    private void loadMedicines(String search) {

        tableModel.setRowCount(0);

        String sql =
                "SELECT medicine_id, name, company, " +
                "medicine_type, price, quantity_in_stock, " +
                "reorder_level " +
                "FROM medicines ";

        if (!search.isEmpty()) {

            sql +=
                    "WHERE name LIKE ? " +
                    "OR company LIKE ? " +
                    "OR medicine_type LIKE ? ";
        }

        sql +=
                "ORDER BY name";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            if (!search.isEmpty()) {

                String searchValue =
                        "%" + search + "%";

                ps.setString(1, searchValue);
                ps.setString(2, searchValue);
                ps.setString(3, searchValue);
            }

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    int stock =
                            rs.getInt(
                                    "quantity_in_stock"
                            );

                    int reorder =
                            rs.getInt(
                                    "reorder_level"
                            );

                    String status;

                    if (stock <= 0) {

                        status = "OUT OF STOCK";

                    } else if (stock <= reorder) {

                        status = "LOW STOCK";

                    } else {

                        status = "AVAILABLE";
                    }

                    tableModel.addRow(
                            new Object[]{
                                    rs.getInt(
                                            "medicine_id"
                                    ),
                                    rs.getString(
                                            "name"
                                    ),
                                    rs.getString(
                                            "company"
                                    ),
                                    rs.getString(
                                            "medicine_type"
                                    ),
                                    String.format(
                                            "R %.2f",
                                            rs.getDouble(
                                                    "price"
                                            )
                                    ),
                                    stock,
                                    status
                            }
                    );
                }
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading medicines:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // BUTTON STYLE
    // =========================================================

    private void styleButton(JButton button) {

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setFocusPainted(false);
    }
}