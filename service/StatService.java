package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatService {

    public void printStatistics() {
        System.out.println("正在計算統計數據...");
        String sql = "SELECT SUM(CAST(wrong_count AS BIGINT)) as total_errors, SUM(CAST(total_questions AS BIGINT)) as total_answers FROM ExamResults";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                long totalErrors = rs.getLong("total_errors");
                long totalAnswers = rs.getLong("total_answers");

                if (totalAnswers == 0) {
                    System.out.println("目前無資料可統計。");
                    return;
                }

                double errorRate = (double) totalErrors / totalAnswers;
                double accuracy = 1.0 - errorRate;

                System.out.println("===== 統計結果 =====");
                System.out.println("總答題數: " + totalAnswers);
                System.out.println("總錯誤數: " + totalErrors);
                System.out.printf("總答錯率: %.4f%%\n", errorRate * 100);
                System.out.printf("總答對率: %.4f%%\n", accuracy * 100);
                System.out.println("====================");
                System.out.println("結論：因為數據是隨機生成的，答對率應趨近於 50%。");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
