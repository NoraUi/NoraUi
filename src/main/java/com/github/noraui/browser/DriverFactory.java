/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.browser;

import static com.github.noraui.utils.Constants.DOWNLOADED_FILES_FOLDER;
import static com.github.noraui.utils.Constants.USER_DIR;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;
import com.github.noraui.utils.Utilities.OperatingSystem;
import com.github.noraui.utils.Utilities.SystemArchitecture;

@Loggable
public class DriverFactory {

    static Logger LOGGER;

    /** Default web drivers implicit wait **/
    public static final long IMPLICIT_WAIT = 500;

    public static final String IE = "ie";
    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String DEFAULT_DRIVER = CHROME;

    /** Selenium drivers. **/
    private final Map<String, WebDriver> drivers;

    public DriverFactory() {
        drivers = new HashMap<>();
    }

    /**
     * Get selenium driver. Drivers are lazy loaded.
     *
     * @return selenium driver
     */
    public WebDriver getDriver() {
        // Driver's name is retrieved by system properties
        String driverName = Context.getBrowser();
        driverName = driverName != null ? driverName : DEFAULT_DRIVER;
        WebDriver driver = null;
        if (!drivers.containsKey(driverName)) {
            try {
                driver = generateWebDriver(driverName);
            } catch (final TechnicalException e) {
                LOGGER.error("error DriverFactory.getDriver()", e);
            }
        } else {
            driver = drivers.get(driverName);
        }
        return driver;
    }

    /**
     * Clear loaded drivers
     */
    public void clear() {
        for (final WebDriver wd : drivers.values()) {
            wd.quit();
        }
        drivers.clear();
    }

