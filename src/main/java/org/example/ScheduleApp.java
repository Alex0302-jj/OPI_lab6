package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Comparator;

public class ScheduleApp extends Application {

    private final TableView<Training> table = new TableView<>();
    private final ObservableList<Training> trainingData = FXCollections.observableArrayList();
    private final TextArea logArea = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Система розкладу тренувань");

        setupTable();

        // Тестові дані (щоб таблиця не була порожньою)
        trainingData.add(new Training("Біг", LocalDate.of(2024, 2, 1), "08:00", "Олег", 30, 50));
        trainingData.add(new Training("Йога", LocalDate.of(2024, 1, 15), "10:00", "Анна", 60, 40));
        trainingData.add(new Training("Бокс", LocalDate.of(2024, 2, 10), "19:00", "Макс", 90, 100));

        // --- ПРАВА ПАНЕЛЬ (Кнопки) ---
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setPrefWidth(300);

        // 1. Блок додавання
        Label lblAdd = new Label("=== Додати тренування ===");
        TextField tfName = new TextField(); tfName.setPromptText("Назва (напр. Біг)");
        DatePicker dpDate = new DatePicker(); dpDate.setPromptText("Дата тренування");
        TextField tfDur = new TextField(); tfDur.setPromptText("Тривалість (хв)");
        TextField tfEff = new TextField(); tfEff.setPromptText("Ефективність (бали)");
        Button btnAdd = new Button("Зберегти у список");

        btnAdd.setOnAction(e -> {
            try {
                if (dpDate.getValue() == null) {
                    logArea.setText("Будь ласка, оберіть дату!");
                    return;
                }
                trainingData.add(new Training(
                        tfName.getText(), dpDate.getValue(), "12:00", "Користувач",
                        Integer.parseInt(tfDur.getText()), Integer.parseInt(tfEff.getText())
                ));
                // Очищуємо поля
                tfName.clear(); tfDur.clear(); tfEff.clear(); dpDate.setValue(null);
                logArea.setText("Тренування додано!");
            } catch (NumberFormatException ex) {
                logArea.setText("Помилка: Час та Ефективність мають бути числами!");
            }
        });

        // 2. Блок Бінарного пошуку
        Separator sep1 = new Separator();
        Label lblSearch = new Label("=== 1. Бінарний пошук ===");
        DatePicker dpSearch = new DatePicker(); // Окремий календар для пошуку
        dpSearch.setPromptText("Оберіть дату для пошуку");
        Button btnSearch = new Button("Знайти за датою");

        btnSearch.setOnAction(e -> {
            LocalDate dateToFind = dpSearch.getValue();
            if (dateToFind != null) {
                // ВАЖЛИВО: Перед бінарним пошуком список ОБОВ'ЯЗКОВО має бути відсортований
                trainingData.sort(Comparator.comparing(Training::getDate));
                logArea.setText("Список відсортовано. Шукаємо: " + dateToFind);

                // Виклик алгоритму
                Training result = Algorithms.binarySearchRecursive(trainingData, dateToFind, 0, trainingData.size() - 1);

                if (result != null) {
                    logArea.setText("ЗНАЙДЕНО!\nТренування: " + result.getName() + "\nТренер: " + result.getCoach());
                    table.getSelectionModel().select(result); // Виділити в таблиці
                    table.scrollTo(result);
                } else {
                    logArea.setText("На дату " + dateToFind + " тренувань не знайдено.");
                }
            } else {
                logArea.setText("Оберіть дату в календарі пошуку!");
            }
        });

        // 3. Блок Алгоритмів (Рюкзак та Прім)
        Separator sep2 = new Separator();
        Label lblAlgo = new Label("=== Інші алгоритми ===");
        TextField tfLimit = new TextField(); tfLimit.setPromptText("Ваш ліміт часу (хв)");
        Button btnKnapsack = new Button("2. Оптимізація (Рюкзак)");

        btnKnapsack.setOnAction(e -> {
            try {
                int limit = Integer.parseInt(tfLimit.getText());
                String res = Algorithms.solveKnapsack(trainingData, limit);
                logArea.setText(res);
            } catch (NumberFormatException ex) {
                logArea.setText("Введіть коректне число хвилин!");
            }
        });

        Button btnPrim = new Button("3. Побудувати мережу (Прім)");
        btnPrim.setOnAction(e -> logArea.setText(Algorithms.runPrimsAlgorithm()));

        // Поле для виводу тексту
        logArea.setPrefHeight(200);
        logArea.setWrapText(true);

        rightPanel.getChildren().addAll(
                lblAdd, tfName, dpDate, tfDur, tfEff, btnAdd,
                sep1, lblSearch, dpSearch, btnSearch,
                sep2, lblAlgo, tfLimit, btnKnapsack, btnPrim,
                new Label("Результат виконання:"), logArea
        );

        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setRight(rightPanel);

        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTable() {
        TableColumn<Training, String> nameCol = new TableColumn<>("Вид спорту");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Training, LocalDate> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Training, Integer> durCol = new TableColumn<>("Хв");
        durCol.setCellValueFactory(new PropertyValueFactory<>("durationMin"));

        TableColumn<Training, Integer> effCol = new TableColumn<>("Ефект");
        effCol.setCellValueFactory(new PropertyValueFactory<>("efficiency"));

        table.getColumns().addAll(nameCol, dateCol, durCol, effCol);
        table.setItems(trainingData);
    }
}