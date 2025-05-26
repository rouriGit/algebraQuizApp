package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AlgebraQuiz extends Application {

    private int currentQuestion = 0;
    private int totalQuestions = 0;
    private int correctAnswers = 0;
    private int max = 99;
    private int answer;
    private int num1, num2;
    private int plusMinus;

    private Label questionCountLabel = new Label();
    private Label questionLabel = new Label();
    private Label feedbackLabel = new Label();
    private Label resultLabel = new Label();
    private ComboBox<String> comboBox = new ComboBox<>();
    private Button button1, button2, button3, button4;
    private VBox quizContainer = new VBox(20);

    @Override
    public void start(Stage primaryStage) {
        setupComboBox();
        Button startButton = new Button("Start Quiz");
        startButton.setOnAction(e -> startQuiz());

        VBox root = new VBox(20, 
            new HBox(10, new Label("Questions:"), comboBox, startButton),
            quizContainer
        );
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Algebra Quiz");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupComboBox() {
        comboBox.getItems().addAll("5", "10", "15", "20");
        comboBox.setPromptText("choose from ");
        comboBox.setPrefWidth(100);
    }

    private void startQuiz() {
            totalQuestions = Integer.parseInt(comboBox.getValue());
            currentQuestion = 0;
            correctAnswers = 0;
            quizContainer.getChildren().clear();
            quizContainer.getChildren().addAll(questionCountLabel, questionLabel, feedbackLabel);
            loadNextQuestion();
    }

    private void loadNextQuestion() {
        if (currentQuestion >= totalQuestions) {
            showFinalResult();
            return;
        }

        currentQuestion++;
        initializeQuestion();
        updateQuestionDisplay();
    }

    private void initializeQuestion() {
        plusMinus = (int)(Math.random() * 2);
        num1 = (int)(Math.random() * max);
        num2 = (int)(Math.random() * max);
        answer = plusMinus == 0 ? num1 + num2 : num1 - num2;
    }

    private void updateQuestionDisplay() {
        questionCountLabel.setText("Q" + currentQuestion + "/" + totalQuestions);
        questionLabel.setText(num1 + (plusMinus == 0 ? " + " : " - ") + num2 + " = ?");
        feedbackLabel.setText("");

        int[] choices = generateChoices(answer);
        setupAnswerButtons(choices);
    }

    private int[] generateChoices(int correctAnswer) {
        return new int[] {
            correctAnswer,
            correctAnswer + (int)(Math.random() * 5) + 1,
            correctAnswer - (int)(Math.random() * 5) - 1,
            correctAnswer * 2
        };
    }

    private void setupAnswerButtons(int[] choices) {
        if (quizContainer.getChildren().size() > 3) {
            quizContainer.getChildren().remove(3);
        }

        HBox buttonBox = new HBox(10);
        for (int i = 0; i < choices.length; i++) {
            Button button = new Button(String.valueOf(choices[i]));
            int choice = choices[i];
            button.setOnAction(e -> checkAnswer(choice));
            buttonBox.getChildren().add(button);
        }
        quizContainer.getChildren().add(buttonBox);
    }

    private void checkAnswer(int userChoice) {
        if (userChoice == answer) {
            correctAnswers++;
            feedbackLabel.setText("Correct!");
            feedbackLabel.setStyle("-fx-text-fill: green;");
        } else {
            feedbackLabel.setText("Oooops... correction: " + answer);
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
        
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                javafx.application.Platform.runLater(this::loadNextQuestion);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showFinalResult() {
        double percentage = (double)correctAnswers / totalQuestions * 100;
        resultLabel.setText(String.format("result: %d out of %d (accuracy: %.1f%%)", correctAnswers, totalQuestions, percentage));
        resultLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        quizContainer.getChildren().clear();
        quizContainer.getChildren().addAll(
            new Label("End Quiz"),
            resultLabel,
            new Button("Try Again") {{
                setOnAction(e -> startQuiz());
            }}
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
