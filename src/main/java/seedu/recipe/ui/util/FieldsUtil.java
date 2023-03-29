package seedu.recipe.ui.util;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 * A utility class containing methods for working with TextField components.
 */
public class FieldsUtil {
    /**
     * Creates a dynamic TextField with the specified initial text.
     * The TextField will support UP, DOWN, and TAB navigation.
     * If the TextField is the last in the VBox and gains focus, a new empty TextField will be added below it.
     * If the TextField loses focus and is the last in the VBox and empty, it will be removed.
     *
     * @param text The initial text for the TextField.
     * @return The created dynamic TextField.
     */
    public static TextField createDynamicTextField(String text) {
        TextField textField = new TextField(text);

        //Keyboard listener for navigation
        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            int currentIndex = ((VBox) textField.getParent()).getChildren().indexOf(textField);
            handleNavigation(event, textField, currentIndex);
            });

        //Text field listener for automatically adding/removing new input rows
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            VBox parentBox = (VBox) textField.getParent();
            if (parentBox == null) {
                return;
            }
            // Check if the TextField has gained focus
            if (newValue) {
                addNewRow(parentBox, textField);
            } 
            else {
                removeNewRow(parentBox, textField);
            }
        });

        return textField;
    }

    /**
     * Returns the next TextField below the current TextField within the same parent VBox, if any.
     *
     * @param currentTextField The current TextField.
     * @return The next TextField below the current TextField or null if there's no TextField below it.
     */
    private static TextField getNextTextField(TextField currentTextField) {
        VBox parentBox = (VBox) currentTextField.getParent();
        int currentIndex = parentBox.getChildren().indexOf(currentTextField);
        int lastIndex = parentBox.getChildren().size() - 1;

        if (currentIndex < lastIndex) {
            Node nextNode = parentBox.getChildren().get(currentIndex + 1);
            if (nextNode instanceof TextField) {
                return (TextField) nextNode;
            }
        }

        return null;
    }
    
    /**
     * Handles keyboard navigation events for TextField components.
     *
     * @param event       The KeyEvent to handle.
     * @param textField   The TextField to perform navigation on.
     * @param currentIndex The index of the TextField in its parent VBox.
     */
    public static void handleNavigation(KeyEvent event, TextField textField, int currentIndex) {
        TextField nextField = (TextField) ((VBox) textField.getParent()).getChildren().get(currentIndex + 1);

        // >>> Should be removed - arrow keys are for navigation WITHIN text field
        // Condition 2.1: DOWN key pressed
        if (event.getCode() == KeyCode.DOWN) {
            nextField.requestFocus();

        // Condition 2.2: Purely TAB key pressed
        } else if (event.getCode() == KeyCode.TAB && !event.isShiftDown()) {
            // If it is a new placeholder row and there's another TextField after it, skip to the field after
            if (nextField.getText().isEmpty()
                    && currentIndex + 2
                        < ((VBox) textField.getParent()).getChildren().size()) {
                nextField = (TextField) ((VBox) textField.getParent())
                        .getChildren()
                        .get(currentIndex + 2);
            }
            nextField.requestFocus();
        // Shift + TAB
        } else if (event.getCode() == KeyCode.TAB) {
            ObservableList<Node> childList = ((VBox) textField.getParent()).getChildren();
            if (textField.getText().isEmpty()) {
                childList.remove(textField);
            } else {
                int index = childList.indexOf(textField);
                if (index > 0) {
                    childList.get(index - 1).requestFocus();
                }
            }
        }
        event.consume();
    }
    
    /**
     * Adds a new empty TextField to the end of the VBox if the provided TextField is the last one.
     *
     * @param parentBox The VBox containing the TextField.
     * @param textField The TextField to check if it is the last one in the VBox.
     */
    public static void addNewRow(VBox parentBox, TextField textField) {
        int lastIndex = parentBox.getChildren().size() - 1;
        // Check if it's the last TextField in the VBox
        if (parentBox.getChildren().indexOf(textField) == lastIndex) {
            TextField newField = createDynamicTextField("");
            parentBox.getChildren().add(newField);
        }
    };

    /**
     * Removes the empty TextField below the current TextField if both are empty and not focused.
     *
     * @param parentBox The VBox containing the TextField.
     * @param textField The TextField to check for removal conditions.
     */
    public static void removeNewRow(VBox parentBox, TextField textField) {
        // Check if it's the last TextField, it's empty, and the focus is not in the same VBox, then remove it
        if (getNextTextField(textField) != null
                && getNextTextField(textField).getText().isEmpty()
                && textField.getText().isEmpty()) {
            parentBox.getChildren().remove(getNextTextField(textField));
        }
    }
}
