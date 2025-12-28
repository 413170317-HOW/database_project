package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class DataService {

    public void generateAndInsertData(int studentCount, int questionCount) {
        System.out.println("開始生成並寫入資料 (模擬 " + studentCount + " 名學生)...");
        ensureTableExists();

        String insertSQL = "INSERT INTO ExamResults (student_id, wrong_count, total_questions, created_at) VALUES (?, ?, ?, GETDATE())";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            if (conn == null)
                return;

            // Disable auto-commit for batch processing performance
            conn.setAutoCommit(false);

            Random random = new Random();
            long startTime = System.currentTimeMillis();

            for (int i = 1; i <= studentCount; i++) {
                int wrongCount = 0;
                // 模擬 42 題是非題，正確答案皆為 True
                // 隨機生成 True/False，如果生成 False 則錯誤
                for (int q = 0; q < questionCount; q++) {
                    boolean studentAnswer = random.nextBoolean();
                    boolean correctAnswer = true;

                    if (studentAnswer != correctAnswer) {
                        wrongCount++;
                    }
                }

                pstmt.setInt(1, i);
                pstmt.setInt(2, wrongCount);
                pstmt.setInt(3, questionCount);
                pstmt.addBatch();

                // 每 1000 筆執行一次 batch，避免記憶體溢出
                if (i % 1000 == 0) {
                    pstmt.executeBatch();
                    System.out.print("."); // 進度條效果
                }
            }

            pstmt.executeBatch(); // 執行剩餘的
            conn.commit(); // 提交事務

            long endTime = System.currentTimeMillis();
            System.out.println("\n資料寫入完成！耗時: " + (endTime - startTime) + " ms");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ensureTableExists() {
        String createTableSQL = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ExamResults' AND xtype='U') " +
                "BEGIN " +
                "CREATE TABLE ExamResults (" +
                "id INT IDENTITY(1,1) PRIMARY KEY, " +
                "student_id INT, " +
                "wrong_count INT, " +
                "total_questions INT, " +
                "created_at DATETIME" +
                "); " +
                "END";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(createTableSQL);
            }
        } catch (SQLException e) {
            System.out.println("初始化 ExamResults 資料表失敗: " + e.getMessage());
        }
    }
}
