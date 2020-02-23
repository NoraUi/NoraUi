package com.github.noraui.browser.waits;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.noraui.utils.Context;

public class Wait {
    /**
     * Single global instance of WebDriverWait
     */
    private static WebDriverWait webDriverWait;

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

    public static Object until(ExpectedCondition<?> condition, boolean not) {
        return untilAnd(condition, not).get();
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
     * @param timeOutInSeconds
     *            The timeout in seconds when an expectation is called
     * @return The function's return value if the function returned something different
     *         from null or false before the timeout expired.
     */
    public static Object until(ExpectedCondition<?> condition, int timeOutInSeconds) {
        return untilAnd(condition, timeOutInSeconds, false).get();
    }

    public static Object until(ExpectedCondition<?> condition, int timeOutInSeconds, boolean not) {
        return untilAnd(condition, timeOutInSeconds, not).get();
    }

    @SuppressWarnings("unchecked")
    public static <T> ChainableWait<T> untilAnd(ExpectedCondition<T> condition) {
        return (ChainableWait<T>) untilAnd(condition, false);
    }

    public static <T> ChainableWait<?> untilAnd(ExpectedCondition<T> condition, boolean not) {
        if (webDriverWait == null) {
            webDriverWait = new WebDriverWait(Context.getDriver(), Context.getTimeout());
        }
        return not ? new ChainableWait<Boolean>(webDriverWait).wait(ExpectedConditions.not(condition))
                : new ChainableWait<T>(webDriverWait).wait(condition);
    }

    @SuppressWarnings("unchecked")
    public static <T> ChainableWait<T> untilAnd(ExpectedCondition<T> condition, int timeOutInSeconds) {
        return (ChainableWait<T>) untilAnd(condition, timeOutInSeconds, false);
    }

    public static <T> ChainableWait<?> untilAnd(ExpectedCondition<T> condition, int timeOutInSeconds, boolean not) {
        return not
                ? new ChainableWait<Boolean>(new WebDriverWait(Context.getDriver(), timeOutInSeconds))
                        .wait(ExpectedConditions.not(condition))
                : new ChainableWait<T>(new WebDriverWait(Context.getDriver(), timeOutInSeconds)).wait(condition);
    }
}
