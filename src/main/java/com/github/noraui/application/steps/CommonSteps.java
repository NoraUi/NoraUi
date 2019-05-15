/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.Page;
import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.browser.WindowManager;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.exception.AssertError;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinConditionedLoopedStep;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.utils.Constants;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;
import com.mifmif.common.regex.Generex;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.fr.Et;
import cucumber.api.java.fr.Lorsque;
import cucumber.api.java.fr.Quand;
import cucumber.metrics.annotation.time.Time;
import cucumber.metrics.annotation.time.TimeName;

public class CommonSteps extends Step {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonSteps.class);

    
    /**
     * Waits a time in second.
     *
     * @param time
     *            is time to wait
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws InterruptedException
     *             Exception for the sleep
     */
    @Conditioned
    @Lorsque("Je patiente {int} seconde(s)(\\?)")
    @Then("I wait {int} second(s)(\\?)")
    public void wait(int time, List<GherkinStepCondition> conditions) throws InterruptedException {
        Thread.sleep((long) time * 1000);
    }

    /**
     * Waits invisibility of element with timeout of x seconds.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param time
     *            is custom timeout
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @Lorsque("Je patiente l'invisibilité de {string} avec un timeout de {int} secondes(\\?)")
    @Then("I wait invisibility of {string} with timeout of {int} seconds(\\?)")
    public void waitInvisibilityOf(String pageElement, int time, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        final WebElement we = Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName));
        Context.waitUntil(ExpectedConditions.invisibilityOf(we), time);
    }

    /**
     * Waits staleness of element with timeout of x seconds.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param time
     *            is custom timeout
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @Lorsque("Je patiente la disparition de {string} avec un timeout de {int} seconde(s)(\\?)")
    @Then("I wait staleness of {string} with timeout of {int} second(s)(\\?)")
    public void waitStalenessOf(String pageElement, int time, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        final WebElement we = Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName));
        Context.waitUntil(ExpectedConditions.stalenessOf(we), time);
    }

    /**
     * Loops on steps execution for a specific number of times.
     *
     * @param actual
     *            actual value for global condition.
     * @param expected
     *            expected value for global condition.
     * @param times
     *            Number of loops.
     * @param steps
     *            List of steps run in a loop.
     */
    @Lorsque("Si {string} vérifie {string}, je fais {string} fois:")
    @Then("If {string} matches {string}, I do {string} times:")
    public void loop(String actual, String expected, int times, List<GherkinConditionedLoopedStep> steps) {
        try {
            if (new GherkinStepCondition("loopKey", expected, actual).checkCondition()) {
                for (int i = 0; i < times; i++) {
                    runAllStepsInLoop(steps);
                }
            }
        } catch (final TechnicalException e) {
            throw new AssertError(Messages.getMessage(TechnicalException.TECHNICAL_SUBSTEP_ERROR_MESSAGE) + e.getMessage());
        }
    }

    /**
     * Does steps execution until a given condition is unverified.
     *
     * @param actual
     *            actual value for global condition.
     * @param expected
     *            expected value for global condition.
     * @param key
     *            key of 'expected' values ('actual' values)
     * @param breakCondition
     *            'expected' values
     * @param tries
     *            number of max tries (no infinity loop).
     * @param conditions
     *            list of steps run in a loop.
     */
    @Lorsque("Si {string} vérifie {string}, je fais jusqu'à {string} respecte {string} avec {int} essais maxi:")
    @Then("If {string} matches {string}, I do until {string} respects {string} with {int} max tries:")
    public void doUntil(String actual, String expected, String key, String breakCondition, int tries, List<GherkinConditionedLoopedStep> conditions) {
        try {
            if (new GherkinStepCondition("doUntilKey", expected, actual).checkCondition()) {
                int i = 0;
                do {
                    i++;
                    runAllStepsInLoop(conditions);
                } while (!Pattern.compile(breakCondition).matcher(Context.getValue(key) == null ? "" : Context.getValue(key)).find() && i <= tries);
            }
        } catch (final TechnicalException e) {
            throw new AssertError(Messages.getMessage(TechnicalException.TECHNICAL_SUBSTEP_ERROR_MESSAGE) + e.getMessage());
        }
    }

    /**
     * While a given condition is verified, does steps execution.
     *
     * @param actual
     *            actual value for global condition.
     * @param expected
     *            expected value for global condition.
     * @param key
     *            key of 'expected' values ('actual' values)
     * @param breakCondition
     *            'expected' values
     * @param tries
     *            number of max tries (no infinity loop).
     * @param conditions
     *            list of steps run in a loop.
     */
    @Lorsque("Si {string} vérifie {string}, Tant que {string} respecte {string} je fais avec {int} essais maxi:")
    @Then("If {string} matches {string}, While {string} respects {string} I do with {int} max tries:")
    public void whileDo(String actual, String expected, String key, String breakCondition, int tries, List<GherkinConditionedLoopedStep> conditions) {
        try {
            if (new GherkinStepCondition("whileDoKey", expected, actual).checkCondition()) {
                int i = 0;
                while (!Pattern.compile(breakCondition).matcher(Context.getValue(key) == null ? "" : Context.getValue(key)).find() && i <= tries) {
                    i++;
                    runAllStepsInLoop(conditions);
                }
            }
        } catch (final TechnicalException e) {
            throw new AssertError(Messages.getMessage(TechnicalException.TECHNICAL_SUBSTEP_ERROR_MESSAGE) + e.getMessage());
        }
    }

    /**
     * Generic method to check that the value given in parameters is not empty.
     * A first research is made from {@link com.github.noraui.gherkin.ScenarioRegistry} to retrieve value if a key is provided.
     *
     * @param data
     *            Name of the value to check
     * @param textOrKey
     *            Is the new data (text or text in context (after a save))
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_EMPTY_DATA} message (no screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Time(name = "{textOrKey}")
    @Lorsque("Je vérifie que {string} {string} n'est pas vide(\\?)")
    @Given("I check that {string} {string} is not empty(\\?)")
    public void checkNotEmpty(String data, @TimeName("textOrKey") String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        if (!"".equals(data)) {
            final String value = Context.getValue(textOrKey) != null ? Context.getValue(textOrKey) : textOrKey;
            if ("".equals(value)) {
                new Result.Failure<>(textOrKey, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_EMPTY_DATA), data), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
            }
        }
    }

    /**
     * Check all mandatory fields.
     *
     * @param mandatoryFields
     *            is a Map of mandatory fields
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_EMPTY_DATA} message (no screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Lorsque("Je vérifie les champs obligatoires:")
    @Given("I check mandatory fields:")
    public void checkMandatoryFields(Map<String, String> mandatoryFields) throws TechnicalException, FailureException {
        final List<String> errors = new ArrayList<>();
        for (final Entry<String, String> element : mandatoryFields.entrySet()) {
            if ("".equals(element.getValue())) {
                errors.add(element.getKey());
            }
        }
        if (!errors.isEmpty()) {
            final StringBuilder errorMessage = new StringBuilder();
            int index = errorMessage.length();
            for (int j = 0; j < errors.size(); j++) {
                errorMessage.append(Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_EMPTY_DATA), errors.get(j)));
                if (j == 0) {
                    errorMessage.setCharAt(index, Character.toUpperCase(errorMessage.charAt(index)));
                } else {
                    errorMessage.setCharAt(index, Character.toLowerCase(errorMessage.charAt(index)));
                }
                if ('.' == errorMessage.charAt(errorMessage.length() - 1)) {
                    errorMessage.deleteCharAt(errorMessage.length() - 1);
                }
                errorMessage.append(", ");
                index = errorMessage.length();
            }
            errorMessage.delete(errorMessage.length() - 2, errorMessage.length());
            errorMessage.append('.');
            new Result.Failure<>(mandatoryFields, errorMessage.toString(), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * Save field in memory if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param pageElement
     *            The concerned page of field AND name of the field to save in memory. (sample: demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} or {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE}
     *             message (with screenshot, with
     *             exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je sauvegarde la valeur de {string}(\\?)")
    @And("I save the value of {string}(\\?)")
    public void saveElementValue(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        saveElementValue('-' + elementName, Page.getInstance(page));
    }

    /**
     * Save field in data output provider if all 'expected' parameters equals 'actual' parameters in conditions.
     * The value is saved directly into the data output provider (Excel, CSV, ...).
     *
     * @param pageElement
     *            The concerned page of field AND name of the field to save in data output provider. (sample: demo.DemoPage-button)
     * @param targetColumn
     *            Target column (in data output provider) to save retrieved value.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error (with message and screenshot)
     * @throws TechnicalException
     *             is thrown if the scenario encounters a technical error (format, configuration, data, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} or {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE}
     */
    @Conditioned
    @Et("Je sauvegarde la valeur de {string} dans la colonne {string} du fournisseur de données en sortie(\\?)")
    @And("I save the value of {string} in {string} column of data output provider(\\?)")
    public void saveValueInDataOutputProvider(String pageElement, String targetColumn, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        String value = "";
        try {
            value = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)))).getText();
            if (value == null) {
                value = "";
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, Page.getInstance(page).getCallBack());
        }
        Context.getCurrentScenario().write(Messages.format("Value of %s is: %s\n", elementName, value));
        for (final Integer line : Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes()) {
            try {
                Context.getDataOutputProvider().writeDataResult(targetColumn, line, value);
            } catch (final TechnicalException e) {
                new Result.Warning<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_WRITE_MESSAGE_IN_RESULT_FILE), targetColumn), false, 0);
            }
        }
    }

    /**
     * Save field in memory if all 'expected' parameters equals 'actual' parameters in conditions.
     * The value is saved directly into the Context targetKey.
     *
     * @param pageElement
     *            The concerned page of field AND name of the field to save in memory. (sample: demo.DemoPage-button)
     * @param targetKey
     *            Target key to save retrieved value.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error (with message and screenshot)
     * @throws TechnicalException
     *             is thrown if the scenario encounters a technical error (format, configuration, data, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} or {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE}
     */
    @Conditioned
    @Et("Je sauvegarde la valeur de {string} dans la clé {string} du contexte(\\?)")
    @And("I save the value of {string} in {string} context key(\\?)")
    public void saveValue(String pageElement, String targetKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        saveElementValue('-' + elementName, targetKey, Page.getInstance(page));
    }

    /**
     * Click on html element if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clique sur {string}(\\?)")
    @When("I click on {string}(\\?)")
    public void clickOn(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        LOGGER.debug("{} clickOn: {}", page, elementName);
        clickOn(Page.getInstance(page).getPageElementByKey('-' + elementName));
    }

    /**
     * Click on html element using Javascript if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clique via js sur {string}(\\?)")
    @When("I click by js on {string}(\\?)")
    public void clickOnByJs(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        LOGGER.debug("{} clickOnByJs: {}", page, elementName);
        clickOnByJs(Page.getInstance(page).getPageElementByKey('-' + elementName));
    }

    /**
     * Click on html element using Javascript if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param xpath
     *            xpath of html element
     * @param page
     *            The concerned page of toClick
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clique via js sur xpath {string} de {string} page(\\?)")
    @When("I click by js on xpath {string} from {string} page(\\?)")
    public void clickOnXpathByJs(String xpath, String page, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        LOGGER.debug("clickOnByJs with xpath {} on {} page", xpath, page);
        clickOnByJs(Page.getInstance(page), xpath);
    }

    /**
     * Click on html element and switch window when the scenario contain more one windows (one more application for example), if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param windowKey
     *            the key of window (popup, ...) Example: 'demo.Popup1DemoPage'.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @Quand("Je clique sur {string} et passe sur {string} de type fenêtre(\\?)")
    @When("I click on {string} and switch to {string} window(\\?)")
    public void clickOnAndSwitchWindow(String pageElement, String windowKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        final String wKey = Page.getInstance(page).getApplication() + Page.getInstance(windowKey).getPageKey();
        final String handleToSwitch = Context.getWindows().get(wKey);
        if (handleToSwitch != null) {
            Context.getDriver().switchTo().window(handleToSwitch);
            // As a workaround: NoraUi specify window size manually, e.g. window_size: 1920 x 1080 (instead of .window().maximize()).
            Context.getDriver().manage().window().setSize(new Dimension(1920, 1080));
            Context.setMainWindow(windowKey);
        } else {
            try {
                final Set<String> initialWindows = getDriver().getWindowHandles();
                clickOn(Page.getInstance(page).getPageElementByKey('-' + elementName));
                final String newWindowHandle = Context.waitUntil(WindowManager.newWindowOpens(initialWindows));
                Context.addWindow(wKey, newWindowHandle);
                getDriver().switchTo().window(newWindowHandle);
                // As a workaround: NoraUi specify window size manually, e.g. window_size: 1920 x 1080 (instead of .window().maximize()).
                Context.getDriver().manage().window().setSize(new Dimension(1920, 1080));
                Context.setMainWindow(newWindowHandle);
            } catch (final Exception e) {
                new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_SWITCH_WINDOW), windowKey), true, Page.getInstance(page).getCallBack());
            }
            if (!Page.getInstance(windowKey).checkPage()) {
                new Result.Failure<>(windowKey, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_SWITCH_WINDOW), windowKey), true, Page.getInstance(page).getCallBack());
            }
        }
    }

    /**
     * Simulates the mouse over a html element
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_PASS_OVER_ELEMENT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je passe au dessus de {string}(\\?)")
    @When("I pass over {string}(\\?)")
    public void passOver(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        passOver(Page.getInstance(page).getPageElementByKey('-' + elementName));
    }

    /**
     * Update a html input text with a date.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param dateOrKey
     *            Is the new date (date or date in context (after a save))
     * @param dateType
     *            'future', 'future_strict', 'today' or 'any'
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with a message (no screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je mets à jour la date {string} avec une {string} date {string}(\\?)")
    @When("I update date {string} with a {string} date {string}(\\?)")
    public void updateDate(String pageElement, String dateType, String dateOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        final String date = Context.getValue(dateOrKey) != null ? Context.getValue(dateOrKey) : dateOrKey;
        if (!"".equals(date)) {
            final PageElement pe = Page.getInstance(page).getPageElementByKey('-' + elementName);
            if (date.matches(Constants.DATE_FORMAT_REG_EXP)) {
                updateDateValidated(pe, dateType, date);
            } else {
                new Result.Failure<>(date, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_WRONG_DATE_FORMAT), date, elementName), false, pe.getPage().getCallBack());
            }
        }
    }

    /**
     * Update a html select input text with a text data (if it exists in html "option" list).
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param textOrKey
     *            Is the new data (text or text in context (after a save))
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je mets à jour la liste déroulante {string} avec {string}(\\?)")
    @When("I update select list {string} with {string}(\\?)")
    public void updateList(String pageElement, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        updateList(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey);
    }

    /**
     * Update a html input text with a given text.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param textOrKey
     *            Is the new data (text or text in context (after a save))
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je mets à jour le texte {string} avec {string}(\\?)")
    @When("I update text {string} with {string}(\\?)")
    public void updateText(String pageElement, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        updateText(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey);
    }

    /**
     * Update a html input text with a random text.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param randRegex
     *            Is the new data (random value generated and match with randRegex)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je mets à jour le texte {string} avec une valeur aléatoire qui vérifie {string}(\\?)")
    @When("I update text {string} with ramdom match {string}(\\?)")
    public void updateTextWithRamdomValueMatchRegexp(String pageElement, String randRegex, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        updateText(Page.getInstance(page).getPageElementByKey('-' + elementName), new Generex(randRegex).random());
    }

    /**
     * Update a html input text with a given text and then press ENTER key.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param textOrKey
     *            Is the new data (text or text in context (after a save))
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             if the scenario encounters a technical error
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je mets à jour le texte {string} et entre ENTRER avec {string}(\\?)")
    @When("I update text {string} and type ENTER with {string}(\\?)")
    public void updateTextAndEnter(String pageElement, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        updateText(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey, Keys.ENTER);
    }

    /**
     * Checks that mandatory field is no empty with conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param type
     *            Type of the field ('text', 'select'...). Only 'text' is implemented for the moment!
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Lorsque("Je vérifie le champ obligatoire {string} de type {string}(\\?)")
    @Then("I check mandatory field {string} of type {string}(\\?)")
    public void checkMandatoryField(String pageElement, String type, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        final PageElement pe = Page.getInstance(page).getPageElementByKey('-' + elementName);
        if ("text".equals(type)) {
            if (!checkMandatoryTextField(pe)) {
                new Result.Failure<>(pe, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_EMPTY_MANDATORY_FIELD), pe, pe.getPage().getApplication()), true,
                        pe.getPage().getCallBack());
            }
        } else {
            new Result.Failure<>(type, Messages.format(Messages.getMessage(Messages.SCENARIO_ERROR_MESSAGE_TYPE_NOT_IMPLEMENTED), type, "checkMandatoryField"), false,
                    pe.getPage().getCallBack());
        }

    }

    /**
     * Checks if html input text contains expected value.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param textOrKey
     *            Is the new data (text or text in context (after a save))
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je vérifie le texte {string} avec {string}(\\?)")
    @And("I check text {string} with {string}(\\?)")
    public void checkInputText(String pageElement, String textOrKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        if (!checkInputText(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey)) {
            checkText(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey);
        }
    }

    /**
     * Checks if an html element is visible.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je vérifie que {string} est visible(\\?)")
    @And("I check that {string} is visible(\\?)")
    public void checkElementVisible(String pageElement, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        checkElementVisible(Page.getInstance(page).getPageElementByKey('-' + elementName), true);
    }

    /**
     * Checks if an html element is not visible.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je vérifie que {string} n'est pas visible(\\?)")
    @And("I check that {string} is not visible(\\?)")
    public void checkElementNotDisplayed(String pageElement, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        checkElementVisible(Page.getInstance(page).getPageElementByKey('-' + elementName), false);
    }

    /**
     * Checks if an html element is present.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je vérifie que {string} est présent(\\?)")
    @And("I check that {string} is present(\\?)")
    public void checkElementPresent(String pageElement, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        checkElementPresence(Page.getInstance(page).getPageElementByKey('-' + elementName), true);
    }

    /**
     * Checks if an html element is not present.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je vérifie que {string} n'est pas présent(\\?)")
    @And("I check that {string} is not present(\\?)")
    public void checkElementNotPresent(String pageElement, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        checkElementPresence(Page.getInstance(page).getPageElementByKey('-' + elementName), false);
    }

    /**
     * Checks that a given page displays a html alert.
     * This check do not work with IE: https://github.com/SeleniumHQ/selenium/issues/468
     *
     * @param page
     *            The concerned page
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     */
    @Conditioned
    @Lorsque("Je vérifie l'absence d'alerte dans {string}(\\?)")
    @Then("I check absence of alert in {string}(\\?)")
    public void checkAlert(String page, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkAlert(Page.getInstance(page));
    }

    /**
     * Checks that a given page displays a html alert with a message.
     *
     * @param messageOrKey
     *            Is message (message or message in context (after a save)) displayed on html alert
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     */
    @Et("Je vérifie le message {string} sur l'alerte")
    @And("I check message {string} on alert")
    public void checkAlert(String messageOrKey) throws TechnicalException, FailureException {
        final String message = Context.getValue(messageOrKey) != null ? Context.getValue(messageOrKey) : messageOrKey;
        final String msg = getAlertMessage();
        if (msg == null || !msg.equals(message)) {
            new Result.Failure<>(msg, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_NOT_FOUND_ON_ALERT), message), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * Updates the value of a html radio element with conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param valueOrKey
     *            Is the value (value or value in context (after a save)) use for selection
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je mets à jour la liste radio {string} avec {string}(\\?)")
    @And("I update radio list {string} with {string}(\\?)")
    public void updateRadioList(String pageElement, String valueOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        updateRadioList(Page.getInstance(page).getPageElementByKey('-' + elementName), valueOrKey);
    }

    /**
     * Updates the value of html radio element with conditions using a map of keys/printed values.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param valueKeyOrKey
     *            Is valueKey (valueKey or input in context (after a save))
     * @param printedValues
     *            is the display value
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Et("Je mets à jour la liste radio {string} avec {string} à partir de ces valeurs:")
    @And("I update radio list {string} with {string} from these values:")
    public void updateRadioList(String pageElement, String valueKeyOrKey, Map<String, String> printedValues) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        updateRadioList(Page.getInstance(page).getPageElementByKey('-' + elementName), valueKeyOrKey, printedValues);
    }

    /**
     * Updates the value of a html checkbox element with conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param value
     *            To check or not ?
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             if the scenario encounters a technical error
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Lorsque("Je mets à jour la case à cocher {string} avec {string}(\\?)")
    @Then("I update checkbox {string} with {string}(\\?)")
    public void selectCheckbox(String pageElement, boolean value, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        selectCheckbox(Page.getInstance(page).getPageElementByKey('-' + elementName), value);
    }

    /**
     * Updates the value of a html checkbox element with conditions regarding the provided keys/values map.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param value
     *            A key to map with 'values' to find the final right checkbox value
     * @param values
     *            A list of keys/values to map a scenario input value with a checkbox value
     * @throws TechnicalException
     *             if the scenario encounters a technical error
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Lorsque("Je mets à jour la case à cocher {string} avec {string} à partir de ces valeurs:")
    @Then("I update checkbox {string} with {string} from these values:")
    public void selectCheckbox(String pageElement, String value, Map<String, Boolean> values) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        selectCheckbox(Page.getInstance(page).getPageElementByKey('-' + elementName), value, values);
    }

    /**
     * Clears a html element with conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             if the scenario encounters a technical error
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clarifie le texte dans {string}(\\?)")
    @When("I clear text in {string}(\\?)")
    public void clearText(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        clearText(Page.getInstance(page).getPageElementByKey('-' + elementName));
    }

    /**
     * Switches to the given frame.
     *
     * @param pageElement
     *            The concerned page of field AND key of the PageElement representing a frame to switch to.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SWITCH_FRAME} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je passe au cadre {string}(\\?)")
    @When("I switch to {string} frame(\\?)")
    public void switchFrame(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        switchFrame(Page.getInstance(page).getPageElementByKey('-' + elementName));
    }

}
