package com.healthfirst.pims.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.healthfirst.pims.database.DatabaseConnection;

public class AddMedicineFrame extends JFrame {

    // ---- Palette (matches the rest of the app) -----------------------
    private static final Color BRAND_DARK   = new Color(0x0F, 0x4C, 0x81);
    private static final Color BRAND_LIGHT  = new Color(0x2E, 0x86, 0xC1);
    private static final Color BG_LIGHT     = new Color(0xF4, 0xF7, 0xFB);
    private static final Color TEXT_DARK    = new Color(0x2C, 0x3E, 0x50);
    private static final Color TEXT_MUTED   = new Color(0x8A, 0x93, 0x9E);
    private static final Color FIELD_BORDER = new Color(0xD5, 0xDC, 0xE4);
    private static final Color FIELD_FOCUS  = BRAND_LIGHT;
    private static final Color CARD_GREEN   = new Color(0x27, 0xAE, 0x60);

    private RoundedTextField nameField;
    private RoundedTextField companyField;
    private RoundedTextField typeField;
    private RoundedTextField priceField;
    private RoundedTextField quantityField;
    private RoundedTextField reorderField;
    private RoundedTextField expiryField;

    private JComboBox<SupplierItem> supplierComboBox;

    public AddMedicineFrame() {

        setTitle("HealthFirst Pharmacy - Add Medicine");
        setSize(520, 780);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(BG_LIGHT);

        // =========================
        // HEADER
        // =========================

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
        headerPanel.setBounds(0, 0, 520, 80);
        mainPanel.add(headerPanel);

        JLabel titleLabel = new JLabel("Add New Medicine");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(30, 20, 400, 38);
        headerPanel.add(titleLabel);

        // =========================
        // FORM CARD
        // =========================

        RoundedPanel formCard = new RoundedPanel();
        formCard.setLayout(null);
        formCard.setBounds(30, 100, 460, 536);
        mainPanel.add(formCard);

        int rowY = 20;
        final int rowHeight = 62;

        nameField = new RoundedTextField();
        addFormRow(formCard, "MEDICINE NAME", nameField, rowY);
        rowY += rowHeight;

        companyField = new RoundedTextField();
        addFormRow(formCard, "COMPANY", companyField, rowY);
        rowY += rowHeight;

        typeField = new RoundedTextField();
        addFormRow(formCard, "MEDICINE TYPE", typeField, rowY);
        rowY += rowHeight;

        priceField = new RoundedTextField();
        addFormRow(formCard, "PRICE", priceField, rowY);
        rowY += rowHeight;

        quantityField = new RoundedTextField();
        addFormRow(formCard, "QUANTITY IN STOCK", quantityField, rowY);
        rowY += rowHeight;

        reorderField = new RoundedTextField();
        addFormRow(formCard, "REORDER LEVEL", reorderField, rowY);
        rowY += rowHeight;

        expiryField = new RoundedTextField();
        expiryField.setToolTipText("Format: YYYY-MM-DD");
        addFormRow(formCard, "EXPIRY DATE (YYYY-MM-DD)", expiryField, rowY);
        rowY += rowHeight;

        JLabel supplierLabel = new JLabel("SUPPLIER");
        supplierLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        supplierLabel.setForeground(TEXT_MUTED);
        supplierLabel.setBounds(20, rowY, 300, 20);
        formCard.add(supplierLabel);

        supplierComboBox = new JComboBox<>();
        supplierComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        supplierComboBox.setBackground(Color.WHITE);
        supplierComboBox.setBounds(20, rowY + 22, 420, 36);
        formCard.add(supplierComboBox);

        loadSuppliers();

        // =========================
        // BUTTONS
        // =========================

        RoundedButton cancelButton = new RoundedButton(
                "Cancel", Color.WHITE, new Color(0xEA, 0xF3, 0xFB), TEXT_DARK, true);
        cancelButton.setBounds(400, 660, 90, 42);
        mainPanel.add(cancelButton);

        RoundedButton saveButton = new RoundedButton(
                "Save", CARD_GREEN, CARD_GREEN.darker(), Color.WHITE, false);
        saveButton.setBounds(290, 660, 100, 42);
        mainPanel.add(saveButton);

        add(mainPanel);

        // =========================
        // BUTTON ACTIONS
        // =========================

        saveButton.addActionListener(
                e -> saveMedicine()
        );

        cancelButton.addActionListener(
                e -> dispose()
        );
    }

