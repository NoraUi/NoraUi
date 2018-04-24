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
import org.openqa.selenium.JavascriptExecutor;
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
     * The inner text and 'value' attribute of the element are checked.
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
                    final WebElement element = driver.findElement(locator);
                    if (element != null && value != null) {
                        return !((element.getAttribute(VALUE) == null || !value.equals(element.getAttribute(VALUE).trim())) && !value.equals(element.getText().replaceAll("\n", "")));
                    }
                } catch (final Exception e) {
                }
                return false;
            }
        };
    }

    /**
     * Expects that at least one of the given elements is present.
     *
     * @param locators
     *            The list of elements identified by their locators
     * @return true or false
     */
    public static ExpectedCondition<WebElement> atLeastOneOfTheseElementsIsPresent(final By... locators) {
        return new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(@Nullable WebDriver driver) {
                WebElement element = null;
                if (driver != null && locators.length > 0) {
                    for (final By b : locators) {
                        try {
                            element = driver.findElement(b);
                        } catch (final Exception e) {
                            continue;
                        }
                    }
                }
                return element;
            }
        };
    }

    /**
     * An expectation for checking that nb elements that match the locator are present on the web page.
     *
     * @param locator
     *            Locator of element
     * @param nb
     *            Expected number of elements
     * @return the list of WebElements once they are located
     */
    public static ExpectedCondition<List<WebElement>> presenceOfNbElementsLocatedBy(final By locator, final int nb) {
        return new ExpectedCondition<List<WebElement>>() {
            @Override
            public List<WebElement> apply(WebDriver driver) {
                final List<WebElement> elements = driver.findElements(locator);
                return elements.size() == nb ? elements : null;
            }
        };
    }

    /**
     * An expectation for checking that nb elements present on the web page that match the locator
     * are visible. Visibility means that the elements are not only displayed but also have a height
     * and width that is greater than 0.
     *
     * @param locator
     *            Locator of element
     * @param nb
     *            Expected number of elements
     * @return the list of WebElements once they are located
     */
    public static ExpectedCondition<List<WebElement>> visibilityOfNbElementsLocatedBy(final By locator, final int nb) {
        return new ExpectedCondition<List<WebElement>>() {
            @Override
            public List<WebElement> apply(WebDriver driver) {
                int nbElementIsDisplayed = 0;
                final List<WebElement> elements = driver.findElements(locator);
                for (final WebElement element : elements) {
                    if (element.isDisplayed()) {
                        nbElementIsDisplayed++;
                    }
                }
                return nbElementIsDisplayed == nb ? elements : null;
            }
        };
    }

    /**
     * Expects that the target page is completely loaded.
     *
     * @return true or false
     */
    public static ExpectedCondition<Boolean> waitForLoad() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
    }
}
