/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.HttpServiceException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.service.HttpService;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.google.inject.Inject;

import cucumber.api.java.en.And;
import cucumber.api.java.fr.Et;

/**
 * This class contains API REST callable steps.
 */
public class RESTSteps extends Step {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonSteps.class);

    @Inject
    private HttpService httpService;

    /**
     * Save result of REST API in memory if all 'expected' parameters equals 'actual' parameters in conditions.
     * 
     * @param method
     *            GET or POST
     * @param pageKey
     *            is the key of page (example: GOOGLE_HOME)
     * @param uri
     *            end of the url
     * @param targetKey
     *            Target key to save retrieved value.
     * @param conditions
     *            List of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_EMPTY_DATA} message (no screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je sauvegarde la valeur de cette API REST {string} {string} {string} dans {string} du contexte\\./\\?")
    @And("I save the value of REST API {string} {string} {string} in {string} context key\\./\\?")
    public void saveValue(String method, String pageKey, String uri, String targetKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        LOGGER.debug("saveValue of REST API with method [{}].", method);
        LOGGER.debug("saveValue of REST API with pageKey [{}].", pageKey);
        LOGGER.debug("saveValue of REST API with uri [{}].", uri);
        LOGGER.debug("saveValue of REST API in targetKey [{}].", targetKey);
        String json = null;
        try {
            json = httpService.get(Context.getUrlByPagekey(pageKey), uri);
        } catch (HttpServiceException e) {
            LOGGER.error(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_CALL_API_REST), e);
            new Result.Failure<>(Context.getApplicationByPagekey(pageKey), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_CALL_API_REST), true, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
        Context.saveValue(targetKey, json);
    }

    /**
     * Save result of REST API in dataOutputProvider if all 'expected' parameters equals 'actual' parameters in conditions.
     * 
     * @param method
     *            GET or POST
     * @param pageKey
     *            is the key of page (example: GOOGLE_HOME)
     * @param uri
     *            end of the url
     * @param targetColumn
     *            Target column (in data output provider) to save retrieved value.
     * @param conditions
     *            List of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_EMPTY_DATA} message (no screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Et("Je sauvegarde la valeur de cette API REST {string} {string} {string} dans {string} du fournisseur de données en sortie\\./\\?")
    @And("I save the value of REST API {string} {string} {string} in {string} column of data output provider\\./\\?")
    public void saveValueInDataOutputProvider(String method, String pageKey, String uri, String targetColumn, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        LOGGER.debug("saveValue of REST API with method [{}].", method);
        LOGGER.debug("saveValue of REST API with pageKey [{}].", pageKey);
        LOGGER.debug("saveValue of REST API with uri [{}].", uri);
        LOGGER.debug("saveValue of REST API in targetColumn [{}].", targetColumn);
        String json;
        try {
            json = httpService.get(Context.getUrlByPagekey(pageKey), uri);
            for (final Integer line : Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes()) {
                Context.getDataOutputProvider().writeDataResult(targetColumn, line, json);
            }
        } catch (HttpServiceException e) {
            new Result.Failure<>(Context.getApplicationByPagekey(pageKey), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_CALL_API_REST), true, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        } catch (final TechnicalException e) {
            new Result.Warning<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_WRITE_MESSAGE_IN_RESULT_FILE), targetColumn), false, 0);
        }
    }

}
