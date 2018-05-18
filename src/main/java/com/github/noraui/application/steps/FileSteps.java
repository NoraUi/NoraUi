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
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(CommonSteps.class);

    /**
     * Empties the default downloaded files folder.
     *
     * @param conditions
     *            List of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws IOException
     */
    @Conditioned
    @Lorsque("Je vide le repertoire des téléchargements[\\.|\\?]")
    @Given("I clean download directory[\\.|\\?]")
    public void cleanDownloadDirectory(List<GherkinStepCondition> conditions) throws IOException {
        FileUtils.forceMkdir(new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER));
        FileUtils.cleanDirectory(new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER));
    }

    /**
     * Remove a file in the default downloaded files folder
     * 
     * @param file
     *            The name of the file removed.
     * @param conditions
     *            List of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws IOException
     */
    @Conditioned
    @Lorsque("Je supprime le fichier '(.*)' dans repertoire des téléchargements[\\.|\\?]")
    @Given("I remove '(.*)' file in download directory[\\.|\\?]")
    public void removefileInDownloadDirectory(String file, List<GherkinStepCondition> conditions) throws IOException {
        FileUtils.forceDelete(new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER + File.separator + file));
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
     * @throws TechnicalException
     * @throws FailureException
     */
    @Conditioned
    @Alors("Le fichier '(.*)' encodé en '(.*)' vérifie '(.*)'[\\.|\\?]")
    @Then("The file '(.*)' encoded in '(.*)' matches '(.*)'[\\.|\\?]")
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
     * @throws TechnicalException
     * @throws FailureException
     */
    @Conditioned
    @Lorsque("Je patiente que le fichier nommé '(.*)' soit téléchargé avec un timeout de '(.*)' secondes[\\.|\\?]")
    @Then("I wait file named '(.*)' to be downloaded with timeout of '(.*)' seconds[\\.|\\?]")
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
        logger.debug("File downloaded in {} seconds.", nbTry);
    }

    /**
     * Waits the full download of a file with a maximum timeout in seconds.
     *
     * @param page
     *            The page to upload the file from
     * @param element
     *            The file input field
     * @param filename
     *            The name of the file to upload (from the default downloaded files directory)
     * @param conditions
     *            List of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     * @throws FailureException
     */
    @Conditioned
    @Lorsque("J'utilise l'élément '(.*)-(.*)' pour uploader le fichier '(.*)'[\\.|\\?]")
    @Then("I use '(.*)-(.*)' element to upload '(.*)' file[\\.|\\?]")
    public void uploadFile(String page, String element, String filename, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        uploadFile(Page.getInstance(page).getPageElementByKey('-' + element), filename);
    }

}
