package dao;

import database.DBConnection;
import model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public void create(Question q) {
        String sql = "INSERT INTO Questions (question_text, correct_answer, weight, is_active) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, q.getQuestionText());
            pstmt.setBoolean(2, q.isCorrectAnswer());
            pstmt.setInt(3, q.getWeight());
            pstmt.setBoolean(4, q.isActive());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        q.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Question> readAllActive() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM Questions WHERE is_active = 1";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setQuestionText(rs.getString("question_text"));
                q.setCorrectAnswer(rs.getBoolean("correct_answer"));
                q.setWeight(rs.getInt("weight"));
                q.setActive(rs.getBoolean("is_active"));
                questions.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public void update(Question q) {
        String sql = "UPDATE Questions SET question_text = ?, correct_answer = ?, weight = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, q.getQuestionText());
            pstmt.setBoolean(2, q.isCorrectAnswer());
            pstmt.setInt(3, q.getWeight());
            pstmt.setBoolean(4, q.isActive());
            pstmt.setInt(5, q.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        // Soft delete
        String sql = "UPDATE Questions SET is_active = 0 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
