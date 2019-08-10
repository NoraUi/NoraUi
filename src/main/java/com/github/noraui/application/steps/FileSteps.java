/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import static com.github.noraui.utils.Constants.DOWNLOADED_FILES_FOLDER;
import static com.github.noraui.utils.Constants.USER_DIR;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.Page;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.fr.Alors;
import cucumber.api.java.fr.Lorsque;

/**
 * This class contains Gherkin callable steps that handle file uploading and downloading.
 */
public class FileSteps extends Step {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSteps.class);

    /**
     * Empties the default downloaded files folder.
     *
     * @param conditions
     *            List of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     */
    @Conditioned
    @Lorsque("Je vide le repertoire des téléchargements(\\?)")
    @Given("I clean download directory(\\?)")
    public void cleanDownloadDirectory(List<GherkinStepCondition> conditions) {
        try {
            FileUtils.forceMkdir(new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER));
            FileUtils.cleanDirectory(new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER));
        } catch (IOException e) {
            LOGGER.warn("IOException in cleanDownloadDirectory", e);
        }
    }

    /**
     * Remove a file in the default downloaded files folder
     * 
     * @param file
     *            The name of the file removed.
     * @param conditions
     *            List of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     */
    @Conditioned
    @Lorsque("Je supprime le fichier {string} dans repertoire des téléchargements(\\?)")
    @Given("I remove {string} file in download directory(\\?)")
    public void removefileInDownloadDirectory(String file, List<GherkinStepCondition> conditions) throws IOException {
        try {
            FileUtils.forceDelete(new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER + File.separator + file));
        } catch (IOException e) {
            LOGGER.warn("IOException in removefileInDownloadDirectory", e);
        }
    }

    /**
     * Checks that a file in the default downloaded files folder matches the given regexp.
     *
     * @param file
     *            The name of the file
     * @param encoding
     *            The file encoding
     * @param regexp
     *            The pattern to match
     * @param conditions
     *            List of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_FILE_NOT_MATCHES} message (with screenshot, no exception)
     */
    @Conditioned
    @Alors("Le fichier {string} encodé en {string} vérifie {string}(\\?)")
    @Then("The file {string} encoded in {string} matches {string}(\\?)")
    public void checkFile(String file, String encoding, String regexp, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            final Matcher m = Pattern.compile(regexp)
                    .matcher(FileUtils.readFileToString(new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER + File.separator + file), encoding));
            if (!m.find()) {
                new Result.Failure<>(file, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_FILE_NOT_MATCHES), file, regexp), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
            }
        } catch (final IOException e) {
            new Result.Failure<>(file, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_FILE_NOT_FOUND), file), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * Waits the full download of a file with a maximum timeout in seconds.
     *
     * @param file
     *            The name of the file to download
     * @param timeout
     *            The maximum timeout
     * @param conditions
     *            List of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws InterruptedException
     *             Exception for the sleep
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_DOWNLOADED_FILE_NOT_FOUND} message (with screenshot, no exception)
     */
    @Conditioned
    @Lorsque("Je patiente que le fichier nommé {string} soit téléchargé avec un timeout de {int} seconde(s)(\\?)")
    @Then("I wait file named {string} to be downloaded with timeout of {int} second(s)(\\?)")
    public void waitDownloadFile(String file, int timeout, List<GherkinStepCondition> conditions) throws InterruptedException, FailureException, TechnicalException {
        File f;
        int nbTry = 0;
        do {
            if (nbTry >= timeout) {
                new Result.Failure<>(file, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_DOWNLOADED_FILE_NOT_FOUND), file), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
            }
            Thread.sleep(1000);
            f = new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER + File.separator + file);
            nbTry++;
        } while (!(f.exists() && !f.isDirectory()));
        LOGGER.debug("File downloaded in {} seconds.", nbTry);
    }

    /**
     * Waits the full download of a file with a maximum timeout in seconds.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param filename
     *            The name of the file to upload (from the default downloaded files directory)
     * @param conditions
     *            List of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UPLOADING_FILE} message (with screenshot, no exception)
     */
    @Conditioned
    @Lorsque("J'utilise l'élément {string} pour uploader le fichier {string}(\\?)")
    @Then("I use {string} element to upload {string} file(\\?)")
    public void uploadFile(String pageElement, String filename, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        uploadFile(Page.getInstance(page).getPageElementByKey('-' + elementName), filename);
    }

}
