package model;

public class Student {
    private int id;
    private String studentName;
    private int totalScore;
    private String rawAnswers; // Stores "10100..." sequence

    public Student() {
    }

    public Student(String studentName, int totalScore, String rawAnswers) {
        this.studentName = studentName;
        this.totalScore = totalScore;
        this.rawAnswers = rawAnswers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getRawAnswers() {
        return rawAnswers;
    }

    public void setRawAnswers(String rawAnswers) {
        this.rawAnswers = rawAnswers;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", studentName='" + studentName + '\'' +
                ", totalScore=" + totalScore +
                ", rawAnswers='" + rawAnswers + '\'' +
                '}';
    }
}
