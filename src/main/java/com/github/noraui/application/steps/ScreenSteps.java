/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Utilities;

import cucumber.api.java.en.And;
import cucumber.api.java.fr.Et;

/**
 * This class contains Gherkin callable steps that goal of working with the screen (scrennshot, ...).
 */
public class ScreenSteps extends Step {

    /**
     * Specific logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ScreenSteps.class);

    /**
     * Take a screenshot and add to result.
     *
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     */
    @Conditioned
    @Et("Je prends une capture d'écran[\\.|\\?]")
    @And("I take a screenshot[\\.|\\?]")
    public void takeScreenshot(List<GherkinStepCondition> conditions) {
        logger.debug("Current scenario is {}", Context.getCurrentScenario());
        Utilities.takeScreenshot(Context.getCurrentScenario());
    }

    /**
     * Save a screenshot and add to DOWNLOAD_FILES_FOLDER folder.
     *
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws IOException
     *             if file or directory is wrong.
     */
    @Conditioned
    @Et("Je sauvegarde une capture d'écran dans '(.*)'[\\.|\\?]")
    @And("I save a screenshot in '(.*)'[\\.|\\?]")
    public void saveScreenshot(String screenName, List<GherkinStepCondition> conditions) throws IOException {
        logger.debug("Current scenario is {}", Context.getCurrentScenario());
        Utilities.saveScreenshot(screenName);
    }

}
