package com.healthfirst.pims.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

import com.healthfirst.pims.database.DatabaseConnection;

public class AdminDashboard extends JFrame {

    // ---- Palette (matches LoginFrame) --------------------------------
    private static final Color BRAND_DARK   = new Color(0x0F, 0x4C, 0x81);
    private static final Color BRAND_LIGHT  = new Color(0x2E, 0x86, 0xC1);
    private static final Color ACCENT       = new Color(0x1A, 0xBC, 0x9C);
    private static final Color BG_LIGHT     = new Color(0xF4, 0xF7, 0xFB);
    private static final Color TEXT_DARK    = new Color(0x2C, 0x3E, 0x50);
    private static final Color TEXT_MUTED   = new Color(0x8A, 0x93, 0x9E);
    private static final Color CARD_BORDER  = new Color(0xE3, 0xE8, 0xEE);

    private static final Color CARD_BLUE    = new Color(0x2E, 0x86, 0xC1);
    private static final Color CARD_GREEN   = new Color(0x27, 0xAE, 0x60);
    private static final Color CARD_ORANGE  = new Color(0xF3, 0x9C, 0x12);
    private static final Color CARD_PURPLE  = new Color(0x8E, 0x44, 0xAD);
    private static final Color CARD_RED     = new Color(0xC0, 0x39, 0x2B);

    private JLabel lblMedicineCount;
    private JLabel lblSupplierCount;
    private JLabel lblSalesCount;
    private JLabel lblTodaySales;
    private JLabel lblLowStock;
    private JLabel lblExpiring;

    private JButton btnLogout;
    private JButton btnExit;

    public AdminDashboard() {

        setTitle("HealthFirst PIMS - Admin Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        headerPanel.setBounds(0, 0, 1000, 100);
        mainPanel.add(headerPanel);

        // logo badge
        JComponent logoBadge = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(ACCENT);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int barLong = getWidth() / 2, barThick = getWidth() / 6;
                g2.fillRoundRect(cx - barThick / 2, cy - barLong / 2, barThick, barLong, 4, 4);
                g2.fillRoundRect(cx - barLong / 2, cy - barThick / 2, barLong, barThick, 4, 4);
                g2.dispose();
            }
        };
        logoBadge.setBounds(35, 22, 56, 56);
        headerPanel.add(logoBadge);

        JLabel lblTitle = new JLabel("HealthFirst PIMS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(105, 18, 400, 38);
        headerPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel(
                "Pharmacy Information Management System"
        );
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(255, 255, 255, 210));
        lblSubtitle.setBounds(108, 56, 400, 22);
        headerPanel.add(lblSubtitle);

