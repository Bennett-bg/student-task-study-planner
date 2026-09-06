package com.studentplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {

    private final Button homeButton;
    private final Button tasksButton;
    private final Button todayButton;
    private final Button upcomingButton;
    private final Button completedButton;
    private final Button quickAddTaskButton;
    private final Button settingsButton;

    private final VBox taskSubmenu;
    private final Label tasksArrow;

    public Sidebar() {

        getStyleClass().add("sidebar");

        Label sidebarTitle = new Label("MONOLITH");
        sidebarTitle.getStyleClass().add("sidebar-title");

        // =========================
        // HOME
        // =========================

        homeButton = createSidebarButton("Home");

        // =========================
        // TASKS SECTION
        // =========================

        tasksArrow = new Label("▾");
        tasksArrow.getStyleClass().add("tasks-arrow");

        // Keep arrow size and position fixed
        tasksArrow.setMinWidth(14);
        tasksArrow.setPrefWidth(14);
        tasksArrow.setMaxWidth(14);
        tasksArrow.setAlignment(Pos.CENTER);

        tasksButton = createSidebarButton("Tasks");

        HBox tasksHeader = new HBox();
        tasksHeader.setAlignment(Pos.CENTER_LEFT);
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

        // =========================
        // SPACER
        // =========================

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        // =========================
        // QUICK ADD
        // =========================

        quickAddTaskButton = new Button("+ Quick Add Task");

        quickAddTaskButton.getStyleClass().add(
                "quick-add-sidebar-button"
        );

        quickAddTaskButton.setMaxWidth(
                Double.MAX_VALUE
        );

        // =========================
        // SETTINGS
        // =========================

        settingsButton = createSidebarButton("Settings");

        // =========================
        // SIDEBAR CONTENT
        // =========================

        getChildren().addAll(
                sidebarTitle,
                homeButton,
                tasksButton,
                taskSubmenu,
                spacer,
                quickAddTaskButton,
                settingsButton
        );

        // Tasks start expanded
        taskSubmenu.setVisible(true);
        taskSubmenu.setManaged(true);

        tasksButton.setOnAction(e ->
                toggleTaskSubmenu()
        );
    }

    private Button createSidebarButton(String text) {

        Button button = new Button(text);

        button.getStyleClass().add(
                "sidebar-button"
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        return button;
    }

    private Button createSubmenuButton(String text) {

        Button button = new Button(text);

        button.getStyleClass().addAll(
                "sidebar-button",
                "sidebar-submenu-button"
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

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

    public Button getQuickAddTaskButton() {
        return quickAddTaskButton;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }
}