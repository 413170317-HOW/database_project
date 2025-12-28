package service;

import dao.QuestionDAO;
import dao.StudentDAO;
import model.Question;
import model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class SimulationService {

    private QuestionDAO questionDAO = new QuestionDAO();
    private StudentDAO studentDAO = new StudentDAO();

    /**
     * Runs the simulation for a specified number of students.
     * Generates random answers for active questions, calculates scores,
     * and persists everything to the database.
     */
    public void runSimulation(int studentCount) {
        runSimulation(studentCount, null);
    }

    /**
     * Runs the simulation with progress updates.
     * 
     * @param studentCount     Total students to simulate
     * @param progressCallback Callback for progress percentage (0-100), can be null
     */
    public void runSimulation(int studentCount, Consumer<Integer> progressCallback) {
        System.out.println("Starting simulation for " + studentCount + " students...");
        long startTime = System.currentTimeMillis();

        // 1. Load active questions
        List<Question> questions = questionDAO.readAllActive();
        if (questions.isEmpty()) {
            System.out.println("No active questions found. Please add questions first.");
            return;
        }

        List<Student> buffer = new ArrayList<>();
        Random random = new Random();

        // 2. Loop and generate students
        for (int i = 1; i <= studentCount; i++) {
            Student s = new Student();
            s.setStudentName("Student_" + i);

            StringBuilder rawAnswers = new StringBuilder();
            int currentScore = 0;

            for (Question q : questions) {
                // Random answer: true or false
                boolean answer = random.nextBoolean();

                // Build raw answer string (1=true, 0=false)
                rawAnswers.append(answer ? "1" : "0");

                // Calculate score
                if (answer == q.isCorrectAnswer()) {
                    currentScore += q.getWeight();
                }
            }

            s.setRawAnswers(rawAnswers.toString());
            s.setTotalScore(currentScore);
            buffer.add(s);

            // Batch insert every 1000 records to keep memory usage low
            if (buffer.size() >= 1000) {
                studentDAO.batchInsert(buffer);
                buffer.clear();
                if (i % 10000 == 0) {
                    System.out.println("Processed " + i + " students...");
                }
            }

            // Update progress every 1% or at least every 100 items for small batches
            if (progressCallback != null) {
                if (studentCount >= 100) {
                    if (i % (studentCount / 100) == 0) {
                        int percent = (int) ((i / (float) studentCount) * 100);
                        progressCallback.accept(percent);
                    }
                } else {
                    // For very small counts, just update every item
                    int percent = (int) ((i / (float) studentCount) * 100);
                    progressCallback.accept(percent);
                }
            }
        }

        // Insert remaining
        if (!buffer.isEmpty()) {
            studentDAO.batchInsert(buffer);
        }

        if (progressCallback != null) {
            progressCallback.accept(100);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Simulation completed in " + (endTime - startTime) + " ms.");
    }

    /**
     * Recalculates scores for ALL students based on current Question settings.
     * This is used when a Question's weight or correct answer is modified.
     */
    public void recalculateScores() {
        System.out.println("Starting score recalculation...");
        long startTime = System.currentTimeMillis();

        List<Question> questions = questionDAO.readAllActive();
        List<Student> students = studentDAO.readAll(); // Warning: large list if memory is constrained

        System.out.println("Loaded " + students.size() + " students. Processing...");

        List<Student> batchUpdate = new ArrayList<>();
        int count = 0;

        for (Student s : students) {
            String answers = s.getRawAnswers();
            int newScore = 0;

            // Recalculate logic
            for (int i = 0; i < questions.size() && i < answers.length(); i++) {
                char ansChar = answers.charAt(i);
                boolean boolAns = (ansChar == '1');
                Question q = questions.get(i);

                if (boolAns == q.isCorrectAnswer()) {
                    newScore += q.getWeight();
                }
            }

            s.setTotalScore(newScore);
            batchUpdate.add(s);

            if (batchUpdate.size() >= 1000) {
                studentDAO.batchUpdateScores(batchUpdate);
                batchUpdate.clear();
            }

            count++;
            if (count % 10000 == 0)
                System.out.println("Recalculated " + count + " students...");
        }

        if (!batchUpdate.isEmpty()) {
            studentDAO.batchUpdateScores(batchUpdate);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Recalculation completed in " + (endTime - startTime) + " ms.");
    }
}
