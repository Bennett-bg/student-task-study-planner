package com.studentplanner;

import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainContent extends VBox {

    private final TaskManager taskManager;

    private final DashboardView dashboardView;
    private final TodayView todayView;
    private final UpcomingView upcomingView;
    private final CompletedView completedView;
    private final AddTaskView addTaskView;
    private final SettingsView settingsView;

    public MainContent() {

        setPadding(new Insets(35));
        setFillWidth(true);

        taskManager = new TaskManager();

        dashboardView = new DashboardView(taskManager);
        todayView = new TodayView(taskManager);
        upcomingView = new UpcomingView(taskManager);
        completedView = new CompletedView(taskManager);
        addTaskView = new AddTaskView(taskManager);
        settingsView = new SettingsView();

        dashboardView.setMaxHeight(Double.MAX_VALUE);
        todayView.setMaxHeight(Double.MAX_VALUE);
        upcomingView.setMaxHeight(Double.MAX_VALUE);
        completedView.setMaxHeight(Double.MAX_VALUE);
        addTaskView.setMaxHeight(Double.MAX_VALUE);
        settingsView.setMaxHeight(Double.MAX_VALUE);

        showDashboard();
    }

    public void setupTaskSection() {
        // Task pages handle their own task setup.
    }

    public void setupDashboardQuickAdd() {

        dashboardView.getQuickAddButton().setOnAction(e ->
                showAddTask()
        );
    }

    public void showDashboard() {

        dashboardView.refresh();

        getChildren().clear();
        getChildren().add(dashboardView);

        VBox.setVgrow(
                dashboardView,
                Priority.ALWAYS
        );
    }

    public void showToday() {

        todayView.refreshTasks();

        getChildren().clear();
        getChildren().add(todayView);

        VBox.setVgrow(
                todayView,
                Priority.ALWAYS
        );
    }

    public void showUpcoming() {

        upcomingView.refreshTasks();

        getChildren().clear();
        getChildren().add(upcomingView);

        VBox.setVgrow(
                upcomingView,
                Priority.ALWAYS
        );
    }

    public void showCompleted() {

        completedView.refreshTasks();

        getChildren().clear();
        getChildren().add(completedView);

        VBox.setVgrow(
                completedView,
                Priority.ALWAYS
        );
    }

    public void showAddTask() {

        getChildren().clear();
        getChildren().add(addTaskView);

        VBox.setVgrow(
                addTaskView,
                Priority.ALWAYS
        );
    }

    public void showSettings() {

        getChildren().clear();
        getChildren().add(settingsView);

        VBox.setVgrow(
                settingsView,
                Priority.ALWAYS
        );
    }

    public void showQuickAddTask() {

        showAddTask();
    }
}