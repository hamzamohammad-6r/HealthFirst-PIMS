package com.healthfirst.pims.gui;

import com.healthfirst.pims.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CashierDashboard extends JFrame {

    // ---- Palette (matches LoginFrame / AdminDashboard) ---------------
    private static final Color BRAND_DARK   = new Color(0x0F, 0x4C, 0x81);
    private static final Color BRAND_LIGHT  = new Color(0x2E, 0x86, 0xC1);
    private static final Color ACCENT       = new Color(0x1A, 0xBC, 0x9C);
    private static final Color BG_LIGHT     = new Color(0xF4, 0xF7, 0xFB);
    private static final Color TEXT_DARK    = new Color(0x2C, 0x3E, 0x50);
    private static final Color TEXT_MUTED   = new Color(0x8A, 0x93, 0x9E);
    private static final Color CARD_BORDER  = new Color(0xE3, 0xE8, 0xEE);

    private static final Color CARD_BLUE    = new Color(0x2E, 0x86, 0xC1);
    private static final Color CARD_GREEN   = new Color(0x27, 0xAE, 0x60);
    private static final Color CARD_PURPLE  = new Color(0x8E, 0x44, 0xAD);
    private static final Color CARD_RED     = new Color(0xC0, 0x39, 0x2B);

    private int userId;
    private String fullName;

    // Dashboard cards
    private JLabel lblTodaySales;
    private JLabel lblSalesCount;
    private JLabel lblMedicines;

    public CashierDashboard(int userId, String fullName) {

        this.userId = userId;
        this.fullName = fullName;

        setTitle("HealthFirst PIMS - Cashier Dashboard");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        buildUI();
        loadDashboardData();
    }

    // =========================================================
    // BUILD USER INTERFACE
    // =========================================================

    private void buildUI() {

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setPreferredSize(new Dimension(1100, 650));

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
        headerPanel.setBounds(0, 0, 1100, 100);
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

        JLabel titleLabel = new JLabel("HealthFirst PIMS");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setBounds(105, 18, 420, 38);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Pharmacy Management System");
        subtitleLabel.setForeground(new Color(255, 255, 255, 210));
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setBounds(108, 56, 420, 22);
        headerPanel.add(subtitleLabel);

        // cashier avatar chip
        String initial = (fullName != null && !fullName.isEmpty())
                ? fullName.trim().substring(0, 1).toUpperCase()
                : "C";

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
                int tx = (getWidth() - fm.stringWidth(initial)) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(initial, tx, ty);
                g2.dispose();
            }
        };
        avatar.setBounds(830, 32, 36, 36);
        headerPanel.add(avatar);

        JLabel cashierLabel = new JLabel("Cashier: " + fullName);
        cashierLabel.setForeground(Color.WHITE);
        cashierLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cashierLabel.setBounds(875, 40, 200, 22);
        headerPanel.add(cashierLabel);

        // =====================================================
        // WELCOME LABEL
        // =====================================================

        JLabel welcomeLabel = new JLabel("Cashier Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(TEXT_DARK);
        welcomeLabel.setBounds(40, 118, 400, 38);
        mainPanel.add(welcomeLabel);

        // =====================================================
        // DASHBOARD CARDS
        // =====================================================

        RoundedCard salesCard = createStatCard("TODAY'S SALES", CARD_PURPLE, 40, 170, 330);
        lblTodaySales = statValueLabel("R 0.00");
        lblTodaySales.setFont(new Font("Segoe UI", Font.BOLD, 30));
        salesCard.add(lblTodaySales);
        mainPanel.add(salesCard);

        RoundedCard countCard = createStatCard("SALES TODAY", CARD_BLUE, 385, 170, 330);
        lblSalesCount = statValueLabel("0");
        countCard.add(lblSalesCount);
        mainPanel.add(countCard);

        RoundedCard medicineCard = createStatCard("AVAILABLE MEDICINES", CARD_GREEN, 730, 170, 330);
        lblMedicines = statValueLabel("0");
        medicineCard.add(lblMedicines);
        mainPanel.add(medicineCard);

        // =====================================================
        // QUICK ACTIONS
        // =====================================================

        JLabel quickLabel = new JLabel("Quick Actions");
        quickLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        quickLabel.setForeground(TEXT_DARK);
        quickLabel.setBounds(40, 340, 250, 34);
        mainPanel.add(quickLabel);

        RoundedButton btnNewSale = new RoundedButton(
                "New Sale", CARD_GREEN, CARD_GREEN.darker(), Color.WHITE, false);
        btnNewSale.setBounds(40, 385, 240, 46);
        mainPanel.add(btnNewSale);

        RoundedButton btnStock = new RoundedButton(
                "Stock Check", Color.WHITE, new Color(0xEA, 0xF3, 0xFB), TEXT_DARK, true);
        btnStock.setBounds(300, 385, 240, 46);
        mainPanel.add(btnStock);

        RoundedButton btnRefresh = new RoundedButton(
                "Refresh", Color.WHITE, new Color(0xEA, 0xF3, 0xFB), TEXT_DARK, true);
        btnRefresh.setBounds(560, 385, 240, 46);
        mainPanel.add(btnRefresh);

        RoundedButton btnLogout = new RoundedButton(
                "Logout", Color.WHITE, new Color(0xFD, 0xEC, 0xEA), CARD_RED, true);
        btnLogout.setBounds(820, 385, 240, 46);
        mainPanel.add(btnLogout);

        // =====================================================
        // FOOTER
        // =====================================================

        JLabel footerLabel = new JLabel(
                "HealthFirst PIMS - Cashier Module",
                SwingConstants.CENTER
        );
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(TEXT_MUTED);
        footerLabel.setBounds(0, 600, 1100, 24);
        mainPanel.add(footerLabel);

        add(mainPanel);

        // =====================================================
        // BUTTON ACTIONS
        // =====================================================

        btnNewSale.addActionListener(e -> openNewSale());

        btnStock.addActionListener(e -> openStockCheck());

        btnRefresh.addActionListener(e ->
                loadDashboardData()
        );

        btnLogout.addActionListener(e ->
                logout()
        );
    }

    // =========================================================
    // CREATE STAT CARD
    // =========================================================

    private RoundedCard createStatCard(
            String title,
            Color color,
            int x,
            int y,
            int width
    ) {

        RoundedCard card = new RoundedCard(color);
        card.setLayout(null);
        card.setBounds(x, y, width, 140);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(new Color(255, 255, 255, 225));
        titleLabel.setBounds(24, 18, width - 40, 26);
        card.add(titleLabel);

        return card;
    }

    private JLabel statValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(24, 55, 280, 50);
        return lbl;
    }

    // =========================================================
    // LOAD DASHBOARD DATA
    // =========================================================

    private void loadDashboardData() {

        loadTodaySales();
        loadSalesCount();
        loadMedicineCount();
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

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {

                double total =
                        rs.getDouble("total");

                lblTodaySales.setText(
                        String.format(
                                "R %.2f",
                                total
                        )
                );
            }

        } catch (Exception ex) {

            lblTodaySales.setText(
                    "R 0.00"
            );
        }
    }

    // =========================================================
    // SALES COUNT
    // =========================================================

    private void loadSalesCount() {

        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM sales " +
                "WHERE DATE(sale_date) = CURDATE()";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {

                lblSalesCount.setText(
                        String.valueOf(
                                rs.getInt("total")
                        )
                );
            }

        } catch (Exception ex) {

            lblSalesCount.setText("0");
        }
    }

    // =========================================================
    // MEDICINE COUNT
    // =========================================================

    private void loadMedicineCount() {

        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM medicines " +
                "WHERE quantity_in_stock > 0";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {

                lblMedicines.setText(
                        String.valueOf(
                                rs.getInt("total")
                        )
                );
            }

        } catch (Exception ex) {

            lblMedicines.setText("0");
        }
    }

    // =========================================================
    // OPEN NEW SALE
    // =========================================================

    private void openNewSale() {

        NewSaleFrame saleFrame =
                new NewSaleFrame();

        saleFrame.setVisible(true);
    }

    // =========================================================
    // STOCK CHECK
    // =========================================================

    private void openStockCheck() {

        StockCheckFrame stockFrame =
                new StockCheckFrame();

        stockFrame.setVisible(true);
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logout() {

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice ==
                JOptionPane.YES_OPTION) {

            dispose();

            LoginFrame login =
                    new LoginFrame();

            login.setVisible(true);
        }
    }

    // ==================================================================
    // Rounded gradient stat card with soft drop shadow
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

            GradientPaint gp = new GradientPaint(
                    0, 0, fill,
                    w, h, fill.darker()
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(0, 0, w - 3, h - 3, 16, 16));

            g2.dispose();
        }
    }

    // ==================================================================
    // Rounded button with hover/press states (same style as elsewhere)
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
            setFont(new Font("Segoe UI", outlined ? Font.PLAIN : Font.BOLD, 14));
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
}