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

        Label sidebarTitle = new Label("MONOLITH");

        todayButton = new Button("Today");
        upcomingButton = new Button("Upcoming");
        completedButton = new Button("Completed");
        settingsButton = new Button("Settings");

        getChildren().addAll(
                sidebarTitle,
                todayButton,
                upcomingButton,
                completedButton,
                settingsButton
        );
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