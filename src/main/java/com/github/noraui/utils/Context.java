/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.utils;

import static com.github.noraui.utils.Constants.DATA_IN;
import static com.github.noraui.utils.Constants.DATA_OUT;
import static com.github.noraui.utils.Constants.SCENARIO_FILE;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.joda.time.DateTime;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.Application;
import com.github.noraui.application.steps.Step;
import com.github.noraui.browser.Auth;
import com.github.noraui.browser.DriverFactory;
import com.github.noraui.browser.WindowManager;
import com.github.noraui.browser.steps.BrowserSteps;
import com.github.noraui.data.DataIndex;
import com.github.noraui.data.DataInputProvider;
import com.github.noraui.data.DataOutputProvider;
import com.github.noraui.data.DataProvider;
import com.github.noraui.data.DataUtils;
import com.github.noraui.data.console.OutputConsoleDataProvider;
import com.github.noraui.data.csv.CsvDataProvider;
import com.github.noraui.data.db.DBDataProvider;
import com.github.noraui.data.excel.InputExcelDataProvider;
import com.github.noraui.data.excel.OutputExcelDataProvider;
import com.github.noraui.data.gherkin.InputGherkinDataProvider;
import com.github.noraui.data.rest.RestDataProvider;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.Callbacks.Callback;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.ScenarioRegistry;
import com.github.noraui.main.ScenarioInitiator;
import com.github.noraui.model.Model;
import com.github.noraui.model.ModelList;

import cucumber.api.Scenario;

/**
 * Cucumber context.
 */
public class Context {

    /**
     * Specific logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Context.class);

    public static final String STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME = BrowserSteps.class.getCanonicalName();
    public static final String GO_TO_URL_METHOD_NAME = "goToUrl";
    public static final String RESTART_WEB_DRIVER_METHOD_NAME = "restartWebDriver";
    public static final String HTTP_PROXY = "http_proxy";
    public static final String HTTPS_PROXY = "https_proxy";
    public static final String NO_PROXY = "no_proxy";
    public static final String HEADLESS = "headless";
    public static final String LOCALE = "locale";
    public static final String AUTH_TYPE = "authentication";
    public static final String CRYPTO_KEY = "crypto.key";
    public static final String DISPLAY_STACK_TRACE = "display.stacktrace";
    public static final String TIMEOUT_KEY = "timeout";
    public static final String BROWSER_KEY = "browser";
    public static final String MODEL_PACKAGES = "model.packages";
    public static final String SELECTORS_VERSION = "selectors.version";

    /**
     * DEMO
     * 
     * @deprecated because use {@link #COUNTRIES_KEY} instead.
     */
    @Deprecated
    public static final String DEMO_KEY = "demo";

    /**
     * @deprecated because use {@link #COUNTRIES_HOME} instead.
     */
    @Deprecated
    public static final String DEMO_HOME = "DEMO_HOME";

    /**
     * LOGOGAME
     * 
     * @deprecated because use {@link #COUNTRIES_KEY} instead.
     */
    @Deprecated
    public static final String LOGOGAME_KEY = "logogame";

    /**
     * @deprecated because use {@link #COUNTRIES_HOME} instead.
     */
    @Deprecated
    public static final String LOGOGAME_HOME = "LOGOGAME_HOME";

    /**
     * COUNTRIES
     */
    public static final String COUNTRIES_KEY = "countries";
    public static final String COUNTRIES_HOME = "COUNTRIES_HOME";

    /**
     * GITHUBAPI
     */
    public static final String GITHUBAPI_KEY = "githubapi";
    public static final String GITHUBAPI_HOME = "GITHUBAPI_HOME";

    private static final String NOT_SET_LABEL = "NOT_SET_LABEL";
    private static final String CONTEXT_PROPERTIES_FILE_NOT_FOUND = "CONTEXT_PROPERTIES_FILE_NOT_FOUND";
    private static final String CONTEXT_APP_INI_FILE_NOT_FOUND = "CONTEXT_APP_INI_FILE_NOT_FOUND";
    private static final String CONTEXT_LOCALE_USED = "CONTEXT_LOCALE_USED";
    private static final String CONTEXT_ERROR_WHEN_PLUGING_DATA_PROVIDER = "CONTEXT_ERROR_WHEN_PLUGING_DATA_PROVIDER";
    private static Properties scenariosProperties = null;
    private static Properties webdriversProperties = null;

