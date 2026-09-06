package com.studentplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardView extends VBox {

    private final TaskManager taskManager;

    private final Label todayCount;
    private final Label upcomingCount;
    private final Label completedCount;

    public DashboardView(TaskManager taskManager) {

        this.taskManager = taskManager;

        setSpacing(20);
        setPadding(new Insets(0));

        // =========================
        // HEADER
        // =========================

        Label welcomeLabel = new Label("Welcome back");
        welcomeLabel.getStyleClass().add("welcome-label");

        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");

        // =========================
        // SUMMARY COUNTS
        // =========================

        todayCount = new Label();
        upcomingCount = new Label();
        completedCount = new Label();

        // =========================
        // SUMMARY CARDS
        // =========================

        HBox summaryCards = new HBox(15);

        summaryCards.getChildren().addAll(
                createCard("Today's Tasks", todayCount),
                createCard("Upcoming", upcomingCount),
                createCard("Completed", completedCount)
        );

        // =========================
        // PAGE CONTENT
        // =========================

        getChildren().addAll(
                welcomeLabel,
                title,
                summaryCards
        );

        refresh();
    }

    private VBox createCard(String title, Label count) {

        Label cardTitle = new Label(title);
        cardTitle.getStyleClass().add("card-title");

        count.getStyleClass().add("card-count");

        VBox card = new VBox(8);

        card.setAlignment(Pos.CENTER_LEFT);

        card.getStyleClass().add("dashboard-card");

        card.setPrefWidth(200);
        card.setPrefHeight(100);

        card.getChildren().addAll(
                cardTitle,
                count
        );

        return card;
    }

    public void refresh() {

        todayCount.setText(
                String.valueOf(
                        taskManager.getTodayTasks().size()
                )
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
    }
}