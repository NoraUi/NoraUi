package noraui.application.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.fr.Et;
import cucumber.api.java.fr.Lorsque;
import cucumber.api.java.fr.Quand;
import cucumber.metrics.annotation.time.Time;
import cucumber.metrics.annotation.time.TimeName;
import noraui.application.page.Page;
import noraui.application.page.Page.PageElement;
import noraui.cucumber.annotation.Conditioned;
import noraui.exception.AssertError;
import noraui.exception.Callbacks;
import noraui.exception.FailureException;
import noraui.exception.Result;
import noraui.exception.TechnicalException;
import noraui.gherkin.GherkinConditionedLoopedStep;
import noraui.gherkin.GherkinStepCondition;
import noraui.utils.Constants;
import noraui.utils.Context;
import noraui.utils.Messages;
import noraui.utils.Utilities;

public class CommonSteps extends Step {

    /**
     * Waits a time in second.
     *
     * @param time
     *            is time to wait
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
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
     * Loop on steps execution for a specific number of times.
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
    @Lorsque("Si '(.*)' matche '(.*)', je fais '(.*)' fois:")
    @Then("If '(.*)' matches '(.*)', I do '(.*)' times:")
    public void loop(String actual, String expected, int times, List<GherkinConditionedLoopedStep> steps) {
        try {
            if (new GherkinStepCondition("loopKey", expected, actual).checkCondition()) {
                for (int i = 0; i < times; i++) {
                    runAllStepsInLoop(steps);
                }
            }
        } catch (TechnicalException e) {
            throw new AssertError(TechnicalException.TECHNICAL_SUBSTEP_ERROR_MESSAGE + e.getMessage());
        }
    }

    /**
     * Do steps execution until a given condition is unverified.
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
    @Lorsque("Si '(.*)' matche '(.*)', je fais jusqu'à '(.*)' respecte '(.*)' avec '(.*)' essais maxi:")
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
        } catch (TechnicalException e) {
            throw new AssertError(TechnicalException.TECHNICAL_SUBSTEP_ERROR_MESSAGE + e.getMessage());
        }
    }

    /**
     * While a given condition is verified, do steps execution.
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
    @Lorsque("Si '(.*)' matche '(.*)', Tant que '(.*)' respecte '(.*)' je fais avec '(.*)' essais maxi:")
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
        } catch (TechnicalException e) {
            throw new AssertError(TechnicalException.TECHNICAL_SUBSTEP_ERROR_MESSAGE + e.getMessage());
        }
    }

    /**
     * Generic method to check that the value given in parameters is not empty.
     * A first research is made from {@link noraui.gherkin.ScenarioRegistry} to retrieve value if a key is provided.
     *
     * @param data
     *            Name of the value to check
     * @param textOrKey
     *            The value
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_EMPTY_DATA} message (no screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Time(name = "{textOrKey}")
    @Lorsque("Je vérifie que (.*) '(.*)' n'est pas vide[\\.|\\?]")
    @Given("I check that (.*) '(.*)' is not empty[\\.|\\?]")
    public void checkNotEmpty(String data, @TimeName("textOrKey") String textOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        if (!"".equals(data)) {
            String value = Context.getValue(textOrKey) != null ? Context.getValue(textOrKey) : textOrKey;
            if ("".equals(value)) {
                new Result.Failure<>(textOrKey, Messages.format(Messages.FAIL_MESSAGE_EMPTY_DATA, data), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
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
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_EMPTY_DATA} message (no screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Lorsque("Je vérifie les champs obligatoires:")
    @Given("I check mandatory fields:")
    public void checkMandatoryFields(Map<String, String> mandatoryFields) throws TechnicalException, FailureException {
        List<String> errors = new ArrayList<>();
        for (Entry<String, String> element : mandatoryFields.entrySet()) {
            if ("".equals(element.getValue())) {
                errors.add(element.getKey());
            }
        }
        if (!errors.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            int index = errorMessage.length();
            for (int j = 0; j < errors.size(); j++) {
                errorMessage.append(Messages.format(Messages.FAIL_MESSAGE_EMPTY_DATA, errors.get(j)));
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
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} or {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE}
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
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error (with message and screenshot)
     * @throws TechnicalException
     *             is thrown if the scenario encounters a technical error (format, configuration, data, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} or {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE}
     */
    @Conditioned
    @Et("Je sauvegarde la valeur de '(.*)' dans la colonne '(.*)' du fournisseur de données en sortie[\\.|\\?]")
    @And("I save the value of '(.*)-(.*)' in '(.*)' column of data output provider[\\.|\\?]")
    public void saveValueInDataOutputProvider(String page, String field, String targetColumn, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String value = "";
        try {
            value = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + field)))).getText();
            if (value == null) {
                value = "";
            }
        } catch (Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT, true, Page.getInstance(page).getCallBack());
        }
        for (Integer line : Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes()) {
            try {
                Context.getDataOutputProvider().writeDataResult(targetColumn, line, value);
            } catch (TechnicalException e) {
                new Result.Failure<>(e.getMessage(), Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_WRITE_MESSAGE_IN_RESULT_FILE, targetColumn), true, Page.getInstance(page).getCallBack());
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
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error (with message and screenshot)
     * @throws TechnicalException
     *             is thrown if the scenario encounters a technical error (format, configuration, data, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} or {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE}
     */
    @Conditioned
    @Et("Je sauvegarde la valeur de '(.*)' dans la clé '(.*)' du contexte[\\.|\\?]")
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
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Lorsque("Je clique sur '(.*)-(.*)'[\\.|\\?]")
    @When("I click on '(.*)-(.*)'[\\.|\\?]")
    public void clickOn(String page, String toClick, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        loggerStep.debug(page + " clickOn: " + toClick);
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
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je clique via js sur '(.*)-(.*)'[\\.|\\?]")
    @When("I click by js on '(.*)-(.*)'[\\.|\\?]")
    public void clickOnByJs(String page, String toClick, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        loggerStep.debug(page + " clickOnByJs: " + toClick);
        clickOnByJs(Page.getInstance(page).getPageElementByKey('-' + toClick));
    }

    /**
     * Update a html input text with a date.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            is target element
     * @param date
     *            is the new date
     * @param dateType
     *            'future', 'future_strict', 'today' or 'any'
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with a message (no screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je met à jour la date '(.*)-(.*)' avec une '(.*)' date '(.*)'[\\.|\\?]")
    @When("I update date '(.*)-(.*)' with a '(.*)' date '(.*)'[\\.|\\?]")
    public void updateDate(String page, String elementName, String dateType, String date, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        if (!"".equals(date)) {
            PageElement pageElement = Page.getInstance(page).getPageElementByKey('-' + elementName);
            if (date.matches(Constants.DATE_FORMAT_REG_EXP)) {
                updateDateValidated(pageElement, dateType, date);
            } else {
                new Result.Failure<>(date, Messages.format(Messages.FAIL_MESSAGE_WRONG_DATE_FORMAT, date, elementName), false, pageElement.getPage().getCallBack());
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
     * @param text
     *            Is the new data
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je met à jour la liste déroulante '(.*)-(.*)' avec '(.*)'[\\.|\\?]")
    @When("I update select list '(.*)-(.*)' with '(.*)'[\\.|\\?]")
    public void updateList(String page, String elementName, String text, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateList(Page.getInstance(page).getPageElementByKey('-' + elementName), text);
    }

    /**
     * Update a html input text with a given text.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            Is target element
     * @param text
     *            Is the new data (text)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je met à jour le texte '(.*)-(.*)' avec '(.*)'[\\.|\\?]")
    @When("I update text '(.*)-(.*)' with '(.*)'[\\.|\\?]")
    public void updateText(String page, String elementName, String text, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateText(Page.getInstance(page).getPageElementByKey('-' + elementName), text);
    }

    /**
     * Update a html input text with a given text and then press ENTER key.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to update
     * @param textOrKey
     *            The updated value. Can be a key from registry of plain text
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             if the scenario encounters a technical error
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("Je met à jour le texte '(.*)-(.*)' et entre ENTRER avec '(.*)'[\\.|\\?]")
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
     *            Type of the field ('text', 'select'...)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
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
        PageElement pageElement = Page.getInstance(page).getPageElementByKey('-' + fieldName);
        if (pageElement != null) {
            switch (type) {
                case "text":
                    if (!checkMandatoryTextField(pageElement)) {
                        new Result.Failure<>(pageElement, Messages.format(Messages.FAIL_MESSAGE_EMPTY_MANDATORY_FIELD, pageElement, pageElement.getPage().getApplication()), true,
                                pageElement.getPage().getCallBack());
                    }
                    break;
                default:
                    new Result.Failure<>(type, Messages.format(Messages.SCENARIO_ERROR_MESSAGE_TYPE_NOT_IMPLEMENTED, type, "checkMandatoryField"), false, pageElement.getPage().getCallBack());
                    break;
            }
        } else {
            new Result.Failure<>(fieldName, Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT, true, Page.getInstance(page).getCallBack());
        }
    }

    /**
     * Checks if html input text contains expected value.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to check
     * @param text
     *            Expected value in input text (value can be null).
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je vérifie le texte '(.*)-(.*)' avec '(.*)'[\\.|\\?]")
    @And("I check text '(.*)-(.*)' with '(.*)'[\\.|\\?]")
    public void checkInputText(String page, String elementName, String text, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        if (!checkInputText(Page.getInstance(page).getPageElementByKey('-' + elementName), text)) {
            checkText(Page.getInstance(page).getPageElementByKey('-' + elementName), text);
        }
    }

    /**
     * Checks if an html element is displayed.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to check
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @And("I check that '(.*)-(.*)' is displayed[\\.|\\?]")
    public void checkElementPresent(String page, String elementName, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkElementPresence(Page.getInstance(page).getPageElementByKey('-' + elementName), true);
    }

    /**
     * Checks if an html element is not displayed.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            The key of the PageElement to check
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @And("I check that '(.*)-(.*)' is not displayed[\\.|\\?]")
    public void checkElementNotPresent(String page, String elementName, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkElementPresence(Page.getInstance(page).getPageElementByKey('-' + elementName), false);
    }

    /**
     * Checks that a given page displays a html alert.
     *
     * @param page
     *            The concerned page
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     */
    @Conditioned
    @Then("I check absence of alert in '(.*)'[\\.|\\?]")
    public void checkAlert(String page, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkAlert(Page.getInstance(page));
    }

    /**
     * Updates the value of a html radio element with conditions.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            is target element
     * @param input
     *            is the new value
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je met à jour la liste radio '(.*)-(.*)' avec '(.*)'[\\.|\\?]")
    @And("I update radio list '(.*)-(.*)' with '(.*)'[\\.|\\?]")
    public void updateRadioList(String page, String elementName, String input, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        updateRadioList(Page.getInstance(page).getPageElementByKey('-' + elementName), input);
    }

    /**
     * Updates the value of html radio element with conditions using a map of keys/printed values.
     *
     * @param page
     *            The concerned page of elementName
     * @param elementName
     *            is target element
     * @param input
     *            is the new value
     * @param printedValues
     *            is the display value
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @And("I update radio list '(.*)-(.*)' with '(.*)' from these values:")
    public void updateRadioList(String page, String elementName, String input, Map<String, String> printedValues) throws TechnicalException, FailureException {
        updateRadioList(Page.getInstance(page).getPageElementByKey('-' + elementName), input, printedValues);
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
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             if the scenario encounters a technical error
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
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
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
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
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SWITCH_FRAME} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @When("I switch to '(.*)' frame[\\.|\\?]")
    public void switchFrame(String page, String elementName, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        switchFrame(Page.getInstance(page).getPageElementByKey('-' + elementName));
    }

}