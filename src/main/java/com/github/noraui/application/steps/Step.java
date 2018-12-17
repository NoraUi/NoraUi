/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import static com.github.noraui.utils.Constants.DOWNLOADED_FILES_FOLDER;
import static com.github.noraui.utils.Constants.USER_DIR;
import static com.github.noraui.utils.Constants.VALUE;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.IPage;
import com.github.noraui.application.page.Page;
import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.browser.DriverFactory;
import com.github.noraui.browser.steps.BrowserSteps;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.cucumber.injector.NoraUiInjector;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinConditionedLoopedStep;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.service.CryptoService;
import com.github.noraui.service.UserNameService;
import com.github.noraui.utils.Constants;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;
import com.google.inject.Inject;

import cucumber.api.CucumberOptions;
import cucumber.runtime.java.StepDefAnnotation;

public class Step implements IStep {

    /**
     * Specific logger
     */
    protected static final Logger logger = LoggerFactory.getLogger(Step.class);
    private static final String SECURE_MASK = "[secure]";

    @Inject
    private UserNameService userNameService;

    @Inject
    private CryptoService cryptoService;

    protected Step() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkStep(IPage page) {
        page.checkPage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebDriver getDriver() {
        return Context.getDriver();
    }

    /**
     * Click on html element.
     *
     * @param toClick
     *            html element
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void clickOn(PageElement toClick, Object... args) throws TechnicalException, FailureException {
        displayMessageAtTheBeginningOfMethod("clickOn: %s in %s", toClick.toString(), toClick.getPage().getApplication());
        try {
            Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(toClick, args))).click();
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK), toClick, toClick.getPage().getApplication()), true,
                    toClick.getPage().getCallBack());
        }
    }

    /**
     * Click on html element by Javascript.
     *
     * @param toClick
     *            html element
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void clickOnByJs(PageElement toClick, Object... args) throws TechnicalException, FailureException {
        displayMessageAtTheBeginningOfMethod("clickOnByJs: %s in %s", toClick.toString(), toClick.getPage().getApplication());
        try {
            Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(toClick, args)));
            ((JavascriptExecutor) getDriver())
                    .executeScript("document.evaluate(\"" + Utilities.getLocatorValue(toClick, args).replace("\"", "\\\"") + "\", document, null, XPathResult.UNORDERED_NODE_SNAPSHOT_TYPE, null).snapshotItem(0).click();");
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK), toClick, toClick.getPage().getApplication()), true,
                    toClick.getPage().getCallBack());
        }
    }

    /**
     * Click on html element by Javascript.
     *
     * @param page
     *            page target application
     * @param xpath
     *            XPath of an element to evaluate
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void clickOnByJs(Page page, String xpath) throws TechnicalException, FailureException {
        displayMessageAtTheBeginningOfMethod("clickOnByJs: %s in %s", xpath, page.getApplication());
        try {
            Context.waitUntil(ExpectedConditions.elementToBeClickable(By.xpath(xpath.replaceAll("\\\\'", "'"))));
            ((JavascriptExecutor) getDriver()).executeScript("document.evaluate(\"" + xpath + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.click();");
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_EVALUATE_XPATH), xpath, page.getApplication()), true, page.getCallBack());
        }
    }

    /**
     * Update a html input text with a text.
     *
     * @param pageElement
     *            Is target element
     * @param textOrKey
     *            Is the new data (text or text in context (after a save))
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateText(PageElement pageElement, String textOrKey, Object... args) throws TechnicalException, FailureException {
        updateText(pageElement, textOrKey, null, args);
    }

    /**
     * Update a html input text with a text.
     *
     * @param pageElement
     *            Is target element
     * @param textOrKey
     *            Is the new data (text or text in context (after a save))
     * @param keysToSend
     *            character to send to the element after {@link org.openqa.selenium.WebElement#sendKeys(CharSequence...) sendKeys} with textOrKey
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateText(PageElement pageElement, String textOrKey, CharSequence keysToSend, Object... args) throws TechnicalException, FailureException {
        String value = getTextOrKey(textOrKey);
        if (!"".equals(value)) {
            try {
                final WebElement element = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(pageElement, args)));
                element.clear();
                if (DriverFactory.IE.equals(Context.getBrowser())) {
                    final String javascript = "arguments[0].value='" + value + "';";
                    ((JavascriptExecutor) getDriver()).executeScript(javascript, element);
                } else {
                    element.sendKeys(value);
                }
                if (keysToSend != null) {
                    element.sendKeys(keysToSend);
                }
            } catch (final Exception e) {
                new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_ERROR_ON_INPUT), pageElement, pageElement.getPage().getApplication()), true,
                        pageElement.getPage().getCallBack());
            }
        } else {
            logger.debug("Empty data provided. No need to update text. If you want clear data, you need use: \"I clear text in ...\"");
        }
    }

    /**
     * Update a html input text with "".
     *
     * @param pageElement
     *            Is target element
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_ERROR_CLEAR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void clearText(PageElement pageElement, Object... args) throws TechnicalException, FailureException {
        clearText(pageElement, null, args);
    }

    /**
     * Update a html input text with "".
     *
     * @param pageElement
     *            Is target element
     * @param keysToSend
     *            character to send to the element after {@link org.openqa.selenium.WebElement#sendKeys(CharSequence...) sendKeys} with textOrKey
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_ERROR_CLEAR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void clearText(PageElement pageElement, CharSequence keysToSend, Object... args) throws TechnicalException, FailureException {
        try {
            final WebElement element = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement, args)));
            element.clear();
            if (keysToSend != null) {
                element.sendKeys(keysToSend);
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_ERROR_CLEAR_ON_INPUT), pageElement, pageElement.getPage().getApplication()), true,
                    pageElement.getPage().getCallBack());
        }
    }

    /**
     * Checks if input text contains expected value.
     *
     * @param pageElement
     *            Is target element
     * @param textOrKey
     *            Is the data to check (text or text in context (after a save))
     * @return true or false
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     */
    protected boolean checkInputText(PageElement pageElement, String textOrKey) throws FailureException, TechnicalException {
        WebElement inputText = null;
        String value = getTextOrKey(textOrKey);
        try {
            inputText = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement)));
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
        return !(inputText == null || value == null || inputText.getAttribute(VALUE) == null || !value.equals(inputText.getAttribute(VALUE).trim()));
    }

    /**
     * Expects that an element contains expected value.
     *
     * @param pageElement
     *            Is target element
     * @param textOrKey
     *            Is the expected data (text or text in context (after a save))
     * @throws FailureException
     *             if the scenario encounters a functional error
     * @throws TechnicalException
     */
    protected void expectText(PageElement pageElement, String textOrKey) throws FailureException, TechnicalException {
        WebElement element = null;
        String value = getTextOrKey(textOrKey);
        try {
            element = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement)));
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
        try {
            Context.waitUntil(ExpectSteps.textToBeEqualsToExpectedValue(Utilities.getLocator(pageElement), value));
        } catch (final Exception e) {
            logger.error("error in expectText. element is [{}]", element == null ? null : element.getText());
            new Result.Failure<>(element == null ? null : element.getText(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_WRONG_EXPECTED_VALUE), pageElement,
                    textOrKey.startsWith(cryptoService.getPrefix()) ? SECURE_MASK : value, pageElement.getPage().getApplication()), true, pageElement.getPage().getCallBack());
        }

    }

    /**
     * Checks mandatory text field.
     *
     * @param pageElement
     *            Is concerned element
     * @return true or false
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected boolean checkMandatoryTextField(PageElement pageElement) throws FailureException {
        WebElement inputText = null;
        try {
            inputText = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement)));
        } catch (final Exception e) {
            new Result.Failure<>(pageElement, Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
        return !(inputText == null || "".equals(inputText.getAttribute(VALUE).trim()));
    }

    protected String readValueTextField(PageElement pageElement) throws FailureException {
        try {
            return Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement))).getAttribute(VALUE);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
        return null;
    }

    /**
     * Checks if HTML text contains expected value.
     *
     * @param pageElement
     *            Is target element
     * @param textOrKey
     *            Is the new data (text or text in context (after a save))
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_WRONG_EXPECTED_VALUE} message (with screenshot, with exception) or with
     *             {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} message
     *             (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void checkText(PageElement pageElement, String textOrKey) throws TechnicalException, FailureException {
        WebElement webElement = null;
        String value = getTextOrKey(textOrKey);
        try {
            webElement = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement)));
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }

        final String innerText = webElement == null ? null : webElement.getText();
        logger.info("checkText() expected [{}] and found [{}].", textOrKey.startsWith(cryptoService.getPrefix()) ? SECURE_MASK : value, innerText);
        if (!value.equals(innerText)) {
            new Result.Failure<>(innerText, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_WRONG_EXPECTED_VALUE), pageElement,
                    textOrKey.startsWith(cryptoService.getPrefix()) ? SECURE_MASK : value, pageElement.getPage().getApplication()), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * Checks if an html element (PageElement) is displayed.
     *
     * @param pageElement
     *            Is target element
     * @param displayed
     *            Is target element supposed to be displayed
     * @throws FailureException
     *             if the scenario encounters a functional error. Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} message
     *             (with screenshot, with exception)
     */
    protected void checkElementVisible(PageElement pageElement, boolean displayed) throws FailureException {
        if (displayed) {
            try {
                Context.waitUntil(ExpectedConditions.visibilityOfElementLocated(Utilities.getLocator(pageElement)));
            } catch (final Exception e) {
                new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_ELEMENT_STILL_VISIBLE), true, pageElement.getPage().getCallBack());
            }
        } else {
            try {
                Context.waitUntil(ExpectedConditions.not(ExpectedConditions.visibilityOfElementLocated(Utilities.getLocator(pageElement))));
            } catch (final Exception e) {
                new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_ELEMENT_STILL_VISIBLE), true, pageElement.getPage().getCallBack());
            }
        }
    }

    /**
     * Checks if an html element (PageElement) is displayed.
     *
     * @param pageElement
     *            Is target element
     * @param present
     *            Is target element supposed to be present
     * @throws FailureException
     *             if the scenario encounters a functional error. Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} message
     *             (with screenshot, with exception)
     */
    protected void checkElementPresence(PageElement pageElement, boolean present) throws FailureException {
        if (present) {
            try {
                Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement)));
            } catch (final Exception e) {
                new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
            }
        } else {
            try {
                Context.waitUntil(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(Utilities.getLocator(pageElement))));
            } catch (final Exception e) {
                new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_ELEMENT_STILL_VISIBLE), true, pageElement.getPage().getCallBack());
            }
        }
    }

    /**
     * Update a html select with a text value.
     *
     * @param pageElement
     *            Is target element
     * @param textOrKey
     *            Is the new data (text or text in context (after a save))
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     *             or
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_VALUE_NOT_AVAILABLE_IN_THE_LIST} message (no screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateList(PageElement pageElement, String textOrKey) throws TechnicalException, FailureException {
        String value = getTextOrKey(textOrKey);
        try {
            setDropDownValue(pageElement, value);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_ERROR_ON_INPUT), pageElement, pageElement.getPage().getApplication()), true,
                    pageElement.getPage().getCallBack());
        }
    }

    /**
     * Check text selected in list (html select option).
     *
     * @param pageElement
     *            Is target element
     * @param text
     *            is text searched
     * @return true or false
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected boolean checkTextSelectedInList(PageElement pageElement, String text) throws FailureException {
        try {
            final WebElement select = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(pageElement)));
            final Select dropDown = new Select(select);
            return text.equalsIgnoreCase(dropDown.getFirstSelectedOption().getText());
        } catch (final Exception e) {
            new Result.Failure<>(text, Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
            return false;
        }
    }

    /**
     * Update a html input text value with a date.
     *
     * @param pageElement
     *            Is target element
     * @param dateType
     *            "any", "future", "today", "future_strict"
     * @param date
     *            Is the new data (date)
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_WRONG_DATE_FORMAT} message (no screenshot, no exception) or with
     *             {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNEXPECTED_DATE} message
     *             (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateDateValidated(PageElement pageElement, String dateType, String date) throws TechnicalException, FailureException {
        logger.debug("updateDateValidated with elementName={}, dateType={} and date={}", pageElement, dateType, date);
        final DateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        final Date today = Calendar.getInstance().getTime();
        try {
            final Date valideDate = formatter.parse(date);
            if ("any".equals(dateType)) {
                logger.debug("update Date with any date: {}", date);
                updateText(pageElement, date);
            } else if (formatter.format(today).equals(date) && ("future".equals(dateType) || "today".equals(dateType))) {
                logger.debug("update Date with today");
                updateText(pageElement, date);
            } else if (valideDate.after(Calendar.getInstance().getTime()) && ("future".equals(dateType) || "future_strict".equals(dateType))) {
                logger.debug("update Date with a date after today: {}", date);
                updateText(pageElement, date);
            } else {
                new Result.Failure<>(date, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNEXPECTED_DATE), Messages.getMessage(Messages.DATE_GREATER_THAN_TODAY)), true,
                        pageElement.getPage().getCallBack());
            }
        } catch (final ParseException e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_WRONG_DATE_FORMAT), pageElement, date), false, pageElement.getPage().getCallBack());
        }

    }

    /**
     * Save value in memory using default target key (Page key + field).
     *
     * @param field
     *            is name of the field to retrieve.
     * @param page
     *            is target page.
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} message (with screenshot, with exception) or with
     *             {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE} message
     *             (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void saveElementValue(String field, Page page) throws TechnicalException, FailureException {
        logger.debug("saveValueInStep: {} in {}.", field, page.getApplication());
        saveElementValue(field, page.getPageKey() + field, page);
    }

    /**
     * Save value in memory.
     *
     * @param field
     *            is name of the field to retrieve.
     * @param targetKey
     *            is the key to save value to
     * @param page
     *            is target page.
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} message (with screenshot, with exception) or with
     *             {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE} message
     *             (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void saveElementValue(String field, String targetKey, Page page) throws TechnicalException, FailureException {
        logger.debug("saveValueInStep: {} to {} in {}.", field, targetKey, page.getApplication());
        String txt = "";
        try {
            final WebElement elem = Utilities.findElement(page, field);
            txt = elem.getAttribute(VALUE) != null ? elem.getAttribute(VALUE) : elem.getText();
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, page.getCallBack());
        }
        try {
            Context.saveValue(targetKey, txt);
            Context.getCurrentScenario().write("SAVE " + targetKey + "=" + txt);
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE), page.getPageElementByKey(field), page.getApplication()), true,
                    page.getCallBack());
        }
    }

    /**
     * Update html radio button by value (value corresponding to key "index").
     *
     * @param pageElement
     *            Is concerned element
     * @param valueKeyOrKey
     *            key printedValues
     * @param printedValues
     *            contain all possible value (order by key)
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateRadioList(PageElement pageElement, String valueKeyOrKey, Map<String, String> printedValues) throws TechnicalException, FailureException {
        final String valueKey = Context.getValue(valueKeyOrKey) != null ? Context.getValue(valueKeyOrKey) : valueKeyOrKey;
        try {
            final List<WebElement> radioButtons = Context.waitUntil(ExpectedConditions.presenceOfAllElementsLocatedBy(Utilities.getLocator(pageElement)));
            String radioToSelect = printedValues.get(valueKey);
            if (radioToSelect == null) {
                radioToSelect = printedValues.get("Default");
            }
            for (final WebElement button : radioButtons) {
                if (button.getAttribute(VALUE).equals(radioToSelect)) {
                    button.click();
                    break;
                }
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON), pageElement), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * Checks that given value is matching the selected radio list button.
     *
     * @param pageElement
     *            The page element
     * @param value
     *            The value to check the selection from
     * @return true if the given value is selected, false otherwise.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected boolean checkRadioList(PageElement pageElement, String value) throws FailureException {
        try {
            final List<WebElement> radioButtons = Context.waitUntil(ExpectedConditions.presenceOfAllElementsLocatedBy(Utilities.getLocator(pageElement)));
            for (final WebElement button : radioButtons) {
                if (button.getAttribute(VALUE).equalsIgnoreCase(value) && button.isSelected()) {
                    return true;
                }
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT), true, pageElement.getPage().getCallBack());
        }
        return false;
    }

    /**
     * Update html radio button by text "input".
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
    protected void updateRadioList(PageElement pageElement, String valueOrKey) throws TechnicalException, FailureException {
        final String value = Context.getValue(valueOrKey) != null ? Context.getValue(valueOrKey) : valueOrKey;
        try {
            final List<WebElement> radioButtons = Context.waitUntil(ExpectedConditions.presenceOfAllElementsLocatedBy(Utilities.getLocator(pageElement)));
            for (final WebElement button : radioButtons) {
                if (button.getAttribute(VALUE).contains(value)) {
                    button.click();
                    break;
                }
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON), pageElement), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * Passes over a specific page element triggering 'mouseover' js event.
     *
     * @param element
     *            Target page element
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_PASS_OVER_ELEMENT} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void passOver(PageElement element) throws TechnicalException, FailureException {
        try {
            final String javascript = "var evObj = document.createEvent('MouseEvents');"
                    + "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);" + "arguments[0].dispatchEvent(evObj);";
            ((JavascriptExecutor) getDriver()).executeScript(javascript, Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(element))));

        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_PASS_OVER_ELEMENT), element, element.getPage().getApplication()), true,
                    element.getPage().getCallBack());
        }

    }

    /**
     * Checks a checkbox type element.
     *
     * @param element
     *            Target page element
     * @param checked
     *            Final checkbox value
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void selectCheckbox(PageElement element, boolean checked, Object... args) throws TechnicalException, FailureException {
        try {
            final WebElement webElement = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(element, args)));
            if (webElement.isSelected() != checked) {
                webElement.click();
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT), element, element.getPage().getApplication()), true,
                    element.getPage().getCallBack());
        }
    }

    /**
     * Checks a checkbox type element (value corresponding to key "valueKey").
     *
     * @param element
     *            Target page element
     * @param valueKeyOrKey
     *            is valueKey (valueKey or key in context (after a save)) to match in values map
     * @param values
     *            Values map
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void selectCheckbox(PageElement element, String valueKeyOrKey, Map<String, Boolean> values) throws TechnicalException, FailureException {
        final String valueKey = Context.getValue(valueKeyOrKey) != null ? Context.getValue(valueKeyOrKey) : valueKeyOrKey;
        try {
            final WebElement webElement = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(element)));
            Boolean checkboxValue = values.get(valueKey);
            if (checkboxValue == null) {
                checkboxValue = values.get("Default");
            }
            if (webElement.isSelected() != checkboxValue.booleanValue()) {
                webElement.click();
            }
        } catch (final Exception e) {
            new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT), element, element.getPage().getApplication()), true,
                    element.getPage().getCallBack());
        }
    }

    /**
     * Switches to the given frame.
     *
     * @param element
     *            The PageElement representing a frame.
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SWITCH_FRAME} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void switchFrame(PageElement element, Object... args) throws FailureException, TechnicalException {
        try {
            Context.waitUntil(ExpectedConditions.frameToBeAvailableAndSwitchToIt(Utilities.getLocator(element, args)));
        } catch (final Exception e) {
            new Result.Failure<>(element, Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_SWITCH_FRAME), element, element.getPage().getApplication()), true,
                    element.getPage().getCallBack());
        }
    }

    /**
     * Updates a html file input with the path of the file to upload.
     *
     * @param pageElement
     *            Is target element
     * @param fileOrKey
     *            Is the file path (text or text in context (after a save))
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value com.github.noraui.utils.Messages#FAIL_MESSAGE_UPLOADING_FILE} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void uploadFile(PageElement pageElement, String fileOrKey, Object... args) throws TechnicalException, FailureException {
        final String path = Context.getValue(fileOrKey) != null ? Context.getValue(fileOrKey) : System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER + File.separator + fileOrKey;
        if (!"".equals(path)) {
            try {
                final WebElement element = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement, args)));
                element.clear();
                if (DriverFactory.IE.equals(Context.getBrowser())) {
                    final String javascript = "arguments[0].value='" + path + "';";
                    ((JavascriptExecutor) getDriver()).executeScript(javascript, element);
                } else {
                    element.sendKeys(path);
                }
            } catch (final Exception e) {
                new Result.Failure<>(e.getMessage(), Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_UPLOADING_FILE), path), true, pageElement.getPage().getCallBack());
            }
        } else {
            logger.debug("Empty data provided. No need to update file upload path. If you want clear data, you need use: \"I clear text in ...\"");
        }
    }

    /**
     * Displays message (concerned activity and list of authorized activities) at the beginning of method in logs.
     *
     * @param methodName
     *            is name of java method
     * @param act
     *            is name of activity
     * @param concernedActivity
     *            is concerned activity
     * @param concernedActivities
     *            is a list of authorized activities
     */
    protected void displayMessageAtTheBeginningOfMethod(String methodName, String act, String concernedActivity, List<String> concernedActivities) {
        logger.debug("{} {}: {} with {} concernedActivity(ies)", act, methodName, concernedActivity, concernedActivities.size());
        int i = 0;
        for (final String activity : concernedActivities) {
            i++;
            logger.debug("  activity N°{}={}", i, activity);
        }
    }

    /**
     * Display message (list of authorized activities) at the beginning of method in logs.
     *
     * @param methodName
     *            is name of java method
     * @param act
     *            is name of activity
     * @param concernedActivities
     *            is a list of authorized activities
     */
    protected void displayMessageAtTheBeginningOfMethod(String methodName, String act, List<String> concernedActivities) {
        logger.debug("{}: {} with {} concernedActivity(ies)", act, methodName, concernedActivities.size());
        int i = 0;
        for (final String activity : concernedActivities) {
            i++;
            logger.debug("  activity N°{}={}", i, activity);
        }
    }

    /**
     * Display message (list of elements) at the beginning of method in logs.
     *
     * @param methodName
     *            is name of java method
     * @param concernedElements
     *            is a list of concerned elements (example: authorized activities)
     */
    protected void displayConcernedElementsAtTheBeginningOfMethod(String methodName, List<String> concernedElements) {
        logger.debug("{}: with {} concernedElements", methodName, concernedElements.size());
        int i = 0;
        for (final String element : concernedElements) {
            i++;
            logger.debug("  element N°{}={}", i, element);
        }
    }

    private void displayMessageAtTheBeginningOfMethod(String message, String element, String application) throws TechnicalException {
        try {
            logger.debug(message, element, application);
        } catch (final Exception te) {
            throw new TechnicalException("Technical problem in the code Messages.formatMessage(String templateMessage, String... args) in NoraUi.", te);
        }
    }

    private void setDropDownValue(PageElement element, String text) throws TechnicalException, FailureException {
        final WebElement select = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(element)));
        final Select dropDown = new Select(select);
        final int index = userNameService.findOptionByIgnoreCaseText(text, dropDown);
        if (index != -1) {
            dropDown.selectByIndex(index);
        } else {
            new Result.Failure<>(text.startsWith(cryptoService.getPrefix()) ? SECURE_MASK : text,
                    Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_VALUE_NOT_AVAILABLE_IN_THE_LIST), element, element.getPage().getApplication()), false, element.getPage().getCallBack());
        }

    }

    /**
     * Checks that a given page displays a html alert.
     * CAUTION: This check do not work with IE: https://github.com/SeleniumHQ/selenium/issues/468
     *
     * @param page
     *            A Page
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void checkAlert(Page page) throws FailureException {
        if (!DriverFactory.IE.equals(Context.getBrowser())) {
            final String txt = getAlertMessage();
            if (txt != null) {
                new Result.Failure<>(txt, Messages.getMessage(Messages.FAIL_MESSAGE_ALERT_FOUND), true, page.getCallBack());
            }
        } else {
            Context.getCurrentScenario().write("SKIPPED for Internet Explorer browser.");
        }
    }

    /**
     * Runs a bunch of steps for a Gherkin loop.
     *
     * @param loopedSteps
     *            GherkinConditionedLoopedStep steps to run
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    protected void runAllStepsInLoop(List<GherkinConditionedLoopedStep> loopedSteps) throws TechnicalException {
        for (final GherkinConditionedLoopedStep loopedStep : loopedSteps) {
            final List<GherkinStepCondition> stepConditions = new ArrayList<>();
            final String[] expecteds = loopedStep.getExpected().split(";");
            final String[] actuals = loopedStep.getActual().split(";");
            if (actuals.length != expecteds.length) {
                throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_EXPECTED_ACTUAL_SIZE_DIFFERENT));
            }
            for (int i = 0; i < expecteds.length; i++) {
                stepConditions.add(new GherkinStepCondition(loopedStep.getKey(), expecteds[i], actuals[i]));
            }
            boolean found = false;
            for (final Entry<String, Method> elem : Context.getCucumberMethods().entrySet()) {
                final Matcher matcher = Pattern.compile("value=(.*)\\)").matcher(elem.getKey());
                if (matcher.find()) {
                    final Matcher matcher2 = Pattern.compile(matcher.group(1)).matcher(loopedStep.getStep());
                    if (matcher2.find()) {
                        Object[] tab;
                        if (elem.getValue().isAnnotationPresent(Conditioned.class)) {
                            tab = new Object[matcher2.groupCount() + 1];
                            tab[matcher2.groupCount()] = stepConditions;
                        } else {
                            tab = new Object[matcher2.groupCount()];
                        }

                        for (int i = 0; i < matcher2.groupCount(); i++) {
                            final Parameter param = elem.getValue().getParameters()[i];
                            if (param.getType() == int.class) {
                                final int ii = Integer.parseInt(matcher2.group(i + 1));
                                tab[i] = ii;
                            } else if (param.getType() == boolean.class) {
                                tab[i] = Boolean.parseBoolean(matcher2.group(i + 1));
                            } else {
                                tab[i] = matcher2.group(i + 1);
                            }
                        }
                        try {
                            found = true;
                            elem.getValue().invoke(NoraUiInjector.getNoraUiInjectorSource().getInstance(elem.getValue().getDeclaringClass()), tab);
                            break;
                        } catch (final Exception e) {
                            throw new TechnicalException("\"" + loopedStep.getStep() + "\"", e.getCause());
                        }
                    }
                }
            }
            if (!found) {
                throw new TechnicalException(String.format(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_STEP_UNDEFINED), loopedStep.getStep()));
            }
        }
    }

    /**
     * @return a String with the message of Alert, return null if no alert message.
     */
    protected String getAlertMessage() {
        String msg = null;
        Alert alert = getDriver().switchTo().alert();
        if (alert != null) {
            msg = alert.getText();
            alert.accept();
        }
        return msg;
    }

    /**
     * Gets all Cucumber methods.
     *
     * @param clazz
     *            Class which is the main point of the application (Decorated with the annotation {@link cucumber.api.CucumberOptions})
     * @return a Map of all Cucumber glue code methods of the application. First part of the entry is the Gherkin matching regular expression.
     *         Second part is the corresponding invokable method.
     */
    public static Map<String, Method> getAllCucumberMethods(Class<?> clazz) {
        final Map<String, Method> result = new HashMap<>();
        final CucumberOptions co = clazz.getAnnotation(CucumberOptions.class);
        final Set<Class<?>> classes = getClasses(co.glue());
        classes.add(BrowserSteps.class);

        for (final Class<?> c : classes) {
            final Method[] methods = c.getDeclaredMethods();
            for (final Method method : methods) {
                for (final Annotation stepAnnotation : method.getAnnotations()) {
                    if (stepAnnotation.annotationType().isAnnotationPresent(StepDefAnnotation.class)) {
                        result.put(stepAnnotation.toString(), method);
                    }
                }
            }
        }
        return result;
    }

    private static Set<Class<?>> getClasses(String[] packagesName) {
        final Set<Class<?>> result = new HashSet<>();
        for (final String packageName : packagesName) {
            result.addAll(new Reflections(packageName, new SubTypesScanner(false)).getSubTypesOf(Step.class));
        }
        return result;
    }

    /**
     * @param textOrKey
     *            Is the new data (text or text in context (after a save))
     * @return a string from context or not (and crypted or not).
     * @throws TechnicalException
     */
    protected String getTextOrKey(String textOrKey) throws TechnicalException {
        String value = Context.getValue(textOrKey) != null ? Context.getValue(textOrKey) : textOrKey;
        if (value.startsWith(cryptoService.getPrefix())) {
            value = cryptoService.decrypt(value);
        }
        return value;
    }

}
