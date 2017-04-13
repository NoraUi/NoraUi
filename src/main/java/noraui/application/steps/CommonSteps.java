package noraui.application.steps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Keys;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.fr.Lorsque;
import cucumber.metrics.annotation.time.Time;
import cucumber.metrics.annotation.time.TimeName;
import noraui.application.page.Page;
import noraui.application.page.Page.PageElement;
import noraui.cucumber.annotation.Conditioned;
import noraui.cucumber.injector.NoraUiInjector;
import noraui.exception.Callbacks;
import noraui.exception.FailureException;
import noraui.exception.Result;
import noraui.exception.TechnicalException;
import noraui.gherkin.GherkinLoopedStepCondition;
import noraui.gherkin.GherkinStepCondition;
import noraui.utils.Constants;
import noraui.utils.Context;
import noraui.utils.Messages;

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
    @Lorsque("Puis j'attends '(.*)' secondes[\\.|\\?]")
    @Then("I wait '(.*)' seconds[\\.|\\?]")
    public void wait(int time, List<GherkinStepCondition> conditions) throws InterruptedException {
        Thread.sleep((long) time * 1000);
    }

    /**
     * Loop.
     *
     * @param time
     *            number of run.
     * @param conditions
     *            list of steps run in a loop.
     * @throws TechnicalException
     *             is thrown if you have a technical error (IllegalAccessException, IllegalArgumentException, InvocationTargetException, ...) in NoraUi.
     *             Exception with {@value noraui.exception.TechnicalException#TECHNICAL_SUBSTEP_ERROR_MESSAGE} message.
     */
    @Then("I do '(.*)' times:")
    public void loop(int time, List<GherkinLoopedStepCondition> conditions) throws TechnicalException {
        try {
            Map<String, Method> cucumberClass = Context.getCucumberMethods();
            for (int i = 0; i < time; i++) {
                runAllStepsInLoop(conditions, cucumberClass);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new TechnicalException(TechnicalException.TECHNICAL_SUBSTEP_ERROR_MESSAGE, e.getCause());
        }
    }

    /**
     * Do until.
     *
     * @param key
     *            key of 'expected' values ('actual' values)
     * @param breakCondition
     *            'expected' values
     * @param tries
     *            number of max tries (no infinity loop).
     * @param conditions
     *            list of steps run in a loop.
     * @throws TechnicalException
     *             is thrown if you have a technical error (IllegalAccessException, IllegalArgumentException, InvocationTargetException, ...) in NoraUi.
     *             Exception with {@value noraui.exception.TechnicalException#TECHNICAL_SUBSTEP_ERROR_MESSAGE} message.
     */
    @Then("I do until '(.*)' respects '(.*)' with '(.*)' max tries:")
    public void doUntil(String key, String breakCondition, int tries, List<GherkinLoopedStepCondition> conditions) throws TechnicalException {
        try {
            Map<String, Method> cucumberClass = Context.getCucumberMethods();
            int i = 0;
            do {
                i++;
                runAllStepsInLoop(conditions, cucumberClass);
            } while (!(Pattern.compile(breakCondition).matcher(Context.getValue(key) == null ? "" : Context.getValue(key)).find() || tries == i));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new TechnicalException(TechnicalException.TECHNICAL_SUBSTEP_ERROR_MESSAGE, e.getCause());
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
    @And("I save the value of '(.*)-(.*)'[\\.|\\?]")
    public void saveElementValue(String page, String field, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        saveElementValue('-' + field, Page.getInstance(page));
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
    @And("I check text '(.*)-(.*)' with '(.*)'[\\.|\\?]")
    public void checkInputText(String page, String elementName, String text, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        checkInputText(Page.getInstance(page).getPageElementByKey('-' + elementName), text);
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
    @When("I clear text in '(.*)-(.*)'[\\.|\\?]")
    public void clearText(String page, String elementName, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        clearText(Page.getInstance(page).getPageElementByKey('-' + elementName));
    }

    private void runAllStepsInLoop(List<GherkinLoopedStepCondition> conditions, Map<String, Method> cucumberClass)
            throws TechnicalException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        for (GherkinLoopedStepCondition condition : conditions) {
            List<GherkinStepCondition> stepConditions = new ArrayList<>();
            GherkinStepCondition g = new GherkinStepCondition();
            g.setActual(condition.getActual());
            g.setExpected(condition.getExpected());
            stepConditions.add(g);
            for (Entry<String, Method> elem : cucumberClass.entrySet()) {
                Matcher matcher = Pattern.compile("value=(.*)\\)").matcher(elem.getKey());
                if (matcher.find()) {
                    Matcher matcher2 = Pattern.compile(matcher.group(1)).matcher(condition.getStep());
                    if (matcher2.find()) {
                        Object[] tab;
                        if (elem.getValue().isAnnotationPresent(Conditioned.class)) {
                            tab = new Object[matcher2.groupCount() + 1];
                            tab[matcher2.groupCount()] = stepConditions;
                        } else {
                            tab = new Object[matcher2.groupCount()];
                        }

                        for (int i = 0; i < matcher2.groupCount(); i++) {
                            Parameter param = elem.getValue().getParameters()[i];
                            if (param.getType() == int.class) {
                                int ii = Integer.parseInt(matcher2.group(i + 1));
                                tab[i] = ii;
                            } else if (param.getType() == boolean.class) {
                                tab[i] = Boolean.parseBoolean(matcher2.group(i + 1));
                            } else {
                                tab[i] = matcher2.group(i + 1);
                            }
                        }
                        elem.getValue().invoke(NoraUiInjector.getNoraUiInjectorSource().getInstance(elem.getValue().getDeclaringClass()), tab);
                    }

                }
            }
        }
    }

}