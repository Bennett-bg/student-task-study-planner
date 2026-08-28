package com.studentplanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");

        VBox mainContent = new VBox();
        mainContent.getStyleClass().add("main-content");

        Label sidebarTitle = new Label("MONOLITH");
        sidebarTitle.getStyleClass().add("sidebar-title");

        Button todayButton = new Button("Today");
        Button upcomingButton = new Button("Upcoming");
        Button completedButton = new Button("Completed");
        Button settingsButton = new Button("Settings");

        todayButton.getStyleClass().add("sidebar-button");
        upcomingButton.getStyleClass().add("sidebar-button");
        completedButton.getStyleClass().add("sidebar-button");
        settingsButton.getStyleClass().add("sidebar-button");

        sidebar.getChildren().addAll(
                sidebarTitle,
                todayButton,
                upcomingButton,
                completedButton,
                settingsButton
        );

        Label welcome = new Label("Welcome to Monolith");
        welcome.getStyleClass().add("welcome-label");

        Label tasksTitle = new Label("Today's Tasks");
        tasksTitle.getStyleClass().add("page-title");

        Button showAddTaskButton = new Button("Add Task");
        showAddTaskButton.getStyleClass().add("add-task-button");

        TaskForm taskForm = new TaskForm();

        TaskListView taskList = new TaskListView();

        taskForm.setVisible(false);
        taskForm.setManaged(false);

        showAddTaskButton.setOnAction(e -> {

            taskForm.setVisible(true);
            taskForm.setManaged(true);

            showAddTaskButton.setVisible(false);
            showAddTaskButton.setManaged(false);
        });

        taskForm.setOnTaskSaved(task -> {

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

        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        stage.setTitle("Student Task & Study Planner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}