package com.studentplanner;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public class MainContent extends VBox {

    private final TaskManager taskManager;

    private final TodayView todayView;
    private final UpcomingView upcomingView;
    private final CompletedView completedView;
    private final SettingsView settingsView;

    public MainContent() {

        setPadding(new Insets(35));

        taskManager = new TaskManager();

        todayView = new TodayView(taskManager);
        upcomingView = new UpcomingView(taskManager);
        completedView = new CompletedView(taskManager);
        settingsView = new SettingsView();

        showToday();
    }

    public void setupTaskSection() {
        // Task pages handle their own task setup.
    }

    public void showToday() {
        getChildren().clear();
        getChildren().add(todayView);
    }

    public void showUpcoming() {
        getChildren().clear();
        getChildren().add(upcomingView);
    }

    public void showCompleted() {
        getChildren().clear();
        getChildren().add(completedView);
    }

    public void showSettings() {
        getChildren().clear();
        getChildren().add(settingsView);
    }
}