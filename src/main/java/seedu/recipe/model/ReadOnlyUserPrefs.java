package seedu.recipe.model;

import java.nio.file.Path;

import seedu.recipe.commons.core.GuiSettings;

/**
 * Unmodifiable view of user prefs.
 */
public interface ReadOnlyUserPrefs {

    GuiSettings getGuiSettings();

    Path getRecipeBookFilePath();

}
