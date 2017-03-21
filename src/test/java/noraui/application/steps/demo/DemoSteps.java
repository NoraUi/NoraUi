package noraui.application.steps.demo;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import noraui.application.page.Page;
import noraui.application.page.Page.PageElement;
import noraui.application.page.demo.DemoPage;
import noraui.application.steps.Step;
import noraui.exception.FailureException;
import noraui.exception.Result;
import noraui.exception.TechnicalException;
import noraui.utils.Constants;
import noraui.utils.Messages;

public class DemoSteps extends Step {

    private static Logger logger = Logger.getLogger(DemoSteps.class.getName());

    private DemoPage demoPage;

    public DemoSteps() throws TechnicalException {
        super();
        this.demoPage = (DemoPage) Page.getInstance(DemoPage.class);
    }

    /**
     * Click on html element in DEMO.
     *
     * @param toClick
     *            html element
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     */
    @When("I click on 'DEMO_HOME(.*)'")
    public void clickOn(String toClick) throws TechnicalException, FailureException {
        logger.info("DEMO clickOn: " + toClick);
        clickOn(demoPage.getPageElementByKey(toClick));
    }

    @When("I update date 'DEMO_HOME(.*)' with a '(.*)' date '(.*)'")
    public void updateDate(String elementName, String dateType, String date) throws TechnicalException, FailureException {
        if (!"".equals(date)) {
            PageElement pageElement = demoPage.getPageElementByKey(elementName);
            if (date.matches(Constants.DATE_FORMAT_REG_EXP)) {
                updateDateValidated(pageElement, dateType, date);
            } else {
                new Result.Failure<>(date, Messages.format(Messages.FAIL_MESSAGE_WRONG_DATE_FORMAT, date, elementName), false, demoPage.getCallBack());
            }
        }
    }

    /**
     * Update value of html radio.
     *
     * @param elementName
     *            is target element
     * @param input
     *            is the new value
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON} message (with screenshot, with exception)
     * @throws FailureException
     */
    @And("I update radio list 'DEMO_HOME(.*)' with '(.*)'")
    public void updateRadioList(String elementName, String input) throws TechnicalException, FailureException {
        updateRadioList(demoPage.getPageElementByKey(elementName), input);
    }

    @Then("I update checkbox 'DEMO_HOME(.*)' with '(.*)':")
    public void selectCheckbox(String elementKey, String valueKey, Map<String, Boolean> values) throws TechnicalException, FailureException {
        selectCheckbox(demoPage.getPageElementByKey(elementKey), true);
        selectCheckbox(demoPage.getPageElementByKey(elementKey), valueKey, values);
        checkRadioList(demoPage.getPageElementByKey(elementKey), valueKey);
    }

    /**
     * Click on html element by Javascript in DEMO.
     *
     * @param toClick
     *            html element
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     */
    @When("I click by js on 'DEMO_HOME(.*)'")
    public void clickOnByJs(String toClick) throws TechnicalException, FailureException {
        logger.info("DEMO clickOn by js: " + toClick);
        clickOnByJs(demoPage.getPageElementByKey(toClick));
    }

    @And("I check message '(.*)' on alert")
    public void check(String message) throws TechnicalException, FailureException {
        ((JavascriptExecutor) getDriver()).executeScript("console.log('test');");
        String msg = getLastConsoleAlertMessage();
        if (msg == null || !msg.equals(message)) {
            new Result.Failure<>(message, Messages.format(Messages.FAIL_MESSAGE_NOT_FOUND_ON_ALERT, message), false, demoPage.getCallBack());
        }
    }

    /**
     * Update a html input text with a text in a DEMO page.
     *
     * @param elementName
     *            Is target element
     * @param text
     *            Is the new data (text)
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     */
    @When("I update text 'DEMO_HOME(.*)' with '(.*)'")
    public void updateText(String elementName, String text) throws TechnicalException, FailureException {
        clearText(demoPage.getPageElementByKey(elementName));
        updateText(demoPage.getPageElementByKey(elementName), text);
        checkMandatoryTextField(demoPage.getPageElementByKey(elementName));
        String value = readValueTextField(demoPage.getPageElementByKey(elementName));
        if (!text.equals(value)) {
            new Result.Failure<>(value, Messages.format(Messages.FAIL_MESSAGE_WRONG_EXPECTED_VALUE, demoPage.getPageElementByKey(elementName), text, demoPage.getApplication()), true,
                    demoPage.getCallBack());
        }
        saveValueInStep(elementName, demoPage);
    }

    @And("I check text 'DEMO_HOME(.*)' with '(.*)'")
    public void checkInputText(String elementName, String text) throws FailureException {
        checkInputText(demoPage.getPageElementByKey(elementName), text);
    }

    /**
     * Update a html select input text with a text data (if exist in html "option" list).
     *
     * @param elementName
     *            Is target element
     * @param text
     *            Is the new data
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     */
    @When("I update select list 'DEMO_HOME(.*)' with '(.*)'")
    public void updateList(String elementName, String text) throws TechnicalException, FailureException {
        updateList(demoPage.getPageElementByKey(elementName), text);
        checkTextSelectedInList(demoPage.getPageElementByKey(elementName), text);
    }

}
