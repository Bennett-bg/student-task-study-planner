package com.studentplanner;

import java.time.LocalDate;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TaskForm extends VBox {


private final TextField taskInput;
private final TextField subjectInput;
private final DatePicker dueDatePicker;
private final ComboBox<Task.Priority> priorityBox;

private final Button saveButton;
private final Button cancelButton;

private Consumer<Task> onTaskSaved;
private Runnable onCancelled;

public TaskForm() {

    setSpacing(15);
    setPadding(new Insets(22));
    getStyleClass().add("task-form");

    // =========================
    // INPUTS
    // =========================

    taskInput = new TextField();
    taskInput.setPromptText("Enter task");
    taskInput.getStyleClass().add("form-input");

    subjectInput = new TextField();
    subjectInput.setPromptText("Enter subject (optional)");
    subjectInput.getStyleClass().add("form-input");

    dueDatePicker = new DatePicker();
    dueDatePicker.setPromptText("Select due date");
    dueDatePicker.getStyleClass().add("form-input");

    // Prevent selecting dates before today
    dueDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {

        @Override
        public void updateItem(LocalDate date, boolean empty) {

            super.updateItem(date, empty);

            if (date.isBefore(LocalDate.now())) {
                setDisable(true);
            }
        }
    });

    priorityBox = new ComboBox<>();

    priorityBox.getItems().addAll(
            Task.Priority.VERY_LOW,
            Task.Priority.LOW,
            Task.Priority.MEDIUM,
            Task.Priority.HIGH,
            Task.Priority.URGENT
    );

    priorityBox.setPromptText("Select priority");
    priorityBox.getStyleClass().add("form-input");

    // =========================
    // FRIENDLY PRIORITY NAMES
    // =========================

    priorityBox.setCellFactory(listView -> new ListCell<>() {

        @Override
        protected void updateItem(Task.Priority priority, boolean empty) {

            super.updateItem(priority, empty);

            if (empty || priority == null) {
                setText(null);
            } else {
                setText(formatPriority(priority));
            }
        }
    });

    priorityBox.setButtonCell(new ListCell<>() {

        @Override
        protected void updateItem(Task.Priority priority, boolean empty) {

            super.updateItem(priority, empty);

            if (empty || priority == null) {
                setText(null);
            } else {
                setText(formatPriority(priority));
            }
        }
    });

    // =========================
    // LABELS
    // =========================

    Label taskLabel = new Label("TASK");
    taskLabel.getStyleClass().add("form-label");

    Label subjectLabel = new Label("SUBJECT");
    subjectLabel.getStyleClass().add("form-label");

    Label dateLabel = new Label("DUE DATE");
    dateLabel.getStyleClass().add("form-label");

    Label priorityLabel = new Label("PRIORITY");
    priorityLabel.getStyleClass().add("form-label");

    // =========================
    // GRID LAYOUT
    // =========================

    GridPane grid = new GridPane();

    grid.setHgap(18);
    grid.setVgap(8);

    grid.add(taskLabel, 0, 0);
    grid.add(subjectLabel, 1, 0);

    grid.add(taskInput, 0, 1);
    grid.add(subjectInput, 1, 1);

    grid.add(dateLabel, 0, 2);
    grid.add(priorityLabel, 1, 2);

    grid.add(dueDatePicker, 0, 3);
    grid.add(priorityBox, 1, 3);

    GridPane.setHgrow(
            taskInput,
            javafx.scene.layout.Priority.ALWAYS
    );

    GridPane.setHgrow(
            subjectInput,
            javafx.scene.layout.Priority.ALWAYS
    );

    GridPane.setHgrow(
            dueDatePicker,
            javafx.scene.layout.Priority.ALWAYS
    );

    GridPane.setHgrow(
            priorityBox,
            javafx.scene.layout.Priority.ALWAYS
    );

    // =========================
    // BUTTONS
    // =========================

    saveButton = new Button("Save Task");
    saveButton.getStyleClass().add("form-button");

    cancelButton = new Button("Cancel");
    cancelButton.getStyleClass().addAll(
            "form-button",
            "cancel-button"
    );

    HBox buttons = new HBox(
            10,
            saveButton,
            cancelButton
    );

    buttons.setAlignment(Pos.CENTER_LEFT);

    // =========================
    // FORM CONTENT
    // =========================

    getChildren().addAll(
            grid,
            buttons
    );

    // =========================
    // EVENTS
    // =========================

    saveButton.setOnAction(e -> saveTask());

    cancelButton.setOnAction(e -> {

        if (onCancelled != null) {
            onCancelled.run();
        }
    });

    // Enter moves through the form
    taskInput.setOnAction(e -> subjectInput.requestFocus());

    subjectInput.setOnAction(e -> dueDatePicker.requestFocus());

    dueDatePicker.setOnAction(e -> priorityBox.requestFocus());

    priorityBox.setOnAction(e -> saveTask());
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

private void saveTask() {

    String title = taskInput.getText().trim();
    String subject = subjectInput.getText().trim();

    LocalDate dueDate = dueDatePicker.getValue();
    Task.Priority priority = priorityBox.getValue();

    if (title.isBlank()
            || dueDate == null
            || priority == null) {
        return;
    }

    // Extra protection against past dates
    if (dueDate.isBefore(LocalDate.now())) {
        return;
    }

    Task newTask = new Task(
            title,
            subject,
            dueDate,
            priority
    );

    if (onTaskSaved != null) {
        onTaskSaved.accept(newTask);
    }
}

public void clear() {

    taskInput.clear();
    subjectInput.clear();
    dueDatePicker.setValue(null);
    priorityBox.setValue(null);
}

public void setOnTaskSaved(Consumer<Task> onTaskSaved) {
    this.onTaskSaved = onTaskSaved;
}

public void setOnCancelled(Runnable onCancelled) {
    this.onCancelled = onCancelled;
}


}
