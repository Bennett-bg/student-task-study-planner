package com.studentplanner;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public class MainContent extends VBox {

    private final TaskManager taskManager;

    private final DashboardView dashboardView;
    private final TodayView todayView;
    private final UpcomingView upcomingView;
    private final CompletedView completedView;
    private final SettingsView settingsView;

    public MainContent() {

        setPadding(new Insets(35));

        taskManager = new TaskManager();

        dashboardView = new DashboardView(taskManager);
        todayView = new TodayView(taskManager);
        upcomingView = new UpcomingView(taskManager);
        completedView = new CompletedView(taskManager);
        settingsView = new SettingsView();

        showDashboard();
    }

    public void setupTaskSection() {
        // Task pages handle their own task setup.
    }

    public void showDashboard() {

        dashboardView.refresh();

        getChildren().clear();
        getChildren().add(dashboardView);
    }

    public void showToday() {

        todayView.refreshTasks();

        getChildren().clear();
        getChildren().add(todayView);
    }

    public void showUpcoming() {

        upcomingView.refreshTasks();

        getChildren().clear();
        getChildren().add(upcomingView);
    }

    public void showCompleted() {

        completedView.refreshTasks();

        getChildren().clear();
        getChildren().add(completedView);
    }

    public void showSettings() {

        getChildren().clear();
        getChildren().add(settingsView);
    }
}