package com.healthfirst.pims.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.healthfirst.pims.database.DatabaseConnection;

public class AddSupplierFrame extends JFrame {

    // ---- Palette (matches the rest of the app) -----------------------
    private static final Color BRAND_DARK   = new Color(0x0F, 0x4C, 0x81);
    private static final Color BRAND_LIGHT  = new Color(0x2E, 0x86, 0xC1);
    private static final Color BG_LIGHT     = new Color(0xF4, 0xF7, 0xFB);
    private static final Color TEXT_DARK    = new Color(0x2C, 0x3E, 0x50);
    private static final Color TEXT_MUTED   = new Color(0x8A, 0x93, 0x9E);
    private static final Color FIELD_BORDER = new Color(0xD5, 0xDC, 0xE4);
    private static final Color FIELD_FOCUS  = BRAND_LIGHT;
    private static final Color CARD_GREEN   = new Color(0x27, 0xAE, 0x60);

    private RoundedTextField txtName;
    private RoundedTextField txtContactPerson;
    private RoundedTextField txtPhone;
    private RoundedTextField txtEmail;
    private RoundedTextField txtAddress;

    public AddSupplierFrame() {

        setTitle("Add Supplier");
        setSize(550, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // =========================
        // MAIN PANEL
        // =========================

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(BG_LIGHT);

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
        headerPanel.setBounds(0, 0, 550, 80);
        panel.add(headerPanel);

        JLabel lblTitle = new JLabel("Add Supplier");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(30, 20, 300, 38);
        headerPanel.add(lblTitle);

        // =========================
        // FORM CARD
        // =========================

        RoundedPanel formCard = new RoundedPanel();
        formCard.setLayout(null);
        formCard.setBounds(30, 100, 490, 350);
        panel.add(formCard);

        int rowY = 20;
        final int rowHeight = 62;

        txtName = new RoundedTextField();
        addFormRow(formCard, "SUPPLIER NAME", txtName, rowY);
        rowY += rowHeight;

        txtContactPerson = new RoundedTextField();
        addFormRow(formCard, "CONTACT PERSON", txtContactPerson, rowY);
        rowY += rowHeight;

        txtPhone = new RoundedTextField();
        addFormRow(formCard, "PHONE", txtPhone, rowY);
        rowY += rowHeight;

        txtEmail = new RoundedTextField();
        addFormRow(formCard, "EMAIL", txtEmail, rowY);
        rowY += rowHeight;

        txtAddress = new RoundedTextField();
        addFormRow(formCard, "ADDRESS", txtAddress, rowY);

        // =========================
        // BUTTONS
        // =========================

        RoundedButton btnClear = new RoundedButton(
                "Clear", Color.WHITE, new Color(0xEA, 0xF3, 0xFB), TEXT_DARK, true);
        btnClear.setBounds(340, 470, 130, 42);
        panel.add(btnClear);

        RoundedButton btnAdd = new RoundedButton(
                "Add Supplier", CARD_GREEN, CARD_GREEN.darker(), Color.WHITE, false);
        btnAdd.setBounds(190, 470, 140, 42);
        panel.add(btnAdd);

        // =========================
        // BUTTON ACTIONS
        // =========================

        btnAdd.addActionListener(e -> addSupplier());

        btnClear.addActionListener(e -> clearFields());

        // =========================
        // ADD PANEL
        // =========================

        add(panel);

        // Put cursor in first field
        txtName.requestFocus();
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

        field.setBounds(20, y + 22, 450, 36);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(field);
    }

    // =========================================================
    // ADD SUPPLIER
    // =========================================================

    private void addSupplier() {

        String name =
                txtName.getText().trim();

        String contactPerson =
                txtContactPerson.getText().trim();

        String phone =
                txtPhone.getText().trim();

        String email =
                txtEmail.getText().trim();

        String address =
                txtAddress.getText().trim();

        // =========================
        // VALIDATION
        // =========================

        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier Name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            txtName.requestFocus();
            return;
        }

        // =========================
        // INSERT SQL
        // =========================

        String sql =
                "INSERT INTO suppliers " +
                "(name, contact_person, phone, email, address) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            pst.setString(1, name);
            pst.setString(2, contactPerson);
            pst.setString(3, phone);
            pst.setString(4, email);
            pst.setString(5, address);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding supplier:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // CLEAR FIELDS
    // =========================================================

    private void clearFields() {

        txtName.setText("");
        txtContactPerson.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");

        txtName.requestFocus();
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

    // =========================================================
    // MAIN METHOD
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new AddSupplierFrame().setVisible(true);

        });
    }
}