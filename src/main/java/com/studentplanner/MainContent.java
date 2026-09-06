package com.studentplanner;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainContent extends VBox {

    private final Label welcome;
    private final Label pageTitle;

    private final Button showAddTaskButton;
    private final TaskForm taskForm;
    private final TaskListView taskList;

    private final TaskManager taskManager;

    public MainContent() {

        setSpacing(15);
        setPadding(new Insets(35));

        // =========================
        // DATA MANAGER
        // =========================

        taskManager = new TaskManager();

        // =========================
        // PAGE CONTENT
        // =========================

        welcome = new Label("Welcome to Monolith");
        welcome.getStyleClass().add("welcome-label");

        pageTitle = new Label("Today's Tasks");
        pageTitle.getStyleClass().add("page-title");

        showAddTaskButton = new Button("Add Task");
        showAddTaskButton.getStyleClass().add("add-task-button");

        taskForm = new TaskForm();

        taskList = new TaskListView(taskManager);

        taskForm.setVisible(false);
        taskForm.setManaged(false);

        getChildren().addAll(
                welcome,
                pageTitle,
                showAddTaskButton,
                taskForm,
                taskList
        );
    }

    public void setupTaskSection() {

        // =========================
        // SHOW ADD TASK FORM
        // =========================

        showAddTaskButton.setOnAction(e -> {

            taskForm.setVisible(true);
            taskForm.setManaged(true);

            showAddTaskButton.setVisible(false);
            showAddTaskButton.setManaged(false);
        });

        // =========================
        // SAVE TASK
        // =========================

        taskForm.setOnTaskSaved(task -> {

            // Store the task in TaskManager
            taskManager.addTask(task);

            // Display the task in the UI
            taskList.getListView().getItems().add(task);

            // Clear and hide the form
            taskForm.clear();

            taskForm.setVisible(false);
            taskForm.setManaged(false);

            showAddTaskButton.setVisible(true);
            showAddTaskButton.setManaged(true);
        });

        // =========================
        // CANCEL
        // =========================

        taskForm.setOnCancelled(() -> {

            taskForm.clear();

            taskForm.setVisible(false);
            taskForm.setManaged(false);

            showAddTaskButton.setVisible(true);
            showAddTaskButton.setManaged(true);
        });
    }

    // =========================
    // SIDEBAR PAGES
    // =========================

    public void showToday() {
        pageTitle.setText("Today's Tasks");
    }

    public void showUpcoming() {
        pageTitle.setText("Upcoming");
    }

    public void showCompleted() {
        pageTitle.setText("Completed");
    }

    public void showSettings() {
        pageTitle.setText("Settings");
    }
}