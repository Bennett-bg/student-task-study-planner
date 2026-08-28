
package com.studentplanner;

import java.time.LocalDate;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        VBox sidebar = new VBox();
        VBox mainContent = new VBox();

        Label sidebarTitle = new Label("MONOLITH");

        Button todayButton = new Button("Today");
        Button upcomingButton = new Button("Upcoming");
        Button completedButton = new Button("Completed");
        Button settingsButton = new Button("Settings");

        // TODAY BUTTON
        todayButton.setOnAction(e -> {
            mainContent.getChildren().clear();

            Label title = new Label("Today's Tasks");

            mainContent.getChildren().add(title);
        });

        sidebar.getChildren().addAll(
                sidebarTitle,
                todayButton,
                upcomingButton,
                completedButton,
                settingsButton
        );

        Label welcome = new Label("Welcome to Monolith");
        Label tasksTitle = new Label("Today's Tasks");

        Task task = new Task(
                "Finish DSA assignment",
                "DSA",
                LocalDate.of(2026, 8, 25),
                Task.Priority.HIGH
        );

        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter a task");

        TextField subjectInput = new TextField();
        subjectInput.setPromptText("Enter subject");

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Select due date");

        ComboBox<Task.Priority> priorityBox = new ComboBox<>();

        priorityBox.getItems().addAll(
                Task.Priority.VERY_LOW,
                Task.Priority.LOW,
                Task.Priority.MEDIUM,
                Task.Priority.HIGH,
                Task.Priority.URGENT
        );

        priorityBox.setPromptText("Select priority");

        Button addTaskButton = new Button("Add Task");

        ListView<Task> taskList = new ListView<>();

        taskList.getItems().add(task);

        taskList.setCellFactory(listView -> new ListCell<Task>() {

            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                if (empty || task == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    checkBox.setText(task.toString());
                    checkBox.setSelected(task.isCompleted());

                    checkBox.setOnAction(e ->
                            task.setCompleted(checkBox.isSelected())
                    );

                    setGraphic(checkBox);
                    setText(null);
                }
            }
        });

        addTaskButton.setOnAction(e -> {

            String title = taskInput.getText();
            String subject = subjectInput.getText();

            LocalDate dueDate = dueDatePicker.getValue();

            Task.Priority priority = priorityBox.getValue();

            if (!title.isBlank()
                    && !subject.isBlank()
                    && dueDate != null
                    && priority != null) {

                Task newTask = new Task(
                        title,
                        subject,
                        dueDate,
                        priority
                );

                taskList.getItems().add(newTask);

                taskInput.clear();
                subjectInput.clear();
                dueDatePicker.setValue(null);
                priorityBox.setValue(null);
            }
        });

        mainContent.getChildren().addAll(
                welcome,
                tasksTitle,
                taskInput,
                subjectInput,
                dueDatePicker,
                priorityBox,
                addTaskButton,
                taskList
        );

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Student Task & Study Planner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

