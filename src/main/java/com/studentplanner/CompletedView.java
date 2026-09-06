package com.studentplanner;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CompletedView extends VBox {

    private final TaskManager taskManager;
    private final TaskListView taskList;

    public CompletedView(TaskManager taskManager) {

        this.taskManager = taskManager;

        setSpacing(15);
        setPadding(new Insets(0));

        Label title = new Label("Completed");
        title.getStyleClass().add("page-title");

        taskList = new TaskListView(taskManager);

        getChildren().addAll(
                title,
                taskList
        );

        VBox.setVgrow(taskList, javafx.scene.layout.Priority.ALWAYS);
    }

    public void refreshTasks() {

        taskList.getListView().getItems().setAll(
                taskManager.getCompletedTasks()
        );
    }
}