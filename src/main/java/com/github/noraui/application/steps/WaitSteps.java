/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.browser.waits.Wait;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;

import io.cucumber.java.en.Then;
import io.cucumber.java.fr.Alors;
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
    @Lorsque("L'attribut {string} de {page-element} {should-shouldnot} contenir {string}(\\?)")
    @Then("The attribute {string} of {page-element} {should-shouldnot} contain {string}(\\?)")
    public void waitAttributeContains(final String attribute, PageElement pageElement, final Boolean not, final String value, List<GherkinStepCondition> conditions)
            throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.attributeContains(Utilities.getLocator(pageElement), attribute, value), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("L'attribut {string} de {page-element} {should-shouldnot} être égale à {string}(\\?)")
    @Then("The attribute {string} of {page-element} {should-shouldnot} be equals to {string}(\\?)")
    public void waitAttributeToBe(final String attribute, PageElement pageElement, final Boolean not, final String value, List<GherkinStepCondition> conditions)
            throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.attributeToBe(Utilities.getLocator(pageElement), attribute, value), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("L'élément {page-element} {should-shouldnot} être cliquable(\\?)")
    @Then("he element {page-element} {should-shouldnot} be clickable(\\?)")
    public void waitElementToBeClickable(PageElement pageElement, final Boolean not, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.elementToBeClickable(Utilities.getLocator(pageElement)), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("L'élément {page-element} {should-shouldnot} être sélectionné(\\?)")
    @Then("The element {page-element} {should-shouldnot} be selected(\\?)")
    public void waitElementToBeNotSelected(PageElement pageElement, final Boolean not, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.elementSelectionStateToBe(Utilities.getLocator(pageElement), !not));
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param not
     *            boolean operator
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Lorsque("L'élément {page-element} {is-isnot} invisible(\\?)")
    @Then("The element {page-element} {is-isnot} invisible(\\?)")
    public void waitInvisibilityOf(PageElement pageElement, final Boolean not, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.invisibilityOf(Utilities.findElement(pageElement)), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * Waits invisibility of element with timeout of x seconds.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param not
     *            boolean operator
     * @param timeOutInSeconds
     *            is custom timeout in seconds
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     * @throws FailureException
     *             if the scenario encounters a functional error.
     */
    @Conditioned
    @Lorsque("L'élément {page-element} {should-shouldnot} être invisible dans les {int} seconde(s)(\\?)")
    @Then("The element {page-element} {should-shouldnot} be invisible within {int} second(s)(\\?)")
    public void waitInvisibilityOf(PageElement pageElement, final Boolean not, int timeOutInSeconds, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.invisibilityOf(Utilities.findElement(pageElement)), timeOutInSeconds, not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("L'élément {page-element} avec le texte {string} {should-shouldnot} être invisible(\\?)")
    @Then("The element {page-element} avec le texte {string} {should-shouldnot} be invisible(\\?)")
    public void waitInvisibilityOfElementWithText(PageElement pageElement, final String text, final Boolean not, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.invisibilityOfElementWithText(Utilities.getLocator(pageElement), text), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("L'élément {page-element} avec le texte {string} {should-shouldnot} être invisible dans les {int} seconde(s)(\\?)")
    @Then("The element {page-element} with the text {string} {should-shouldnot} be invisible within {int} second(s)(\\?)")
    public void waitInvisibilityOfElementWithTextWithTimeout(PageElement pageElement, final String text, final Boolean not, int timeOutInSeconds, List<GherkinStepCondition> conditions)
            throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.invisibilityOfElementWithText(Utilities.getLocator(pageElement), text), timeOutInSeconds, not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("Le nombre d'élements {page-element} {should-shouldnot} être égale à {int}(\\?)")
    @Then("The number of elements {page-element} {should-shouldnot} be equal to {int}(\\?)")
    public void waitNumberOfElementsToBe(PageElement pageElement, final Boolean not, final int number, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.numberOfElementsToBe(Utilities.getLocator(pageElement), number), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("Le nombre d'éléments {page-element} {should-shouldnot} être inférieur à {int}(\\?)")
    @Then("The number of elements {page-element} {should-shouldnot} be less than {int}(\\?)")
    public void waitNumberOfElementsToBeLessThan(PageElement pageElement, final Boolean not, final Integer number, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.numberOfElementsToBeLessThan(Utilities.getLocator(pageElement), number), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("Le nombre d'éléments {page-element} {should-shouldnot} être supérieur à {int}(\\?)")
    @Then("The number of elements {page-element} {should-shouldnot} be more than {int}(\\?)")
    public void waitNumberOfElementsToBeMoreThan(PageElement pageElement, final Boolean not, final Integer number, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(Utilities.getLocator(pageElement), number), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("Le nombre de fenêtres {should-shouldnot} être {int}(\\?)")
    @Then("The number of windows {should-shouldnot} be {int}(\\?)")
    public void waitNumberOfWindowsToBe(final Boolean not, final Integer expectedNumberOfWindows, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.numberOfWindowsToBe(expectedNumberOfWindows), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    @Conditioned
    @Lorsque("L'élément {page-element} {should-shouldnot} être présent(\\?)")
    @Then("The element {page-element} {should-shouldnot} be present(\\?)")
    public void waitPresenceOfElementLocated(PageElement pageElement, final Boolean not, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(Utilities.getLocator(pageElement)), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("Les éléments imbriqués {page-element} et {page-element} {should-shouldnot} être présents(\\?)")
    @Then("Nested elements {page-element} and {page-element} {should-shouldnot} be present(\\?)")
    public void waitPresenceOfNestedElementLocatedBy(PageElement pageElement, PageElement childPageElement, final Boolean not, List<GherkinStepCondition> conditions)
            throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(Utilities.getLocator(pageElement), Utilities.getLocator(childPageElement)), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * Waits staleness of element with timeout of x seconds.
     *
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param not
     *            boolean operator
     * @param time
     *            is custom timeout
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Lorsque("L'élément {page-element} {should-shouldnot} périmer dans les {int} seconde(s)(\\?)")
    @Then("The element {page-element} {should-shouldnot} become stale within the {int} second(s)(\\?)")
    public void waitStalenessOf(PageElement pageElement, final Boolean not, int time, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.stalenessOf(Utilities.findElement(pageElement)), time, not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("Le texte de {page-element} {should-shouldnot} respecter {string}(\\?)")
    @Then("The text of {page-element} {should-shouldnot} respect {string}(\\?)")
    public void waitTextMatches(PageElement pageElement, final Boolean not, String regexp, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Pattern pattern = Pattern.compile(regexp);
            Wait.until(ExpectedConditions.textMatches(Utilities.getLocator(pageElement), pattern), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("Le texte de {page-element} {should-shouldnot} être {string}(\\?)")
    @Then("The text of {page-element} {should-shouldnot} be {string}(\\?)")
    public void waitTextToBe(PageElement pageElement, final Boolean not, final String value, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.textToBe(Utilities.getLocator(pageElement), value), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("Le texte de {page-element} {should-shouldnot} contenir {string}(\\?)")
    @Then("The text of {page-element} {should-shouldnot} contain {string}(\\?)")
    public void waitTextToBePresentInElementLocated(PageElement pageElement, final Boolean not, final String text, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.textToBePresentInElementLocated(Utilities.getLocator(pageElement), text), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * Waits text to be present in element value.
     * 
     * @param pageElement
     *            The concerned page of field AND key of PageElement concerned (sample: $demo.DemoPage-button)
     * @param not
     *            boolean operator
     * @param text
     *            to be present in the value attribute of the element found by the locator.
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Lorsque("La valeur de l'élément {page-element} {should-shouldnot} être {string}(\\?)")
    @Then("The value of element {page-element} {should-shouldnot} be {string}(\\?)")
    public void waitTextToBePresentInElementValue(PageElement pageElement, final Boolean not, String text, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.textToBePresentInElementValue(Utilities.getLocator(pageElement), text), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

    @Conditioned
    @Lorsque("Le titre {should-shouldnot} contenir {string}(\\?)")
    @Then("Title {should-shouldnot} contain {string}(\\?)")
    public void waitTitleContains(final Boolean not, final String title, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.titleContains(title), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    @Conditioned
    @Lorsque("Le titre {should-shouldnot} être égale à {string}(\\?)")
    @Then("Title {should-shouldnot} be equal to {string}(\\?)")
    public void waitTitleIs(final Boolean not, final String title, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.titleIs(title), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    @Conditioned
    @Lorsque("L'url courante {should-shouldnot} contenir {string}(\\?)")
    @Then("Current url {should-shouldnot} contain {string}(\\?)")
    public void waitUrlContains(final Boolean not, final String fraction, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.urlContains(fraction), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    @Conditioned
    @Lorsque("L'url courante {should-shouldnot} respecter {string}(\\?)")
    @Then("Current url {should-shouldnot} respect {string}(\\?)")
    public void waitUrlMatches(final Boolean not, final String regex, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.urlMatches(regex), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    @Conditioned
    @Lorsque("L'url courante {should-shouldnot} être égale à {string}(\\?)")
    @Then("Current url {should-shouldnot} be equal to {string}(\\?)")
    public void waitUrlToBe(final Boolean not, final String url, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.urlToBe(url), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    @Conditioned
    @Alors("L'élément {page-element} {is-isnot} visible(\\?)")
    @Then("The element {page-element} {is-isnot} visible(\\?)")
    public void waitVisibilityOf(final PageElement pageElement, final Boolean not, List<GherkinStepCondition> conditions) throws TechnicalException, FailureException {
        WebElement webElement = null;
        try {
            webElement = Utilities.findElement(pageElement);
            Wait.until(ExpectedConditions.visibilityOf(webElement), not);
        } catch (Exception e) {
            if (!not) {
                new Result.Failure<>(pageElement, Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
            }
        }
    }

    @Conditioned
    @Alors("Les éléments imbriqués {page-element} et {page-element} {should-shouldnot} être visibles(\\?)")
    @Then("Nested elements {page-element} and {page-element} {should-shouldnot} be visible(\\?)")
    public void waitVisibilityOfNestedElementsLocatedBy(final PageElement pageElement, final Boolean not, final PageElement childPageElement, List<GherkinStepCondition> conditions)
            throws TechnicalException, FailureException {
        try {
            Wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(Utilities.getLocator(pageElement), Utilities.getLocator(childPageElement)), not);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
    }

}
