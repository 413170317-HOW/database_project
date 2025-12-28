package dao;

import database.DBConnection;
import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void batchInsert(List<Student> students) {
        String sql = "INSERT INTO Students (student_name, total_score, raw_answers) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // Important for batch performance

            int count = 0;
            for (Student s : students) {
                pstmt.setString(1, s.getStudentName());
                pstmt.setInt(2, s.getTotalScore());
                pstmt.setString(3, s.getRawAnswers());
                pstmt.addBatch();

                count++;
                if (count % 1000 == 0) {
                    pstmt.executeBatch();
                    conn.commit();
                }
            }
            // Insert remaining
            pstmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void batchUpdateScores(List<Student> students) {
        String sql = "UPDATE Students SET total_score = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            int count = 0;
            for (Student s : students) {
                pstmt.setInt(1, s.getTotalScore());
                pstmt.setInt(2, s.getId());
                pstmt.addBatch();

                count++;
                if (count % 1000 == 0) {
                    pstmt.executeBatch();
                    conn.commit();
                }
            }
            pstmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Student> readAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Students"; // Warning: Could be huge

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setStudentName(rs.getString("student_name"));
                s.setTotalScore(rs.getInt("total_score"));
                s.setRawAnswers(rs.getString("raw_answers"));
                students.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<Student> findByScoreLessThan(int score) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Students WHERE total_score < ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, score);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setId(rs.getInt("id"));
                    s.setStudentName(rs.getString("student_name"));
                    s.setTotalScore(rs.getInt("total_score"));
                    s.setRawAnswers(rs.getString("raw_answers"));
                    students.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public void deleteAll() {
        String sql = "TRUNCATE TABLE Students"; // Faster than DELETE
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