    // =========================
    // HELPER: add a labeled field row to the form card
    // =========================

    private void addFormRow(JPanel card, String labelText, JComponent field, int y) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_MUTED);
        label.setBounds(20, y, 400, 20);
        card.add(label);

        field.setBounds(20, y + 22, 420, 36);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(field);
    }

    // =========================
    // LOAD SUPPLIERS
    // =========================

    private void loadSuppliers() {

        String sql =
                "SELECT supplier_id, name "
                + "FROM suppliers "
                + "ORDER BY name";

        try {

            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                int id =
                        result.getInt("supplier_id");

                String name =
                        result.getString("name");

                supplierComboBox.addItem(
                        new SupplierItem(
                                id,
                                name
                        )
                );
            }

            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load suppliers:\n"
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =========================
    // SAVE MEDICINE
    // =========================

    private void saveMedicine() {

        String name =
                nameField.getText().trim();

        String company =
                companyField.getText().trim();

        String type =
                typeField.getText().trim();

        String price =
                priceField.getText().trim();

        String quantity =
                quantityField.getText().trim();

        String reorder =
                reorderField.getText().trim();

        String expiry =
                expiryField.getText().trim();

        SupplierItem selectedSupplier =
                (SupplierItem)
                supplierComboBox.getSelectedItem();

        // =========================
        // VALIDATION
        // =========================

        if (name.isEmpty()
                || price.isEmpty()
                || quantity.isEmpty()
                || reorder.isEmpty()
                || expiry.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please complete all required fields."
            );

            return;
        }

        // =========================
        // DATABASE INSERT
        // =========================

        String sql =
                "INSERT INTO medicines "
                + "(name, company, medicine_type, price, "
                + "quantity_in_stock, reorder_level, "
                + "expiry_date, supplier_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {

            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(
                    1,
                    name
            );

            statement.setString(
                    2,
                    company
            );

            statement.setString(
                    3,
                    type
            );

            statement.setBigDecimal(
                    4,
                    new java.math.BigDecimal(price)
            );

            statement.setInt(
                    5,
                    Integer.parseInt(quantity)
            );

            statement.setInt(
                    6,
                    Integer.parseInt(reorder)
            );

            statement.setDate(
                    7,
                    java.sql.Date.valueOf(expiry)
            );

            // Supplier
            if (selectedSupplier == null) {

                statement.setNull(
                        8,
                        java.sql.Types.INTEGER
                );

            } else {

                statement.setInt(
                        8,
                        selectedSupplier.getId()
                );
            }

            statement.executeUpdate();

            statement.close();
            connection.close();

            JOptionPane.showMessageDialog(
                    this,
                    "Medicine added successfully!"
            );

            dispose();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers for "
                    + "price, quantity and reorder level."
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid expiry date.\n\n"
                    + "Use this format:\n"
                    + "YYYY-MM-DD\n\n"
                    + "Example: 2027-12-31"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not add medicine:\n"
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =========================
    // SUPPLIER ITEM
    // =========================

    private static class SupplierItem {

        private int id;
        private String name;

        public SupplierItem(
                int id,
                String name) {

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

    // ==================================================================
    // Rounded white card container (used for the form)
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

    // =========================
    // MAIN
    // =========================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            AddMedicineFrame frame =
                    new AddMedicineFrame();

            frame.setVisible(true);
        });
    }
}