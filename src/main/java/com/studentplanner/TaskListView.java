package com.studentplanner;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

public class TaskListView extends ListView<Task> {

    public TaskListView() {

        setCellFactory(listView -> new ListCell<Task>() {

            private final CheckBox checkBox = new CheckBox();
            private final Button deleteButton = new Button("Delete");
            private final HBox taskRow = new HBox();

            {
                taskRow.getChildren().addAll(
                        checkBox,
                        deleteButton
                );
            }

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                if (empty || task == null) {
                    setGraphic(null);
                    setText(null);
                } else {

                    checkBox.setText(task.toString());
                    checkBox.setSelected(task.isCompleted());

                    checkBox.setOnAction(e ->
                            task.setCompleted(checkBox.isSelected())
                    );

                    deleteButton.setOnAction(e -> {

                        Alert confirmation = new Alert(
                                Alert.AlertType.CONFIRMATION
                        );

                        confirmation.setTitle("Delete Task");
                        confirmation.setHeaderText("Delete this task?");
                        confirmation.setContentText(task.toString());

                        confirmation.showAndWait().ifPresent(response -> {

                            if (response == ButtonType.OK) {
                                getItems().remove(task);
                            }

                        });
                    });

                    setGraphic(taskRow);
                    setText(null);
                }
            }
        });
    }
}