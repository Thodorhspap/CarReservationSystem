package view;

import controller.AuthController;
import model.entities.User;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class LoginView extends JFrame {

    private final AuthController authController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginView(AuthController authController) {
        this.authController = authController;
        buildUI();
    }

    private void buildUI() {
        setTitle("Car Reservation System — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BACKGROUND);

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BACKGROUND);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.OUTLINE_VARIANT),
                new EmptyBorder(32, 32, 28, 32)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel title = new JLabel("Fleet Intelligence", SwingConstants.CENTER);
        title.setFont(UITheme.fontH1());
        title.setForeground(UITheme.PRIMARY);
        card.add(title, gbc);

        gbc.gridy = 1;
        JLabel subtitle = new JLabel("CAR RESERVATION SYSTEM", SwingConstants.CENTER);
        subtitle.setFont(UITheme.fontSmall());
        subtitle.setForeground(UITheme.ON_SURFACE_VARIANT);
        card.add(subtitle, gbc);

        gbc.gridy = 2;
        card.add(Box.createVerticalStrut(18), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3;
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(UITheme.fontBodyBold());
        userLabel.setForeground(UITheme.ON_SURFACE);
        gbc.gridx = 0; gbc.gridwidth = 2;
        card.add(userLabel, gbc);

        gbc.gridy = 4;
        usernameField = new JTextField();
        usernameField.setFont(UITheme.fontBody());
        usernameField.setPreferredSize(new Dimension(0, 34));
        card.add(usernameField, gbc);

        gbc.gridy = 5;
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UITheme.fontBodyBold());
        passLabel.setForeground(UITheme.ON_SURFACE);
        card.add(passLabel, gbc);

        gbc.gridy = 6;
        passwordField = new JPasswordField();
        passwordField.setFont(UITheme.fontBody());
        passwordField.setPreferredSize(new Dimension(0, 34));
        card.add(passwordField, gbc);

        gbc.gridy = 7;
        card.add(Box.createVerticalStrut(10), gbc);

        JButton loginButton = new JButton("Login");
        UITheme.stylePrimaryButton(loginButton);
        loginButton.setPreferredSize(new Dimension(0, 40));
        gbc.gridy = 8;
        card.add(loginButton, gbc);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(UITheme.fontSmall());
        messageLabel.setForeground(UITheme.ERROR);
        gbc.gridy = 9;
        card.add(messageLabel, gbc);

        outer.add(card);
        add(outer);

        // Action listener — η μόνη λογική εδώ είναι η δρομολόγηση, όχι επιχειρησιακή λογική
        loginButton.addActionListener(e -> handleLogin());

        // Enter key στο passwordField ενεργοποιεί login
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Συμπληρώστε username και password.");
            return;
        }

        try {
            boolean success = authController.login(username, password);
            if (success) {
                User user = authController.getLoggedInUser();
                openDashboard(user);
            } else {
                messageLabel.setText("Λάθος username ή password.");
                passwordField.setText("");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Σφάλμα σύνδεσης με τη βάση.");
            ex.printStackTrace();
        }
    }

    private void openDashboard(User user) {
        dispose(); // κλείνει το login window

        switch (user.getRole()) {
            case "CUSTOMER" -> new CustomerDashboardView(authController).setVisible(true);
            case "SECRETARY" -> new SecretaryDashboardView(authController).setVisible(true);
            case "ADMIN"    -> new AdminDashboardView(authController).setVisible(true);
            default -> JOptionPane.showMessageDialog(null, "Άγνωστος ρόλος: " + user.getRole());
        }
    }
}
