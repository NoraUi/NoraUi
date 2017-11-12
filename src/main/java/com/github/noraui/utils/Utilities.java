package com.github.noraui.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.ini4j.Ini;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.Page;
import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.browser.DriverFactory;

import cucumber.api.Scenario;

public class Utilities {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Utilities.class);

    private static final String UTILITIES_ERROR_TAKING_SCREENSHOT = "UTILITIES_ERROR_TAKING_SCREENSHOT";

    /**
     * @param applicationKey
     *            is key of application
     * @param code
     *            is key of selector (CAUTION: if you use any % char. {@link String#format(String, Object...)})
     * @param args
     *            is list of args ({@link String#format(String, Object...)})
     * @return the selector
     */
    public static String getSelectorValue(String applicationKey, String code, Object... args) {
        String selector = "";
        logger.debug("getLocator with this application key : {}", applicationKey);
        logger.debug("getLocator with this locator file : {}", Context.iniFiles.get(applicationKey));
        final Ini ini = Context.iniFiles.get(applicationKey);

        final Map<String, String> section = ini.get(code);
        if (section != null) {
            final Entry<String, String> entry = section.entrySet().iterator().next();
            selector = String.format(entry.getValue(), args);
        }
        return selector;
    }

    /**
     * @param element
     *            is a PageElement
     * @param args
     *            is list of args ({@link String#format(String, Object...)})
     * @return the selector
     */
    public static String getLocatorValue(PageElement element, Object... args) {
        return getSelectorValue(element.getPage().getApplication(), element.getPage().getPageKey() + element.getKey(), args);
    }

    /**
     * This method read a application descriptor file and return a {@link org.openqa.selenium.By} object (xpath, id, link ...).
     *
     * @param applicationKey
     *            Name of application. Each application has its fair description file.
     * @param code
     *            Name of element on the web Page.
     * @param args
     *            list of description (xpath, id, link ...) for code.
     * @return a {@link org.openqa.selenium.By} object (xpath, id, link ...)
     */
    public static By getLocator(String applicationKey, String code, Object... args) {
        By locator = null;
        logger.debug("getLocator with this application key : {}", applicationKey);
        logger.debug("getLocator with this locator file : {}", Context.iniFiles.get(applicationKey));
        final Ini ini = Context.iniFiles.get(applicationKey);

        final Map<String, String> section = ini.get(code);
        if (section != null) {
            final Entry<String, String> entry = section.entrySet().iterator().next();
            final String selector = String.format(entry.getValue(), args);
            if ("css".equals(entry.getKey())) {
                locator = By.cssSelector(selector);
            } else if ("link".equals(entry.getKey())) {
                locator = By.linkText(selector);
            } else if ("id".equals(entry.getKey())) {
                locator = By.id(selector);
            } else if ("name".equals(entry.getKey())) {
                locator = By.name(selector);
            } else if ("xpath".equals(entry.getKey())) {
                locator = By.xpath(selector);
            } else if ("class".equals(entry.getKey())) {
                locator = By.className(selector);
            } else {
                Assert.fail(entry.getKey() + " NOT implemented!");
            }
        } else {
            Assert.fail(code + " NOT implemented in ini file " + Context.iniFiles.get(applicationKey) + "!");
        }
        return locator;
    }

    /**
     * This method read a application descriptor file and return a {@link org.openqa.selenium.By} object (xpath, id, link ...).
     *
     * @param page
     *            is target page
     * @param code
     *            Name of element on the web Page.
     * @param args
     *            list of description (xpath, id, link ...) for code.
     * @return a {@link org.openqa.selenium.By} object (xpath, id, link ...)
     */
    public static By getLocator(Page page, String code, Object... args) {
        return getLocator(page.getApplication(), page.getPageKey() + code, args);
    }

    /**
     * This method read a application descriptor file and return a {@link org.openqa.selenium.By} object (xpath, id, link ...).
     *
     * @param element
     *            is PageElement find in page.
     * @param args
     *            list of description (xpath, id, link ...) for code.
     * @return a {@link org.openqa.selenium.By} object (xpath, id, link ...)
     */
    public static By getLocator(PageElement element, Object... args) {
        return getLocator(element.getPage().getApplication(), element.getPage().getPageKey() + element.getKey(), args);
    }

    /**
     * Find the first {@link WebElement} using the given method.
     * This method is affected by the 'implicit wait' times in force at the time of execution.
     * The findElement(..) invocation will return a matching row, or try again repeatedly until
     * the configured timeout is reached.
     *
     * @param webDriver
     *            instance of webDriver
     * @param applicationKey
     *            key of application
     * @param code
     *            Name of element on the web Page.
     * @param args
     *            can be a index i
     * @return the first {@link WebElement} using the given method
     */
    public static WebElement findElement(WebDriver webDriver, String applicationKey, String code, Object... args) {
        return webDriver.findElement(getLocator(applicationKey, code, args));
    }

    /**
     * Find the first {@link WebElement} using the given method.
     * This method is affected by the 'implicit wait' times in force at the time of execution.
     * The findElement(..) invocation will return a matching row, or try again repeatedly until
     * the configured timeout is reached.
     *
     * @param page
     *            is target page
     * @param code
     *            Name of element on the web Page.
     * @param args
     *            can be a index i
     * @return the first {@link WebElement} using the given method
     */
    public static WebElement findElement(Page page, String code, Object... args) {
        return Context.getDriver().findElement(getLocator(page.getApplication(), page.getPageKey() + code, args));
    }

    /**
     * Find the first {@link WebElement} using the given method.
     * This method is affected by the 'implicit wait' times in force at the time of execution.
     * The findElement(..) invocation will return a matching row, or try again repeatedly until
     * the configured timeout is reached.
     *
     * @param element
     *            is PageElement find in page.
     * @param args
     *            can be a index i
     * @return the first {@link WebElement} using the given method
     */
    public static WebElement findElement(PageElement element, Object... args) {
        return Context.getDriver().findElement(getLocator(element.getPage().getApplication(), element.getPage().getPageKey() + element.getKey(), args));
    }

    /**
     * Set value to a variable (null is forbiden, so set default value).
     *
     * @param value
     *            is value setted if value is not null.
     * @param defValue
     *            is value setted if value is null.
     * @return a {link java.lang.String} with the value not null.
     */
    public static String setProperty(String value, String defValue) {
        if (value != null) {
            return value;
        }
        return defValue;
    }

    /**
     * Check if element present and get first one.
     *
     * @param element
     *            is {link org.openqa.selenium.By} find in page.
     * @return first {link org.openqa.selenium.WebElement} finded present element.
     */
    public static WebElement isElementPresentAndGetFirstOne(By element) {

        final WebDriver webDriver = Context.getDriver();

        webDriver.manage().timeouts().implicitlyWait(DriverFactory.IMPLICIT_WAIT * 2, TimeUnit.MICROSECONDS);

        final List<WebElement> foundElements = webDriver.findElements(element);
        final boolean exists = !foundElements.isEmpty();

        webDriver.manage().timeouts().implicitlyWait(DriverFactory.IMPLICIT_WAIT, TimeUnit.MICROSECONDS);

        if (exists) {
            return foundElements.get(0);
        } else {
            return null;
        }

    }

    /**
     * Check if element {link org.openqa.selenium.By} is present.
     *
     * @param element
     *            is {link org.openqa.selenium.By} find in page.
     * @return a boolean with the result.
     */
    public static boolean isElementPresent(By element) {
        return isElementPresentAndGetFirstOne(element) != null;
    }

    /**
     * Indicates a driver that can capture a screenshot and store it in different ways.
     *
     * @param scenario
     *            is instance of {link cucumber.api.Scenario}
     */
    public static void takeScreenshot(Scenario scenario) {
        if (!DriverFactory.HTMLUNIT.equals(Context.getBrowser())) {
            final byte[] screenshot = ((TakesScreenshot) Context.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/png");
        } else {
            logger.warn(Messages.getMessage(UTILITIES_ERROR_TAKING_SCREENSHOT), Context.getBrowser());
        }
    }

    public enum OperatingSystem {

        WINDOWS("windows", "windows", ".exe"), LINUX("linux", "linux", ""), MAC("mac", "mac", "");

        private final String operatingSystemName;
        private final String operatingSystemDir;
        private final String suffixBinary;

        OperatingSystem(String operatingSystemName, String operatingSystemDir, String suffixBinary) {
            this.operatingSystemName = operatingSystemName;
            this.operatingSystemDir = operatingSystemDir;
            this.suffixBinary = suffixBinary;
        }

        public static OperatingSystem getOperatingSystem(String osName) {
            for (final OperatingSystem operatingSystemName : values()) {
                if (osName.toLowerCase().contains(operatingSystemName.getOperatingSystemName())) {
                    return operatingSystemName;
                }
            }
            throw new IllegalArgumentException("Unrecognised operating system name '" + osName + "'");
        }

        public static Set<OperatingSystem> getCurrentOperatingSystemAsAHashSet() {
            final String currentOperatingSystemName = System.getProperties().getProperty("os.name");

            final Set<OperatingSystem> listOfOperatingSystems = new HashSet<>();
            listOfOperatingSystems.add(getOperatingSystem(currentOperatingSystemName));

            return listOfOperatingSystems;
        }

        public static OperatingSystem getCurrentOperatingSystem() {
            final String currentOperatingSystemName = System.getProperties().getProperty("os.name");
            return getOperatingSystem(currentOperatingSystemName);
        }

        public String getOperatingSystemName() {
            return operatingSystemName;
        }

        public String getOperatingSystemDir() {
            return operatingSystemDir;
        }

        public String getSuffixBinary() {
            return suffixBinary;
        }

    }

    public enum SystemArchitecture {

        ARCHITECTURE_64_BIT("64bit"), ARCHITECTURE_32_BIT("32bit");

        private final String systemArchitectureName;
        private static final SystemArchitecture defaultSystemArchitecture = ARCHITECTURE_32_BIT;
        private static List<String> architecture64bitNames = Arrays.asList("amd64", "x86_64");

        SystemArchitecture(String systemArchitectureName) {
            this.systemArchitectureName = systemArchitectureName;
        }

        public String getSystemArchitectureName() {
            return systemArchitectureName;
        }

        public static SystemArchitecture getSystemArchitecture(String currentArchitecture) {
            SystemArchitecture result = defaultSystemArchitecture;
            if (architecture64bitNames.contains(currentArchitecture)) {
                result = ARCHITECTURE_64_BIT;
            }
            return result;
        }

        public static SystemArchitecture getCurrentSystemArchitecture() {
            final String currentArchitecture = System.getProperties().getProperty("os.arch");
            logger.info("os.arch: {}", currentArchitecture);
            return getSystemArchitecture(currentArchitecture);
        }

    }

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
     * An expectation for checking that there is at least one element present on a web page.
     *
     * @param locator
     *            used to find the element
     * @param nb
     *            is exactly number of responses
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
     *            used to find the element
     * @param nb
     *            is exactly number of responses
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

}