    /**
     * Static context instance.
     */
    protected static volatile Context instance = null;

    /**
     * Context driver factory.
     */
    private final DriverFactory driverFactory;

    /**
     * Context window factory.
     */
    private final WindowManager windowManager;

    /**
     * Context scenario registry.
     */
    private final ScenarioRegistry scenarioRegistry;

    /**
     * Current running data from Scenario data set.
     */
    private int currentScenarioData;

    /**
     * Current number of failures from Scenario.
     */
    private int nbFailure;

    /**
     * Current number of warnings from Scenario.
     */
    private int nbWarning;

    /**
     * Does current Scenario contain warnings ?
     */
    private boolean scenarioHasWarning;

    /**
     * Excel filename for the current running scenario
     */
    private String scenarioName;

    /**
     * Current Cucumber scenario
     */
    private Scenario currentScenario;

    /**
     * start date of current Cucumber scenario.
     */
    private DateTime startCurrentScenario;

    /**
     * Single global instance of WebDriverWait
     */
    private WebDriverWait webDriverWait;

    /**
     * Single global custom instance of WebDriverWait
     */
    private WebDriverWait webDriverCustomWait;

    /**
     * browser: chrome, firefox or ie.
     */
    private String browser;

    /**
     * Maximum timeout
     */
    private int timeout;

    /**
     * cryptoKey for offend password or confidential data. Use this special char in front of your key: ℗.
     * Example: ℗password
     */
    private String cryptoKey;

    /**
     * Proxy
     */
    private Proxy proxy;

    /**
     * Current locale
     */
    private Locale currentLocale;

    /**
     * List of model packages in case of using Models for input data
     */
    private String modelPackages = "";

    /**
     * Is stacktrace displayed ?
     */
    private boolean displayStackTrace;

    /**
     * Is headless mode enable ?
     */
    private boolean isHeadless;

    /**
     * Instance of DataInputProvider
     */
    private DataInputProvider dataInputProvider;

    /**
     * Instance of DataOutputProvider
     */
    private DataOutputProvider dataOutputProvider;

    /**
     * All java methods mapped by cucumber annotations.
     */
    private Map<String, Method> cucumberMethods;

    /**
     * Selectors version
     */
    protected String selectorsVersion;

    /**
     * Map of ExceptionCallbacks
     */
    protected Callbacks exceptionCallbacks;

    /**
     * Map of Applications
     */
    protected Map<String, Application> applications;

    protected static volatile Map<String, Ini> iniFiles;

    protected String resourcesPath;

    protected Properties applicationProperties;

    /**
     * Private constructor
     */
    protected Context() {
        driverFactory = new DriverFactory();
        windowManager = new WindowManager();
        scenarioRegistry = new ScenarioRegistry();
        currentScenarioData = nbFailure = nbWarning = 0;
        scenarioHasWarning = false;
        exceptionCallbacks = new Callbacks();
        applications = new HashMap<>();
        cucumberMethods = new HashMap<>();
    }

    /**
     * Get context singleton.
     *
     * @return context instance
     */
    public static Context getInstance() {
        if (instance == null) {
            instance = new Context();
        }
        return instance;
    }

    public synchronized void initializeEnv(String propertiesFile) {
        logger.info("Context > initializeEnv()");

        iniFiles = new HashMap<>();
        applicationProperties = initPropertiesFile(Thread.currentThread().getContextClassLoader(), propertiesFile);

        // init locale
        initializeLocale();

        // init scenarios paths
        initializeScenarioProperties(ScenarioInitiator.class.getClassLoader());

        resourcesPath = System.getProperty("resourcespath");

        // set list of model packages
        modelPackages = getProperty(MODEL_PACKAGES, applicationProperties);

        plugDataProvider(applicationProperties);

        // Paths configuration
        getDataInputProvider().setDataInPath(resourcesPath + DATA_IN);
        getDataOutputProvider().setDataOutPath(resourcesPath + DATA_OUT);

    }

