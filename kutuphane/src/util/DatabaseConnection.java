package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=javakutuphane;encrypt=true;trustServerCertificate=true;"; // Veritabanı URL
    private static final String DB_USER = "javauser"; // Veritabanı kullanıcı adı
    private static final String DB_PASSWORD = "javauser"; // Veritabanı şifresi

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
