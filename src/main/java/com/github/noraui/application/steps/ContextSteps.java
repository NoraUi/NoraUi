/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import java.util.List;

import org.slf4j.Logger;

import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;

import io.cucumber.java.en.And;

@Loggable
public class ContextSteps extends Step {

	static Logger log;

	/**
	 * Check that value in context equals to value.
	 * 
	 * @param srcKey     is source key in context.
	 * @param value      is value checked (String).
	 * @param conditions list of 'expected' values condition and 'actual' values
	 *                   ({@link com.github.noraui.gherkin.GherkinStepCondition}).
	 * @throws FailureException if the scenario encounters a functional error
	 */
	@Conditioned
	@And("I check that value in {string} context key equals to {string}")
	public void checkThatValueInContextEqualsTo(String srcKey, String value, List<GherkinStepCondition> conditions)
			throws FailureException {
		if (!Context.getValue(srcKey).equals(value)) {
			log.error("Error when open a new window: {}");
			new Result.Failure<>("", Messages.getMessage(Messages.FAIL_MESSAGE_CONTEXT_NOT_EQUALS), true,
					Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
		}
	}

	/**
	 * Check that value in context equals ignore case to value.
	 * 
	 * @param srcKey     is source key in context.
	 * @param value      is value checked (String).
	 * @param conditions list of 'expected' values condition and 'actual' values
	 *                   ({@link com.github.noraui.gherkin.GherkinStepCondition}).
	 * @throws FailureException if the scenario encounters a functional error
	 */
	@Conditioned
	@And("I check that value in {string} context key equals ignore case to {string}")
	public void checkThatValueInContextEqualsIgnoreCaseTo(String srcKey, String value,
			List<GherkinStepCondition> conditions) throws FailureException {
		if (!Context.getValue(srcKey).equalsIgnoreCase(value)) {
			log.error("Error when open a new window: {}");
			new Result.Failure<>("", Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_OPEN_A_NEW_WINDOW), true,
					Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
		}
	}

	/**
	 * Check that value in context contains value.
	 * 
	 * @param srcKey     is source key in context.
	 * @param value      is value checked (String).
	 * @param conditions list of 'expected' values condition and 'actual' values
	 *                   ({@link com.github.noraui.gherkin.GherkinStepCondition}).
	 * @throws FailureException if the scenario encounters a functional error
	 */
	@Conditioned
	@And("I check that value in {string} context key contains {string}")
	public void checkThatValueInContextContains(String srcKey, String value, List<GherkinStepCondition> conditions)
			throws FailureException {
		if (!Context.getValue(srcKey).contains(value)) {
			log.error("Error when open a new window: {}");
			new Result.Failure<>("", Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_OPEN_A_NEW_WINDOW), true,
					Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
		}
	}

}
