package com.studentplanner;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TaskPage extends VBox {

    private final TaskManager taskManager;
    private final TaskListView taskList;

    private final Button showAddTaskButton;
    private final TaskForm taskForm;

    public TaskPage(
            TaskManager taskManager,
            String title
    ) {

        this.taskManager = taskManager;

        setSpacing(15);
        setPadding(new Insets(0));

        Label pageTitle = new Label(title);
        pageTitle.getStyleClass().add("page-title");

        showAddTaskButton = new Button("Add Task");
        showAddTaskButton.getStyleClass().add("add-task-button");

        taskForm = new TaskForm();
        taskList = new TaskListView(taskManager);

        taskForm.setVisible(false);
        taskForm.setManaged(false);

        getChildren().addAll(
                pageTitle,
                showAddTaskButton,
                taskForm,
                taskList
        );

        setupTaskSection();
    }

    private void setupTaskSection() {

        showAddTaskButton.setOnAction(e -> {

            taskForm.setVisible(true);
            taskForm.setManaged(true);

            showAddTaskButton.setVisible(false);
            showAddTaskButton.setManaged(false);
        });

        taskForm.setOnTaskSaved(task -> {

            taskManager.addTask(task);

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

    public TaskListView getTaskList() {
        return taskList;
    }
}