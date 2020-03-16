/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.selenium;

import static com.github.noraui.Constants.VALUE;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.beust.jcommander.internal.Nullable;

public class NoraUiExpectedConditions {

    private NoraUiExpectedConditions() {
        // Utility class
    }

    /**
     * Expects that the target element equals the given value as text.
     * The inner text and 'value' attribute of the element are checked.
     *
     * @param locator
     *            is the selenium locator
     * @param value
     *            is the expected value
     * @return true or false
     */
    public static ExpectedCondition<Boolean> textToBeEqualsToExpectedValue(final By locator, final String value) {
        return (@Nullable WebDriver driver) -> {
            try {
                final WebElement element = driver.findElement(locator);
                if (element != null && value != null) {
                    return !((element.getAttribute(VALUE) == null || !value.equals(element.getAttribute(VALUE).trim())) && !value.equals(element.getText().replaceAll("\n", "")));
                }
            } catch (final Exception e) {
            }
            return false;
        };
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
    public static ExpectedCondition<Boolean> textContainsExpectedValue(final By locator, final String value) {
        return (@Nullable WebDriver driver) -> {
            try {
                final WebElement element = driver.findElement(locator);
                if (element != null && value != null) {
                    return !((element.getAttribute(VALUE) == null || !element.getAttribute(VALUE).trim().contains(value)) && !element.getText().replaceAll("\n", "").contains(value));
                }
            } catch (final Exception e) {
            }
            return false;
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
        return (@Nullable WebDriver driver) -> {
            WebElement element = null;
            if (driver != null && locators.length > 0) {
                for (final By b : locators) {
                    try {
                        element = driver.findElement(b);
                        break;
                    } catch (final Exception e) {
                    }
                }
            }
            return element;
        };
    }

    /**
     * An expectation for checking that nb elements that match the locator are present on the web page.
     *
     * @param locator
     *            Locator of elements
     * @param nb
     *            Expected number of elements
     * @return the list of WebElements once they are located
     */
    public static ExpectedCondition<List<WebElement>> presenceOfNbElementsLocatedBy(final By locator, final int nb) {
        return (WebDriver driver) -> {
            try {
                final List<WebElement> elements = driver.findElements(locator);
                return elements.size() == nb ? elements : null;
            } catch (final Exception e) {
                return null;
            }
        };
    }

    /**
     * An expectation for checking that nb elements that match (or more) the locator are present on the web page.
     *
     * @param locator
     *            Locator of elements
     * @param nb
     *            Expected number of elements
     * @return the list of WebElements once they are located
     */
    public static ExpectedCondition<List<WebElement>> presenceOfMinimumElementsLocatedBy(final By locator, final int nb) {
        return (WebDriver driver) -> {
            try {
                final List<WebElement> elements = driver.findElements(locator);
                return elements.size() >= nb ? elements : null;
            } catch (final Exception e) {
                return null;
            }
        };
    }

    /**
     * An expectation for checking that nb elements that match (or less) the locator are present on the web page.
     *
     * @param locator
     *            Locator of elements
     * @param nb
     *            Expected number of elements
     * @return the list of WebElements once they are located
     */
    public static ExpectedCondition<List<WebElement>> presenceOfMaximumElementsLocatedBy(final By locator, final int nb) {
        return (WebDriver driver) -> {
            try {
                final List<WebElement> elements = driver.findElements(locator);
                return elements.size() <= nb ? elements : null;
            } catch (final Exception e) {
                return null;
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
        return (WebDriver driver) -> {
            int nbElementIsDisplayed = 0;
            final List<WebElement> elements;
            try {
                elements = driver.findElements(locator);
                for (final WebElement element : elements) {
                    if (element.isDisplayed()) {
                        nbElementIsDisplayed++;
                    }
                }
            } catch (final Exception e) {
                return null;
            }
            return nbElementIsDisplayed == nb ? elements : null;
        };
    }

    /**
     * Expects that the target page is completely loaded.
     *
     * @return true or false
     */
    public static ExpectedCondition<Boolean> waitForLoad() {
        return (WebDriver driver) -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }

    /**
     * @param currentHandles
     *            is list of opened windows.
     * @return a string with new Window Opens (GUID)
     */
    public static ExpectedCondition<String> newWindowOpens(final Set<String> currentHandles) {
        return (@Nullable WebDriver driver) -> {
            if (driver != null && !currentHandles.equals(driver.getWindowHandles())) {
                for (String s : driver.getWindowHandles()) {
                    if (!currentHandles.contains(s)) {
                        return s;
                    }
                }
            }
            return null;
        };
    }

    /**
     * Click can be done without alert on Element.
     *
     * @param element
     *            is target WebElement.
     * @return true or false
     */
    public static ExpectedCondition<Boolean> clickCanBeDoneWithoutAlertOnElement(final WebElement element) {
        return (@Nullable WebDriver driver) -> {
            try {
                element.click();
                return true;
            } catch (UnhandledAlertException e) {
                driver.switchTo().alert().dismiss();
            } catch (ElementNotVisibleException e) {
            }
            return false;
        };
    }

    /**
     * click can be done without alert on element located.
     *
     * @param locator
     *            is the selenium locator
     * @return true or false
     */
    public static ExpectedCondition<Boolean> clickCanBeDoneWithoutAlertOnElementLocated(final By locator) {
        return (@Nullable WebDriver driver) -> {
            try {
                driver.findElement(locator).click();
                return true;
            } catch (UnhandledAlertException e) {
                driver.switchTo().alert().dismiss();
            } catch (ElementNotVisibleException e) {
            }
            return false;
        };
    }

    /**
     * text to be presentInElement on element located.
     *
     * @param locator
     *            is the selenium locator
     * @return content string (not empty).
     */
    public static ExpectedCondition<String> textToBePresentInElement(final By locator) {
        return (@Nullable WebDriver driver) -> {
            try {
                final WebElement element = driver.findElement(locator);
                if (element != null && element.getText() != null && !"".equals(element.getText())) {
                    return element.getText();
                }
            } catch (final Exception e) {
            }
            return null;
        };
    }

}
