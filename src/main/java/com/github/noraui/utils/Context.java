/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.utils;

import static com.github.noraui.Constants.DATA_IN;
import static com.github.noraui.Constants.DATA_OUT;
import static com.github.noraui.Constants.SCENARIO_FILE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.joda.time.DateTime;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;

import com.github.noraui.Constants;
import com.github.noraui.application.Application;
import com.github.noraui.application.steps.Step;
import com.github.noraui.browser.Auth;
import com.github.noraui.browser.DriverFactory;
import com.github.noraui.browser.WindowManager;
import com.github.noraui.browser.steps.BrowserSteps;
import com.github.noraui.browser.waits.Wait;
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
import com.github.noraui.log.NoraUiLoggingInjector;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.main.ScenarioInitiator;
import com.github.noraui.model.Model;
import com.github.noraui.model.ModelList;
import com.github.noraui.statistics.Statistics;
import com.github.noraui.statistics.StatisticsService;

import cucumber.runtime.java.StepDefAnnotation;
import io.cucumber.core.api.Scenario;
import io.cucumber.junit.CucumberOptions;

/**
 * Cucumber context.
 */
@Loggable
public class Context {

    static Logger log;

    public static final String STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME = BrowserSteps.class.getCanonicalName();
    /**
     * @deprecated As of release 4.2.3, replaced by {@link com.github.noraui.browser.steps.BrowserSteps#GO_TO_URL_METHOD_NAME}
     */
    public static final String GO_TO_URL_METHOD_NAME = "goToUrl";
    /**
     * @deprecated As of release 4.2.3, replaced by {@link com.github.noraui.browser.steps.BrowserSteps#RESTART_WEB_DRIVER_METHOD_NAME}
     */
    public static final String RESTART_WEB_DRIVER_METHOD_NAME = "restartWebDriver";
    public static final String HTTP_PROXY = "http_proxy";
    public static final String HTTPS_PROXY = "https_proxy";
    public static final String NO_PROXY = "no_proxy";
    public static final String OKHTTP_CONNECT_TIMEOUT = "connectTimeout";
    public static final String OKHTTP_WRITE_TIMEOUT = "writeTimeout";
    public static final String OKHTTP_READ_TIMEOUT = "readTimeout";
    public static final String HEADLESS = "headless";
    public static final String LOCALE = "locale";
    public static final String AUTH_TYPE = "authentication";
    public static final String CRYPTO_KEY = "crypto.key";
    public static final String DISPLAY_STACK_TRACE = "display.stacktrace";
    public static final String TIMEOUT_KEY = "timeout";
    public static final String BROWSER_KEY = "browser";
    public static final String MODEL_PACKAGES = "model.packages";
    public static final String SELECTORS_VERSION = "selectors.version";

    protected StatisticsService statistics = new StatisticsService();

    /**
     * BAKERY
     */
    public static final String BAKERY_KEY = "bakery";
    public static final String BAKERY_HOME = "BAKERY_HOME";
    public static final String BAKERY_ADMIN = "BAKERY_ADMIN";
    public static final String BAKERY_REF = "BAKERY_REF";
    public static final String BAKERY_DEMO = "BAKERY_DEMO";

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
     * Current running step index.
     */
    private int currentStepIndex;

    /**
     * Current running sub step index.
     */
    private int currentSubStepIndex;

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
     * 
     */
    private int connectTimeout;

    /**
     * 
     */
    private int writeTimeout;

    /**
     * 
     */
    private int readTimeout;

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
     * As of release 4.4, replaced by same property in src\main\resources\webdrivers.properties.
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

    /**
     * Map of properties
     */
    protected Map<String, String> properties;

    protected static volatile Map<String, Ini> iniFiles;

    protected String resourcesPath;

    protected Properties applicationProperties;

