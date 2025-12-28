package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // 連線設定
    // 注意：localhost 是本機，1433 是 SQL Server 預設 Port
    // encrypt=true;trustServerCertificate=true; 是解決新版驅動 SSL 報錯的關鍵
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=ProjectG5;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "G5";
    private static final String PASSWORD = "passw0rd";

    public static Connection getConnection() {
        try {
            // 載入驅動 (新版 JDBC 其實可以省略這行，但寫著保險)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}