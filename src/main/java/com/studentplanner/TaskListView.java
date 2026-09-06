package com.studentplanner;

import java.time.format.DateTimeFormatter;

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
            private final Button deleteButton = new Button("×");

            private final VBox taskInfo = new VBox();
            private final HBox taskRow = new HBox();

            {
                taskRow.setAlignment(Pos.CENTER_LEFT);
                taskRow.setSpacing(12);

                taskInfo.setSpacing(3);

                taskRow.getChildren().addAll(
                        checkBox,
                        taskInfo,
                        deleteButton
                );

                HBox.setHgrow(taskInfo, Priority.ALWAYS);

                deleteButton.setFocusTraversable(false);
                deleteButton.getStyleClass().add("delete-button");
            }

            @Override
            protected void updateItem(Task task, boolean empty) {

                super.updateItem(task, empty);

                if (empty || task == null) {

                    setGraphic(null);
                    setText(null);

                } else {

                    Label taskTitle = new Label(task.getTitle());

                    String subject = task.getSubject();

                    String details;

                    if (subject == null || subject.isBlank()) {

                        details =
                                task.getDueDate().format(
                                        DateTimeFormatter.ofPattern("MMM d")
                                )
                                + "  •  "
                                + formatPriority(task.getPriority());

                    } else {

                        details =
                                subject
                                + "  •  "
                                + task.getDueDate().format(
                                        DateTimeFormatter.ofPattern("MMM d")
                                )
                                + "  •  "
                                + formatPriority(task.getPriority());
                    }

                    Label taskDetails = new Label(details);

                    taskTitle.setStyle(
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;"
                    );

                    taskDetails.setStyle(
                            "-fx-font-size: 12px;" +
                            "-fx-text-fill: #888888;"
                    );

                    updateTitleStyle(taskTitle, task.isCompleted());

                    taskInfo.getChildren().clear();

                    taskInfo.getChildren().addAll(
                            taskTitle,
                            taskDetails
                    );

                    checkBox.setText("");
                    checkBox.setSelected(task.isCompleted());

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

                    deleteButton.setOnAction(e -> {

                        taskManager.removeTask(task);
                        taskList.getItems().remove(task);
                    });

                    setGraphic(taskRow);
                    setText(null);
                }
            }
        });

        getChildren().add(taskList);

        VBox.setVgrow(taskList, Priority.ALWAYS);
    }

    private void updateTitleStyle(Label title, boolean completed) {

        if (completed) {

            title.setStyle(
                    "-fx-font-size: 15px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #777777;" +
                    "-fx-strikethrough: true;"
            );

        } else {

            title.setStyle(
                    "-fx-font-size: 15px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #eeeeee;" +
                    "-fx-strikethrough: false;"
            );
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