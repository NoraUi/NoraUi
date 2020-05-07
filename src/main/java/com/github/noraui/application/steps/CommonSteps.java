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
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.github.noraui.Constants;
import com.github.noraui.application.page.Page;
import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.browser.WindowManager;
import com.github.noraui.browser.waits.Wait;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.cucumber.metrics.annotation.time.Time;
import com.github.noraui.cucumber.metrics.annotation.time.TimeName;
import com.github.noraui.exception.AssertError;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.exception.WarningException;
import com.github.noraui.gherkin.GherkinConditionedLoopedStep;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;
import com.mifmif.common.regex.Generex;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Lorsque;
import io.cucumber.java.fr.Quand;

@Loggable
public class CommonSteps extends Step {

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
    @Lorsque("Si {string} vérifie {string}, je fais {int} fois:")
    @Then("If {string} matches {string}, I do {int} times:")
    public void loop(String actual, String expected, int times, List<GherkinConditionedLoopedStep> steps) {
        try {
            if (new GherkinStepCondition("loopKey", expected, actual).checkCondition()) {
                Context.setCurrentSubStepIndex(1);
                for (int i = 0; i < times; i++) {
                    runAllStepsInLoop(steps);
                }
                Context.setCurrentSubStepIndex(0);
            }
        } catch (final TechnicalException e) {
            Context.setCurrentSubStepIndex(0);
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
                Context.setCurrentSubStepIndex(1);
                do {
                    i++;
                    runAllStepsInLoop(conditions);
                } while (!Pattern.compile(breakCondition).matcher(Context.getValue(key) == null ? "" : Context.getValue(key)).find() && i < tries);
                Context.setCurrentSubStepIndex(0);
            }
        } catch (final TechnicalException e) {
            Context.setCurrentSubStepIndex(0);
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
                Context.setCurrentSubStepIndex(1);
                while (!Pattern.compile(breakCondition).matcher(Context.getValue(key) == null ? "" : Context.getValue(key)).find() && i < tries) {
                    i++;
                    runAllStepsInLoop(conditions);
                }
                Context.setCurrentSubStepIndex(0);
            }
        } catch (final TechnicalException e) {
            Context.setCurrentSubStepIndex(0);
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
            if ("".equals(element.getValue()) || element.getValue() == null) {
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
     *            The concerned page of field AND name of the field to save in memory. (sample: $demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} or {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE}
     *             message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je sauvegarde la valeur de {page-element}(\\?)")
    @And("I save the value of {page-element}(\\?)")
    public void saveElementValue(PageElement pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        saveElementValue(pageElement);
    }

    /**
     * Save field in memory if all 'expected' parameters equals 'actual' parameters in conditions.
     * The value is saved directly into the Context targetKey.
     *
     * @param pageElement
     *            The concerned page of field AND name of the field to save in memory. (sample: $demo.DemoPage-button)
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
    @Et("Je sauvegarde la valeur de {page-element} dans la clé {string} du contexte(\\?)")
    @And("I save the value of {page-element} in {string} context key(\\?)")
    public void saveValue(PageElement pageElement, String targetKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        saveElementValue(pageElement, targetKey);
    }

    /**
     * Save field in data output provider if all 'expected' parameters equals 'actual' parameters in conditions.
     * The value is saved directly into the data output provider (Excel, CSV, ...).
     *
     * @param pageElement
     *            The concerned page of field AND name of the field to save in data output provider. (sample: $demo.DemoPage-button)
     * @param targetColumn
     *            Target column (in data output provider) to save retrieved value.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error (with message and screenshot)
     * @throws TechnicalException
     *             is thrown if the scenario encounters a technical error (format, configuration, data, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} or {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE}
     * @throws WarningException
     *             if the scenario encounters a functional warning (with message and screenshot)
     */
    @Conditioned
    @Et("Je sauvegarde la valeur de {page-element} dans la colonne {string} du fournisseur de données en sortie(\\?)")
    @And("I save the value of {page-element} in {string} column of data output provider(\\?)")
    public void saveValueInDataOutputProvider(PageElement pageElement, String targetColumn, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException, WarningException {
        String value = "";
        try {
            value = Wait.until(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement))).getText();
            if (value == null) {
                value = "";
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
        Context.getCurrentScenario().write(Messages.format("Value of %s is: %s\n", pageElement, value));
        for (final Integer line : Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes()) {
            try {
                Context.getDataOutputProvider().writeDataResult(targetColumn, line, value);
            } catch (final TechnicalException e) {
                new Result.Warning<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_WRITE_MESSAGE_IN_RESULT_FILE), targetColumn), false, 0);
            }
        }
    }

    /**
     * Click on html element if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clique sur {page-element}(\\?)")
    @When("I click on {page-element}(\\?)")
    public void clickOn(PageElement pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        log.debug("{} clickOn: {}", pageElement.getPage().getPageKey(), pageElement);
        clickOn(pageElement);
    }

    /**
     * Click on html element using Javascript if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clique via js sur {page-element}(\\?)")
    @When("I click by js on {page-element}(\\?)")
    public void clickOnByJs(PageElement pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        log.debug("{} clickOnByJs: {}", pageElement.getPage().getPageKey(), pageElement);
        clickOnByJs(pageElement);
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
    @Quand("Je clique via js sur xpath {string} de {page} page(\\?)")
    @When("I click by js on xpath {string} from {page} page(\\?)")
    public void clickOnXpathByJs(String xpath, Page page, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        log.debug("clickOnByJs with xpath {} on {} page", xpath, page.getPageKey());
        clickOnByJs(page, xpath);
    }

    /**
     * Click on html element and switch window when the scenario contain more one windows (one more application for example), if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Quand("Je clique sur {page-element} et passe sur {string} de type fenêtre(\\?)")
    @When("I click on {page-element} and switch to {string} window(\\?)")
    public void clickOnAndSwitchWindow(PageElement pageElement, String windowKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        final Page windowPage = Page.getInstance(windowKey);
        final String wKey = pageElement.getPage().getApplication() + windowPage.getPageKey();
        final String handleToSwitch = Context.getWindows().get(wKey);
        if (handleToSwitch != null) {
            Context.getDriver().switchTo().window(handleToSwitch);
            // As a workaround: NoraUi specify window size manually, e.g. window_size: 1920 x 1080 (instead of .window().maximize()).
            Context.getDriver().manage().window().setSize(new Dimension(1920, 1080));
            Context.setMainWindow(windowKey);
        } else {
            try {
                final Set<String> initialWindows = getDriver().getWindowHandles();
                clickOn(pageElement);
                final String newWindowHandle = Wait.until(WindowManager.newWindowOpens(initialWindows));
                Context.addWindow(wKey, newWindowHandle);
                getDriver().switchTo().window(newWindowHandle);
                // As a workaround: NoraUi specify window size manually, e.g. window_size: 1920 x 1080 (instead of .window().maximize()).
                Context.getDriver().manage().window().setSize(new Dimension(1920, 1080));
                Context.setMainWindow(newWindowHandle);
            } catch (final Exception e) {
                new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_SWITCH_WINDOW), windowKey), true, pageElement.getPage().getCallBack());
            }
            if (!windowPage.checkPage()) {
                new Result.Failure<>(windowKey, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_SWITCH_WINDOW), windowKey), true, pageElement.getPage().getCallBack());
            }
        }
    }

    /**
     * Simulates the mouse over a html element
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_PASS_OVER_ELEMENT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je passe au dessus de {page-element}(\\?)")
    @When("I pass over {page-element}(\\?)")
    public void passOver(PageElement pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        passOver(pageElement);
    }

    /**
     * Update a html input text with a date.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Quand("Je mets à jour la date {page-element} avec une {string} date {string}(\\?)")
    @When("I update date {page-element} with a {string} date {string}(\\?)")
    public void updateDate(PageElement pageElement, String dateType, String dateOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        final String date = Context.getValue(dateOrKey) != null ? Context.getValue(dateOrKey) : dateOrKey;
        if (!"".equals(date)) {
            if (date.matches(Constants.DATE_FORMAT_REG_EXP)) {
                updateDateValidated(pageElement, dateType, date);
            } else {
                new Result.Failure<>(date, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_WRONG_DATE_FORMAT), date, pageElement), false, pageElement.getPage().getCallBack());
            }
        }
    }

    /**
     * Update a html select input text with a text data (if it exists in html "option" list).
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Quand("Je mets à jour la liste déroulante {page-element} avec {string}(\\?)")
    @When("I update select list {page-element} with {string}(\\?)")
    public void updateList(PageElement pageElement, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateList(pageElement, textOrKey);
    }

    /**
     * Update a html input text with a given text.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Quand("Je mets à jour le texte {page-element} avec {string}(\\?)")
    @When("I update text {page-element} with {string}(\\?)")
    public void updateText(PageElement pageElement, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateText(pageElement, textOrKey);
    }

    @Conditioned
    @Quand("Je remplace le texte {page-element} avec {string}(\\?)")
    @When("I set text {page-element} with {string}(\\?)")
    public void setText(PageElement pageElement, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        setText(pageElement, textOrKey);
    }

    @Conditioned
    @Quand("Je remplace le texte {string} avec {string}(\\?)")
    @When("I set text {string} with {string}(\\?)")
    public void setText(String pageElement, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        setText(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey);
    }

    /**
     * Update a html input text with a random text.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Quand("Je mets à jour le texte {page-element} avec une valeur aléatoire qui vérifie {string}(\\?)")
    @When("I update text {page-element} with ramdom match {string}(\\?)")
    public void updateTextWithRamdomValueMatchRegexp(PageElement pageElement, String randRegex, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateText(pageElement, new Generex(randRegex).random());
    }

    /**
     * Update a html input text with a given text and then press org.openqa.selenium.Keys (ENTER, ESCAPE, ...).
     * 
     * @since 4.2.4
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param key
     *            contain org.openqa.selenium.Keys element.
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
    @Quand("Je mets à jour le texte {page-element} et entre {keyboard-key} avec {string}(\\?)")
    @When("I update text {page-element} and type {keyboard-key} with {string}(\\?)")
    public void updateTextAndSeleniumKeys(PageElement pageElement, Keys key, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateText(pageElement, textOrKey, key);
    }

    /**
     * Checks that mandatory field is no empty with conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Lorsque("Je vérifie le champ obligatoire {page-element} de type {string}(\\?)")
    @Then("I check mandatory field {page-element} of type {string}(\\?)")
    public void checkMandatoryField(PageElement pageElement, String type, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        if ("text".equals(type)) {
            if (!checkMandatoryTextField(pageElement)) {
                new Result.Failure<>(pageElement, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_EMPTY_MANDATORY_FIELD), pageElement, pageElement.getPage().getApplication()), true,
                        pageElement.getPage().getCallBack());
            }
        } else {
            new Result.Failure<>(type, Messages.format(Messages.getMessage(Messages.SCENARIO_ERROR_MESSAGE_TYPE_NOT_IMPLEMENTED), type, "checkMandatoryField"), false,
                    pageElement.getPage().getCallBack());
        }

    }

    /**
     * Checks if html input text contains expected value. If your value from Ajax, use "I expect to have {string} with the text {string}".
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Et("Je vérifie le texte {page-element} avec {string}(\\?)")
    @And("I check text {page-element} with {string}(\\?)")
    public void checkInputText(PageElement pageElement, String textOrKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        if (!checkInputText(pageElement, textOrKey)) {
            checkText(pageElement, textOrKey);
        }
    }

    /**
     * Checks if radio list expected value with conditions.
     * 
     * @since 4.2.5
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $bakery.DemoPage-rate)
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
    @Et("Je vérifie la liste radio {page-element} avec {string}(\\?)")
    @And("I check radio list {page-element} with {string}(\\?)")
    public void checkRadioList(PageElement pageElement, String valueOrKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkRadioList(pageElement, valueOrKey);
    }

    /**
     * Checks if radio list expected value using a map of keys/printed values.
     * 
     * @since 4.2.5
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Et("Je vérifie la liste radio {page-element} avec {string} à partir de ces valeurs:")
    @And("I check radio list {page-element} with {string} from these values:")
    public void checkRadioList(PageElement pageElement, String valueKeyOrKey, Map<String, String> printedValues) throws TechnicalException, FailureException {
        super.checkRadioList(pageElement, valueKeyOrKey, printedValues);
    }

    /**
     * @deprecated As of release 4.1, replaced by {@link com.github.noraui.application.steps.WaitSteps#waitVisibilityOf(Page.PageElement pageElement, Boolean not, List)}
     *             "I wait visibility of {string}(\\?)"
     *             Checks if an html element is visible.
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Deprecated
    @Conditioned
    @Et("Je vérifie que {page-element} est visible(\\?)")
    @And("I check that {page-element} is visible(\\?)")
    public void checkElementVisible(PageElement pageElement, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkElementVisible(pageElement, true);
    }

    /**
     * @deprecated As of release 4.1, replaced by {@link com.github.noraui.application.steps.WaitSteps#waitInvisibilityOf(Page.PageElement, Boolean, List)}
     *             "I wait invisibility of {string}(\\?)"
     *             Checks if an html element is not visible.
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Deprecated
    @Conditioned
    @Et("Je vérifie que {page-element} n'est pas visible(\\?)")
    @And("I check that {page-element} is not visible(\\?)")
    public void checkElementNotDisplayed(PageElement pageElement, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkElementVisible(pageElement, false);
    }

    /**
     * @deprecated As of release 4.1, replaced by {@link com.github.noraui.application.steps.WaitSteps#waitPresenceOfElementLocated(Page.PageElement, Boolean, List)}
     *             "The element {page-element} {should-shouldnot} be present(\\?)"
     *             Checks if an html element is present.
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Deprecated
    @Conditioned
    @Et("Je vérifie que {page-element} est présent(\\?)")
    @And("I check that {page-element} is present(\\?)")
    public void checkElementPresent(PageElement pageElement, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkElementPresence(pageElement, true);
    }

    /**
     * @deprecated As of release 4.1, replaced by {@link com.github.noraui.application.steps.WaitSteps#waitPresenceOfElementLocated(Page.PageElement, Boolean, List)}
     *             "The element {page-element} {should-shouldnot} be present(\\?)"
     *             Checks if an html element is not present.
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Deprecated
    @Conditioned
    @Et("Je vérifie que {page-element} n'est pas présent(\\?)")
    @And("I check that {page-element} is not present(\\?)")
    public void checkElementNotPresent(PageElement pageElement, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkElementPresence(pageElement, false);
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
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Et("Je mets à jour la liste radio {page-element} avec {string}(\\?)")
    @And("I update radio list {page-element} with {string}(\\?)")
    public void updateRadioList(PageElement pageElement, String valueOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateRadioList(pageElement, valueOrKey);
    }

    /**
     * Updates the value of html radio element with conditions using a map of keys/printed values.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Et("Je mets à jour la liste radio {page-element} avec {string} à partir de ces valeurs:")
    @And("I update radio list {page-element} with {string} from these values:")
    public void updateRadioList(PageElement pageElement, String valueKeyOrKey, Map<String, String> printedValues) throws TechnicalException, FailureException {
        super.updateRadioList(pageElement, valueKeyOrKey, printedValues);
    }

    /**
     * Updates the value of a html checkbox element with conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
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
    @Lorsque("Je mets à jour la case à cocher {page-element} avec {string}(\\?)")
    @Then("I update checkbox {page-element} with {string}(\\?)")
    public void selectCheckbox(PageElement pageElement, String value, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        selectCheckbox(pageElement, Boolean.parseBoolean(value));
    }

    /**
     * Updates the value of a html checkbox element with conditions regarding the provided keys/values map.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param value
     *            A key to map with 'values' to find the final right checkbox value
     * @param values
     *            A list of keys/values to map a scenario input value with a checkbox value
     * @throws TechnicalException
     *             if the scenario encounters a technical error
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Lorsque("Je mets à jour la case à cocher {page-element} avec {string} à partir de ces valeurs:")
    @Then("I update checkbox {page-element} with {string} from these values:")
    public void selectCheckbox(PageElement pageElement, String value, Map<String, Boolean> values) throws TechnicalException, FailureException {
        super.selectCheckbox(pageElement, value, values);
    }

    /**
     * Clears a html element with conditions.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             if the scenario encounters a technical error
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clarifie le texte dans {page-element}(\\?)")
    @When("I clear text in {page-element}(\\?)")
    public void clearText(PageElement pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        clearText(pageElement);
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
    @Quand("Je passe au cadre {page-element}(\\?)")
    @When("I switch to {page-element} frame(\\?)")
    public void switchFrame(PageElement pageElement, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        switchFrame(pageElement);
    }

}
