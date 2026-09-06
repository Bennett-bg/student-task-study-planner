package com.studentplanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        Sidebar sidebar = new Sidebar();
        MainContent mainContent = new MainContent();

        sidebar.getHomeButton().setOnAction(e ->
                mainContent.showDashboard()
        );

        sidebar.getTodayButton().setOnAction(e ->
                mainContent.showToday()
        );

        sidebar.getUpcomingButton().setOnAction(e ->
                mainContent.showUpcoming()
        );

        sidebar.getCompletedButton().setOnAction(e ->
                mainContent.showCompleted()
        );

        sidebar.getSettingsButton().setOnAction(e ->
                mainContent.showSettings()
        );

        mainContent.setupTaskSection();

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