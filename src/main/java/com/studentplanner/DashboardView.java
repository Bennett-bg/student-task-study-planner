package com.studentplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardView extends VBox {

    private final TaskManager taskManager;

    private final Label todayCount;
    private final Label upcomingCount;
    private final Label completedCount;

    private final TaskListView todayTaskList;
    private final VBox emptyState;

    private final Button quickAddButton;

    public DashboardView(TaskManager taskManager) {

        this.taskManager = taskManager;

        setSpacing(20);
        setPadding(new Insets(0));
        setFillWidth(true);

        // =========================
        // HEADER
        // =========================

        Label welcomeLabel = new Label("Welcome back");
        welcomeLabel.getStyleClass().add("welcome-label");

        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");

        // =========================
        // SUMMARY CARDS
        // =========================

        todayCount = new Label();
        upcomingCount = new Label();
        completedCount = new Label();

        HBox summaryCards = new HBox(15);
        summaryCards.setMaxWidth(Double.MAX_VALUE);

        VBox todayCard = createCard("Today's Tasks", todayCount);
        VBox upcomingCard = createCard("Upcoming", upcomingCount);
        VBox completedCard = createCard("Completed", completedCount);

        summaryCards.getChildren().addAll(
                todayCard,
                upcomingCard,
                completedCard
        );

        HBox.setHgrow(
                todayCard,
                javafx.scene.layout.Priority.ALWAYS
        );

        HBox.setHgrow(
                upcomingCard,
                javafx.scene.layout.Priority.ALWAYS
        );

        HBox.setHgrow(
                completedCard,
                javafx.scene.layout.Priority.ALWAYS
        );

        // =========================
        // TODAY'S TASKS
        // =========================

        Label todayTitle = new Label("Today's Tasks");
        todayTitle.getStyleClass().add("section-title");

        todayTaskList = new TaskListView(taskManager);
        todayTaskList.setMaxWidth(Double.MAX_VALUE);

        // =========================
        // EMPTY STATE
        // =========================

        Label emptyTitle = new Label("No tasks for today");
        emptyTitle.getStyleClass().add("empty-state-title");

        Label emptyMessage = new Label(
                "You're all caught up. Enjoy your day!"
        );
        emptyMessage.getStyleClass().add("empty-state-message");

        quickAddButton = new Button("Quick Add Task");
        quickAddButton.getStyleClass().add("add-task-button");

        emptyState = new VBox(
                8,
                emptyTitle,
                emptyMessage,
                quickAddButton
        );

        emptyState.setAlignment(Pos.CENTER);
        emptyState.setMaxWidth(Double.MAX_VALUE);
        emptyState.getStyleClass().add("empty-state");

        // =========================
        // TODAY SECTION
        // =========================

        VBox todaySection = new VBox(
                12,
                todayTitle,
                todayTaskList,
                emptyState
        );

        todaySection.setFillWidth(true);
        todaySection.setMaxWidth(Double.MAX_VALUE);

        VBox.setVgrow(
                todaySection,
                javafx.scene.layout.Priority.ALWAYS
        );

        VBox.setVgrow(
                todayTaskList,
                javafx.scene.layout.Priority.ALWAYS
        );

        VBox.setVgrow(
                emptyState,
                javafx.scene.layout.Priority.ALWAYS
        );

        // =========================
        // PAGE CONTENT
        // =========================

        getChildren().addAll(
                welcomeLabel,
                title,
                summaryCards,
                todaySection
        );

        refresh();
    }

    private VBox createCard(String title, Label count) {

        Label cardTitle = new Label(title);
        cardTitle.getStyleClass().add("card-title");

        count.getStyleClass().add("card-count");

        VBox card = new VBox(8);

        card.getStyleClass().add("dashboard-card");

        card.setPrefHeight(100);
        card.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(
                cardTitle,
                count
        );

        return card;
    }

    public void refresh() {

        int todayTasks = taskManager.getTodayTasks().size();

        todayCount.setText(
                String.valueOf(todayTasks)
        );

        upcomingCount.setText(
                String.valueOf(
                        taskManager.getUpcomingTasks().size()
                )
        );

        completedCount.setText(
                String.valueOf(
                        taskManager.getCompletedTasks().size()
                )
        );

        todayTaskList.getListView().getItems().setAll(
                taskManager.getTodayTasks()
        );

        boolean hasTodayTasks = todayTasks > 0;

        todayTaskList.setVisible(hasTodayTasks);
        todayTaskList.setManaged(hasTodayTasks);

        emptyState.setVisible(!hasTodayTasks);
        emptyState.setManaged(!hasTodayTasks);
    }

    // =========================
    // QUICK ADD BUTTON
    // =========================

    public Button getQuickAddButton() {
        return quickAddButton;
    }
}