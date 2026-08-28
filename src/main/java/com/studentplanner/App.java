
package com.studentplanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        VBox sidebar = new VBox();
        VBox mainContent = new VBox();

        Label sidebarTitle = new Label("MONOLITH");

        Button todayButton = new Button("Today");
        Button upcomingButton = new Button("Upcoming");
        Button completedButton = new Button("Completed");
        Button settingsButton = new Button("Settings");

        // TODAY BUTTON
        todayButton.setOnAction(e -> {
            // Today's view will be properly implemented later.
        });

        sidebar.getChildren().addAll(
                sidebarTitle,
                todayButton,
                upcomingButton,
                completedButton,
                settingsButton
        );

        Label welcome = new Label("Welcome to Monolith");
        Label tasksTitle = new Label("Today's Tasks");

        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter a task");

        TextField subjectInput = new TextField();
        subjectInput.setPromptText("Enter subject");

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Select due date");

        ComboBox<Task.Priority> priorityBox = new ComboBox<>();

        priorityBox.getItems().addAll(
                Task.Priority.VERY_LOW,
                Task.Priority.LOW,
                Task.Priority.MEDIUM,
                Task.Priority.HIGH,
                Task.Priority.URGENT
        );

        priorityBox.setPromptText("Select priority");

        Button addTaskButton = new Button("Save Task");
        Button cancelButton = new Button("Cancel");

        Button showAddTaskButton = new Button("Add Task");

        VBox taskForm = new VBox();

        HBox formButtons = new HBox();

        formButtons.getChildren().addAll(
                addTaskButton,
                cancelButton
        );

        taskForm.getChildren().addAll(
                taskInput,
                subjectInput,
                dueDatePicker,
                priorityBox,
                formButtons
        );

        taskForm.setVisible(false);
        taskForm.setManaged(false);

        ListView<Task> taskList = new ListView<>();

        taskList.setCellFactory(listView -> new ListCell<Task>() {

            private final CheckBox checkBox = new CheckBox();
            private final Button deleteButton = new Button("Delete");
            private final HBox taskRow = new HBox();

            {
                taskRow.getChildren().addAll(
                        checkBox,
                        deleteButton
                );
            }

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                if (empty || task == null) {
                    setGraphic(null);
                    setText(null);
                } else {

                    checkBox.setText(task.toString());
                    checkBox.setSelected(task.isCompleted());

                    checkBox.setOnAction(e ->
                            task.setCompleted(checkBox.isSelected())
                    );

                    deleteButton.setOnAction(e -> {

                        Alert confirmation = new Alert(
                                Alert.AlertType.CONFIRMATION
                        );

                        confirmation.setTitle("Delete Task");
                        confirmation.setHeaderText("Delete this task?");
                        confirmation.setContentText(task.toString());

                        confirmation.showAndWait().ifPresent(response -> {

                            if (response == javafx.scene.control.ButtonType.OK) {
                                taskList.getItems().remove(task);
                            }

                        });
                    });

                    setGraphic(taskRow);
                    setText(null);
                }
            }
        });

        showAddTaskButton.setOnAction(e -> {

            taskForm.setVisible(true);
            taskForm.setManaged(true);

            showAddTaskButton.setVisible(false);
            showAddTaskButton.setManaged(false);
        });

        addTaskButton.setOnAction(e -> {

            String title = taskInput.getText();
            String subject = subjectInput.getText();

            var dueDate = dueDatePicker.getValue();

            Task.Priority priority = priorityBox.getValue();

            if (!title.isBlank()
                    && !subject.isBlank()
                    && dueDate != null
                    && priority != null) {

                Task newTask = new Task(
                        title,
                        subject,
                        dueDate,
                        priority
                );

                taskList.getItems().add(newTask);

                taskInput.clear();
                subjectInput.clear();
                dueDatePicker.setValue(null);
                priorityBox.setValue(null);

                taskForm.setVisible(false);
                taskForm.setManaged(false);

                showAddTaskButton.setVisible(true);
                showAddTaskButton.setManaged(true);
            }
        });

        cancelButton.setOnAction(e -> {

            taskInput.clear();
            subjectInput.clear();
            dueDatePicker.setValue(null);
            priorityBox.setValue(null);

            taskForm.setVisible(false);
            taskForm.setManaged(false);

            showAddTaskButton.setVisible(true);
            showAddTaskButton.setManaged(true);
        });

        mainContent.getChildren().addAll(
                welcome,
                tasksTitle,
                showAddTaskButton,
                taskForm,
                taskList
        );

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Student Task & Study Planner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

