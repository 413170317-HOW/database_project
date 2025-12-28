package view;

import dao.QuestionDAO;
import dao.StudentDAO;
import model.Question;
import model.Student;
import service.SimulationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private QuestionDAO questionDAO = new QuestionDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private SimulationService simulationService = new SimulationService();

    private JTable questionTable;
    private DefaultTableModel questionModel;

    private JTable studentTable;
    private DefaultTableModel studentModel;
    private JLabel avgLabel;
    private JLabel stdDevLabel;

    private JProgressBar progressBar;
    private JTextArea logArea;
    private JButton runSimulationBtn;

    public MainFrame() {
        setTitle("考試模擬系統");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Question Management
        tabbedPane.addTab("題目管理", createQuestionPanel());

        // Tab 2: Simulation Control
        tabbedPane.addTab("模擬執行", createSimulationPanel());

        // Tab 3: Analysis
        tabbedPane.addTab("成績分析", createAnalysisPanel());

        add(tabbedPane);
    }

    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columns = { "ID", "題目內容", "正確答案", "權重" };
        questionModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        questionTable = new JTable(questionModel);
        refreshQuestionTable();
        panel.add(new JScrollPane(questionTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("新增題目");
        JButton editButton = new JButton("編輯題目");
        JButton deleteButton = new JButton("刪除題目");

        addButton.addActionListener(e -> {
            QuestionDialog dialog = new QuestionDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                questionDAO.create(dialog.getQuestion());
                refreshQuestionTable();
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = questionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "請選擇要編輯的題目。");
                return;
            }
            int id = (int) questionModel.getValueAt(selectedRow, 0);
            Question q = getQuestionFromModel(selectedRow);
            q.setId(id); // Ensure ID is set

            QuestionDialog dialog = new QuestionDialog(this, q);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Question updatedQ = dialog.getQuestion();
                // Check if weight or answer changed? For simplicity, always update and
                // recalculate
                questionDAO.update(updatedQ);
                refreshQuestionTable();

                // Recalculate Scores
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        JOptionPane.showMessageDialog(MainFrame.this, "正在重新計算成績... 請稍候。");
                        simulationService.recalculateScores();
                        return null;
                    }

                    @Override
                    protected void done() {
                        JOptionPane.showMessageDialog(MainFrame.this, "成績重算完成！");
                    }
                }.execute();
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = questionTable.getSelectedRow();
            if (selectedRow == -1)
                return;
            int id = (int) questionModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "確定要刪除此題目嗎？", "確認",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                questionDAO.delete(id);
                refreshQuestionTable();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Helper to generic question object from table row (not perfect, but sufficient
    // for finding ID)
    // Actually better to re-read from DB or store objects in model.
    // For simplicity, we re-construct partial object or fetch by ID if we had
    // findById.
    // Since we don't have findById in DAO interface shown earlier (only readAll),
    // we will construct from table data which is safe enough for update (DAO uses
    // ID).
    private Question getQuestionFromModel(int row) {
        Question q = new Question();
        q.setQuestionText((String) questionModel.getValueAt(row, 1));
        q.setCorrectAnswer((Boolean) questionModel.getValueAt(row, 2));
        q.setWeight((Integer) questionModel.getValueAt(row, 3));
        q.setActive(true);
        return q;
    }

    private void refreshQuestionTable() {
        questionModel.setRowCount(0);
        List<Question> list = questionDAO.readAllActive();
        for (Question q : list) {
            questionModel.addRow(new Object[] { q.getId(), q.getQuestionText(), q.isCorrectAnswer(), q.getWeight() });
        }
    }

    private JPanel createSimulationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        centerPanel.add(new JLabel("目標: 100,000 名學生"), gbc);

        gbc.gridy = 1;
        runSimulationBtn = new JButton("執行模擬");
        centerPanel.add(runSimulationBtn, gbc);

        gbc.gridy = 2;
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(300, 30));
        centerPanel.add(progressBar, gbc);

        panel.add(centerPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        runSimulationBtn.addActionListener(e -> {
            runSimulationBtn.setEnabled(false);
            log("開始模擬...");

            SwingWorker<Void, Integer> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    simulationService.runSimulation(100000, this::publish);
                    return null;
                }

                @Override
                protected void process(List<Integer> chunks) {
                    int val = chunks.get(chunks.size() - 1);
                    progressBar.setValue(val);
                }

                @Override
                protected void done() {
                    runSimulationBtn.setEnabled(true);
                    log("模擬完成。");
                    JOptionPane.showMessageDialog(MainFrame.this, "模擬執行完成！");
                }
            };
            worker.execute();
        });

        return panel;
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
    }

    private JPanel createAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top Filter
        JPanel topPanel = new JPanel();
        JTextField scoreField = new JTextField(10);
        JButton filterButton = new JButton("篩選分數 <");
        avgLabel = new JLabel("平均分數: 0.0");
        stdDevLabel = new JLabel("標準差: 0.0");

        topPanel.add(new JLabel("分數 < "));
        topPanel.add(scoreField);
        topPanel.add(filterButton);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(avgLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(stdDevLabel);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "姓名", "總分" };
        studentModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(studentModel);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        filterButton.addActionListener(e -> {
            try {
                int score = Integer.parseInt(scoreField.getText());
                List<Student> students = studentDAO.findByScoreLessThan(score);
                updateAnalysisTable(students);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "數字格式錯誤");
            }
        });

        return panel;
    }

    private void updateAnalysisTable(List<Student> students) {
        studentModel.setRowCount(0);
        double sum = 0;
        for (Student s : students) {
            studentModel.addRow(new Object[] { s.getId(), s.getStudentName(), s.getTotalScore() });
            sum += s.getTotalScore();
        }

        if (students.isEmpty()) {
            avgLabel.setText("平均分數: 0.0");
            stdDevLabel.setText("標準差: 0.0");
            return;
        }

        double avg = sum / students.size();

        double varianceSum = 0;
        for (Student s : students) {
            varianceSum += Math.pow(s.getTotalScore() - avg, 2);
        }
        double stdDev = Math.sqrt(varianceSum / students.size());

        avgLabel.setText(String.format("平均分數: %.2f", avg));
        stdDevLabel.setText(String.format("標準差: %.2f", stdDev));
    }
}