    public synchronized void initializeRobot(Class<?> clazz) throws TechnicalException {
        logger.info("Context > initializeRobot() with {}", clazz.getCanonicalName());
        // set browser: chrome,firefox or ie
        browser = getProperty(BROWSER_KEY, applicationProperties);

        // set Webdriver file: src/test/resources/drivers/...
        initializeWebdriversProperties(Thread.currentThread().getContextClassLoader());

        // wait delay until web element is displayed.
        timeout = setIntProperty(TIMEOUT_KEY, applicationProperties);

        // set version of selectors used to deliver several versions
        selectorsVersion = getProperty(SELECTORS_VERSION, applicationProperties);

        // proxies configuration
        proxy = new Proxy();
        proxy.setAutodetect(true);
        final String httpProxy = getProperty(HTTP_PROXY, applicationProperties);
        if (httpProxy != null && !"".equals(httpProxy)) {
            proxy.setAutodetect(false);
            proxy.setHttpProxy(httpProxy);
        }

        final String httpsProxy = getProperty(HTTPS_PROXY, applicationProperties);
        if (httpsProxy != null && !"".equals(httpsProxy)) {
            proxy.setAutodetect(false);
            proxy.setSslProxy(httpsProxy);
        }

        final String noProxy = getProperty(NO_PROXY, applicationProperties);
        if (noProxy != null && !"".equals(noProxy)) {
            proxy.setAutodetect(false);
            proxy.setNoProxy(noProxy);
        }

        // authentication mode configuration
        Auth.setAuthenticationType(getProperty(AUTH_TYPE, applicationProperties));

        // set crypto key
        cryptoKey = System.getProperty(CRYPTO_KEY);
        if (cryptoKey == null) {
            logger.warn("{} not set. You can not use crypto feature.", CRYPTO_KEY);
        }

        // stacktrace configuration
        displayStackTrace = "true".equals(getProperty(DISPLAY_STACK_TRACE, applicationProperties));

        // enable browser headless mode ?
        isHeadless = "true".equals(getProperty(HEADLESS, applicationProperties));

        // init driver callbacks
        exceptionCallbacks.put(Callbacks.RESTART_WEB_DRIVER, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, RESTART_WEB_DRIVER_METHOD_NAME);
        exceptionCallbacks.put(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_DEMO_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, GO_TO_URL_METHOD_NAME, DEMO_HOME);
        exceptionCallbacks.put(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_LOGOGAME_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, GO_TO_URL_METHOD_NAME, LOGOGAME_HOME);
        exceptionCallbacks.put(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_COUNTRIES_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, GO_TO_URL_METHOD_NAME, COUNTRIES_HOME);
        // init applications
        initApplicationDom(clazz.getClassLoader(), selectorsVersion, DEMO_KEY);
        applications.put(DEMO_KEY, new Application(DEMO_HOME, getProperty(DEMO_KEY, applicationProperties) + "/index.html"));

        initApplicationDom(clazz.getClassLoader(), selectorsVersion, LOGOGAME_KEY);
        applications.put(LOGOGAME_KEY, new Application(LOGOGAME_HOME, getProperty(LOGOGAME_KEY, applicationProperties) + "/index.html"));

        initApplicationDom(clazz.getClassLoader(), selectorsVersion, COUNTRIES_KEY);
        applications.put(COUNTRIES_KEY, new Application(COUNTRIES_HOME, getProperty(COUNTRIES_KEY, applicationProperties) + "/index.html"));

        applications.put(GITHUBAPI_KEY, new Application(GITHUBAPI_HOME, getProperty(GITHUBAPI_KEY, applicationProperties)));

        // read and init all cucumber methods
        cucumberMethods = Step.getAllCucumberMethods(clazz);
    }

    /**
     * Clear context
     */
    public static void clear() {
        instance.driverFactory.clear();
        instance.windowManager.clear();
        instance.scenarioRegistry.clear();
        instance.webDriverWait = null;
        instance.webDriverCustomWait = null;
        instance.scenarioName = null;
    }

    /**
     * Get the selenium driver from the driver factory.
     *
     * @return selenium webdriver
     */
    public static WebDriver getDriver() {
        return getInstance().driverFactory.getDriver();
    }

    public static Map<String, String> getWindows() {
        return getInstance().windowManager.getWindows();
    }

    public static String getWindow(String key) {
        return getInstance().windowManager.getWindow(key);
    }

    public static void addWindow(String key, String windowHandle) {
        getInstance().windowManager.addWindow(key, windowHandle);
    }

    public static void removeWindow(String key) {
        getInstance().windowManager.removeWindow(key);
    }

    public static void setMainWindow(String window) {
        getInstance().windowManager.setMainWindow(window);
    }

