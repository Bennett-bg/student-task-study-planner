package com.studentplanner;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AddTaskView extends VBox {

    private final TaskManager taskManager;
    private final TaskForm taskForm;

    public AddTaskView(TaskManager taskManager) {

        this.taskManager = taskManager;

        setSpacing(10);
        setPadding(new Insets(0));
        setFillWidth(true);

        // =========================
        // HEADER
        // =========================

        Label title = new Label("Add Task");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label(
                "Create a new task and keep your schedule organized."
        );
        subtitle.getStyleClass().add("welcome-label");

        // =========================
        // FORM CARD
        // =========================

        Label sectionTitle = new Label("Task Details");
        sectionTitle.getStyleClass().add("form-section-title");

        Label sectionDescription = new Label(
                "Add the information needed to keep your task organized."
        );
        sectionDescription.getStyleClass().add(
                "form-section-description"
        );

        taskForm = new TaskForm();

        taskForm.setMaxWidth(Double.MAX_VALUE);

        VBox formCard = new VBox(
                10,
                sectionTitle,
                sectionDescription,
                taskForm
        );

        formCard.setMaxWidth(Double.MAX_VALUE);
        formCard.getStyleClass().add("add-task-card");

        // =========================
        // EVENTS
        // =========================

        taskForm.setOnTaskSaved(task -> {

            taskManager.addTask(task);

            taskForm.clear();
        });

        taskForm.setOnCancelled(() -> {

            taskForm.clear();
        });

        // =========================
        // PAGE CONTENT
        // =========================

        getChildren().addAll(
                title,
                subtitle,
                formCard
        );

        VBox.setVgrow(
                formCard,
                javafx.scene.layout.Priority.ALWAYS
        );
    }
}