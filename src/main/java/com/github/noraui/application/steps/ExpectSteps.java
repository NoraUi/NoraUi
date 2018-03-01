/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import static com.github.noraui.utils.Constants.VALUE;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.beust.jcommander.internal.Nullable;
import com.github.noraui.application.page.Page;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;

import cucumber.api.java.en.And;
import cucumber.api.java.fr.Et;

/**
 * This class contains Gherkin callable steps that aim for expecting a specific result.
 */
public class ExpectSteps extends Step {

    /**
     * Checks if an html element contains expected value.
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
    @Et("Je m'attends à avoir '(.*)-(.*)' avec le texte '(.*)'[\\.|\\?]")
    @And("I expect to have '(.*)-(.*)' with the text '(.*)'[\\.|\\?]")
    public void expectText(String page, String elementName, String textOrKey, List<GherkinStepCondition> conditions) throws FailureException, TechnicalException {
        expectText(Page.getInstance(page).getPageElementByKey('-' + elementName), textOrKey);
    }

    /**
     * Expects that the target element contains the given value as text.
     * The inner text and 'value' attribute of the element is checked.
     *
     * @param locator
     *            is the selenium locator
     * @param value
     *            is the expected value
     * @return true or false
     */
    public static ExpectedCondition<Boolean> textToBeEqualsToExpectedValue(final By locator, final String value) {
        return new ExpectedCondition<Boolean>() {
            /**
             * {@inheritDoc}
             */
            @Override
            public Boolean apply(@Nullable WebDriver driver) {
                try {
                    WebElement element = driver.findElement(locator);
                    if (element != null && value != null) {
                        return !((element.getAttribute(VALUE) == null || !value.equals(element.getAttribute(VALUE).trim())) && !value.equals(element.getText()));
                    }
                } catch (Exception e) {
                }
                return false;
            }
        };
    }
}