    /**
     * Generates an ie webdriver. Unable to use it with a proxy. Causes a crash.
     *
     * @return
     *         An ie webdriver
     * @throws TechnicalException
     *             if an error occured when Webdriver setExecutable to true.
     */
    private WebDriver generateIEDriver() throws TechnicalException {
        final String pathWebdriver = DriverFactory.getPath(Driver.IE);
        if (!new File(pathWebdriver).setExecutable(true)) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE));
        }
        LOGGER.info("Generating IE driver ({}) ...", pathWebdriver);

        System.setProperty(Driver.IE.getDriverName(), pathWebdriver);

        final InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions();
        internetExplorerOptions.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        internetExplorerOptions.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        internetExplorerOptions.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
        internetExplorerOptions.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        internetExplorerOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        internetExplorerOptions.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        internetExplorerOptions.setCapability("disable-popup-blocking", true);

        setLoggingLevel(internetExplorerOptions);

        // Proxy configuration
        if (Context.getProxy().getProxyType() != ProxyType.UNSPECIFIED && Context.getProxy().getProxyType() != ProxyType.AUTODETECT) {
            internetExplorerOptions.setCapability(CapabilityType.PROXY, Context.getProxy());
        }

        return new InternetExplorerDriver(internetExplorerOptions);
    }

    /**
     * Generates a chrome webdriver.
     *
     * @return
     *         A chrome webdriver
     * @throws TechnicalException
     *             if an error occured when Webdriver setExecutable to true.
     */
    private WebDriver generateGoogleChromeDriver() throws TechnicalException {
        final String pathWebdriver = DriverFactory.getPath(Driver.CHROME);
        if (!new File(pathWebdriver).setExecutable(true)) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE));
        }
        LOGGER.info("Generating Chrome driver ({}) ...", pathWebdriver);

        System.setProperty(Driver.CHROME.getDriverName(), pathWebdriver);

        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);

        setLoggingLevel(chromeOptions);

        if (Context.isHeadless()) {
            chromeOptions.addArguments("--headless");
        }

        // Proxy configuration
        if (Context.getProxy().getProxyType() != ProxyType.UNSPECIFIED && Context.getProxy().getProxyType() != ProxyType.AUTODETECT) {
            chromeOptions.setCapability(CapabilityType.PROXY, Context.getProxy());
        }

        // Set custom downloaded file path. When you check content of downloaded file by robot.
        final HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.default_directory", System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER);
        chromeOptions.setExperimentalOption("prefs", chromePrefs);

        // Set custom chromium (if you not use default chromium on your target device)
        final String targetBrowserBinaryPath = Context.getWebdriversProperties("targetBrowserBinaryPath");
        if (targetBrowserBinaryPath != null && !"".equals(targetBrowserBinaryPath)) {
            chromeOptions.setBinary(targetBrowserBinaryPath);
        }

        final String withWhitelistedIps = Context.getWebdriversProperties("withWhitelistedIps");
        if (withWhitelistedIps != null && !"".equals(withWhitelistedIps)) {
            final ChromeDriverService service = new ChromeDriverService.Builder().withWhitelistedIps(withWhitelistedIps).withVerbose(false).build();
            return new ChromeDriver(service, chromeOptions);
        } else {
            return new ChromeDriver(chromeOptions);
        }
    }

    /**
     * Generates a firefox webdriver.
     *
     * @return
     *         A firefox webdriver
     * @throws TechnicalException
     *             if an error occured when Webdriver setExecutable to true.
     */
    private WebDriver generateFirefoxDriver() throws TechnicalException {
        final String pathWebdriver = DriverFactory.getPath(Driver.FIREFOX);
        if (!new File(pathWebdriver).setExecutable(true)) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_WEBDRIVER_SET_EXECUTABLE));
        }
        LOGGER.info("Generating Firefox driver ({}) ...", pathWebdriver);

        System.setProperty(Driver.FIREFOX.getDriverName(), pathWebdriver);

        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        final FirefoxBinary firefoxBinary = new FirefoxBinary();

        firefoxOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        firefoxOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);

        setLoggingLevel(firefoxOptions);

        // Proxy configuration
        if (Context.getProxy().getProxyType() != ProxyType.UNSPECIFIED && Context.getProxy().getProxyType() != ProxyType.AUTODETECT) {
            firefoxOptions.setCapability(CapabilityType.PROXY, Context.getProxy());
        }

        if (Context.isHeadless()) {
            firefoxBinary.addCommandLineOptions("--headless");
            firefoxOptions.setBinary(firefoxBinary);
        }
        firefoxOptions.setLogLevel(FirefoxDriverLogLevel.FATAL);

        return new FirefoxDriver(firefoxOptions);
    }

    /**
     * Generates a selenium webdriver following a name given in parameter.
     * By default a chrome driver is generated.
     *
     * @param driverName
     *            The name of the web driver to generate
     * @return
     *         An instance a web driver whose type is provided by driver name given in parameter
     * @throws TechnicalException
     *             if an error occured when Webdriver setExecutable to true.
     */
    private WebDriver generateWebDriver(String driverName) throws TechnicalException {
        WebDriver driver;
        if (IE.equals(driverName)) {
            driver = generateIEDriver();
        } else if (CHROME.equals(driverName)) {
            driver = generateGoogleChromeDriver();
        } else if (FIREFOX.equals(driverName)) {
            driver = generateFirefoxDriver();
        } else {
            driver = generateGoogleChromeDriver();
        }
        // As a workaround: NoraUi specify window size manually, e.g. window_size: 1920 x 1080 (instead of .window().maximize()).
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.MILLISECONDS);
        drivers.put(driverName, driver);
        return driver;

    }

    /**
     * Sets the logging level of the generated web driver.
     *
     * @param caps
     *            The web driver's MutableCapabilities (FirefoxOptions)
     * @param level
     *            The logging level
     */
    private void setLoggingLevel(MutableCapabilities caps) {
        final LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        logPrefs.enable(LogType.CLIENT, Level.OFF);
        logPrefs.enable(LogType.DRIVER, Level.OFF);
        logPrefs.enable(LogType.PERFORMANCE, Level.OFF);
        logPrefs.enable(LogType.PROFILER, Level.OFF);
        logPrefs.enable(LogType.SERVER, Level.OFF);
        caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
    }

    /**
     * Get the path of the driver given in parameters.
     *
     * @param currentDriver
     *            The driver to get the path of
     * @return
     *         A String representation of the path
     */
    public static String getPath(Driver currentDriver) {
        final OperatingSystem currentOperatingSystem = OperatingSystem.getCurrentOperatingSystem();
        String format = "";
        if ("webdriver.ie.driver".equals(currentDriver.driverName)) {
            format = Utilities.setProperty(Context.getWebdriversProperties(currentDriver.driverName), "src/test/resources/drivers/%s/internetexplorer/%s/IEDriverServer%s");
        } else if ("webdriver.chrome.driver".equals(currentDriver.driverName)) {
            format = Utilities.setProperty(Context.getWebdriversProperties(currentDriver.driverName), "src/test/resources/drivers/%s/googlechrome/%s/chromedriver%s");
        } else if ("webdriver.gecko.driver".equals(currentDriver.driverName)) {
            format = Utilities.setProperty(Context.getWebdriversProperties(currentDriver.driverName), "src/test/resources/drivers/%s/firefox/%s/geckodriver%s");
        }
        return String.format(format, currentOperatingSystem.getOperatingSystemDir(), SystemArchitecture.getCurrentSystemArchitecture().getSystemArchitectureName(),
                currentOperatingSystem.getSuffixBinary());
    }

    /**
     * List of external non-java web drivers.
     *
     * @author Nicolas HALLOUIN
     */
    public enum Driver {
        IE("webdriver.ie.driver"), CHROME("webdriver.chrome.driver"), FIREFOX("webdriver.gecko.driver");
        private String driverName;

        Driver(String driverName) {
            this.driverName = driverName;
        }

        String getDriverName() {
            return driverName;
        }
    }
}
