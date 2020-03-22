/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import java.awt.AWTException;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.browser.waits.Wait;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.service.ScreenService;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;
import com.google.inject.Inject;

import io.cucumber.java.en.And;
import io.cucumber.java.fr.Et;

/**
 * This class contains Gherkin callable steps that goal of working with the screen (scrennshot, ...).
 */
@Loggable
public class ScreenSteps extends Step {

    static Logger log;

    @Inject
    private ScreenService screenService;

    /**
     * Take a screenshot and add to result.
     *
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     */
    @Conditioned
    @Et("Je prends une capture d'écran(\\?)")
    @And("I take a screenshot(\\?)")
    public void takeScreenshot(List<GherkinStepCondition> conditions) {
        log.debug("I take a screenshot for [{}] scenario.", Context.getCurrentScenario());
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
    @Et("Je sauvegarde une capture d\'écran dans {string}(\\?)")
    @And("I save a screenshot in {string}(\\?)")
    public void saveScreenshot(String screenName, List<GherkinStepCondition> conditions) throws IOException {
        log.debug("I save a screenshot in [{}].", screenName);
        screenService.saveScreenshot(screenName);
    }

    /**
     * Save a screenshot of one element only and add to DOWNLOAD_FILES_FOLDER folder.
     * 
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param screenName
     *            name of screenshot file.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}). * list of 'expected' values condition and 'actual' values
     *            ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws IOException
     *             if file or directory is wrong.
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} message (with screenshot, no exception)
     */
    @Conditioned
    @Et("Je sauvegarde une capture d'écran de {page-element} dans {string}(\\?)")
    @And("I save a screenshot of {page-element} in {string}(\\?)")
    public void saveWebElementInScreenshot(PageElement pageElement, String screenName, List<GherkinStepCondition> conditions) throws IOException, FailureException, TechnicalException {
        log.debug("I save a screenshot of [{}-{}] in [{}.jpg]", pageElement.getPage(), pageElement.getKey(), screenName);
        try {
            screenService.saveScreenshot(screenName, Wait.until(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement))));
        } catch (Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * I start video capture and add to DOWNLOAD_FILES_FOLDER folder.
     *
     * @param screenName
     *            name of video file.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws IOException
     *             if file or directory is wrong.
     * @throws AWTException
     *             if configuration video file is wrong.
     */
    @Conditioned
    @Et("Je commence la capture vidéo dans {string}(\\?)")
    @And("I start video capture in {string}(\\?)")
    public void startVideoCapture(String screenName, List<GherkinStepCondition> conditions) throws IOException, AWTException {
        log.debug("I start video capture in [{}].", screenName);
        screenService.startVideoCapture(screenName);
    }

    /**
     * I start video capture and add to DOWNLOAD_FILES_FOLDER folder.
     *
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws IOException
     *             if file or directory is wrong.
     */
    @Conditioned
    @Et("Je stop la capture vidéo(\\?)")
    @And("I stop video capture(\\?)")
    public void stopVideoCapture(List<GherkinStepCondition> conditions) throws IOException {
        log.debug("I stop video capture.");
        screenService.stopVideoCapture();
    }

    @Conditioned
    @Et("Je défile vers {string}(\\?)")
    @And("I scroll to {string}(\\?)")
    public void scrollIntoView(PageElement pageElement, List<GherkinStepCondition> conditions) {
        log.debug("I scroll to [{}]", pageElement);
        screenService.scrollIntoView(Wait.until(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement))));
    }

}
