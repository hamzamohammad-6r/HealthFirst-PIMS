package com.healthfirst.pims.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.sql.*;

import com.healthfirst.pims.database.DatabaseConnection;

public class NewSaleFrame extends JFrame {

    // ---- Palette (matches the rest of the app) -----------------------
    private static final Color BRAND_DARK   = new Color(0x0F, 0x4C, 0x81);
    private static final Color BRAND_LIGHT  = new Color(0x2E, 0x86, 0xC1);
    private static final Color ACCENT       = new Color(0x1A, 0xBC, 0x9C);
    private static final Color BG_LIGHT     = new Color(0xF4, 0xF7, 0xFB);
    private static final Color TEXT_DARK    = new Color(0x2C, 0x3E, 0x50);
    private static final Color TEXT_MUTED   = new Color(0x8A, 0x93, 0x9E);
    private static final Color FIELD_BORDER = new Color(0xD5, 0xDC, 0xE4);
    private static final Color FIELD_FOCUS  = BRAND_LIGHT;
    private static final Color CARD_GREEN   = new Color(0x27, 0xAE, 0x60);
    private static final Color CARD_RED     = new Color(0xC0, 0x39, 0x2B);

    private JComboBox<MedicineItem> cmbMedicine;
    private RoundedTextField txtQuantity;
    private JLabel lblPrice;
    private JLabel lblStock;
    private JLabel lblTotal;

    private JTable saleTable;
    private DefaultTableModel tableModel;

    private BigDecimal grandTotal = BigDecimal.ZERO;