        // admin avatar chip
        JComponent avatar = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                String initial = "A";
                int tx = (getWidth() - fm.stringWidth(initial)) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(initial, tx, ty);
                g2.dispose();
            }
        };
        avatar.setBounds(770, 32, 36, 36);
        headerPanel.add(avatar);

        JLabel lblAdmin = new JLabel("Administrator");
        lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAdmin.setForeground(Color.WHITE);
        lblAdmin.setBounds(815, 40, 150, 22);
        headerPanel.add(lblAdmin);

        // =====================================================
        // DASHBOARD TITLE
        // =====================================================

        JLabel lblDashboard = new JLabel("Dashboard Overview");
        lblDashboard.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblDashboard.setForeground(TEXT_DARK);
        lblDashboard.setBounds(40, 118, 350, 38);
        mainPanel.add(lblDashboard);

        // =====================================================
        // STAT CARDS
        // =====================================================

        JPanel medicineCard = createStatCard("TOTAL MEDICINES", CARD_BLUE, 40, 170);
        lblMedicineCount = statValueLabel("0");
        medicineCard.add(lblMedicineCount);
        mainPanel.add(medicineCard);

        JPanel supplierCard = createStatCard("TOTAL SUPPLIERS", CARD_GREEN, 270, 170);
        lblSupplierCount = statValueLabel("0");
        supplierCard.add(lblSupplierCount);
        mainPanel.add(supplierCard);

        JPanel salesCard = createStatCard("TOTAL SALES", CARD_ORANGE, 500, 170);
        lblSalesCount = statValueLabel("0");
        salesCard.add(lblSalesCount);
        mainPanel.add(salesCard);

        JPanel todaySalesCard = createStatCard("TODAY'S SALES", CARD_PURPLE, 730, 170);
        lblTodaySales = statValueLabel("R 0.00");
        lblTodaySales.setFont(new Font("Segoe UI", Font.BOLD, 22));
        todaySalesCard.add(lblTodaySales);
        mainPanel.add(todaySalesCard);

        // =====================================================
        // ALERTS
        // =====================================================

        JLabel lblAlerts = new JLabel("Inventory Alerts");
        lblAlerts.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAlerts.setForeground(TEXT_DARK);
        lblAlerts.setBounds(40, 340, 300, 34);
        mainPanel.add(lblAlerts);

        // Low Stock
        RoundedCard lowStockCard = new RoundedCard(CARD_RED);
        lowStockCard.putClientProperty("cardMode", "accent");
        lowStockCard.setLayout(null);
        lowStockCard.setBounds(40, 385, 400, 100);
        mainPanel.add(lowStockCard);

        JLabel lblLowStockTitle = new JLabel("LOW STOCK MEDICINES");
        lblLowStockTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLowStockTitle.setForeground(TEXT_MUTED);
        lblLowStockTitle.setBounds(28, 16, 280, 24);
        lowStockCard.add(lblLowStockTitle);

        lblLowStock = new JLabel("0");
        lblLowStock.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLowStock.setForeground(CARD_RED);
        lblLowStock.setBounds(28, 48, 250, 36);
        lowStockCard.add(lblLowStock);

        // Expiring
        RoundedCard expiryCard = new RoundedCard(CARD_ORANGE);
        expiryCard.putClientProperty("cardMode", "accent");
        expiryCard.setLayout(null);
        expiryCard.setBounds(470, 385, 400, 100);
        mainPanel.add(expiryCard);

        JLabel lblExpiryTitle = new JLabel("EXPIRING MEDICINES");
        lblExpiryTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblExpiryTitle.setForeground(TEXT_MUTED);
        lblExpiryTitle.setBounds(28, 16, 280, 24);
        expiryCard.add(lblExpiryTitle);

        lblExpiring = new JLabel("0");
        lblExpiring.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblExpiring.setForeground(CARD_ORANGE);
        lblExpiring.setBounds(28, 48, 250, 36);
        expiryCard.add(lblExpiring);

        // =====================================================
        // QUICK ACTIONS
        // =====================================================

        JLabel lblQuickActions = new JLabel("Quick Actions");
        lblQuickActions.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblQuickActions.setForeground(TEXT_DARK);
        lblQuickActions.setBounds(40, 510, 250, 34);
        mainPanel.add(lblQuickActions);

        RoundedButton btnAddMedicine = new RoundedButton(
                "Add Medicine", Color.WHITE, new Color(0xEA, 0xF3, 0xFB), CARD_BLUE, true);
        btnAddMedicine.setBounds(40, 555, 150, 42);
        mainPanel.add(btnAddMedicine);

        RoundedButton btnAddSupplier = new RoundedButton(
                "Add Supplier", Color.WHITE, new Color(0xEA, 0xF3, 0xFB), CARD_BLUE, true);
        btnAddSupplier.setBounds(205, 555, 150, 42);
        mainPanel.add(btnAddSupplier);

        RoundedButton btnNewSale = new RoundedButton(
                "New Sale", CARD_GREEN, CARD_GREEN.darker(), Color.WHITE, false);
        btnNewSale.setBounds(370, 555, 150, 42);
        mainPanel.add(btnNewSale);

        RoundedButton btnRefresh = new RoundedButton(
                "Refresh Dashboard", Color.WHITE, new Color(0xEA, 0xF3, 0xFB), TEXT_DARK, true);
        btnRefresh.setBounds(535, 555, 160, 42);
        mainPanel.add(btnRefresh);

        RoundedButton btnLogoutBtn = new RoundedButton(
                "Logout", Color.WHITE, new Color(0xEA, 0xF3, 0xFB), TEXT_DARK, true);
        btnLogoutBtn.setBounds(710, 555, 110, 42);
        mainPanel.add(btnLogoutBtn);
        btnLogout = btnLogoutBtn;

        RoundedButton btnExitBtn = new RoundedButton(
                "Exit", CARD_RED, CARD_RED.darker(), Color.WHITE, false);
        btnExitBtn.setBounds(835, 555, 100, 42);
        mainPanel.add(btnExitBtn);
        btnExit = btnExitBtn;

        // =====================================================
        // BUTTON ACTIONS
        // =====================================================

        btnAddMedicine.addActionListener(e ->
                new AddMedicineFrame().setVisible(true)
        );

        btnAddSupplier.addActionListener(e ->
                new AddSupplierFrame().setVisible(true)
        );

        btnNewSale.addActionListener(e ->
                new NewSaleFrame().setVisible(true)
        );

        btnRefresh.addActionListener(e ->
                loadDashboardData()
        );

        btnLogout.addActionListener(e -> {

            int choice =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to logout?",
                            "Confirm Logout",
                            JOptionPane.YES_NO_OPTION
                    );

            if (choice == JOptionPane.YES_OPTION) {

                dispose();

                new LoginFrame().setVisible(true);
            }
        });

        btnExit.addActionListener(e -> {

            int choice =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to exit?",
                            "Confirm Exit",
                            JOptionPane.YES_NO_OPTION
                    );

            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        // =====================================================
        // ADD PANEL
        // =====================================================

        add(mainPanel);

        // Load live database information
        loadDashboardData();
    }

    // =========================================================
    // CREATE STAT CARD (rounded, colored, with subtle shadow)
    // =========================================================

    private JPanel createStatCard(
            String title,
            Color color,
            int x,
            int y
    ) {

        RoundedCard card = new RoundedCard(color);
        card.setLayout(null);
        card.setBounds(x, y, 210, 140);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(new Color(255, 255, 255, 225));
        titleLabel.setBounds(22, 18, 180, 26);
        card.add(titleLabel);

        return card;
    }

    private JLabel statValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(22, 52, 220, 50);
        return lbl;
    }

    // =========================================================
    // LOAD DASHBOARD DATA
    // =========================================================

    private void loadDashboardData() {

        loadMedicineCount();
        loadSupplierCount();
        loadSalesCount();
        loadTodaySales();
        loadLowStock();
        loadExpiringMedicines();
    }

    // =========================================================
    // MEDICINE COUNT
    // =========================================================

    private void loadMedicineCount() {

        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM medicines";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery()
        ) {

            if (rs.next()) {

                lblMedicineCount.setText(
                        String.valueOf(
                                rs.getInt("total")
                        )
                );
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    // =========================================================
    // SUPPLIER COUNT
    // =========================================================

    private void loadSupplierCount() {

        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM suppliers";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery()
        ) {

            if (rs.next()) {

                lblSupplierCount.setText(
                        String.valueOf(
                                rs.getInt("total")
                        )
                );
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    // =========================================================
    // TOTAL SALES
    // =========================================================

    private void loadSalesCount() {

        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM sales";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery()
        ) {

            if (rs.next()) {

                lblSalesCount.setText(
                        String.valueOf(
                                rs.getInt("total")
                        )
                );
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    // =========================================================
    // TODAY'S SALES
    // =========================================================

    private void loadTodaySales() {

        String sql =
                "SELECT COALESCE(SUM(total_amount), 0) AS total " +
                "FROM sales " +
                "WHERE DATE(sale_date) = CURDATE()";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery()
        ) {

            if (rs.next()) {

                lblTodaySales.setText(
                        "R " +
                        String.format(
                                "%.2f",
                                rs.getDouble("total")
                        )
                );
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    // =========================================================
    // LOW STOCK
    // =========================================================

    private void loadLowStock() {

        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM medicines " +
                "WHERE quantity_in_stock <= reorder_level";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery()
        ) {

            if (rs.next()) {

                lblLowStock.setText(
                        rs.getInt("total")
                                + " medicine(s)"
                );
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    // =========================================================
    // EXPIRING MEDICINES
    // =========================================================

    private void loadExpiringMedicines() {

        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM medicines " +
                "WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery()
        ) {

            if (rs.next()) {

                lblExpiring.setText(
                        rs.getInt("total")
                                + " medicine(s)"
                );
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    // ==================================================================
    // Rounded card with soft drop shadow. Two modes:
    //  - colored "fill" card (stat tiles): pass a saturated color
    //  - white card with a left accent bar (alerts): handled by caller
    //    setting background white and using accent as border stripe
    // ==================================================================
    private static class RoundedCard extends JPanel {
        private final Color fill;

        RoundedCard(Color fill) {
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // soft shadow
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fill(new RoundRectangle2D.Float(3, 5, w - 6, h - 6, 16, 16));

            // white background cards (alerts) get a white body + colored left stripe;
            // stat cards get a full color fill. Distinguish via a client property.
            Object mode = getClientProperty("cardMode");
            if ("accent".equals(mode)) {
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 3, h - 3, 16, 16));
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 4, h - 4, 16, 16));
                g2.setColor(fill);
                g2.fill(new RoundRectangle2D.Float(0, 0, 6, h - 3, 16, 16));
                g2.fillRect(3, 0, 4, h - 3);
            } else {
                GradientPaint gp = new GradientPaint(
                        0, 0, fill,
                        w, h, fill.darker()
                );
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 3, h - 3, 16, 16));
            }

            g2.dispose();
        }
    }

    // ==================================================================
    // Rounded button with hover/press states (same style as LoginFrame)
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
                g2.setColor(CARD_BORDER);
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

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            AdminDashboard dashboard =
                    new AdminDashboard();

            dashboard.setVisible(true);
        });
    }
}