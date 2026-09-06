package com.studentplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CompletedView extends VBox {

    private final TaskManager taskManager;

    private final TaskListView taskList;
    private final VBox emptyState;

    public CompletedView(TaskManager taskManager) {

        this.taskManager = taskManager;

        setSpacing(12);
        setPadding(new Insets(0));
        setFillWidth(true);

        // =========================
        // HEADER
        // =========================

        Label title = new Label("Completed");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label(
                "Tasks completed in the last 30 days"
        );
        subtitle.getStyleClass().add("completed-subtitle");

        // =========================
        // TASK LIST
        // =========================

        taskList = new TaskListView(taskManager);
        taskList.setMaxWidth(Double.MAX_VALUE);

        // =========================
        // EMPTY STATE
        // =========================

        Label emptyTitle = new Label(
                "No completed tasks"
        );
        emptyTitle.getStyleClass().add("empty-state-title");

        Label emptyMessage = new Label(
                "Tasks you complete will appear here."
        );
        emptyMessage.getStyleClass().add("empty-state-message");

        emptyState = new VBox(
                8,
                emptyTitle,
                emptyMessage
        );

        emptyState.setAlignment(Pos.CENTER);
        emptyState.setMaxWidth(Double.MAX_VALUE);
        emptyState.getStyleClass().add("empty-state");

        // =========================
        // PAGE CONTENT
        // =========================

        getChildren().addAll(
                title,
                subtitle,
                taskList,
                emptyState
        );

        VBox.setVgrow(
                taskList,
                javafx.scene.layout.Priority.ALWAYS
        );

        VBox.setVgrow(
                emptyState,
                javafx.scene.layout.Priority.ALWAYS
        );

        refreshTasks();
    }

    public void refreshTasks() {

        var completedTasks =
                taskManager.getCompletedTasks();

        taskList.getListView()
                .getItems()
                .setAll(completedTasks);

        boolean hasCompletedTasks =
                !completedTasks.isEmpty();

        taskList.setVisible(hasCompletedTasks);
        taskList.setManaged(hasCompletedTasks);

        emptyState.setVisible(!hasCompletedTasks);
        emptyState.setManaged(!hasCompletedTasks);
    }
}