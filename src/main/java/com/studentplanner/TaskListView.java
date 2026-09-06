package com.studentplanner;

import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TaskListView extends VBox {

    private final ListView<Task> taskList;
    private final TaskManager taskManager;

    public TaskListView(TaskManager taskManager) {

        this.taskManager = taskManager;

        taskList = new ListView<>();

        taskList.setCellFactory(listView -> new ListCell<Task>() {

            private final CheckBox checkBox = new CheckBox();

            private final Label taskTitle = new Label();
            private final Label taskDetails = new Label();

            private final VBox taskInfo = new VBox(
                    3,
                    taskTitle,
                    taskDetails
            );

            private final Button deleteButton = new Button("×");

            private final HBox taskRow = new HBox(
                    12,
                    checkBox,
                    taskInfo,
                    deleteButton
            );

            {
                // =========================
                // TASK ROW
                // =========================

                taskRow.setAlignment(Pos.CENTER_LEFT);
                taskRow.setPadding(new Insets(12, 14, 12, 14));

                taskRow.getStyleClass().add("task-row");

                // Keep information compact but allow it to expand
                HBox.setHgrow(taskInfo, Priority.ALWAYS);

                // =========================
                // TASK TITLE
                // =========================

                taskTitle.getStyleClass().add("task-title");

                // =========================
                // TASK DETAILS
                // =========================

                taskDetails.getStyleClass().add("task-details");

                // =========================
                // DELETE BUTTON
                // =========================

                deleteButton.setFocusTraversable(false);
                deleteButton.getStyleClass().add("delete-button");

                deleteButton.setOnAction(e -> {

                    Task task = getItem();

                    if (task != null) {
                        taskManager.removeTask(task);
                        taskList.getItems().remove(task);
                    }
                });

                // =========================
                // CHECKBOX
                // =========================

                checkBox.setFocusTraversable(false);
            }

            @Override
            protected void updateItem(Task task, boolean empty) {

                super.updateItem(task, empty);

                if (empty || task == null) {

                    setGraphic(null);
                    setText(null);

                } else {

                    // =========================
                    // TASK TITLE
                    // =========================

                    taskTitle.setText(task.getTitle());

                    // =========================
                    // TASK DETAILS
                    // =========================

                    String subject = task.getSubject();

                    String date = task.getDueDate().format(
                            DateTimeFormatter.ofPattern("MMM d")
                    );

                    String priority = formatPriority(
                            task.getPriority()
                    );

                    if (subject == null || subject.isBlank()) {

                        taskDetails.setText(
                                date + "  •  " + priority
                        );

                    } else {

                        taskDetails.setText(
                                subject
                                + "  •  "
                                + date
                                + "  •  "
                                + priority
                        );
                    }

                    // =========================
                    // COMPLETION STATE
                    // =========================

                    checkBox.setSelected(
                            task.isCompleted()
                    );

                    updateTitleStyle(
                            taskTitle,
                            task.isCompleted()
                    );

                    // Prevent duplicate listeners from
                    // previous recycled cells
                    checkBox.setOnAction(e -> {

                        taskManager.setTaskCompleted(
                                task,
                                checkBox.isSelected()
                        );

                        updateTitleStyle(
                                taskTitle,
                                task.isCompleted()
                        );
                    });

                    setGraphic(taskRow);
                    setText(null);
                }
            }
        });

        getChildren().add(taskList);

        VBox.setVgrow(
                taskList,
                Priority.ALWAYS
        );
    }

    private void updateTitleStyle(
            Label title,
            boolean completed
    ) {

        if (completed) {

            title.getStyleClass().remove("task-title");

            if (!title.getStyleClass().contains("task-title-completed")) {
                title.getStyleClass().add("task-title-completed");
            }

        } else {

            title.getStyleClass().remove(
                    "task-title-completed"
            );

            if (!title.getStyleClass().contains("task-title")) {
                title.getStyleClass().add("task-title");
            }
        }
    }

    private String formatPriority(Task.Priority priority) {

        return switch (priority) {

            case VERY_LOW -> "Very Low";
            case LOW -> "Low";
            case MEDIUM -> "Medium";
            case HIGH -> "High";
            case URGENT -> "Urgent";
        };
    }

    public ListView<Task> getListView() {
        return taskList;
    }
}