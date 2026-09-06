package com.studentplanner;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {

    private final Button homeButton;
    private final Button tasksButton;
    private final Button todayButton;
    private final Button upcomingButton;
    private final Button completedButton;
    private final Button settingsButton;

    private final VBox taskSubmenu;
    private final Label tasksArrow;

    public Sidebar() {

        getStyleClass().add("sidebar");

        Label sidebarTitle = new Label("MONOLITH");
        sidebarTitle.getStyleClass().add("sidebar-title");

        homeButton = createSidebarButton("Home");

        // =========================
        // TASKS SECTION
        // =========================

        tasksArrow = new Label("▾");
        tasksArrow.getStyleClass().add("tasks-arrow");

        tasksButton = createSidebarButton("Tasks");

        HBox tasksHeader = new HBox();
        tasksHeader.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        tasksHeader.setSpacing(8);

        Label tasksLabel = new Label("Tasks");
        tasksLabel.getStyleClass().add("sidebar-button-label");

        tasksHeader.getChildren().addAll(
                tasksLabel,
                tasksArrow
        );

        tasksButton.setGraphic(tasksHeader);
        tasksButton.setText(null);

        todayButton = createSubmenuButton("Today");
        upcomingButton = createSubmenuButton("Upcoming");
        completedButton = createSubmenuButton("Completed");

        taskSubmenu = new VBox(
                todayButton,
                upcomingButton,
                completedButton
        );

        taskSubmenu.setSpacing(3);
        taskSubmenu.setPadding(
                new Insets(0, 0, 5, 12)
        );

        settingsButton = createSidebarButton("Settings");

        // =========================
        // SIDEBAR CONTENT
        // =========================

        getChildren().addAll(
                sidebarTitle,
                homeButton,
                tasksButton,
                taskSubmenu,
                settingsButton
        );

        // Tasks start expanded
        taskSubmenu.setVisible(true);
        taskSubmenu.setManaged(true);

        tasksButton.setOnAction(e -> toggleTaskSubmenu());
    }

    private Button createSidebarButton(String text) {

        Button button = new Button(text);

        button.getStyleClass().add("sidebar-button");

        button.setMaxWidth(Double.MAX_VALUE);

        return button;
    }

    private Button createSubmenuButton(String text) {

        Button button = new Button(text);

        button.getStyleClass().addAll(
                "sidebar-button",
                "sidebar-submenu-button"
        );

        button.setMaxWidth(Double.MAX_VALUE);

        return button;
    }

    private void toggleTaskSubmenu() {

        boolean visible = taskSubmenu.isVisible();

        taskSubmenu.setVisible(!visible);
        taskSubmenu.setManaged(!visible);

        if (visible) {
            tasksArrow.setText("▸");
        } else {
            tasksArrow.setText("▾");
        }
    }

    public Button getHomeButton() {
        return homeButton;
    }

    public Button getTodayButton() {
        return todayButton;
    }

    public Button getUpcomingButton() {
        return upcomingButton;
    }

    public Button getCompletedButton() {
        return completedButton;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }
}