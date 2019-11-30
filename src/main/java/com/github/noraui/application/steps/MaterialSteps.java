/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import static com.github.noraui.utils.Constants.VALUE;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import com.github.noraui.application.page.Page;
import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;

import io.cucumber.java.en.Then;
import io.cucumber.java.fr.Lorsque;

@Loggable
public class MaterialSteps extends Step {

    static Logger log;
    
    /**
     * 
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param value
     *            To check or not ?
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_EMPTY_DATA} message (no screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Lorsque("Je mets à jour le mat-slide-toggle {string} avec {string}(\\?)")
    @Then("I update mat-slide-toggle {string} with {string}(\\?)")
    public void selectCheckbox(String pageElement, String value, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        selectMatSlideToggle(Page.getInstance(page).getPageElementByKey('-' + elementName), Boolean.parseBoolean(value));
    }
    
    /**
     * 
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param valueOrKey
     *            Is the value (value or value in context (after a save)) use for selection
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_EMPTY_DATA} message (no screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Lorsque("Je mets à jour le mat-radio-button {string} avec {string}(\\?)")
    @Then("I update mat-radio-button {string} with {string}(\\?)")
    public void updateRadioList(String pageElement, String valueOrKey, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        updateMatRadioButton(Page.getInstance(page).getPageElementByKey('-' + elementName), valueOrKey);
    }

    /**
     * Checks a Material Slide Toggle type element.
     *
     * @param element
     *            Target page element
     * @param checked
     *            Final Material Slide Toggle value
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void selectMatSlideToggle(PageElement element, boolean checked, Object... args) throws TechnicalException, FailureException {
        try {
            final WebElement webElement = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(element, args)));
            if ("false".equals(webElement.getAttribute("ng-reflect-checked")) && checked || "true".equals(webElement.getAttribute("ng-reflect-checked")) && !checked) {
                webElement.click();
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_MATERIAL_UNABLE_TO_CHECK_ELEMENT), element, element.getPage().getApplication()), true,
                    element.getPage().getCallBack());
        }
    }
    
    /**
     * Update Material radio list by text "input".
     *
     * @param pageElement
     *            Is concerned element
     * @param valueOrKey
     *            Is the value (value or value in context (after a save)) use for selection
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateMatRadioButton(PageElement pageElement, String valueOrKey) throws TechnicalException, FailureException {
        final String value = Context.getValue(valueOrKey) != null ? Context.getValue(valueOrKey) : valueOrKey;
        try {
            final List<WebElement> radioButtons = Context.waitUntil(ExpectedConditions.presenceOfAllElementsLocatedBy(Utilities.getLocator(pageElement)));
            for (final WebElement button : radioButtons) {
                if (button.getAttribute(VALUE).equals(value)) {
                    button.click();
                    break;
                }
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_MATERIAL_UNABLE_TO_SELECT_RADIO_BUTTON), pageElement), true, pageElement.getPage().getCallBack());
        }
    }

}
