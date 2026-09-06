package com.studentplanner;

import java.util.List;
import java.util.function.Supplier;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TaskPage extends VBox {

    private final TaskManager taskManager;
    private final TaskListView taskList;

    private final TaskForm taskForm;
    private final Supplier<List<Task>> taskSupplier;

    private final VBox emptyState;

    public TaskPage(
            TaskManager taskManager,
            String title,
            Supplier<List<Task>> taskSupplier
    ) {

        this.taskManager = taskManager;
        this.taskSupplier = taskSupplier;

        setSpacing(15);
        setPadding(new Insets(0));
        setFillWidth(true);

        // =========================
        // HEADER
        // =========================

        Label pageTitle = new Label(title);
        pageTitle.getStyleClass().add("page-title");

        // =========================
        // TASK FORM
        // =========================

        taskForm = new TaskForm();

        taskForm.setVisible(false);
        taskForm.setManaged(false);

        // =========================
        // TASK LIST
        // =========================

        taskList = new TaskListView(taskManager);

        taskList.setMaxWidth(Double.MAX_VALUE);
        taskList.setMaxHeight(Double.MAX_VALUE);

        // =========================
        // EMPTY STATE
        // =========================

        Label emptyTitle = new Label("No tasks here");
        emptyTitle.getStyleClass().add("empty-state-title");

        Label emptyMessage = new Label(
                "You're all caught up. Enjoy your day!"
        );
        emptyMessage.getStyleClass().add("empty-state-message");

        emptyState = new VBox(
                8,
                emptyTitle,
                emptyMessage
        );

        emptyState.setAlignment(Pos.CENTER);
        emptyState.setMaxWidth(Double.MAX_VALUE);
        emptyState.setMaxHeight(Double.MAX_VALUE);
        emptyState.getStyleClass().add("empty-state");

        // =========================
        // CONTENT AREA
        // =========================

        VBox contentArea = new VBox(
                taskList,
                emptyState
        );

        contentArea.setSpacing(0);
        contentArea.setFillWidth(true);
        contentArea.setMaxWidth(Double.MAX_VALUE);
        contentArea.setMaxHeight(Double.MAX_VALUE);

        VBox.setVgrow(
                contentArea,
                javafx.scene.layout.Priority.ALWAYS
        );

        VBox.setVgrow(
                taskList,
                javafx.scene.layout.Priority.ALWAYS
        );

        VBox.setVgrow(
                emptyState,
                javafx.scene.layout.Priority.ALWAYS
        );

        // =========================
        // PAGE CONTENT
        // =========================

        getChildren().addAll(
                pageTitle,
                taskForm,
                contentArea
        );

        // =========================
        // TASK EVENTS
        // =========================

        setupTaskSection();

        refreshTasks();
    }

    private void setupTaskSection() {

        taskForm.setOnTaskSaved(task -> {

            taskManager.addTask(task);

            taskForm.clear();

            taskForm.setVisible(false);
            taskForm.setManaged(false);

            refreshTasks();
        });

        taskForm.setOnCancelled(() -> {

            taskForm.clear();

            taskForm.setVisible(false);
            taskForm.setManaged(false);

            refreshTasks();
        });
    }

    // =========================
    // GLOBAL QUICK ADD SUPPORT
    // =========================

    public void showTaskForm() {

        taskForm.setVisible(true);
        taskForm.setManaged(true);

        emptyState.setVisible(false);
        emptyState.setManaged(false);
    }

    public void hideTaskForm() {

        taskForm.clear();

        taskForm.setVisible(false);
        taskForm.setManaged(false);

        refreshTasks();
    }

    public void refreshTasks() {

        List<Task> tasks = taskSupplier.get();

        taskList.getListView().getItems().setAll(tasks);

        boolean hasTasks = !tasks.isEmpty();

        taskList.setVisible(hasTasks);
        taskList.setManaged(hasTasks);

        if (!taskForm.isVisible()) {
            emptyState.setVisible(!hasTasks);
            emptyState.setManaged(!hasTasks);
        }
    }

    public TaskListView getTaskList() {
        return taskList;
    }
}