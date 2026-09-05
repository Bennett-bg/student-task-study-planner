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

public MainContent() {

    setSpacing(15);
    setPadding(new Insets(35));

    welcome = new Label("Welcome to Monolith");
    welcome.getStyleClass().add("welcome-label");

    pageTitle = new Label("Today's Tasks");
    pageTitle.getStyleClass().add("page-title");

    showAddTaskButton = new Button("Add Task");
    showAddTaskButton.getStyleClass().add("add-task-button");

    taskForm = new TaskForm();
    taskList = new TaskListView();

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

    showAddTaskButton.setOnAction(e -> {

        taskForm.setVisible(true);
        taskForm.setManaged(true);

        showAddTaskButton.setVisible(false);
        showAddTaskButton.setManaged(false);
    });

    taskForm.setOnTaskSaved(task -> {

        taskList.getListView().getItems().add(task);

        taskForm.clear();

        taskForm.setVisible(false);
        taskForm.setManaged(false);

        showAddTaskButton.setVisible(true);
        showAddTaskButton.setManaged(true);
    });

    taskForm.setOnCancelled(() -> {

        taskForm.clear();

        taskForm.setVisible(false);
        taskForm.setManaged(false);

        showAddTaskButton.setVisible(true);
        showAddTaskButton.setManaged(true);
    });
}

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