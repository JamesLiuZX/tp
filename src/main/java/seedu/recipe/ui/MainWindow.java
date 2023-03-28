package seedu.recipe.ui;

import java.util.Map;
import java.util.logging.Logger;
import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seedu.recipe.commons.core.GuiSettings;
import seedu.recipe.commons.core.LogsCenter;
import seedu.recipe.logic.Logic;
import seedu.recipe.logic.commands.CommandResult;
import seedu.recipe.logic.commands.exceptions.CommandException;
import seedu.recipe.logic.parser.exceptions.ParseException;
import seedu.recipe.ui.events.DeleteRecipeEvent;

/**
 * Represents the main window of the application. This class is responsible for
 * initializing and configuring the UI components and managing the primary stage.
 * It also contains methods for handling UI events, such as deleting or editing recipes.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private final Stage primaryStage;
    private final Logic logic;
    private final HelpWindow helpWindow;

    // Independent Ui parts residing in this Ui container
    private RecipeListPanel recipeListPanel;
    private ResultDisplay resultDisplay;
    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem importMenuItem;

    @FXML
    private StackPane recipeListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Constructor that creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     * Initializes and configures the UI components.
     *
     * @param primaryStage the primary stage for this main window.
     * @param logic the main logic instance of the application.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        assert primaryStage != null : "Primary stage cannot be null";
        assert logic != null : "Logic cannot be null";

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        getRoot().addEventFilter(DeleteRecipeEvent.DELETE_RECIPE_EVENT_TYPE, this::handleDeleteRecipeEvent);

        helpWindow = new HelpWindow();
    }

    /**
     * Returns the primary stage of this main window.
     *
     * @return the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Sets accelerator for the help menu.
     */
    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
        //setAccelerator(importMenuItem, KeyCombination.valueOf("F2"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();

        // Set the file extension filter for JSON files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            // The selectedFile variable now contains the selected JSON file
            // We can now pass this file to your import handling method
            return;
        }
    }

    /**
     * Handles the DeleteRecipeEvent by executing the appropriate delete command
     * based on the provided event data.
     *
     * @param event the DeleteRecipeEvent containing the index of the recipe to be deleted.
     */
    private void handleDeleteRecipeEvent(DeleteRecipeEvent event) {
        int recipeIndex = event.getRecipeIndex();
        try {
            String commandText = "delete " + (recipeIndex);
            executeCommand(commandText);
        } catch (CommandException | ParseException e) {
            logger.info("Failed to delete recipe: " + recipeIndex);
        }
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        // Assertions to check if placeholders are not null
        assert recipeListPanelPlaceholder != null : "RecipeListPanel placeholder cannot be null";
        assert resultDisplayPlaceholder != null : "ResultDisplay placeholder cannot be null";
        assert statusbarPlaceholder != null : "Status bar placeholder cannot be null";
        assert commandBoxPlaceholder != null : "Command box placeholder cannot be null";

        recipeListPanel = new RecipeListPanel(logic.getFilteredRecipeList(), this::executeCommand);
        recipeListPanelPlaceholder.getChildren().add(recipeListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getRecipeBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }
    /**
     * Makes the primary stage of this main window visible.
     */
    void show() {
        primaryStage.show();
    }
    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                                                  (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    /**
     * Returns the {@code RecipeListPanel} contained in this main window.
     *
     * @return the {@code RecipeListPanel} instance.
     */
    public RecipeListPanel getRecipeListPanel() {
        return recipeListPanel;
    }

    /**
     * Executes the command based on the given {@code commandText} and returns the result.
     * Updates the UI components based on the command result.
     *
     * @param commandText the command text to execute.
     * @return the resulting {@code CommandResult} after executing the command.
     * @throws CommandException if the command execution fails.
     * @throws ParseException if the command text cannot be parsed.
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("Invalid command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
