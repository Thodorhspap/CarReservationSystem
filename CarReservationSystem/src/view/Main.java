package view;

import com.formdev.flatlaf.FlatLightLaf;
import controller.AuthController;
import util.UITheme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("@accentColor", UITheme.SECONDARY);
        UIManager.put("Panel.background", UITheme.BACKGROUND);

        // Εκκίνηση UI στο Event Dispatch Thread (EDT) — απαίτηση της Swing
        SwingUtilities.invokeLater(() -> {
            AuthController authController = new AuthController();
            LoginView loginView = new LoginView(authController);
            loginView.setVisible(true);
        });
    }
}
