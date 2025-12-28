package model;

public class Question {
    private int id;
    private String questionText;
    private boolean correctAnswer;
    private int weight;
    private boolean isActive;

    public Question() {
    }

    public Question(String questionText, boolean correctAnswer, int weight) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.weight = weight;
        this.isActive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", correctAnswer=" + correctAnswer +
                ", weight=" + weight +
                ", isActive=" + isActive +
                '}';
    }
}
