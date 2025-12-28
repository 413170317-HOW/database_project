package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginService {

    public boolean login(String username, String password) {
        // Ensure table exists (for this demo project)
        ensureTableExists();

        String sql = "SELECT COUNT(*) FROM SystemUsers WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("資料庫連線失敗，無法登入。");
                return false;
            }

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void ensureTableExists() {
        String createTableSQL = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='SystemUsers' AND xtype='U') " +
                "BEGIN " +
                "CREATE TABLE SystemUsers (username NVARCHAR(50), password NVARCHAR(50)); " +
                "INSERT INTO SystemUsers (username, password) VALUES ('admin', 'admin123'); " +
                "END";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(createTableSQL);
            }
        } catch (SQLException e) {
            System.out.println("初始化 SystemUsers 資料表失敗: " + e.getMessage());
        }
    }
}
