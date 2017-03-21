package noraui.application.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.fr.Lorsque;
import cucumber.metrics.annotation.time.Time;
import cucumber.metrics.annotation.time.TimeName;
import noraui.cucumber.annotation.Conditioned;
import noraui.exception.Callbacks;
import noraui.exception.FailureException;
import noraui.exception.Result;
import noraui.exception.TechnicalException;
import noraui.gherkin.GherkinCondition;
import noraui.utils.Context;
import noraui.utils.Messages;

public class CommonSteps extends Step {

    /**
     * Waits a time in second.
     *
     * @param time
     *            is time to wait
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinCondition}).
     * @throws InterruptedException
     *             Exception for the sleep
     */
    @Conditioned
    @Lorsque("Puis j'attends '(.*)' secondes.")
    @Then("I wait '(.*)' seconds.")
    public void wait(int time, List<GherkinCondition> conditions) throws InterruptedException {
        Thread.sleep((long) time * 1000);
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
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_EMPTY_DATA} message (no screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Time(name = "{textOrKey}")
    @Lorsque("Je vérifie que (.*) '(.*)' n'est pas vide.")
    @Given("I check that (.*) '(.*)' is not empty.")
    public void checkNotEmpty(String data, @TimeName("textOrKey") String textOrKey, List<GherkinCondition> conditions) throws TechnicalException, FailureException {
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
            new Result.Failure<>(null, errorMessage.toString(), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

}