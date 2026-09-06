package com.studentplanner;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CompletedView extends VBox {

    public CompletedView(TaskManager taskManager) {

        setSpacing(15);
        setPadding(new Insets(10, 0, 0, 0));

        Label title = new Label("Completed");
        title.getStyleClass().add("page-title");

        getChildren().add(title);
    }
}