    public static String getMainWindow() {
        return getInstance().windowManager.getMainWindow();
    }

    public static void saveValue(String key, String value) {
        getInstance().scenarioRegistry.put(key, value);
    }

    public static String getValue(String key) {
        return getInstance().scenarioRegistry.get(key);
    }

    public static void emptyScenarioRegistry() {
        getInstance().scenarioRegistry.clear();
    }

    public static void goToNextData() {
        getInstance().currentScenarioData++;
        getInstance().scenarioHasWarning = false;
    }

    public static void addFailure() {
        getInstance().nbFailure++;
    }

    public static void addWarning() {
        getInstance().nbWarning++;
    }

    /**
     * @return line's number of data.
     */
    public static int getCurrentScenarioData() {
        return getInstance().currentScenarioData;
    }

    public static int getNbFailure() {
        return getInstance().nbFailure;
    }

    public static int getNbWarning() {
        return getInstance().nbWarning;
    }

    public static void setNbFailure(int nbFailure) {
        getInstance().nbFailure = nbFailure;
    }

    public static void setNbWarning(int nbWarning) {
        getInstance().nbWarning = nbWarning;
    }

    public static void scenarioHasWarning(boolean warning) {
        getInstance().scenarioHasWarning = warning;
    }

    public static boolean scenarioHasWarning() {
        return getInstance().scenarioHasWarning;
    }

    public static void setCurrentScenarioData(int current) {
        getInstance().currentScenarioData = current;
    }

    public static void goToNextFeature() {
        getInstance().currentScenarioData = 0;
        getInstance().nbFailure = 0;
        getInstance().nbWarning = 0;
    }

    public static String getScenarioName() {
        return getInstance().scenarioName;
    }

