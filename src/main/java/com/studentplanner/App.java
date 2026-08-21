package com.studentplanner;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Student Task & Study Planner");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}