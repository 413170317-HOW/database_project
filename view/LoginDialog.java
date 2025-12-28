package view;

import service.LoginService;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean authenticated = false;
    private LoginService loginService = new LoginService();

    public LoginDialog(Frame owner) {
        super(owner, "系統登入", true);
        setSize(300, 180);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("帳號:"));
        usernameField = new JTextField("admin"); // Default convenience
        formPanel.add(usernameField);

        formPanel.add(new JLabel("密碼:"));
        passwordField = new JPasswordField("admin123");
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("登入");
        JButton exitButton = new JButton("退出");

        loginButton.addActionListener(e -> attemptLogin());
        exitButton.addActionListener(e -> System.exit(0));

        // Allow Enter key to login
        getRootPane().setDefaultButton(loginButton);

        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (loginService.login(username, password)) {
            authenticated = true;
            setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "帳號或密碼錯誤", "登入失敗", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
