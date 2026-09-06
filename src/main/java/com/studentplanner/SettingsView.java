package com.studentplanner;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SettingsView extends VBox {

    public SettingsView() {

        setSpacing(15);
        setPadding(new Insets(10, 0, 0, 0));

        Label title = new Label("Settings");
        title.getStyleClass().add("page-title");

        Label message = new Label("Settings will be available here.");
        message.getStyleClass().add("task-details");

        getChildren().addAll(
                title,
                message
        );
    }
}