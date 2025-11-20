import java.io.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

class Question {
    String questionText;
    String[] options;
    int correctAnswerIndex;

    Question(String questionText, String[] options, int correctAnswerIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }
}

public class AdvancedQuizApp {
    static ArrayList<Question> questions = new ArrayList<>();
    static int score = 0;
    static int timeLimit = 15;
    static Scanner sc = new Scanner(System.in);
    static HashMap<Question, Integer> userResponses = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("       WELCOME TO THE JAVA QUIZ     ");
        System.out.println("====================================");


        loadQuestionsFromFile("questions.txt");

        if (questions.isEmpty()) {
            System.out.println("No questions found. Exiting quiz.");
            return;
        }

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println("\nQuestion " + (i + 1) + ": " + q.questionText);
            for (int j = 0; j < q.options.length; j++) {
                System.out.println((j + 1) + ". " + q.options[j]);
            }

            int userAnswer = getUserAnswerWithTimer();
            userResponses.put(q, userAnswer);

            if (userAnswer == (q.correctAnswerIndex + 1)) {
                System.out.println("✅ Correct!\n");
                score++;
            } else {
                System.out.println("❌ Incorrect! Correct answer: " + (q.correctAnswerIndex + 1) + ". " + q.options[q.correctAnswerIndex] + "\n");
            }
        }

        System.out.println("====================================");
        System.out.println("          QUIZ OVER!");
        System.out.println("          Your Score: " + score + "/" + questions.size());

        saveScoreToFile(score);
        showQuizReport();
        System.out.println("====================================");
    }

    static void loadQuestionsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    String questionText = parts[0];
                    String[] options = Arrays.copyOfRange(parts, 1, 5);
                    int correctIndex = Integer.parseInt(parts[5]);
                    questions.add(new Question(questionText, options, correctIndex));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading questions: " + e.getMessage());
        }
    }

    static int getUserAnswerWithTimer() {
        final int[] answer = {-1};
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                if (answer[0] == -1) {
                    System.out.println("\n⏰ Time's up!");
                    answer[0] = 0;
                }
            }
        }, timeLimit * 1000);

        try {
            System.out.print("Your answer (1-4): ");
            answer[0] = sc.nextInt();
        } catch (InputMismatchException e) {
            sc.nextLine();
            System.out.println("Invalid input. Moving to next question.");
        }
        timer.cancel();
        return answer[0];
    }

    static void saveScoreToFile(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))) {
            writer.write("Score: " + score + "/" + questions.size() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving score: " + e.getMessage());
        }
    }

    static void showQuizReport() {
        System.out.println("\n------ Quiz Report ------");
        for (Map.Entry<Question, Integer> entry : userResponses.entrySet()) {
            Question q = entry.getKey();
            int userAnswer = entry.getValue();
            System.out.println(q.questionText);
            System.out.println("Your Answer: " + (userAnswer == 0 ? "No answer" : q.options[userAnswer - 1]));
            System.out.println("Correct Answer: " + q.options[q.correctAnswerIndex]);
            System.out.println("----------------------------");
        }
    }
}