    public NewSaleFrame() {

        setTitle("New Sale");
        setSize(950, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(BG_LIGHT);

        // =====================================================
        // HEADER
        // =====================================================

        JPanel headerPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, BRAND_DARK,
                        getWidth(), getHeight(), BRAND_LIGHT
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBounds(0, 0, 950, 80);
        mainPanel.add(headerPanel);

        JLabel lblTitle = new JLabel("Create New Sale");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(30, 20, 400, 38);
        headerPanel.add(lblTitle);

        // =====================================================
        // SELECTION CARD (medicine / price / stock / quantity)
        // =====================================================

        RoundedPanel selectionCard = new RoundedPanel();
        selectionCard.setLayout(null);
        selectionCard.setBounds(30, 100, 890, 160);
        mainPanel.add(selectionCard);

        JLabel lblMedicine = new JLabel("MEDICINE");
        lblMedicine.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMedicine.setForeground(TEXT_MUTED);
        lblMedicine.setBounds(24, 16, 150, 20);
        selectionCard.add(lblMedicine);

        cmbMedicine = new JComboBox<>();
        cmbMedicine.setBounds(24, 38, 300, 34);
        cmbMedicine.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbMedicine.setBackground(Color.WHITE);
        selectionCard.add(cmbMedicine);

        JLabel lblPriceText = new JLabel("PRICE");
        lblPriceText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPriceText.setForeground(TEXT_MUTED);
        lblPriceText.setBounds(350, 16, 100, 20);
        selectionCard.add(lblPriceText);

        lblPrice = new JLabel("R 0.00");
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPrice.setForeground(TEXT_DARK);
        lblPrice.setBounds(350, 38, 150, 30);
        selectionCard.add(lblPrice);

        JLabel lblStockText = new JLabel("STOCK");
        lblStockText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStockText.setForeground(TEXT_MUTED);
        lblStockText.setBounds(520, 16, 100, 20);
        selectionCard.add(lblStockText);

        lblStock = new JLabel("0");
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblStock.setForeground(ACCENT.darker());
        lblStock.setBounds(520, 38, 120, 30);
        selectionCard.add(lblStock);

        JLabel lblQuantity = new JLabel("QUANTITY");
        lblQuantity.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblQuantity.setForeground(TEXT_MUTED);
        lblQuantity.setBounds(24, 84, 150, 20);
        selectionCard.add(lblQuantity);

        txtQuantity = new RoundedTextField();
        txtQuantity.setBounds(24, 100, 120, 36);
        txtQuantity.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectionCard.add(txtQuantity);

        RoundedButton btnAdd = new RoundedButton(
                "Add to Sale", CARD_GREEN, CARD_GREEN.darker(), Color.WHITE, false);
        btnAdd.setBounds(164, 100, 160, 36);
        selectionCard.add(btnAdd);

        // =====================================================
        // TABLE
        // =====================================================

        tableModel = new DefaultTableModel(
                new String[]{
                        "Medicine ID",
                        "Medicine",
                        "Quantity",
                        "Price",
                        "Subtotal"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        saleTable = new JTable(tableModel);
        saleTable.setRowHeight(30);
        saleTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        saleTable.setForeground(TEXT_DARK);
        saleTable.setGridColor(new Color(0xEC, 0xF0, 0xF3));
        saleTable.setShowVerticalLines(false);
        saleTable.setSelectionBackground(new Color(0xDD, 0xEE, 0xF9));
        saleTable.setSelectionForeground(TEXT_DARK);
        saleTable.setFillsViewportHeight(true);

        JTableHeader header = saleTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new HeaderCellRenderer());

        JScrollPane scrollPane = new JScrollPane(saleTable);
        scrollPane.setBounds(30, 280, 890, 220);
        scrollPane.setBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1));
        mainPanel.add(scrollPane);

        // =====================================================
        // REMOVE BUTTON
        // =====================================================

        RoundedButton btnRemove = new RoundedButton(
                "Remove Selected", Color.WHITE, new Color(0xFD, 0xEC, 0xEA), CARD_RED, true);
        btnRemove.setBounds(30, 520, 170, 38);
        mainPanel.add(btnRemove);

        // =====================================================
        // TOTAL
        // =====================================================

        JLabel lblTotalText = new JLabel("TOTAL:");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalText.setForeground(TEXT_DARK);
        lblTotalText.setBounds(600, 518, 100, 35);
        mainPanel.add(lblTotalText);

        lblTotal = new JLabel("R 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotal.setForeground(BRAND_DARK);
        lblTotal.setBounds(695, 514, 220, 40);
        mainPanel.add(lblTotal);

        // =====================================================
        // SAVE SALE
        // =====================================================

        RoundedButton btnComplete = new RoundedButton(
                "Complete Sale", CARD_GREEN, CARD_GREEN.darker(), Color.WHITE, false);
        btnComplete.setBounds(600, 580, 160, 46);
        mainPanel.add(btnComplete);

        // =====================================================
        // CANCEL
        // =====================================================

        RoundedButton btnCancel = new RoundedButton(
                "Cancel", Color.WHITE, new Color(0xEA, 0xF3, 0xFB), TEXT_DARK, true);
        btnCancel.setBounds(770, 580, 150, 46);
        mainPanel.add(btnCancel);

        // =====================================================
        // EVENTS
        // =====================================================

        cmbMedicine.addActionListener(e -> updateMedicineInfo());

        btnAdd.addActionListener(e -> addMedicineToSale());

        btnRemove.addActionListener(e -> removeSelectedMedicine());

        btnComplete.addActionListener(e -> completeSale());

        btnCancel.addActionListener(e -> dispose());

        // =====================================================
        // LOAD MEDICINES
        // =====================================================

        loadMedicines();

        add(mainPanel);

        txtQuantity.setText("1");
    }

    // =========================================================
    // LOAD MEDICINES
    // =========================================================

    private void loadMedicines() {

        cmbMedicine.removeAllItems();

        String sql =
                "SELECT medicine_id, name, price, quantity_in_stock " +
                "FROM medicines " +
                "ORDER BY name";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery()
        ) {

            while (rs.next()) {

                int id =
                        rs.getInt("medicine_id");

                String name =
                        rs.getString("name");

                BigDecimal price =
                        rs.getBigDecimal("price");

                int stock =
                        rs.getInt("quantity_in_stock");

                MedicineItem medicine =
                        new MedicineItem(
                                id,
                                name,
                                price,
                                stock
                        );

                cmbMedicine.addItem(medicine);
            }

            updateMedicineInfo();

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
    // UPDATE MEDICINE INFORMATION
    // =========================================================

    private void updateMedicineInfo() {

        MedicineItem medicine =
                (MedicineItem) cmbMedicine.getSelectedItem();

        if (medicine == null) {

            lblPrice.setText("R 0.00");
            lblStock.setText("0");

            return;
        }

        lblPrice.setText(
                "R " + medicine.price.toString()
        );

        lblStock.setText(
                String.valueOf(medicine.stock)
        );
    }

    // =========================================================
    // ADD MEDICINE TO SALE
    // =========================================================

    private void addMedicineToSale() {

        MedicineItem medicine =
                (MedicineItem) cmbMedicine.getSelectedItem();

        if (medicine == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int quantity;

        try {

            quantity =
                    Integer.parseInt(
                            txtQuantity.getText().trim()
                    );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid quantity.",
                    "Invalid Quantity",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (quantity <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Quantity must be greater than 0.",
                    "Invalid Quantity",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (quantity > medicine.stock) {

            JOptionPane.showMessageDialog(
                    this,
                    "Not enough stock available.\n\n"
                            + "Available: "
                            + medicine.stock,
                    "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Check if medicine already exists in the sale
        for (int i = 0; i < tableModel.getRowCount(); i++) {

            int existingId =
                    Integer.parseInt(
                            tableModel
                                    .getValueAt(i, 0)
                                    .toString()
                    );

            if (existingId == medicine.id) {

                int existingQuantity =
                        Integer.parseInt(
                                tableModel
                                        .getValueAt(i, 2)
                                        .toString()
                        );

                int newQuantity =
                        existingQuantity + quantity;

                if (newQuantity > medicine.stock) {

                    JOptionPane.showMessageDialog(
                            this,
                            "The total quantity exceeds available stock.\n\n"
                                    + "Available: "
                                    + medicine.stock,
                            "Insufficient Stock",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                BigDecimal subtotal =
                        medicine.price.multiply(
                                BigDecimal.valueOf(newQuantity)
                        );

                tableModel.setValueAt(
                        newQuantity,
                        i,
                        2
                );

                tableModel.setValueAt(
                        subtotal,
                        i,
                        4
                );

                calculateTotal();

                return;
            }
        }

        BigDecimal subtotal =
                medicine.price.multiply(
                        BigDecimal.valueOf(quantity)
                );

        tableModel.addRow(
                new Object[]{
                        medicine.id,
                        medicine.name,
                        quantity,
                        medicine.price,
                        subtotal
                }
        );

        calculateTotal();

        txtQuantity.setText("1");
    }

    // =========================================================
    // REMOVE MEDICINE
    // =========================================================

    private void removeSelectedMedicine() {

        int selectedRow =
                saleTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an item to remove.",
                    "No Item Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        tableModel.removeRow(selectedRow);

        calculateTotal();
    }

    // =========================================================
    // CALCULATE TOTAL
    // =========================================================

    private void calculateTotal() {

        grandTotal =
                BigDecimal.ZERO;

        for (int i = 0;
             i < tableModel.getRowCount();
             i++) {

            BigDecimal subtotal =
                    new BigDecimal(
                            tableModel
                                    .getValueAt(i, 4)
                                    .toString()
                    );

            grandTotal =
                    grandTotal.add(subtotal);
        }

        lblTotal.setText(
                "R " + grandTotal
        );
    }

    // =========================================================
    // COMPLETE SALE
    // =========================================================

    private void completeSale() {

        if (tableModel.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please add at least one medicine.",
                    "Empty Sale",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Complete this sale?\n\n"
                                + "Total: R "
                                + grandTotal,
                        "Confirm Sale",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        Connection con = null;

        try {

            con =
                    DatabaseConnection.getConnection();

            con.setAutoCommit(false);

            // =================================================
            // INSERT SALE
            // =================================================

            String saleSQL =
                    "INSERT INTO sales " +
                    "(total_amount, user_id) " +
                    "VALUES (?, ?)";

            int saleId;

            try (
                    PreparedStatement pst =
                            con.prepareStatement(
                                    saleSQL,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                pst.setBigDecimal(
                        1,
                        grandTotal
                );

                // No logged-in user is being used yet.
                pst.setNull(
                        2,
                        Types.INTEGER
                );

                pst.executeUpdate();

                ResultSet generatedKeys =
                        pst.getGeneratedKeys();

                if (!generatedKeys.next()) {

                    throw new SQLException(
                            "Could not obtain sale ID."
                    );
                }

                saleId =
                        generatedKeys.getInt(1);

                generatedKeys.close();
            }

            // =================================================
            // INSERT SALE ITEMS
            // =================================================

            String itemSQL =
                    "INSERT INTO sale_items " +
                    "(sale_id, medicine_id, quantity_sold, price_at_sale) " +
                    "VALUES (?, ?, ?, ?)";

            String stockSQL =
                    "UPDATE medicines " +
                    "SET quantity_in_stock = quantity_in_stock - ? " +
                    "WHERE medicine_id = ? " +
                    "AND quantity_in_stock >= ?";

            try (
                    PreparedStatement itemPst =
                            con.prepareStatement(itemSQL);

                    PreparedStatement stockPst =
                            con.prepareStatement(stockSQL)
            ) {

                for (int i = 0;
                     i < tableModel.getRowCount();
                     i++) {

                    int medicineId =
                            Integer.parseInt(
                                    tableModel
                                            .getValueAt(i, 0)
                                            .toString()
                            );

                    int quantity =
                            Integer.parseInt(
                                    tableModel
                                            .getValueAt(i, 2)
                                            .toString()
                            );

                    BigDecimal price =
                            new BigDecimal(
                                    tableModel
                                            .getValueAt(i, 3)
                                            .toString()
                            );

                    // Insert sale item
                    itemPst.setInt(
                            1,
                            saleId
                    );

                    itemPst.setInt(
                            2,
                            medicineId
                    );

                    itemPst.setInt(
                            3,
                            quantity
                    );

                    itemPst.setBigDecimal(
                            4,
                            price
                    );

                    itemPst.addBatch();

                    // Reduce stock
                    stockPst.setInt(
                            1,
                            quantity
                    );

                    stockPst.setInt(
                            2,
                            medicineId
                    );

                    stockPst.setInt(
                            3,
                            quantity
                    );

                    int updated =
                            stockPst.executeUpdate();

                    if (updated != 1) {

                        throw new SQLException(
                                "Insufficient stock for medicine ID "
                                        + medicineId
                        );
                    }
                }

                itemPst.executeBatch();
            }

            // =================================================
            // COMMIT
            // =================================================

            con.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Sale completed successfully!\n\n"
                            + "Sale ID: "
                            + saleId
                            + "\n"
                            + "Total: R "
                            + grandTotal,
                    "Sale Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

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
                    "Sale could not be completed.\n\n"
                            + ex.getMessage(),
                    "Sale Error",
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
    // MEDICINE CLASS
    // =========================================================

    private static class MedicineItem {

        int id;
        String name;
        BigDecimal price;
        int stock;

        MedicineItem(
                int id,
                String name,
                BigDecimal price,
                int stock
        ) {

            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // ==================================================================
    // Table header renderer (setBackground/setForeground on JTableHeader
    // alone is ignored by some look-and-feels, e.g. Windows, leaving the
    // header text invisible - a custom renderer fixes that reliably)
    // ==================================================================
    private static class HeaderCellRenderer extends DefaultTableCellRenderer {
        HeaderCellRenderer() {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setBackground(BRAND_DARK);
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    // ==================================================================
    // Rounded white card container (used for the selection panel)
    // ==================================================================
    private static class RoundedPanel extends JPanel {
        RoundedPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            g2.setColor(new Color(0, 0, 0, 14));
            g2.fill(new RoundRectangle2D.Float(2, 4, w - 4, h - 4, 14, 14));

            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, w - 2, h - 2, 14, 14));

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 3, h - 3, 14, 14));

            g2.dispose();
        }
    }

    // ==================================================================
    // Custom rounded, focus-aware text field
    // ==================================================================
    private static class RoundedTextField extends JTextField {
        RoundedTextField() {
            super();
            setOpaque(false);
            setBorder(new EmptyBorder(6, 12, 6, 12));
            setForeground(TEXT_DARK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isFocusOwner() ? FIELD_FOCUS : FIELD_BORDER);
            g2.setStroke(new BasicStroke(isFocusOwner() ? 1.6f : 1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1.5f, getHeight() - 1.5f, 10, 10));
            g2.dispose();
        }
    }

    // ==================================================================
    // Rounded button with hover/press states (shared style)
    // ==================================================================
    private static class RoundedButton extends JButton {
        private final Color base;
        private final Color hover;
        private boolean isHover = false;
        private boolean isPressed = false;
        private final boolean outlined;

        RoundedButton(String text, Color base, Color hover, Color textColor, boolean outlined) {
            super(text);
            this.base = base;
            this.hover = hover;
            this.outlined = outlined;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(textColor);
            setFont(new Font("Segoe UI", outlined ? Font.PLAIN : Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHover = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = isPressed ? hover.darker() : (isHover ? hover : base);
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

            if (outlined) {
                g2.setColor(FIELD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1.5f, getHeight() - 1.5f, 10, 10));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new NewSaleFrame().setVisible(true);

        });
    }
}