    public static void setScenarioName(String scenarioName) {
        if (getInstance().scenarioName == null || !getInstance().scenarioName.equals(scenarioName)) {
            try {
                initDataId(scenarioName);
            } catch (final TechnicalException te) {
                logger.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE), te);
            }
        }
        getInstance().scenarioName = scenarioName;
    }

    public static Scenario getCurrentScenario() {
        return getInstance().currentScenario;
    }

    public static void setCurrentScenario(final Scenario scenario) {
        getInstance().currentScenario = scenario;
    }

    public static DateTime getStartCurrentScenario() {
        return getInstance().startCurrentScenario;
    }

    public static void startCurrentScenario() {
        getInstance().startCurrentScenario = DateTime.now();
    }

    public static <T> T waitUntil(ExpectedCondition<T> condition) {
        if (getInstance().webDriverWait == null) {
            getInstance().webDriverWait = new WebDriverWait(getDriver(), getTimeout());
        }
        return getInstance().webDriverWait.until(condition);
    }

    public static <T> T waitUntil(ExpectedCondition<T> condition, int time) {
        getInstance().webDriverCustomWait = new WebDriverWait(getDriver(), time);
        return getInstance().webDriverCustomWait.until(condition);
    }

    public static DataInputProvider getDataInputProvider() {
        return getInstance().dataInputProvider;
    }

    public static void setDataInputProvider(DataInputProvider dataInputProvider) {
        getInstance().dataInputProvider = dataInputProvider;
    }

    public static DataOutputProvider getDataOutputProvider() {
        return getInstance().dataOutputProvider;
    }

    public static void setDataOutputProvider(DataOutputProvider dataOutputProvider) {
        getInstance().dataOutputProvider = dataOutputProvider;
    }

    protected static Properties initPropertiesFile(ClassLoader loader, String propertiesFileName) {
        if (loader != null) {
            final InputStream in = loader.getResourceAsStream(propertiesFileName);
            final Properties props = new Properties();
            try {
                if (in == null) {
                    logger.error(Messages.getMessage(CONTEXT_PROPERTIES_FILE_NOT_FOUND), propertiesFileName);
                } else {
                    logger.info("Reading properties file ({}).", propertiesFileName);
                    props.load(in);
                }
            } catch (final IOException e) {
                logger.error("error Context.initPropertiesFile()", e);
            }
            logger.info("Loaded properties from {} = {}.", propertiesFileName, props);
            return props;
        }
        return null;
    }

    public static String getProperty(String key, Properties propertyFile) {
        if (propertyFile == null) {
            return null;
        }
        final String p = propertyFile.getProperty(key);
        if (p == null) {
            logger.error("{}{}", key, Messages.getMessage(NOT_SET_LABEL));
        }
        return p;
    }

    public static String getResourcesPath() {
        return getInstance().resourcesPath;
    }

    protected static void initApplicationDom(ClassLoader loader, String version, String applicationKey) {
        try {
            final InputStream data = loader.getResourceAsStream("selectors/" + version + "/" + applicationKey + ".ini");
            if (data != null) {
                final Ini ini = new Ini(data);
                iniFiles.put(applicationKey, ini);
            }
        } catch (final InvalidFileFormatException e) {
            logger.error("error Context.initApplicationDom()", e);
        } catch (final IOException e) {
            logger.error(Messages.getMessage(CONTEXT_APP_INI_FILE_NOT_FOUND), applicationKey, e);
        }
    }

    public static String getScenarioProperty(String key) {
        return getProperty(key, scenariosProperties);
    }

    public static void initializeScenarioProperties(ClassLoader loader) {
        scenariosProperties = initPropertiesFile(loader, SCENARIO_FILE);
    }

    public static String getWebdriversProperties(String key) {
        return getProperty(key, webdriversProperties);
    }

    public static void initializeWebdriversProperties(ClassLoader loader) {
        webdriversProperties = initPropertiesFile(loader, "webdrivers.properties");
    }

    public static String getBrowser() {
        return getInstance().browser;
    }

    public static int getTimeout() {
        return getInstance().timeout;
    }

    public static String getCryptoKey() {
        return getInstance().cryptoKey;
    }

    public static Proxy getProxy() {
        return getInstance().proxy;
    }

    public static Locale getLocale() {
        return getInstance().currentLocale;
    }

    public static boolean isStackTraceDisplayed() {
        return getInstance().displayStackTrace;
    }

    public static boolean isHeadless() {
        return getInstance().isHeadless;
    }

    public static String getModelPackages() {
        return getInstance().modelPackages;
    }

    public static String getSelectorsVersion() {
        return getInstance().selectorsVersion;
    }

    public static Map<String, Method> getCucumberMethods() {
        return getInstance().cucumberMethods;
    }

    public static Callback getCallBack(String key) {
        return getInstance().exceptionCallbacks.get(key);
    }

    public static Application getApplication(String applicationKey) {
        return getInstance().applications.get(applicationKey);
    }

    public static String getUrlByPagekey(String pageKey) {
        if (pageKey != null) {
            for (final Map.Entry<String, Application> application : getInstance().applications.entrySet()) {
                for (final Map.Entry<String, String> urlPage : application.getValue().getUrlPages().entrySet()) {
                    if (pageKey.equals(urlPage.getKey())) {
                        return Auth.usingAuthentication(urlPage.getValue());
                    }
                }
            }
        } else {
            return null;
        }
        return null;
    }

    public static String getApplicationByPagekey(String pageKey) {
        if (pageKey != null) {
            for (final Map.Entry<String, Application> application : getInstance().applications.entrySet()) {
                for (final Map.Entry<String, String> urlPage : application.getValue().getUrlPages().entrySet()) {
                    if (pageKey.equals(urlPage.getKey())) {
                        return application.getKey();
                    }
                }
            }
        } else {
            return null;
        }
        return null;
    }

    private static int setIntProperty(String key, Properties propertyFile) {
        final String property = propertyFile.getProperty(key);
        int p = 0;
        if (property == null) {
            logger.error("{}{}", key, Messages.getMessage(NOT_SET_LABEL));
        } else {
            p = Integer.parseInt(property);
            logger.info("{} = {}", key, p);
        }
        return p;
    }

    private static void initDataId(String scenarioName) throws TechnicalException {
        final List<DataIndex> indexData = new ArrayList<>();
        try {
            Context.getDataInputProvider().prepare(scenarioName);
            final Class<Model> model = Context.getDataInputProvider().getModel(Context.getModelPackages());
            if (model != null) {
                final String[] headers = Context.getDataInputProvider().readLine(0, false);
                if (headers != null) {
                    final Constructor<Model> modelConstructor = DataUtils.getModelConstructor(model, headers);
                    final Map<String, ModelList> fusionedData = DataUtils.fusionProcessor(model, modelConstructor);
                    int dataIndex = 0;
                    for (final Entry<String, ModelList> e : fusionedData.entrySet()) {
                        dataIndex++;
                        indexData.add(new DataIndex(dataIndex, e.getValue().getIds()));
                    }
                } else {
                    logger.error(Messages.getMessage(ScenarioInitiator.SCENARIO_INITIATOR_ERROR_EMPTY_FILE));
                }
            } else {
                for (int i = 1; i < Context.getDataInputProvider().getNbLines(); i++) {
                    final List<Integer> index = new ArrayList<>();
                    index.add(i);
                    indexData.add(new DataIndex(i, index));
                }
            }
            Context.getDataInputProvider().setIndexData(indexData);
        } catch (final Exception te) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + te.getMessage(), te);
        }
    }

    private void initializeLocale() {
        final String locale = getProperty(LOCALE, applicationProperties);
        if (locale != null && !"".equals(locale)) {
            final String[] localeParts = locale.split("_");
            if (localeParts.length == 2) {
                currentLocale = new Locale(localeParts[0], localeParts[1]);
            } else {
                currentLocale = new Locale(localeParts[0]);
            }
        } else {
            currentLocale = Locale.getDefault();
        }
        logger.info(Messages.getMessage(CONTEXT_LOCALE_USED), currentLocale);
    }

    private void plugDataProvider(Properties applicationProperties) {
        try {
            final String dataIn = getProperty("dataProvider.in.type", applicationProperties);
            final String dataOut = getProperty("dataProvider.out.type", applicationProperties);

            // plug input provider
            if (DataProvider.type.EXCEL.toString().equals(dataIn)) {
                dataInputProvider = new InputExcelDataProvider();
            } else if (DataProvider.type.CSV.toString().equals(dataIn)) {
                dataInputProvider = new CsvDataProvider();
            } else if (DataProvider.type.DB.toString().equals(dataIn)) {
                dataInputProvider = new DBDataProvider(getProperty("dataProvider.db.type", applicationProperties), getProperty("dataProvider.db.user", applicationProperties),
                        getProperty("dataProvider.db.password", applicationProperties), getProperty("dataProvider.db.hostname", applicationProperties),
                        getProperty("dataProvider.db.port", applicationProperties), getProperty("dataProvider.db.name", applicationProperties));
            } else if (DataProvider.type.REST.toString().equals(dataIn)) {
                dataInputProvider = new RestDataProvider(getProperty("dataProvider.rest.type", applicationProperties), getProperty("dataProvider.rest.hostname", applicationProperties),
                        getProperty("dataProvider.rest.port", applicationProperties));
            } else if (DataProvider.type.GHERKIN.toString().equals(dataIn)) {
                dataInputProvider = new InputGherkinDataProvider();
            } else {
                dataInputProvider = (DataInputProvider) Class.forName(dataIn).getConstructor().newInstance();
            }

            // plug output provider
            if (DataProvider.type.EXCEL.toString().equals(dataOut)) {
                dataOutputProvider = new OutputExcelDataProvider();
            } else if (DataProvider.type.CSV.toString().equals(dataOut)) {
                if (dataInputProvider instanceof CsvDataProvider) {
                    dataOutputProvider = (CsvDataProvider) dataInputProvider;
                } else {
                    dataOutputProvider = new CsvDataProvider();
                }
            } else if (DataProvider.type.DB.toString().equals(dataOut)) {
                // TODO DBDataProvider not implemented for outputDataProvider
            } else if (DataProvider.type.REST.toString().equals(dataOut)) {
                if (dataInputProvider instanceof RestDataProvider) {
                    dataOutputProvider = (RestDataProvider) dataInputProvider;
                } else {
                    dataOutputProvider = new RestDataProvider(getProperty("dataProvider.rest.type", applicationProperties), getProperty("dataProvider.rest.hostname", applicationProperties),
                            getProperty("dataProvider.rest.port", applicationProperties));
                }
            } else if (DataProvider.type.CONSOLE.toString().equals(dataOut)) {
                dataOutputProvider = new OutputConsoleDataProvider();
            } else {
                if (Class.forName(dataOut).isInstance(dataInputProvider)) {
                    dataOutputProvider = (DataOutputProvider) dataInputProvider;
                } else {
                    dataOutputProvider = (DataOutputProvider) Class.forName(dataOut).getConstructor().newInstance();
                }
            }
        } catch (final Exception e) {
            logger.error(Messages.getMessage(CONTEXT_ERROR_WHEN_PLUGING_DATA_PROVIDER), e);
        }
    }
}
