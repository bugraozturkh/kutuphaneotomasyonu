package gui;

import dao.AdminDAO;
import model.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;  // Şifreyi göster/gizle için checkbox

    public LoginForm() {
        getContentPane().setBackground(new Color(128, 128, 255));
        setTitle("ADMİN GİRİŞ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        usernameLabel.setForeground(new Color(0, 0, 0));
        usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        usernameLabel.setBounds(27, 103, 166, 54);

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setForeground(new Color(0, 0, 0));
        passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        passwordLabel.setBounds(102, 186, 79, 45);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Tahoma", Font.BOLD, 20));
        usernameField.setBounds(171, 104, 185, 54);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Tahoma", Font.BOLD, 20));
        passwordField.setBounds(171, 182, 185, 54);
        passwordField.setEchoChar('*');  // Varsayılan olarak şifreyi gizler

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
                } else {
                    passwordField.setEchoChar('*'); // Şifreyi gizle
                }
            }
        });

        // Giriş yap butonu
        JButton loginButton = new JButton("Giriş Yap");
        loginButton.setBackground(new Color(255, 255, 255));
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 20));
        loginButton.setBounds(181, 261, 166, 38);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String adminKullaniciAdi = usernameField.getText();
                String adminSifre = new String(passwordField.getPassword());

                AdminDAO adminDAO = new AdminDAO();
                Admin admin = adminDAO.validateLogin(adminKullaniciAdi, adminSifre);

                if (admin != null) {
                    JOptionPane.showMessageDialog(null, "Giriş Başarılı , Hoşgeldin " + adminKullaniciAdi);
                    // Ana menüyü aç
                    openMainMenu();
                } else {
                    JOptionPane.showMessageDialog(null, "Geçersiz kullanıcı adı veya şifre");
                }
            }
        });

        // Hesabınız yok mu? Kayıt Ol butonu
        JLabel registerLabel = new JLabel("Hesabınız yok mu? ");
        registerLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        registerLabel.setForeground(Color.BLACK);
        registerLabel.setBounds(171, 310, 150, 20);
        getContentPane().add(registerLabel);

        JButton registerButton = new JButton("Kayıt Ol");
        registerButton.setBackground(new Color(255, 255, 255));
        registerButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        registerButton.setBounds(320, 305, 100, 30);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kayıt formuna yönlendir
                openRegisterForm();
            }
        });

        getContentPane().setLayout(null);

        getContentPane().add(usernameLabel);
        getContentPane().add(usernameField);
        getContentPane().add(passwordLabel);
        getContentPane().add(passwordField);
        getContentPane().add(showPasswordCheckBox);  // Checkbox'ı ekle
        getContentPane().add(loginButton);

        getContentPane().add(registerButton);  // Kayıt Ol butonunu ekle
        getContentPane().add(registerLabel);  // Kayıt Ol metnini ekle

        JLabel lblNewLabel = new JLabel("ADMİN GİRİŞ");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        lblNewLabel.setForeground(new Color(0, 0, 0));
        lblNewLabel.setBounds(171, 22, 329, 53);
        getContentPane().add(lblNewLabel);

        setSize(580, 389);
        setVisible(true);
    }

    private void openMainMenu() {
        // Giriş başarılı olduğunda ana menüyü aç
        MainMenu mainMenu = new MainMenu();
        mainMenu.setVisible(true);
        this.dispose(); // Giriş penceresini kapat
    }

    private void openRegisterForm() {
        // Kayıt ol formunu aç
        RegisterForm registerForm = new RegisterForm();
        registerForm.setVisible(true);
        this.dispose(); // Giriş penceresini kapat
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}

