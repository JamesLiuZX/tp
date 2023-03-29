package seedu.recipe.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmationDialog {

    public static final String MESSAGE_DELETE_CONFIRMATION = "Are you sure you want to delete this recipe?";

    public boolean getConfirmation() {
        Stage confirmationDialog = new Stage();
        confirmationDialog.initModality(Modality.APPLICATION_MODAL);
        confirmationDialog.setTitle("Confirm Deletion");

        AtomicBoolean confirmed = new AtomicBoolean(false);

        Label label = new Label(MESSAGE_DELETE_CONFIRMATION);
        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");
        // set confirm button on action
        confirmButton.setOnAction(e -> {
            confirmed.set(true);
            confirmationDialog.close();
        });
        // set cancel button on action
        cancelButton.setOnAction(e -> confirmationDialog.close());
        // set confirm button on key pressed
        confirmButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmed.set(true);
                confirmationDialog.close();
            }
        });
        // set cancel button on key pressed
        cancelButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmed.set(false);
                confirmationDialog.close();
            }
        });
        // initialize HBox buttons
        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(confirmButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        // initialize VBox layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, buttons);
        layout.setAlignment(Pos.CENTER);
        // initialize scene
        Scene scene = new Scene(layout, 300, 200);
        confirmationDialog.setScene(scene);
        // set scene on key pressed
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmed.set(true);
                confirmationDialog.close();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                confirmed.set(false);
                confirmationDialog.close();
            }
        });
        confirmationDialog.showAndWait();
        return confirmed.get();
    }
}