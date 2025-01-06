package dao;

import model.Admin;
import util.DatabaseConnection;

import java.sql.*;

public class AdminDAO {
    private Connection connection;

    public AdminDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Admin giriş kontrolü
    public Admin validateLogin(String adminKullaniciAdi, String adminSifre) {
        Admin admin = null;
        String sql = "SELECT * FROM tableAdmin WHERE adminKullaniciAdi = ? AND adminSifre = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, adminKullaniciAdi);
            statement.setString(2, adminSifre);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                admin = new Admin();
                admin.setAdminId(rs.getInt("adminId"));
                admin.setAdminKullaniciAdi(rs.getString("adminKullaniciAdi"));
                admin.setAdminSifre(rs.getString("adminSifre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return admin;
    }

    // Admin kaydı oluşturma
    public boolean registerAdmin(String adminKullaniciAdi, String adminSifre, String adminEmail) {
        // Öncelikle kullanıcı adı ve e-posta kontrolü yapalım.
        if (isAdminExist(adminKullaniciAdi, adminEmail)) {
            return false; // Kullanıcı adı veya e-posta zaten var
        }

        String sql = "INSERT INTO tableAdmin (adminKullaniciAdi, adminSifre, adminEmail) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, adminKullaniciAdi);
            statement.setString(2, adminSifre);
            statement.setString(3, adminEmail);
            int rowsInserted = statement.executeUpdate();

            return rowsInserted > 0; // Başarıyla kayıt yapıldıysa true döner
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Hata olursa false döner
        }
    }

    // Admin kullanıcı adı veya e-posta kontrolü
    private boolean isAdminExist(String adminKullaniciAdi, String adminEmail) {
        String sql = "SELECT * FROM tableAdmin WHERE adminKullaniciAdi = ? OR adminEmail = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, adminKullaniciAdi);
            statement.setString(2, adminEmail);
            ResultSet rs = statement.executeQuery();

            return rs.next(); // Eğer kullanıcı adı veya e-posta varsa, true döner
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}





