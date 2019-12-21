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
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;

import io.cucumber.java.en.And;

@Loggable
public class ContextSteps extends Step {

    static Logger log;

    /**
     * Check that value (or value in context) equals to value (or value in context).
     * 
     * @param srcValueOrKey
     *            is source value (or value in context).
     * @param value
     *            is value compared (or value in context).
     * @param conditions
     *            list of 'expected' values condition and 'actual' values
     *            ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @And("I check that {string} equals to {string}(\\?)")
    public void checkThatValueInContextEqualsTo(String srcValueOrKey, String compareValueOrKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        final String src = Context.getValue(srcValueOrKey) != null ? Context.getValue(srcValueOrKey) : srcValueOrKey;
        final String compare = Context.getValue(compareValueOrKey) != null ? Context.getValue(compareValueOrKey) : compareValueOrKey;
        if (!src.equals(compare)) {
            log.error("The value « {} » not equals « {} »", src, compare);
            new Result.Failure<>("", Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_CONTEXT_NOT_EQUALS), src, compare), true, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * Check that value (or value in context) equals ignore case to value (or value in context).
     * 
     * @param srcValueOrKey
     *            is source value (or value in context).
     * @param value
     *            is value compared (or value in context).
     * @param conditions
     *            list of 'expected' values condition and 'actual' values
     *            ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @And("I check that {string} equals ignore case to {string}(\\?)")
    public void checkThatValueInContextEqualsIgnoreCaseTo(String srcValueOrKey, String compareValueOrKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        final String src = Context.getValue(srcValueOrKey) != null ? Context.getValue(srcValueOrKey) : srcValueOrKey;
        final String compare = Context.getValue(compareValueOrKey) != null ? Context.getValue(compareValueOrKey) : compareValueOrKey;
        if (!src.equalsIgnoreCase(compare)) {
            log.error("The value « {} » not equals ignore case « {} »", src, compare);
            new Result.Failure<>("", Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_CONTEXT_NOT_EQUALS_IGNORE_CASE), src, compare), true, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * Check that value (or value in context) contains value (or value in context).
     * 
     * @param srcValueOrKey
     *            is source value (or value in context).
     * @param value
     *            is value compared (or value in context).
     * @param conditions
     *            list of 'expected' values condition and 'actual' values
     *            ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @And("I check that {string} contains {string}(\\?)")
    public void checkThatValueInContextContains(String srcValueOrKey, String compareValueOrKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        final String src = Context.getValue(srcValueOrKey) != null ? Context.getValue(srcValueOrKey) : srcValueOrKey;
        final String compare = Context.getValue(compareValueOrKey) != null ? Context.getValue(compareValueOrKey) : compareValueOrKey;
        if (!src.contains(compare)) {
            log.error("The value « {} » not contains « {} »", src, compare);
            new Result.Failure<>("", Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_CONTEXT_NOT_CONTAINS), src, compare), true, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

}
