package view;

import model.Question;

import javax.swing.*;
import java.awt.*;

public class QuestionDialog extends JDialog {
    private JTextField questionTextField;
    private JCheckBox correctAnswerCheckBox;
    private JSpinner weightSpinner;
    private boolean confirmed = false;
    private Question question;

    public QuestionDialog(Frame owner, Question question) {
        super(owner, "題目詳情", true);
        this.question = question;
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("題目內容:"));
        questionTextField = new JTextField(question != null ? question.getQuestionText() : "");
        formPanel.add(questionTextField);

        formPanel.add(new JLabel("正確答案 (True/False):"));
        correctAnswerCheckBox = new JCheckBox("True (是)", question != null && question.isCorrectAnswer());
        formPanel.add(correctAnswerCheckBox);

        formPanel.add(new JLabel("權重 (分數):"));
        weightSpinner = new JSpinner(new SpinnerNumberModel(question != null ? question.getWeight() : 10, 1, 100, 1));
        formPanel.add(weightSpinner);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("儲存");
        JButton cancelButton = new JButton("取消");

        saveButton.addActionListener(e -> {
            confirmed = true;
            setVisible(false);
        });

        cancelButton.addActionListener(e -> setVisible(false));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Question getQuestion() {
        String text = questionTextField.getText();
        boolean correct = correctAnswerCheckBox.isSelected();
        int weight = (int) weightSpinner.getValue();

        if (question == null) {
            return new Question(text, correct, weight);
        } else {
            question.setQuestionText(text);
            question.setCorrectAnswer(correct);
            question.setWeight(weight);
            return question;
        }
    }
}
