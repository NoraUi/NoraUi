package com.github.noraui.browser.waits;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.noraui.utils.Context;

import java.util.HashMap;
import java.util.Map;

public class Wait {

    private static Wait uniqueInstance;

    /**
     * Single global instance of WebDriverWait
     */
    private final Map<WebDriver, WebDriverWait> webDriverWaits;

    public Wait() {
        webDriverWaits = new HashMap<WebDriver, WebDriverWait>();
    }

    // Méthode statique qui sert de pseudo-constructeur (utilisation du mot clef
    // "synchronized" pour le multithread).
    public static synchronized Wait getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Wait();
        }
        return uniqueInstance;
    }

    public void addDriver(WebDriver webDriver){
        if(!webDriverWaits.containsKey(webDriver)) {
            webDriverWaits.put(webDriver, new WebDriverWait(webDriver, Context.getTimeout()));
        }
    }

    public void removeDriver(WebDriver webDriver){
        webDriverWaits.remove(webDriver);
    }

    protected WebDriverWait getWebDriverWait(){
        return webDriverWaits.get(Context.getDriver());
    }

    /**
     * Wait will ignore instances of NotFoundException that are encountered (thrown) by default in
     * the 'until' condition, and immediately propagate all others. You can add more to the ignore
     * list by calling ignoring(exceptions to add).
     *
     * @param <T>
     *            The function's expected return type.
     * @param condition
     *            the parameter to pass to the {@link ExpectedCondition}
     * @return The function's return value if the function returned something different
     *         from null or false before the timeout expired.
     */
    @SuppressWarnings("unchecked")
    public static <T> T until(ExpectedCondition<T> condition) {
        return (T) untilAnd(condition, false).get();
    }

    @SuppressWarnings("unchecked")
    public static <T> T until(ExpectedCondition<T> condition, boolean not) {
        return (T) untilAnd(condition, not).get();
    }

    /**
     * Wait will ignore instances of NotFoundException that are encountered (thrown) by default in
     * the 'until' condition, and immediately propagate all others. You can add more to the ignore
     * list by calling ignoring(exceptions to add).
     *
     * @param condition
     *            the parameter to pass to the {@link ExpectedCondition}
     * @param <T>
     *            The function's expected return type.
     * @param timeOutInSeconds
     *            The timeout in seconds when an expectation is called
     * @return The function's return value if the function returned something different
     *         from null or false before the timeout expired.
     */
    @SuppressWarnings("unchecked")
    public static <T> T until(ExpectedCondition<T> condition, int timeOutInSeconds) {
        return (T) untilAnd(condition, timeOutInSeconds, false).get();
    }

    @SuppressWarnings("unchecked")
    public static <T> T until(ExpectedCondition<T> condition, int timeOutInSeconds, boolean not) {
        return (T) untilAnd(condition, timeOutInSeconds, not).get();
    }

    @SuppressWarnings("unchecked")
    public static <T> ChainableWait<T> untilAnd(ExpectedCondition<T> condition) {
        return (ChainableWait<T>) untilAnd(condition, false);
    }

    public static <T> ChainableWait<?> untilAnd(ExpectedCondition<T> condition, boolean not) {
        return not ? new ChainableWait<Boolean>(getInstance().getWebDriverWait()).wait(ExpectedConditions.not(condition)) : new ChainableWait<T>(getInstance().getWebDriverWait()).wait(condition);
    }

    @SuppressWarnings("unchecked")
    public static <T> ChainableWait<T> untilAnd(ExpectedCondition<T> condition, int timeOutInSeconds) {
        return (ChainableWait<T>) untilAnd(condition, timeOutInSeconds, false);
    }

    public static <T> ChainableWait<?> untilAnd(ExpectedCondition<T> condition, int timeOutInSeconds, boolean not) {
        return not ? new ChainableWait<Boolean>(new WebDriverWait(Context.getDriver(), timeOutInSeconds)).wait(ExpectedConditions.not(condition))
                : new ChainableWait<T>(new WebDriverWait(Context.getDriver(), timeOutInSeconds)).wait(condition);
    }
}
