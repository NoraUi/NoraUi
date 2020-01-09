/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import com.github.noraui.application.page.Page;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Utilities;

import io.cucumber.java.en.Then;
import io.cucumber.java.fr.Lorsque;

@Loggable
public class WaitSteps extends Step {

    static Logger log;

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
    @Lorsque("J'attends {int} seconde(s)(\\?)")
    @Then("I wait {int} second(s)(\\?)")
    public void wait(int time, List<GherkinStepCondition> conditions) throws InterruptedException {
        Thread.sleep((long) time * 1000);
    }

    @Conditioned
    @Lorsque("J'attends que l'attribut de {string} contient {string}(\\?)")
    @Then("I wait attribute of {string} contains {string}(\\?)")
    public void waitAttributeContains(String pageElement, final String attribute, final String value, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.attributeContains(Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName)), attribute, value));
    }

    @Conditioned
    @Lorsque("J'attends que l'attribut de {string} correspond à {string}(\\?)")
    @Then("I wait attribute of {string} equals {string}(\\?)")
    public void waitAttributeToBe(String pageElement, final String attribute, final String value, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.attributeToBe(Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName)), attribute, value));
    }

    @Conditioned
    @Lorsque("J'attends que {string} soit cliquable(\\?)")
    @Then("I wait for {string} to be clickable(\\?)")
    public void waitElementToBeClickable(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName))));
    }

    @Conditioned
    @Lorsque("J'attends que {string} soit sélectionné(\\?)")
    @Then("I wait for {string} to be selected(\\?)")
    public void waitElementToBeSelected(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.elementSelectionStateToBe(Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName)), true));
    }

    @Conditioned
    @Lorsque("J'attends que {string} ne soit pas sélectionné(\\?)")
    @Then("I wait for {string} to be not selected(\\?)")
    public void waitElementToBeNotSelected(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.elementSelectionStateToBe(Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName)), false));
    }

    @Conditioned
    @Lorsque("J'attends l'invisibilité de {string}(\\?)")
    @Then("I wait invisibility of {string}(\\?)")
    public void waitInvisibilityOf(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.invisibilityOf(Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName))));
    }

    /**
     * Waits invisibility of element with timeout of x seconds.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param time
     *            is custom timeout in seconds
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @Lorsque("J'attends l'invisibilité de {string} avec un timeout de {int} secondes(\\?)")
    @Then("I wait invisibility of {string} with timeout of {int} seconds(\\?)")
    public void waitInvisibilityOf(String pageElement, int timeOutInSeconds, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.invisibilityOf(Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName))), timeOutInSeconds);
    }

    @Conditioned
    @Lorsque("J'attends l'invisibilité de l'élément avec le text {string}(\\?)")
    @Then("I wait invisibility of element with text {string}(\\?)")
    public void waitInvisibilityOfElementWithText(String pageElement, final String text, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.invisibilityOfElementWithText(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)), text));
    }

    @Conditioned
    @Lorsque("J'attends l'invisibilité de l'élément avec le text {string} avec un timeout de {int} secondes(\\?)")
    @Then("I wait invisibility of element with text {string} with timeout of {int} seconds(\\?)")
    public void waitInvisibilityOfElementWithTextWithTimeout(String pageElement, final String text, int timeOutInSeconds, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.invisibilityOfElementWithText(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)), text), timeOutInSeconds);
    }

    @Conditioned
    @Lorsque("J'attends que le nombre de {string} soient {int}(\\?)")
    @Then("I wait number of {string} to be {int}(\\?)")
    public void waitNumberOfElementsToBe(String pageElement, final int number, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.numberOfElementsToBe(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)), number));
    }

    @Conditioned
    @Lorsque("J'attends que le nombre de {string} soient inférieur à {int}(\\?)")
    @Then("I wait number of {string} to be less {int}(\\?)")
    public void waitNumberOfElementsToBeLessThan(String pageElement, final Integer number, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.numberOfElementsToBeLessThan(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)), number));
    }

    @Conditioned
    @Lorsque("J'attends que le nombre de {string} soient supérieur à {int}(\\?)")
    @Then("I wait number of {string} to be more than {int}(\\?)")
    public void waitNumberOfElementsToBeMoreThan(String pageElement, final Integer number, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)), number));
    }

    @Conditioned
    @Lorsque("J'attends que le nombre de fenêtre soient {int}(\\?)")
    @Then("I wait number of windows to be {int}(\\?)")
    public void waitNumberOfWindowsToBe(final Integer expectedNumberOfWindows, List<GherkinStepCondition> conditions) throws TechnicalException {
        Context.waitUntil(ExpectedConditions.numberOfWindowsToBe(expectedNumberOfWindows));
    }

    @Conditioned
    @Lorsque("J'attends la présence de {string}(\\?)")
    @Then("I wait presence of {string}(\\?)")
    public void waitPresenceOfElementLocated(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName))));
    }

    @Conditioned
    @Lorsque("J'attends la présence de {string} et {string} imbriqué(\\?)")
    @Then("I wait presence of {string} and nested {string}(\\?)")
    public void waitPresenceOfNestedElementLocatedBy(String pageElement, String childPageElement, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        String childPage = childPageElement.split("-")[0];
        String childElementName = childPageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.presenceOfNestedElementLocatedBy(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)),
                Utilities.getLocator(Page.getInstance(childPage).getPageElementByKey('-' + childElementName))));
    }

    /**
     * Waits staleness of element with timeout of x seconds.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param time
     *            is custom timeout
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @Lorsque("J'attends la disparition de {string} avec un timeout de {int} seconde(s)(\\?)")
    @Then("I wait staleness of {string} with timeout of {int} second(s)(\\?)")
    public void waitStalenessOf(String pageElement, int time, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.stalenessOf(Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName))), time);
    }

    @Conditioned
    @Lorsque("J'attends que le text de {string} vérifie le texte {string}(\\?)")
    @Then("I wait {string} text matches {string}(\\?)")
    public void waitTextMatches(String pageElement, String regexp, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Pattern pattern = Pattern.compile(regexp);
        Context.waitUntil(ExpectedConditions.textMatches(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)), pattern));
    }

    @Conditioned
    @Lorsque("J'attends que le text de {string} correspond à {string}(\\?)")
    @Then("I wait {string} equals {string}(\\?)")
    public void waitTextToBe(String pageElement, final String value, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.textToBe(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)), value));
    }

    @Conditioned
    @Lorsque("J'attends que le text de {string} contient {string}(\\?)")
    @Then("I wait {string} text contains {string}(\\?)")
    public void waitTextToBePresentInElementLocated(String pageElement, final String text, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.textToBePresentInElementLocated(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)), text));
    }

    /**
     * Waits text to be present in element value.
     * 
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: demo.DemoPage-button)
     * @param text
     *            to be present in the value attribute of the element found by the locator.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Conditioned
    @Lorsque("J'attends que le texte soit présent dans la valeur de l'élément {string} avec {string}(\\?)")
    @Then("I wait text to be present in element value {string} with {string}(\\?)")
    public void waitTextToBePresentInElementValue(String pageElement, String text, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.textToBePresentInElementValue(Utilities.getLocator(Page.getInstance(page).getPageElementByKey('-' + elementName)), text));
    }

    @Conditioned
    @Lorsque("J'attends que le titre correspond {string}(\\?)")
    @Then("I wait title contains {string}(\\?)")
    public void waitTitleContains(final String title, List<GherkinStepCondition> conditions) throws TechnicalException {
        Context.waitUntil(ExpectedConditions.titleContains(title));
    }

    @Conditioned
    @Lorsque("J'attends que le titre correspond à {string}(\\?)")
    @Then("I wait title equals {string}(\\?)")
    public void waitTitleIs(final String title, List<GherkinStepCondition> conditions) throws TechnicalException {
        Context.waitUntil(ExpectedConditions.titleIs(title));
    }

    @Conditioned
    @Lorsque("J'attends que l'url correspond {string}(\\?)")
    @Then("I wait url contains {string}(\\?)")
    public void waitUrlContains(final String fraction, List<GherkinStepCondition> conditions) throws TechnicalException {
        Context.waitUntil(ExpectedConditions.urlContains(fraction));
    }

    @Conditioned
    @Lorsque("J'attends que l'url vérifie le texte {string}(\\?)")
    @Then("I wait url matches {string}(\\?)")
    public void waitUrlMatches(final String regex, List<GherkinStepCondition> conditions) throws TechnicalException {
        Context.waitUntil(ExpectedConditions.urlMatches(regex));
    }

    @Conditioned
    @Lorsque("J'attends que l'url correspond à {string}(\\?)")
    @Then("I wait url equals {string}(\\?)")
    public void waitUrlToBe(final String url, List<GherkinStepCondition> conditions) throws TechnicalException {
        Context.waitUntil(ExpectedConditions.urlToBe(url));
    }

    @Conditioned
    @Lorsque("J'attends la visibilité de {string}(\\?)")
    @Then("I wait visibility of {string}(\\?)")
    public void waitVisibilityOf(String pageElement, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.visibilityOf(Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName))));
    }

    @Conditioned
    @Lorsque("J'attends la visibilité de {string} et {string} imbriqué(\\?)")
    @Then("I wait visibility of {string} and nested {string}(\\?)")
    public void waitVisibilityOfNestedElementsLocatedBy(String pageElement, String childPageElement, List<GherkinStepCondition> conditions) throws TechnicalException {
        String page = pageElement.split("-")[0];
        String elementName = pageElement.split("-")[1];
        String childPage = childPageElement.split("-")[0];
        String childElementName = childPageElement.split("-")[1];
        Context.waitUntil(ExpectedConditions.visibilityOfNestedElementsLocatedBy(Utilities.findElement(Page.getInstance(page).getPageElementByKey('-' + elementName)),
                Utilities.getLocator(Page.getInstance(childPage).getPageElementByKey('-' + childElementName))));
    }

}
