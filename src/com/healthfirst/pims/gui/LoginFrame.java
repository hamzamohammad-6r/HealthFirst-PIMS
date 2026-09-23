package com.healthfirst.pims.gui;

import com.healthfirst.pims.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    // ---- Palette ----------------------------------------------------
    private static final Color BRAND_DARK   = new Color(0x0F, 0x4C, 0x81);  // deep medical blue
    private static final Color BRAND_LIGHT  = new Color(0x2E, 0x86, 0xC1);  // lighter blue for gradient
    private static final Color ACCENT       = new Color(0x1A, 0xBC, 0x9C);  // teal accent (pharmacy cross)
    private static final Color BG_LIGHT     = new Color(0xF4, 0xF7, 0xFB);
    private static final Color FIELD_BORDER = new Color(0xD5, 0xDC, 0xE4);
    private static final Color FIELD_FOCUS  = BRAND_LIGHT;
    private static final Color TEXT_DARK    = new Color(0x2C, 0x3E, 0x50);
    private static final Color TEXT_MUTED   = new Color(0x8A, 0x93, 0x9E);

    private RoundedTextField txtUsername;
    private RoundedPasswordField txtPassword;

    public LoginFrame() {

        setTitle("HealthFirst PIMS - Login");
        setSize(460, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(false);

        buildUI();
    }

    private void buildUI() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildForm(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    // ------------------------------------------------------------------
    // Header: gradient banner with logo badge + titles
    // ------------------------------------------------------------------
    private JPanel buildHeader() {

        JPanel header = new JPanel(new GridBagLayout()) {
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
        header.setPreferredSize(new Dimension(460, 190));
        header.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        gbc.gridy = 0;
        header.add(buildLogoBadge(), gbc);

        JLabel title = new JLabel("HealthFirst PIMS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(12, 0, 2, 0);
        header.add(title, gbc);

        JLabel subtitle = new JLabel("Pharmacy Inventory & Management System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(255, 255, 255, 210));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        header.add(subtitle, gbc);

        return header;
    }

    private JComponent buildLogoBadge() {
        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth(), getHeight());

                // simple pharmacy "+" cross
                g2.setColor(ACCENT);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int barLong = getWidth() / 2, barThick = getWidth() / 6;
                g2.fillRoundRect(cx - barThick / 2, cy - barLong / 2, barThick, barLong, 4, 4);
                g2.fillRoundRect(cx - barLong / 2, cy - barThick / 2, barLong, barThick, 4, 4);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(64, 64));
        badge.setOpaque(false);
        return badge;
    }

    // ------------------------------------------------------------------
    // Form: rounded fields, labels, buttons
    // ------------------------------------------------------------------
    private JPanel buildForm() {

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(30, 45, 10, 45));

        JLabel userLabel = fieldLabel("USERNAME");
        txtUsername = new RoundedTextField();
        styleField(txtUsername);

        JLabel passLabel = fieldLabel("PASSWORD");
        txtPassword = new RoundedPasswordField();
        styleField(txtPassword);

        RoundedButton btnLogin = new RoundedButton("Sign In", BRAND_DARK, BRAND_LIGHT);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        RoundedButton btnExit = new RoundedButton("Exit", new Color(0xEC, 0xF0, 0xF3), new Color(0xE0, 0xE6, 0xEB));
        btnExit.setForeground(TEXT_MUTED);
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnExit.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        wrapper.add(userLabel);
        wrapper.add(Box.createVerticalStrut(6));
        wrapper.add(txtUsername);
        wrapper.add(Box.createVerticalStrut(18));
        wrapper.add(passLabel);
        wrapper.add(Box.createVerticalStrut(6));
        wrapper.add(txtPassword);
        wrapper.add(Box.createVerticalStrut(28));
        wrapper.add(btnLogin);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(btnExit);

        btnLogin.addActionListener(e -> login());
        btnExit.addActionListener(e -> System.exit(0));
        txtPassword.addActionListener(e -> login());

        return wrapper;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleField(JComponent field) {
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 42));
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_LIGHT);
        JLabel note = new JLabel("\u00A9 HealthFirst PIMS  \u2022  Authorized personnel only");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        note.setForeground(TEXT_MUTED);
        footer.add(note);
        return footer;
    }

    // ------------------------------------------------------------------
    // Login logic (unchanged behavior, just referencing new fields)
    // ------------------------------------------------------------------
    private void login() {

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your username and password.",
                    "Login Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "SELECT user_id, username, role, full_name " +
                "FROM users " +
                "WHERE username = ? AND password = ?";

        try (Connection con =
                     DatabaseConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int userId = rs.getInt("user_id");
                    String role = rs.getString("role");
                    String fullName = rs.getString("full_name");

                    JOptionPane.showMessageDialog(
                            this,
                            "Welcome, " + fullName + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    dispose();

                    if ("Admin".equalsIgnoreCase(role)) {

                        AdminDashboard dashboard =
                                new AdminDashboard();

                        dashboard.setVisible(true);

                    } else if ("Cashier".equalsIgnoreCase(role)) {

                        CashierDashboard dashboard =
                                new CashierDashboard(
                                        userId,
                                        fullName
                                );

                        dashboard.setVisible(true);

                    } else {

                        JOptionPane.showMessageDialog(
                                null,
                                "Unknown user role.",
                                "Login Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );

                    txtPassword.setText("");
                    txtPassword.requestFocus();
                }

            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database connection error:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==================================================================
    // Custom rounded, focus-aware text field
    // ==================================================================
    private static class RoundedTextField extends JTextField {
        RoundedTextField() {
            super();
            setOpaque(false);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(TEXT_DARK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isFocusOwner() ? FIELD_FOCUS : FIELD_BORDER);
            g2.setStroke(new BasicStroke(isFocusOwner() ? 1.6f : 1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1.5f, getHeight() - 1.5f, 12, 12));
            g2.dispose();
        }

        @Override
        public void repaint() {
            super.repaint();
        }
    }

    // ==================================================================
    // Custom rounded, focus-aware password field
    // ==================================================================
    private static class RoundedPasswordField extends JPasswordField {
        RoundedPasswordField() {
            super();
            setOpaque(false);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(TEXT_DARK);
            setEchoChar('\u2022');
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isFocusOwner() ? FIELD_FOCUS : FIELD_BORDER);
            g2.setStroke(new BasicStroke(isFocusOwner() ? 1.6f : 1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1.5f, getHeight() - 1.5f, 12, 12));
            g2.dispose();
        }
    }

    // ==================================================================
    // Custom rounded button with hover/press states
    // ==================================================================
    private static class RoundedButton extends JButton {
        private final Color base;
        private final Color hover;
        private boolean isHover = false;
        private boolean isPressed = false;

        RoundedButton(String text, Color base, Color hover) {
            super(text);
            this.base = base;
            this.hover = hover;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
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
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.dispose();

            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            LoginFrame login =
                    new LoginFrame();

            login.setVisible(true);
        });
    }
}