package noraui.browser.steps;

import static noraui.utils.Constants.ALERT_KEY;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.fr.Et;
import cucumber.api.java.fr.Lorsque;
import cucumber.api.java.fr.Quand;
import cucumber.metrics.annotation.time.Time;
import cucumber.metrics.annotation.time.TimeName;
import cucumber.metrics.annotation.time.Times;
import noraui.browser.Auth;
import noraui.browser.WindowManager;
import noraui.cucumber.annotation.Conditioned;
import noraui.exception.Callbacks;
import noraui.exception.FailureException;
import noraui.exception.Result;
import noraui.exception.TechnicalException;
import noraui.gherkin.GherkinCondition;
import noraui.utils.Context;
import noraui.utils.Messages;

public class BrowserSteps {

    private static final Logger logger = Logger.getLogger(BrowserSteps.class);

    /**
     * Open new window with conditions.
     *
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinCondition}).
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Quand("J'ouvre une nouvelle fenêtre.")
    @When("I open a new window.")
    public void openNewWindow(List<GherkinCondition> conditions) throws FailureException {
        try {
            Set<String> initialWindows = Context.getDriver().getWindowHandles();
            String js = "window.open(\"\");";
            ((JavascriptExecutor) Context.getDriver()).executeScript(js);
            String newWindowHandle = Context.waitUntil(WindowManager.newWindowOpens(initialWindows));
            Context.getDriver().switchTo().window(newWindowHandle);
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.FAIL_MESSAGE_UNABLE_TO_OPEN_A_NEW_WINDOW, true, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * Open Url if different with conditions.
     *
     * @param pageKey
     *            is the key of page (example: SALTO_HOME)
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_APPLICATION} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Times({ @Time(name = "AM"), @Time(name = "{pageKey}") })
    @Conditioned
    @Lorsque("'(.*)' est ouvert.")
    @Given("'(.*)' is opened.")
    public void openUrlIfDifferent(@TimeName("pageKey") String pageKey, List<GherkinCondition> conditions) throws TechnicalException, FailureException {
        goToUrl(pageKey, false);
    }

    /**
     * Go to Url.
     *
     * @param pageKey
     *            is key corresponding to url of target.
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_A_NEW_WINDOW} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Et("Je retourne vers '(.*)'")
    @And("I go back to '(.*)'")
    public void goToUrl(String pageKey) throws TechnicalException, FailureException {
        goToUrl(pageKey, true);
    }

    /**
     * Switch window when the scenario contain more one windows (one more application for example).
     *
     * @param windowKey
     *            the key of window (popup, ...) Example: OSCAR.
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SWITCH_WINDOW} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Quand("Je passe à la fenêtre '(.*)'")
    @When("I switch to '(.*)' window")
    public void switchWindow(String windowKey) throws TechnicalException, FailureException {
        String handleToSwitch = Context.getWindows().get(windowKey);
        if (handleToSwitch != null) {
            Context.getDriver().switchTo().window(handleToSwitch);
            Context.getDriver().manage().window().maximize();
            Context.setMainWindow(windowKey);
        } else {
            new Result.Failure<>(windowKey, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_SWITCH_WINDOW, windowKey), true, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * Restart WebDriver with a {@link Context#clear()}.
     */
    @Et("Je redémarre le web driver")
    @And("I restart the web driver")
    public void restartWebDriver() {
        Context.clear();
        Auth.setConnected(false);
    }

