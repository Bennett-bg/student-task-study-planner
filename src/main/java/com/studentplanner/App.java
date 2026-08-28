package com.studentplanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        // UI components
        Sidebar sidebar = new Sidebar();
        TaskForm taskForm = new TaskForm();
        TaskListView taskList = new TaskListView();

        // Main content
        VBox mainContent = new VBox();

        Label welcome = new Label("Welcome to Monolith");
        Label tasksTitle = new Label("Today's Tasks");

        Button showAddTaskButton = new Button("Add Task");

        // Keep the task form hidden initially
        taskForm.setVisible(false);
        taskForm.setManaged(false);

        // Show task form
        showAddTaskButton.setOnAction(e -> {

            taskForm.setVisible(true);
            taskForm.setManaged(true);

            showAddTaskButton.setVisible(false);
            showAddTaskButton.setManaged(false);
        });

        // Save task
        taskForm.getSaveButton().setOnAction(e -> {

            if (taskForm.isValid()) {

                Task newTask = new Task(
                        taskForm.getTaskTitle(),
                        taskForm.getSubject(),
                        taskForm.getDueDate(),
                        taskForm.getPriority()
                );

                taskList.getItems().add(newTask);

                taskForm.clear();

                taskForm.setVisible(false);
                taskForm.setManaged(false);

                showAddTaskButton.setVisible(true);
                showAddTaskButton.setManaged(true);
            }
        });

        // Cancel task creation
        taskForm.getCancelButton().setOnAction(e -> {

            taskForm.clear();

            taskForm.setVisible(false);
            taskForm.setManaged(false);

            showAddTaskButton.setVisible(true);
            showAddTaskButton.setManaged(true);
        });

        // Sidebar navigation
        sidebar.getTodayButton().setOnAction(e -> {
            tasksTitle.setText("Today's Tasks");
        });

        sidebar.getUpcomingButton().setOnAction(e -> {
            tasksTitle.setText("Upcoming Tasks");
        });

        sidebar.getCompletedButton().setOnAction(e -> {
            tasksTitle.setText("Completed Tasks");
        });

        sidebar.getSettingsButton().setOnAction(e -> {
            tasksTitle.setText("Settings");
        });

        // Main content
        mainContent.getChildren().addAll(
                welcome,
                tasksTitle,
                showAddTaskButton,
                taskForm,
                taskList
        );

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        // Scene
        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Student Task & Study Planner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
