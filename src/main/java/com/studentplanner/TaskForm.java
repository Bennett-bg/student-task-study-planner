package com.studentplanner;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class TaskForm extends VBox {

    private final TextField taskInput;
    private final TextField subjectInput;
    private final DatePicker dueDatePicker;
    private final ComboBox<Task.Priority> priorityBox;

    private final Button saveButton;
    private final Button cancelButton;

    public TaskForm() {

        taskInput = new TextField();
        taskInput.setPromptText("Enter a task");

        subjectInput = new TextField();
        subjectInput.setPromptText("Enter subject");

        dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Select due date");

        priorityBox = new ComboBox<>();

        priorityBox.getItems().addAll(
                Task.Priority.VERY_LOW,
                Task.Priority.LOW,
                Task.Priority.MEDIUM,
                Task.Priority.HIGH,
                Task.Priority.URGENT
        );

        priorityBox.setPromptText("Select priority");

        saveButton = new Button("Save Task");
        cancelButton = new Button("Cancel");

        HBox formButtons = new HBox();
        formButtons.getChildren().addAll(
                saveButton,
                cancelButton
        );

        getChildren().addAll(
                taskInput,
                subjectInput,
                dueDatePicker,
                priorityBox,
                formButtons
        );
    }

    public String getTaskTitle() {
        return taskInput.getText();
    }

    public String getSubject() {
        return subjectInput.getText();
    }

    public LocalDate getDueDate() {
        return dueDatePicker.getValue();
    }

    public Task.Priority getPriority() {
        return priorityBox.getValue();
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public void clear() {
        taskInput.clear();
        subjectInput.clear();
        dueDatePicker.setValue(null);
        priorityBox.setValue(null);
    }

    public boolean isValid() {
        return !getTaskTitle().isBlank()
                && !getSubject().isBlank()
                && getDueDate() != null
                && getPriority() != null;
    }
}