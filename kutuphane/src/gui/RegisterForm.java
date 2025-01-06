package gui;

import dao.AdminDAO;
import model.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JCheckBox showPasswordCheckBox;  // Şifreyi göster/gizle için checkbox
    private boolean passwordVisible = false; // Şifre görünür mü kontrolü

    public RegisterForm() {
        getContentPane().setBackground(new Color(128, 128, 255));
        setTitle("ADMİN KAYIT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        usernameLabel.setForeground(new Color(0, 0, 0));
        usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        usernameLabel.setBounds(27, 103, 166, 54);

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setForeground(new Color(0, 0, 0));
        passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        passwordLabel.setBounds(102, 186, 79, 45);

        JLabel confirmPasswordLabel = new JLabel("Şifre Tekrarı:");
        confirmPasswordLabel.setForeground(new Color(0, 0, 0));
        confirmPasswordLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        confirmPasswordLabel.setBounds(27, 267, 166, 54);

        JLabel emailLabel = new JLabel("E-posta:");
        emailLabel.setForeground(new Color(0, 0, 0));
        emailLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        emailLabel.setBounds(77, 352, 171, 45);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Tahoma", Font.BOLD, 20));
        usernameField.setBounds(171, 104, 185, 54);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Tahoma", Font.BOLD, 20));
        passwordField.setBounds(171, 182, 185, 54);
        passwordField.setEchoChar('*');  // Varsayılan olarak şifreyi gizler

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Tahoma", Font.BOLD, 20));
        confirmPasswordField.setBounds(171, 267, 185, 54);
        confirmPasswordField.setEchoChar('*');  // Varsayılan olarak şifreyi gizler

        emailField = new JTextField(20);
        emailField.setFont(new Font("Tahoma", Font.BOLD, 20));
        emailField.setBounds(171, 348, 185, 54);

        // Şifreyi göster/gizle için checkbox
        showPasswordCheckBox = new JCheckBox("Şifreyi Göster");
        showPasswordCheckBox.setFont(new Font("Tahoma", Font.BOLD, 12));
        showPasswordCheckBox.setBounds(375, 196, 150, 30);
        showPasswordCheckBox.setBackground(new Color(128, 128, 255));
        showPasswordCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Checkbox işaretlendiğinde şifreyi göster, kaldırıldığında gizle
                if (showPasswordCheckBox.isSelected()) {
                    passwordField.setEchoChar((char) 0); // Şifreyi göster
                    confirmPasswordField.setEchoChar((char) 0); // Şifreyi göster
                } else {
                    passwordField.setEchoChar('*'); // Şifreyi gizle
                    confirmPasswordField.setEchoChar('*'); // Şifreyi gizle
                }
            }
        });

        JButton registerButton = new JButton("Kayıt Ol");
        registerButton.setBackground(new Color(255, 255, 255));
        registerButton.setFont(new Font("Tahoma", Font.BOLD, 20));
        registerButton.setBounds(181, 429, 166, 38);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String email = emailField.getText();

                // E-posta doğrulama
                if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(null, "Geçersiz e-posta adresi! Lütfen '@' işaretini ve geçerli bir domain girin.");
                    return;
                }

                // Şifrelerin eşleşmesi kontrolü
                if (password.equals(confirmPassword)) {
                    AdminDAO adminDAO = new AdminDAO();
                    boolean isRegistered = adminDAO.registerAdmin(username, password, email);

                    if (isRegistered) {
                        JOptionPane.showMessageDialog(null, "Kayıt Başarılı! Hoşgeldin " + username);
                        // Giriş formuna yönlendir
                        openLoginForm();
                    } else {
                        JOptionPane.showMessageDialog(null, "Kullanıcı adı veya e-posta zaten kullanılıyor.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Şifreler eşleşmiyor!");
                }
            }
        });

        // "Zaten hesabınız var mı?" sorusu ve "Giriş Yap" butonu ekle
        JLabel alreadyHaveAccountLabel = new JLabel("Zaten hesabınız var mı?");
        alreadyHaveAccountLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        alreadyHaveAccountLabel.setBounds(375, 399, 200, 30);
        getContentPane().add(alreadyHaveAccountLabel);

        JButton loginButton = new JButton("Giriş Yap");
        loginButton.setBackground(new Color(255, 255, 255));
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 20));
        loginButton.setBounds(375, 429, 166, 38);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginForm();
            }
        });

        getContentPane().setLayout(null);

        getContentPane().add(usernameLabel);
        getContentPane().add(usernameField);
        getContentPane().add(passwordLabel);
        getContentPane().add(passwordField);
        getContentPane().add(confirmPasswordLabel);
        getContentPane().add(confirmPasswordField);
        getContentPane().add(emailLabel);
        getContentPane().add(emailField);
        getContentPane().add(showPasswordCheckBox);  // Checkbox'ı ekle
        getContentPane().add(registerButton);
        getContentPane().add(alreadyHaveAccountLabel);  // "Zaten hesabınız var mı?" etiketini ekle
        getContentPane().add(loginButton); // Giriş yap butonunu ekle

        JLabel lblNewLabel = new JLabel("ADMİN KAYIT");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        lblNewLabel.setForeground(new Color(0, 0, 0));
        lblNewLabel.setBounds(171, 22, 329, 53);
        getContentPane().add(lblNewLabel);

        setSize(580, 600); // Yüksekliği arttırdık
        setVisible(true);
    }

    // E-posta geçerliliğini kontrol eden metod
    private boolean isValidEmail(String email) {
        return email.contains("@");  // Basit e-posta kontrolü
    }

    private void openLoginForm() {
        // Kayıt başarılı olduğunda veya Giriş Yap butonuna basıldığında giriş yap formuna yönlendir
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
        this.dispose(); // Kayıt formunu kapat
    }

    public static void main(String[] args) {
        new RegisterForm();
    }
}
