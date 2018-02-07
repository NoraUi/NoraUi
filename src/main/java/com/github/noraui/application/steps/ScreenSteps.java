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
import com.github.noraui.service.ScreenService;
import com.github.noraui.utils.Context;
import com.google.inject.Inject;

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

    @Inject
    private ScreenService screenService;

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
        logger.debug("I take a screenshot for [{}] scenario.", Context.getCurrentScenario());
        screenService.takeScreenshot(Context.getCurrentScenario());
    }

    /**
     * Save a screenshot and add to DOWNLOAD_FILES_FOLDER folder.
     *
     * @param screenName
     *            name of screenshot file.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws IOException
     *             if file or directory is wrong.
     */
    @Conditioned
    @Et("Je sauvegarde une capture d'écran dans '(.*)'[\\.|\\?]")
    @And("I save a screenshot in '(.*)'[\\.|\\?]")
    public void saveScreenshot(String screenName, List<GherkinStepCondition> conditions) throws IOException {
        logger.debug("I save a screenshot in [{}].", screenName);
        screenService.saveScreenshot(screenName);
    }

    /**
     * I start video capture and add to DOWNLOAD_FILES_FOLDER folder.
     *
     * @param screenName
     *            name of video file.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws Exception
     */
    @Conditioned
    @Et("Je commence la capture vidéo dans '(.*)'[\\.|\\?]")
    @And("I start video capture in '(.*)'[\\.|\\?]")
    public void startVideoCapture(String screenName, List<GherkinStepCondition> conditions) throws Exception {
        logger.debug("I start video capture in [{}].", screenName);
        screenService.startVideoCapture(screenName);
    }

    /**
     * I start video capture and add to DOWNLOAD_FILES_FOLDER folder.
     *
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws IOException
     */
    @Conditioned
    @Et("J'arrête la capture vidéo[\\.|\\?]")
    @And("I stop video capture[\\.|\\?]")
    public void stopVideoCapture(List<GherkinStepCondition> conditions) throws IOException {
        logger.debug("I stop video capture.");
        screenService.stopVideoCapture();
    }

}