    /**
     * Private constructor
     */
    protected Context() {
        NoraUiLoggingInjector.addInjector(Constants.TOP_LEVEL_PACKAGE);
        driverFactory = new DriverFactory();
        windowManager = new WindowManager();
        scenarioRegistry = new ScenarioRegistry();
        currentScenarioData = nbFailure = nbWarning = 0;
        scenarioHasWarning = false;
        exceptionCallbacks = new Callbacks();
        applications = new HashMap<>();
        properties = new HashMap<>();
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

    /**
     * @param propertiesFileName
     *            is name of properties file.
     */
    public synchronized void initializeEnv(String propertiesFileName) {
        log.info("Context > initializeEnv()");

        iniFiles = new HashMap<>();
        applicationProperties = initPropertiesFile(Thread.currentThread().getContextClassLoader(), propertiesFileName);

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

    /**
     * @param clazz
     *            used to find class loader.
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    public synchronized void initializeRobot(Class<?> clazz) throws TechnicalException {
        log.info("Context > initializeRobot() with {}", clazz.getCanonicalName());
        // set browser: chrome,firefox or ie
        browser = getProperty(BROWSER_KEY, applicationProperties);

        // set Webdriver file: src/test/resources/drivers/...
        initializeWebdriversProperties(Thread.currentThread().getContextClassLoader());

        // wait delay until web element is displayed.
        timeout = getIntProperty(TIMEOUT_KEY, applicationProperties);

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

        // OkHttp timeout
        connectTimeout = getIntProperty(OKHTTP_CONNECT_TIMEOUT, applicationProperties);
        writeTimeout = getIntProperty(OKHTTP_WRITE_TIMEOUT, applicationProperties);
        readTimeout = getIntProperty(OKHTTP_READ_TIMEOUT, applicationProperties);

        // authentication mode configuration
        Auth.setAuthenticationType(getProperty(AUTH_TYPE, applicationProperties));

        // set crypto key
        cryptoKey = System.getProperty(CRYPTO_KEY);
        if (cryptoKey == null) {
            log.warn("{} not set. You can not use crypto feature.", CRYPTO_KEY);
        }

        // stacktrace configuration
        displayStackTrace = "true".equals(getProperty(DISPLAY_STACK_TRACE, applicationProperties));

        // enable browser headless mode ?
        isHeadless = "true".equals(Context.getWebdriversProperties(HEADLESS));

        // init driver callbacks
        exceptionCallbacks.put(Callbacks.RESTART_WEB_DRIVER, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, RESTART_WEB_DRIVER_METHOD_NAME);
        exceptionCallbacks.put(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_GITHUBAPI_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, GO_TO_URL_METHOD_NAME, GITHUBAPI_HOME);
        exceptionCallbacks.put(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_BAKERY_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, GO_TO_URL_METHOD_NAME, BAKERY_HOME);

        // init applications
        initApplicationDom(clazz.getClassLoader(), selectorsVersion, BAKERY_KEY);
        Application bakeryApp = new Application(BAKERY_HOME, getProperty(BAKERY_KEY, applicationProperties) + "/account/login");
        bakeryApp.addUrlPage(BAKERY_DEMO, getProperty(BAKERY_KEY, applicationProperties) + "/public");
        applications.put(BAKERY_KEY, bakeryApp);
        applications.put(GITHUBAPI_KEY, new Application(GITHUBAPI_HOME, getProperty(GITHUBAPI_KEY, applicationProperties)));

        // read and init all cucumber methods
        cucumberMethods = getAllCucumberMethods(clazz);
    }

    /**
     * Clear context
     */
    public static void clear() {
        instance.driverFactory.clear();
        instance.windowManager.clear();
        instance.scenarioRegistry.clear();
        instance.scenarioName = null;
    }

    /**
     * Clear context
     */
    public static void quit() {
        Wait.clear();
        instance.driverFactory.quit();
        instance.windowManager.clear();
        instance.scenarioRegistry.clear();
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
        getInstance().currentStepIndex = 0;
        getInstance().currentSubStepIndex = 0;
    }

    public static void goToNextStep() {
        if (getInstance().currentSubStepIndex > 0) {
            getInstance().currentSubStepIndex++;
        } else {
            getInstance().currentStepIndex++;
        }
    }

    public static void addFailure() {
        getInstance().nbFailure++;
    }

    public static void addWarning() {
        getInstance().nbWarning++;
    }

    public static int getCurrentScenarioData() {
        return getInstance().currentScenarioData;
    }

    public static int getCurrentStepIndex() {
        return getInstance().currentStepIndex;
    }

    public static int getCurrentSubStepIndex() {
        return getInstance().currentSubStepIndex;
    }

    public static void setCurrentSubStepIndex(int index) {
        getInstance().currentSubStepIndex = index;
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
        getInstance().currentStepIndex = 0;
        getInstance().currentSubStepIndex = 0;
        getInstance().currentScenarioData = 0;
        getInstance().nbFailure = 0;
        getInstance().nbWarning = 0;
    }

    public static String getScenarioName() {
        return getInstance().scenarioName;
    }

    /**
     * @param scenarioName
     *            name of scenario as a string.
     */
    public static void setScenarioName(String scenarioName) {
        if (getInstance().scenarioName == null || !getInstance().scenarioName.equals(scenarioName)) {
            try {
                initDataId(scenarioName);
            } catch (final TechnicalException te) {
                log.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE), te);
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

    /**
     * @param loader
     *            is class loader.
     * @param propertiesFileName
     *            is name of properties file.
     * @return Properties Object contain all properties.
     */
    protected static Properties initPropertiesFile(ClassLoader loader, String propertiesFileName) {
        if (loader != null) {
            final InputStream in = loader.getResourceAsStream(propertiesFileName);
            final Properties props = new Properties();
            try {
                if (in == null) {
                    log.error(Messages.getMessage(CONTEXT_PROPERTIES_FILE_NOT_FOUND), propertiesFileName);
                } else {
                    log.info("Reading properties file ({}).", propertiesFileName);
                    props.load(in);
                }
            } catch (final IOException e) {
                log.error("error Context.initPropertiesFile()", e);
            }
            log.info("Loaded properties from {} = {}.", propertiesFileName, props);
            return props;
        }
        return null;
    }

    /**
     * @param key
     *            of property
     * @param propertyFile
     *            object representing the properties file.
     * @return String property
     */
    public static String getProperty(String key, Properties propertyFile) {
        if (propertyFile == null) {
            return null;
        }
        final String p = propertyFile.getProperty(key);
        if (p == null) {
            log.error("{}{}", key, Messages.getMessage(NOT_SET_LABEL));
        }
        return p;
    }

    /**
     * @param key
     *            of property
     * @param propertyFile
     *            object representing the properties file.
     * @return int property
     */
    private static int getIntProperty(String key, Properties propertyFile) {
        final String property = propertyFile.getProperty(key);
        int p = 0;
        if (property == null) {
            log.error("{}{}", key, Messages.getMessage(NOT_SET_LABEL));
        } else {
            p = Integer.parseInt(property);
            log.info("{} = {}", key, p);
        }
        return p;
    }

    public static String getResourcesPath() {
        return getInstance().resourcesPath;
    }

    /**
     * @param loader
     *            is class loader
     * @param version
     *            is version of selector (target application version).
     * @param applicationKey
     *            unic key of application
     */
    protected static void initApplicationDom(ClassLoader loader, String version, String applicationKey) {
        try {
            final InputStream data = loader.getResourceAsStream("selectors/" + version + "/" + applicationKey + ".ini");
            if (data != null) {
                final Ini ini = new Ini(data);
                iniFiles.put(applicationKey, ini);
            }
        } catch (final InvalidFileFormatException e) {
            log.error("error Context.initApplicationDom()", e);
        } catch (final IOException e) {
            log.error(Messages.getMessage(CONTEXT_APP_INI_FILE_NOT_FOUND), applicationKey, e);
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

    public static int getConnectTimeout() {
        return getInstance().connectTimeout;
    }

    public static int getWriteTimeout() {
        return getInstance().writeTimeout;
    }

    public static int getReadTimeout() {
        return getInstance().readTimeout;
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

    public static String getPropertie(String key) {
        return getInstance().properties.get(key);
    }

    /**
     * Get url name in a string by page key.
     *
     * @param pageKey
     *            is key of page
     * @return url in a string
     */
    public static String getUrlByPagekey(String pageKey) {
        return getInstance().applications.values().stream().map(Application::getUrlPages).map(urlPages -> urlPages.get(pageKey)).filter(Objects::nonNull).map(Auth::usingAuthentication).findFirst()
                .orElse(null);
    }

    /**
     * Get application name in a string by page key.
     *
     * @param pageKey
     *            is key of page
     * @return application name in a string
     */
    public static String getApplicationByPagekey(String pageKey) {
        return getInstance().applications.entrySet().stream().filter(a -> a.getValue().getUrlPages().get(pageKey) != null).map(Entry::getKey).findFirst().orElse(null);
    }

    /**
     * statisticsProcessor retrieves specific information from the robot to return it to users statistic feature.
     * 
     * @param loader
     *            is class loader.
     * @param packageName
     *            read for users statistic feature.
     * @return it to users statistic feature.
     */
    protected Statistics statisticsProcessor(ClassLoader loader, String packageName) {
        Statistics stat = new Statistics();
        MavenXpp3Reader reader = new MavenXpp3Reader();
        org.apache.maven.model.Model model;
        try {
            model = reader.read(new FileReader("pom.xml"));
            stat.setNorauiVersion(model.getProperties().getProperty("noraui.version"));
            stat.setName(model.getName());
            stat.setGroupId(model.getGroupId());
            stat.setArtifactId(model.getArtifactId());
            stat.setVersion(model.getVersion());
        } catch (IOException | XmlPullParserException e) {
            log.trace("noraui.version not found.");
        }
        stat.setApplications(applications.entrySet().stream().filter(e -> e.getValue().getHomeUrl() != null).collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getHomeUrl(), (a, b) -> b)));
        // temporarily disabled: too much data
        /*
         * try {
         * Map<String, String> code = ClassPath.from(loader).getTopLevelClassesRecursive(packageName).stream().collect(Collectors.toMap(ClassInfo::getName, c -> read(c.getName()), (a, b) -> b));
         * stat.setCucumberMethods(code);
         * } catch (IOException e1) {
         * log.trace("Cucumber Methods not found.");
         * }
         */
        return stat;
    }

    /**
     * @param className
     *            read for users statistic feature.
     * @return
     */
    private String read(String className) {
        String filePath = "src" + File.separator + "main" + File.separator + "java" + File.separator + className.replaceAll("\\.", "/") + ".java";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while (line != null) {
                line = br.readLine();
                sb.append(line);
            }
        } catch (IOException e) {
            log.trace("java file [{}].java not found.", className);
        }
        return sb.toString();
    }

    /**
     * init all Data index (by model).
     *
     * @param scenarioName
     *            name of scenario.
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    private static void initDataId(String scenarioName) throws TechnicalException {
        List<DataIndex> indexData = new ArrayList<>();
        try {
            Context.getDataInputProvider().prepare(scenarioName);
            final Class<Model> model = Context.getDataInputProvider().getModel(Context.getModelPackages());
            if (model != null) {
                final String[] headers = Context.getDataInputProvider().readLine(0, false);
                if (headers != null) {
                    final Constructor<Model> modelConstructor = DataUtils.getModelConstructor(model, headers);
                    final Map<Integer, Map<String, ModelList>> fusionedData = DataUtils.fusionProcessor(model, modelConstructor);
                    AtomicInteger dataIndex = new AtomicInteger();
                    indexData = fusionedData.values().stream().flatMap(models -> models.values().stream()).map(ModelList::getIds).map(ids -> new DataIndex(dataIndex.incrementAndGet(), ids))
                            .collect(Collectors.toList());
                } else {
                    log.error(Messages.getMessage(ScenarioInitiator.SCENARIO_INITIATOR_ERROR_EMPTY_FILE));
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

    /**
     * initialize Locale (fr, en).
     */
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
        log.info(Messages.getMessage(CONTEXT_LOCALE_USED), currentLocale);
    }

    /**
     * @param applicationProperties
     */
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
                throw new NotImplementedException("Context.plugDataProvider() with 'DB' as output provider is not yet implemented");
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
            log.error(Messages.getMessage(CONTEXT_ERROR_WHEN_PLUGING_DATA_PROVIDER), e);
        }
    }

