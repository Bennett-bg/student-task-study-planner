package com.studentplanner;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {

    private final Button todayButton;
    private final Button upcomingButton;
    private final Button completedButton;
    private final Button settingsButton;

    public Sidebar() {

        getStyleClass().add("sidebar");

        Label sidebarTitle = new Label("MONOLITH");
        sidebarTitle.getStyleClass().add("sidebar-title");

        todayButton = createSidebarButton("Today");
        upcomingButton = createSidebarButton("Upcoming");
        completedButton = createSidebarButton("Completed");
        settingsButton = createSidebarButton("Settings");

        getChildren().addAll(
                sidebarTitle,
                todayButton,
                upcomingButton,
                completedButton,
                settingsButton
        );
    }

    private Button createSidebarButton(String text) {

        Button button = new Button(text);

        button.getStyleClass().add("sidebar-button");

        button.setMaxWidth(Double.MAX_VALUE);

        return button;
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