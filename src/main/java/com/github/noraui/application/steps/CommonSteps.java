/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
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

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.Page;
import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.browser.DriverFactory;
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
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(CommonSteps.class);

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
    @Lorsque("J'attends '(.*)' secondes[\\.|\\?]")
    @Then("I wait '(.*)' seconds[\\.|\\?]")
    public void wait(int time, List<GherkinStepCondition> conditions) throws InterruptedException {
        Thread.sleep((long) time * 1000);
    }

    /**
     * Waits invisibility of element with timeout of x seconds.
     *
     * @param page
     *            The concerned page of field
     * @param element
     *            is key of PageElement concerned
     * @param time
     *            is custom timeout
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @Lorsque("J'attends l'invisibilité de '(.*)-(.*)' avec un timeout de '(.*)' secondes[\\.|\\?]")
    @Then("I wait invisibility of '(.*)-(.*)' with timeout of '(.*)' seconds[\\.|\\?]")
    public void waitInvisibilityOf(String page, String element, int time, List<GherkinStepCondition> conditions) throws TechnicalException {
        final WebElement we = Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + element));
        Context.waitUntil(ExpectedConditions.invisibilityOf(we), time);
    }

    /**
     * Waits staleness of element with timeout of x seconds.
     *
     * @param page
     *            The concerned page of field
     * @param element
     *            is key of PageElement concerned
     * @param time
     *            is custom timeout
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @Lorsque("J'attends la diparition de '(.*)-(.*)' avec un timeout de '(.*)' secondes[\\.|\\?]")
    @Then("I wait staleness of '(.*)-(.*)' with timeout of '(.*)' seconds[\\.|\\?]")
    public void waitStalenessOf(String page, String element, int time, List<GherkinStepCondition> conditions) throws TechnicalException {
        final WebElement we = Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + element));
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
    @Lorsque("Si '(.*)' vérifie '(.*)', je fais '(.*)' fois:")
    @Then("If '(.*)' matches '(.*)', I do '(.*)' times:")
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
    @Lorsque("Si '(.*)' vérifie '(.*)', je fais jusqu'à '(.*)' respecte '(.*)' avec '(.*)' essais maxi:")
    @Then("If '(.*)' matches '(.*)', I do until '(.*)' respects '(.*)' with '(.*)' max tries:")
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
    @Lorsque("Si '(.*)' vérifie '(.*)', Tant que '(.*)' respecte '(.*)' je fais avec '(.*)' essais maxi:")
    @Then("If '(.*)' matches '(.*)', While '(.*)' respects '(.*)' I do with '(.*)' max tries:")
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
    @Lorsque("Je vérifie que (.*) '(.*)' n'est pas vide[\\.|\\?]")
    @Given("I check that (.*) '(.*)' is not empty[\\.|\\?]")
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
     * @param page
     *            The concerned page of field
     * @param field
     *            Name of the field to save in memory.
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
    @Et("Je sauvegarde la valeur de '(.*)-(.*)'[\\.|\\?]")
    @And("I save the value of '(.*)-(.*)'[\\.|\\?]")
    public void saveElementValue(String page, String field, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        saveElementValue('-' + field, Page.getInstance(page));
    }

    /**
     * Save field in data output provider if all 'expected' parameters equals 'actual' parameters in conditions.
     * The value is saved directly into the data output provider (Excel, CSV, ...).
     *
     * @param page
     *            The concerned page of field
     * @param field
     *            Name of the field to save in data output provider.
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
    @Et("Je sauvegarde la valeur de '(.*)-(.*)' dans la colonne '(.*)' du fournisseur de données en sortie[\\.|\\?]")
    @And("I save the value of '(.*)-(.*)' in '(.*)' column of data output provider[\\.|\\?]")
    public void saveValueInDataOutputProvider(String page, String field, String targetColumn, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String value = "";
        try {
            value = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + field)))).getText();
            if (value == null) {
                value = "";
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, Page.getInstance(page).getCallBack());
        }
        Context.getCurrentScenario().write(Messages.format("Value of %s is: %s\n", field, value));
        for (final Integer line : Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes()) {
            try {
                Context.getDataOutputProvider().writeDataResult(targetColumn, line, value);
            } catch (final TechnicalException e) {
                new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_WRITE_MESSAGE_IN_RESULT_FILE), targetColumn), true,
                        Page.getInstance(page).getCallBack());
            }
        }
    }

    /**
     * Save field in memory if all 'expected' parameters equals 'actual' parameters in conditions.
     * The value is saved directly into the Context targetKey.
     *
     * @param page
     *            The concerned page of field
     * @param field
     *            Name of the field to save in memory.
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
    @Et("Je sauvegarde la valeur de '(.*)-(.*)' dans la clé '(.*)' du contexte[\\.|\\?]")
    @And("I save the value of '(.*)-(.*)' in '(.*)' context key[\\.|\\?]")
    public void saveValue(String page, String field, String targetKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        saveElementValue('-' + field, targetKey, Page.getInstance(page));
    }

    /**
     * Click on html element if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param page
     *            The concerned page of toClick
     * @param toClick
     *            html element.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clique sur '(.*)-(.*)'[\\.|\\?]")
    @When("I click on '(.*)-(.*)'[\\.|\\?]")
    public void clickOn(String page, String toClick, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        logger.debug("{} clickOn: {}", page, toClick);
        clickOn(Page.getInstance(page).getPageElementByKey('-' + toClick));
    }

    /**
     * Click on html element using Javascript if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param page
     *            The concerned page of toClick
     * @param toClick
     *            html element
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clique via js sur '(.*)-(.*)'[\\.|\\?]")
    @When("I click by js on '(.*)-(.*)'[\\.|\\?]")
    public void clickOnByJs(String page, String toClick, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        logger.debug("{} clickOnByJs: {}", page, toClick);
        clickOnByJs(Page.getInstance(page).getPageElementByKey('-' + toClick));
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
    @Quand("Je clique via js sur xpath '(.*)' de '(.*)' page[\\.|\\?]")
    @When("I click by js on xpath '(.*)' from '(.*)' page[\\.|\\?]")
    public void clickOnXpathByJs(String xpath, String page, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        logger.debug("clickOnByJs with xpath {} on {} page", xpath, page);
        clickOnByJs(Page.getInstance(page), xpath);
    }

    /**
     * Click on html element and switch window when the scenario contain more one windows (one more application for example), if all 'expected' parameters equals 'actual' parameters in conditions.
     *
     * @param page
     *            The concerned page of toClick
     * @param toClick
     *            html element
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
    @Quand("Je clique sur '(.*)-(.*)' et passe sur '(.*)' de type fenêtre[\\.|\\?]")
    @When("I click on '(.*)-(.*)' and switch to '(.*)' window[\\.|\\?]")
    public void clickOnAndSwitchWindow(String page, String toClick, String windowKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        final String wKey = Page.getInstance(page).getApplication() + Page.getInstance(windowKey).getPageKey();
        final String handleToSwitch = Context.getWindows().get(wKey);
        if (handleToSwitch != null) {
            Context.getDriver().switchTo().window(handleToSwitch);
            Context.getDriver().manage().window().maximize();
            Context.setMainWindow(windowKey);
        } else {
            try {
                final Set<String> initialWindows = getDriver().getWindowHandles();
                clickOn(Page.getInstance(page).getPageElementByKey('-' + toClick));
                final String newWindowHandle = Context.waitUntil(WindowManager.newWindowOpens(initialWindows));
                Context.addWindow(wKey, newWindowHandle);
                getDriver().switchTo().window(newWindowHandle);
                Context.getDriver().manage().window().maximize();
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
     * Update a html input text with a date.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            is target element
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
    @Quand("Je mets à jour la date '(.*)-(.*)' avec une '(.*)' date '(.*)'[\\.|\\?]")
    @When("I update date '(.*)-(.*)' with a '(.*)' date '(.*)'[\\.|\\?]")
    public void updateDate(String page, String elementName, String dateType, String dateOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        final String date = Context.getValue(dateOrKey) != null ? Context.getValue(dateOrKey) : dateOrKey;
        if (!"".equals(date)) {
            final PageElement pageElement = Page.getInstance(page).getPageElementByKey('-' + elementName);
            if (date.matches(Constants.DATE_FORMAT_REG_EXP)) {
                updateDateValidated(pageElement, dateType, date);
            } else {
                new Result.Failure<>(date, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_WRONG_DATE_FORMAT), date, elementName), false, pageElement.getPage().getCallBack());
            }
        }
    }

    /**
     * Update a html select input text with a text data (if it exists in html "option" list).
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            Is target element
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
    @Quand("Je mets à jour la liste déroulante '(.*)-(.*)' avec '(.*)'[\\.|\\?]")
    @When("I update select list '(.*)-(.*)' with '(.*)'[\\.|\\?]")
    public void updateList(String page, String elementName, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateList(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey);
    }

    /**
     * Update a html input text with a given text.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            Is target element
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
    @Quand("Je mets à jour le texte '(.*)-(.*)' avec '(.*)'[\\.|\\?]")
    @When("I update text '(.*)-(.*)' with '(.*)'[\\.|\\?]")
    public void updateText(String page, String elementName, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateText(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey);
    }

    /**
     * Update a html input text with a given text and then press ENTER key.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to update
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
    @Quand("Je mets à jour le texte '(.*)-(.*)' et entre ENTRER avec '(.*)'[\\.|\\?]")
    @When("I update text '(.*)-(.*)' and type ENTER with '(.*)'[\\.|\\?]")
    public void updateTextAndEnter(String page, String elementName, String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateText(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey, Keys.ENTER);
    }

    /**
     * Checks that mandatory field is no empty with conditions.
     *
     * @param page
     *            The concerned page of fieldName
     * @param fieldName
     *            Name of the field to check (see .ini file)
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
    @Lorsque("Je vérifie le champ obligatoire '(.*)-(.*)' de type '(.*)'[\\.|\\?]")
    @Then("I check mandatory field '(.*)-(.*)' of type '(.*)'[\\.|\\?]")
    public void checkMandatoryField(String page, String fieldName, String type, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        final PageElement pageElement = Page.getInstance(page).getPageElementByKey('-' + fieldName);
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
     * Checks if html input text contains expected value.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to check
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
    @Et("Je vérifie le texte '(.*)-(.*)' avec '(.*)'[\\.|\\?]")
    @And("I check text '(.*)-(.*)' with '(.*)'[\\.|\\?]")
    public void checkInputText(String page, String elementName, String textOrKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        if (!checkInputText(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey)) {
            checkText(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey);
        }
    }

    /**
     * Checks if an html element is visible.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to check
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je vérifie que '(.*)-(.*)' est visible[\\.|\\?]")
    @And("I check that '(.*)-(.*)' is visible[\\.|\\?]")
    public void checkElementVisible(String page, String elementName, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkElementVisible(Page.getInstance(page).getPageElementByKey('-' + elementName), true);
    }

    /**
     * Checks if an html element is not visible.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to check
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je vérifie que '(.*)-(.*)' n'est pas visible[\\.|\\?]")
    @And("I check that '(.*)-(.*)' is not visible[\\.|\\?]")
    public void checkElementNotDisplayed(String page, String elementName, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkElementVisible(Page.getInstance(page).getPageElementByKey('-' + elementName), false);
    }

    /**
     * Checks if an html element is present.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to check
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je vérifie que '(.*)-(.*)' est présent[\\.|\\?]")
    @And("I check that '(.*)-(.*)' is present[\\.|\\?]")
    public void checkElementPresent(String page, String elementName, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkElementPresence(Page.getInstance(page).getPageElementByKey('-' + elementName), true);
    }

    /**
     * Checks if an html element is not present.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to check
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je vérifie que '(.*)-(.*)' n'est pas présent[\\.|\\?]")
    @And("I check that '(.*)-(.*)' is not present[\\.|\\?]")
    public void checkElementNotPresent(String page, String elementName, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
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
    @Lorsque("Je vérifie l'absence d'alerte dans '(.*)'[\\.|\\?]")
    @Then("I check absence of alert in '(.*)'[\\.|\\?]")
    public void checkAlert(String page, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkAlert(Page.getInstance(page));
    }

    /**
     * Checks that a given page displays a html alert with a message.
     * CAUTION: This check do not work with IE: https://github.com/SeleniumHQ/selenium/issues/468
     * CAUTION: This feature is not supported by HtmlUnit web driver
     * CAUTION: This feature is not supported by Mozilla Gecko web driver: https://github.com/mozilla/geckodriver/issues/330
     *
     * @param messageOrKey
     *            Is message (message or message in context (after a save)) displayed on html alert
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     */
    @Et("Je vérifie le message '(.*)' sur l'alerte")
    @And("I check message '(.*)' on alert")
    public void checkAlertInLogs(String messageOrKey) throws TechnicalException, FailureException {
        if (DriverFactory.CHROME.equals(Context.getBrowser()) || DriverFactory.PHANTOM.equals(Context.getBrowser())) {
            final String message = Context.getValue(messageOrKey) != null ? Context.getValue(messageOrKey) : messageOrKey;
            final String msg = getLastConsoleAlertMessage();
            if (msg == null || !msg.equals(message)) {
                new Result.Failure<>(msg, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_NOT_FOUND_ON_ALERT), message), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
            }
        } else {
            Context.getCurrentScenario().write("SKIPPED for " + Context.getBrowser() + " web driver.");
        }
    }

    /**
     * Updates the value of a html radio element with conditions.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            is target element
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
    @Et("Je mets à jour la liste radio '(.*)-(.*)' avec '(.*)'[\\.|\\?]")
    @And("I update radio list '(.*)-(.*)' with '(.*)'[\\.|\\?]")
    public void updateRadioList(String page, String elementName, String valueOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateRadioList(Page.getInstance(page).getPageElementByKey('-' + elementName), valueOrKey);
    }

    /**
     * Updates the value of html radio element with conditions using a map of keys/printed values.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            is target element
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
    @Et("Je mets à jour la liste radio '(.*)-(.*)' avec '(.*)' à partir de ces valeurs:")
    @And("I update radio list '(.*)-(.*)' with '(.*)' from these values:")
    public void updateRadioList(String page, String elementName, String valueKeyOrKey, Map<String, String> printedValues) throws TechnicalException, FailureException {
        updateRadioList(Page.getInstance(page).getPageElementByKey('-' + elementName), valueKeyOrKey, printedValues);
    }

    /**
     * Updates the value of a html checkbox element with conditions.
     *
     * @param page
     *            The concerned page of elementKey
     * @param elementKey
     *            The key of PageElement to select
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
    @Lorsque("Je mets à jour la case à cocher '(.*)-(.*)' avec '(.*)'[\\.|\\?]")
    @Then("I update checkbox '(.*)-(.*)' with '(.*)'[\\.|\\?]")
    public void selectCheckbox(String page, String elementKey, boolean value, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        selectCheckbox(Page.getInstance(page).getPageElementByKey('-' + elementKey), value);
    }

    /**
     * Updates the value of a html checkbox element with conditions regarding the provided keys/values map.
     *
     * @param page
     *            The concerned page of elementKey
     * @param elementKey
     *            The key of PageElement to select
     * @param value
     *            A key to map with 'values' to find the final right checkbox value
     * @param values
     *            A list of keys/values to map a scenario input value with a checkbox value
     * @throws TechnicalException
     *             if the scenario encounters a technical error
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Lorsque("Je mets à jour la case à cocher '(.*)-(.*)' avec '(.*)' à partir de ces valeurs:")
    @Then("I update checkbox '(.*)-(.*)' with '(.*)' from these values:")
    public void selectCheckbox(String page, String elementKey, String value, Map<String, Boolean> values) throws TechnicalException, FailureException {
        selectCheckbox(Page.getInstance(page).getPageElementByKey('-' + elementKey), value, values);
    }

    /**
     * Clears a html element with conditions.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to clear
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             if the scenario encounters a technical error
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clarifie le texte dans '(.*)-(.*)'[\\.|\\?]")
    @When("I clear text in '(.*)-(.*)'[\\.|\\?]")
    public void clearText(String page, String elementName, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        clearText(Page.getInstance(page).getPageElementByKey('-' + elementName));
    }

    /**
     * Switches to the given frame.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement representing a frame to switch to.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SWITCH_FRAME} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je passe au cadre '(.*)'[\\.|\\?]")
    @When("I switch to '(.*)' frame[\\.|\\?]")
    public void switchFrame(String page, String elementName, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        switchFrame(Page.getInstance(page).getPageElementByKey('-' + elementName));
    }

}