    /**
     * Gets all Cucumber methods.
     *
     * @param clazz
     *            Class which is the main point of the application (Decorated with the annotation {@link io.cucumber.junit.CucumberOptions})
     * @return a Map of all Cucumber glue code methods of the application. First part of the entry is the Gherkin matching regular expression.
     *         Second part is the corresponding invokable method.
     */
    private static Map<String, Method> getAllCucumberMethods(Class<?> clazz) {
        final CucumberOptions co = clazz.getAnnotation(CucumberOptions.class);
        final Set<Class<?>> classes = getClasses(co.glue());
        classes.add(BrowserSteps.class);
        //@formatter:off
        return classes.stream() //
               .flatMap(c -> Arrays.stream(c.getDeclaredMethods())) //
               .flatMap(m -> Arrays.stream(m.getAnnotations()) //
               .filter(stepAnnotation -> stepAnnotation.annotationType().isAnnotationPresent(StepDefAnnotation.class)) //
               .map(ann -> {
                 try {
                   return new AbstractMap.SimpleEntry<>(ann.annotationType().getDeclaredMethod("value").invoke(ann).toString(), m);
                 } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                 }
                 return null;
                 })) //
               .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)); //
        //@formatter:on
    }

    private static Set<Class<?>> getClasses(String[] packagesName) {
        return Stream.of(packagesName).flatMap(packageName -> new Reflections(packageName, new SubTypesScanner(false)).getSubTypesOf(Step.class).stream()).collect(Collectors.toSet());
    }

}