    /**
     * Closes window and switches to target window with conditions.
     *
     * @param key
     *            is the key of application (Ex: SALTO).
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_CLOSE_APP} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Lorsque("Je ferme la fenêtre actuelle et passe à la fenêtre '(.*)'.")
    @Then("I close current window and switch to '(.*)' window.")
    public void closeWindowAndSwitchTo(String key, List<GherkinCondition> conditions) throws TechnicalException, FailureException {
        closeWindowAndSwitchTo(key);
    }

    /**
     * Closes all browser windows and switches to target window with conditions.
     *
     * @param key
     *            is the key of application (Ex: SALTO).
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_CLOSE_APP} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Conditioned
    @Lorsque("^Je ferme toutes les fenêtres sauf '(.*)'.")
    @Then("^I close all windows except '(.*)'.")
    public void closeAllWindowsAndSwitchTo(String key, List<GherkinCondition> conditions) throws TechnicalException, FailureException {
        closeAllWindowsAndSwitchTo(key);
    }

    /**
     * Closes a specific window and go to the given url.
     * This method is called by reflexion from @see exceptions.ExceptionCallback#getCallBack(String).
     *
     * @param key
     *            window key to close
     * @param backTo
     *            url to go back to
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_CLOSE_APP} message (with screenshot, with exception)
     *             or {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_APPLICATION} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    public void closeWindowAndSwitchTo(String key, String backTo) throws TechnicalException, FailureException {
        closeWindowAndSwitchTo(key);
        goToUrl(backTo);
    }

    private void closeWindowAndSwitchTo(String key) throws TechnicalException, FailureException {
        String mainWindow = Context.getMainWindow();
        try {
            if (Context.getWindows().size() > 1) {
                closeWindow();
            }
            switchWindow(key);
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_CLOSE_APP, mainWindow), true, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    private void closeWindow() {
        for (Entry<String, String> w : Context.getWindows().entrySet()) {
            if (w.getValue().equals(Context.getDriver().getWindowHandle())) {
                Context.removeWindow(w.getKey());
                break;
            }
        }
        Context.getDriver().close();
    }

    /**
     * Closes all windows except the given one.
     * This method is called by reflexion from @see exceptions.ExceptionCallback#getCallBack(String).
     *
     * @param key
     *            window key to keep
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_CLOSE_APP} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    public void closeAllWindowsAndSwitchTo(String key) throws TechnicalException, FailureException {
        String openedWindows = "";
        String handleToKeep = Context.getWindow(key);
        try {
            if (Context.getWindows().size() > 1) {
                closeAllWindows(handleToKeep);
            }
            Context.getWindows().clear();
            if (handleToKeep != null) {
                switchToWindow(key, handleToKeep);
            } else {
                goToUrl(Context.getApplication(key).getHomeKey(), true);
            }
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_CLOSE_APP, openedWindows), true, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    private void goToUrl(String pageKey, boolean force) throws TechnicalException, FailureException {
        try {
            String urlToOpen = Context.getUrlByPagekey(pageKey);
            if (urlToOpen != null) {
                if (force || !Context.getDriver().getCurrentUrl().startsWith(urlToOpen)) {
                    String windowHandle = Context.getDriver().getWindowHandle();
                    navigateTo(Context.getApplicationByPagekey(pageKey), urlToOpen, windowHandle);
                }
                ((JavascriptExecutor) Context.getDriver()).executeScript("window.alert = function(msg){console.log('" + ALERT_KEY + "' + msg);};");
            } else {
                logger.error("Unable to open page: " + pageKey);
                throw new TechnicalException(String.format(Messages.FAIL_MESSAGE_UNABLE_TO_OPEN_PAGE, pageKey));
            }
        } catch (Exception e) {
            logger.error("Error in goToUrl:", e);
            int indexOfUnderscore = pageKey.indexOf('_');
            String appName = indexOfUnderscore != -1 ? pageKey.substring(0, indexOfUnderscore) : pageKey;
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_OPEN_PAGE, appName), true, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * navigateTo change url on the same window.
     */
    private void navigateTo(String pageKey, String urlToOpen, String windowHandle) {
        Context.addWindow(pageKey, windowHandle);
        Context.setMainWindow(pageKey);
        Context.getDriver().navigate().to(urlToOpen);
    }

    /**
     * switchToWindow switch to an other Window.
     */
    private void switchToWindow(String key, String handleToKeep) {
        Context.addWindow(key, handleToKeep);
        Context.setMainWindow(key);
        Context.getDriver().switchTo().window(handleToKeep);
    }

    private void closeAllWindows(String handleToKeep) {
        for (String windowHandle : Context.getDriver().getWindowHandles()) {
            if (!windowHandle.equals(handleToKeep)) {
                Context.getDriver().switchTo().window(windowHandle);
                Context.getDriver().close();
            }
        }
    }

}
