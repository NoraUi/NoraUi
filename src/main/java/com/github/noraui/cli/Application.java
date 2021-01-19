/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import static com.github.noraui.exception.TechnicalException.TECHNICAL_IO_EXCEPTION;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.github.noraui.log.annotation.Loggable;
import com.google.common.io.Files;

@Loggable
public class Application extends AbstractNoraUiCli {

    static Logger log;

    public static final String SUFFIX_HOME = "_HOME\";";
    public static final String SUFFIX_PORTAL = "_PORTAL\";";
    public static final String SUFFIX_COMMON = "_COMMON\";";
    public static final String SUFFIX_KEY = "_KEY;";

    private String mainPath;
    private String testPath = "src" + File.separator + "test";

    public Application() {
        this.mainPath = "src" + File.separator + "main";
    }

    protected Application(String mainPath) {
        this.mainPath = mainPath;
    }

    /**
     * @return a list of available applications (name).
     */
    public List<String> get() {
        List<String> applications = new ArrayList<>();
        String selectorsPath = mainPath + File.separator + RESOURCES + File.separator + "selectors";
        String[] versions = new File(selectorsPath).list();
        for (String version : versions) {
            applications.addAll(Arrays.asList(new File(selectorsPath + File.separator + version).list()));
        }
        TreeSet<String> hs = new TreeSet<>();
        hs.addAll(applications);
        applications.clear();
        applications.addAll(hs);
        for (int i = 0; i < applications.size(); i++) {
            applications.set(i, applications.get(i).replaceAll(".ini", ""));
        }
        applications.remove(".gitignore");
        return applications;
    }

    /**
     * Add new target application to your robot.
     * Sample if you add google: -f 1 -a google -u http://www.google.com --verbose
     * 
     * @param applicationName
     *            name of application added.
     * @param url
     *            of target application.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    public void add(String applicationName, String url, Class<?> robotContext, boolean verbose) {
        log.info("Add a new application named [{}] with this url: [{}]", applicationName, url);
        addApplicationPages(applicationName, robotContext.getSimpleName().replace("Context", ""), robotContext, verbose);
        addApplicationSteps(applicationName, robotContext.getSimpleName().replace("Context", ""), robotContext, verbose);
        addApplicationContext(applicationName, robotContext, verbose);
        addApplicationSelector(applicationName, verbose);
        addApplicationInPropertiesFile(applicationName, robotContext.getSimpleName().replace("Context", ""), verbose);
        for (final File f : new File("src" + File.separator + "test" + File.separator + RESOURCES + File.separator + ENVIRONMENTS).listFiles()) {
            if (f.isFile() && f.getName().matches(".*\\.properties")) {
                addApplicationInEnvPropertiesFile(applicationName, url, f.getName(), verbose);
            }
        }
    }

    /**
     * Remove target application to your robot.
     * 
     * @param applicationName
     *            name of application removed.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    public void remove(String applicationName, Class<?> robotContext, boolean verbose) {
        log.info("Remove application named [{}].", applicationName);
        removeApplicationPages(applicationName, robotContext, verbose);
        removeApplicationSteps(applicationName, robotContext, verbose);
        removeApplicationModels(applicationName, robotContext, verbose);
        removeApplicationContext(robotContext, applicationName, verbose);
        removeApplicationSelector(applicationName, verbose);
        removeApplicationInPropertiesFile(applicationName, robotContext.getSimpleName().replace("Context", ""), verbose);
        for (final File f : new File("src" + File.separator + "test" + File.separator + RESOURCES + File.separator + ENVIRONMENTS).listFiles()) {
            if (f.isFile() && f.getName().matches(".*\\.properties")) {
                removeApplicationInEnvPropertiesFile(applicationName, f.getName(), verbose);
            }
        }
    }

    /**
     * @param applicationName
     *            name of application added.
     * @param noraRobotName
     *            Name of your robot generated by NoraUi Maven archetype.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addApplicationPages(String applicationName, String noraRobotName, Class<?> robotContext, boolean verbose) {
        addHomePage(applicationName, noraRobotName, robotContext, verbose);
        addPortalPage(applicationName, noraRobotName, robotContext, verbose);
        addCommonPage(applicationName, noraRobotName, robotContext, verbose);
    }

    private void addHomePage(String applicationName, String noraRobotName, Class<?> robotContext, boolean verbose) {
        String pagePath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace("utils", "application/pages/" + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "HomePage")
                + ".java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(noraRobotName)).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace("utils", "application.pages." + applicationName) + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import static " + robotContext.getCanonicalName() + "." + applicationName.toUpperCase() + SUFFIX_KEY).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import org.openqa.selenium.support.ui.ExpectedConditions;").append(System.lineSeparator());
        sb.append(IMPORT_SLF4J_LOGGER).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import " + robotContext.getCanonicalName() + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import com.github.noraui.application.page.Page;").append(System.lineSeparator());
        sb.append("import com.github.noraui.browser.waits.Wait;").append(System.lineSeparator());
        sb.append("import com.github.noraui.log.annotation.Loggable;").append(System.lineSeparator());
        sb.append("import com.github.noraui.utils.Context;").append(System.lineSeparator());
        sb.append("import com.github.noraui.utils.Utilities;").append(System.lineSeparator());
        sb.append("import com.google.inject.Singleton;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(NORAUI_LOGGABLE_ANNOTATION).append(System.lineSeparator());
        sb.append(GOOGLE_INJECT_SINGLETON_ANNOTATION).append(System.lineSeparator());
        sb.append("public class " + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "HomePage extends Page {").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    static Logger log;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    public final PageElement login = new PageElement(\"-login_field\", \"Login\");").append(System.lineSeparator());
        sb.append("    public final PageElement password = new PageElement(\"-password_field\", \"Password\");").append(System.lineSeparator());
        sb.append("    public final PageElement signInButton = new PageElement(\"-sign_in_button\", \"Sign-in button\");").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    public final PageElement signInError = new PageElement(\"-sign_in_error\", \"Sign-in error\");").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    private static final String TITLE_PAGE = \"" + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "\";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    public " + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "HomePage() {").append(System.lineSeparator());
        sb.append("        super();").append(System.lineSeparator());
        sb.append("        this.application = " + applicationName.toUpperCase() + SUFFIX_KEY).append(System.lineSeparator());
        sb.append("        this.pageKey = \"" + applicationName.toUpperCase() + SUFFIX_HOME).append(System.lineSeparator());
        sb.append("        this.callBack = Context.getCallBack(" + noraRobotName + "Context.CLOSE_WINDOW_AND_SWITCH_TO_" + applicationName.toUpperCase() + "_HOME);").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public boolean checkPage(Object... elements) {").append(System.lineSeparator());
        sb.append("        try {").append(System.lineSeparator());
        sb.append("            Wait.until(ExpectedConditions.not(ExpectedConditions.titleIs(\"\")));").append(System.lineSeparator());
        sb.append("            if (!TITLE_PAGE.equals(getDriver().getTitle())) {").append(System.lineSeparator());
        sb.append("                log.error(\"HTML title is not good\");").append(System.lineSeparator());
        sb.append("                return false;").append(System.lineSeparator());
        sb.append("            }").append(System.lineSeparator());
        sb.append("            // Wait.untilAnd(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(this.login)))").append(System.lineSeparator());
        sb.append("            //   .wait(() -> ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(this.password)))").append(System.lineSeparator());
        sb.append("            //   .wait(() -> ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(this.signInButton)));").append(System.lineSeparator());
        sb.append("            return true;").append(System.lineSeparator());
        sb.append("        } catch (Exception e) {").append(System.lineSeparator());
        sb.append("            log.error(\"HTML title Exception\", e);").append(System.lineSeparator());
        sb.append("            return false;").append(System.lineSeparator());
        sb.append("        }").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(pagePath.substring(0, pagePath.lastIndexOf(File.separator))));
            File newSelector = new File(pagePath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    log.info("File [{}] created with success.", pagePath);
                }
            } else {
                if (verbose) {
                    log.info("File [{}] already exist.", pagePath);
                }
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    private void addPortalPage(String applicationName, String noraRobotName, Class<?> robotContext, boolean verbose) {
        String pagePath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace("utils", "application/pages/" + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "Page")
                + ".java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(noraRobotName)).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace("utils", "application.pages." + applicationName) + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import static " + robotContext.getCanonicalName() + "." + applicationName.toUpperCase() + SUFFIX_KEY).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import org.openqa.selenium.support.ui.ExpectedConditions;").append(System.lineSeparator());
        sb.append(IMPORT_SLF4J_LOGGER).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import " + robotContext.getCanonicalName() + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import com.github.noraui.application.page.Page;").append(System.lineSeparator());
        sb.append("import com.github.noraui.browser.waits.Wait;").append(System.lineSeparator());
        sb.append("import com.github.noraui.log.annotation.Loggable;").append(System.lineSeparator());
        sb.append("import com.github.noraui.utils.Context;").append(System.lineSeparator());
        sb.append("import com.github.noraui.utils.Utilities;").append(System.lineSeparator());
        sb.append("import com.google.inject.Singleton;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(NORAUI_LOGGABLE_ANNOTATION).append(System.lineSeparator());
        sb.append(GOOGLE_INJECT_SINGLETON_ANNOTATION).append(System.lineSeparator());
        sb.append("public class " + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "Page extends Page {").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    static Logger log;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    public final PageElement accountMenu = new PageElement(\"-accountMenu\", \"Account menu\");").append(System.lineSeparator());
        sb.append("    public final PageElement signinMenu = new PageElement(\"-signinMenu\", \"Sign-in menu\");").append(System.lineSeparator());
        sb.append("    public final PageElement signoutMenu = new PageElement(\"-signoutMenu\", \"Sign-out menu\");").append(System.lineSeparator());
        sb.append("    public final PageElement signInMessage = new PageElement(\"-sign_in_message\");").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    public " + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "Page() {").append(System.lineSeparator());
        sb.append("        super();").append(System.lineSeparator());
        sb.append("        this.application = " + applicationName.toUpperCase() + SUFFIX_KEY).append(System.lineSeparator());
        sb.append("        this.pageKey = \"" + applicationName.toUpperCase() + SUFFIX_PORTAL).append(System.lineSeparator());
        sb.append("        this.callBack = Context.getCallBack(" + noraRobotName + "Context.CLOSE_WINDOW_AND_SWITCH_TO_" + applicationName.toUpperCase() + "_HOME);").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public boolean checkPage(Object... elements) {").append(System.lineSeparator());
        sb.append("        try {").append(System.lineSeparator());
        sb.append("            Wait.until(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(this.signInMessage, elements)));").append(System.lineSeparator());
        sb.append("            return true;").append(System.lineSeparator());
        sb.append("        } catch (Exception e) {").append(System.lineSeparator());
        sb.append("            log.error(\"" + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "Page not loaded\", e);").append(System.lineSeparator());
        sb.append("            return false;").append(System.lineSeparator());
        sb.append("        }").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(pagePath.substring(0, pagePath.lastIndexOf(File.separator))));
            File newSelector = new File(pagePath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    log.info("File [{}] created with success.", pagePath);
                }
            } else {
                if (verbose) {
                    log.info("File [{}] already exist.", pagePath);
                }
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    private void addCommonPage(String applicationName, String noraRobotName, Class<?> robotContext, boolean verbose) {
        String pagePath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace("utils", "application/pages/" + applicationName)
                .replace(robotContext.getSimpleName(), "").replace("/", Matcher.quoteReplacement(File.separator)) + "CommonPage.java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(noraRobotName)).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace("utils", "application.pages." + applicationName) + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import static " + robotContext.getCanonicalName() + "." + applicationName.toUpperCase() + SUFFIX_KEY).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(IMPORT_SLF4J_LOGGER).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import " + robotContext.getCanonicalName() + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import com.github.noraui.application.page.Page;").append(System.lineSeparator());
        sb.append("import com.github.noraui.log.annotation.Loggable;").append(System.lineSeparator());
        sb.append("import com.github.noraui.utils.Context;").append(System.lineSeparator());
        sb.append("import com.google.inject.Singleton;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(NORAUI_LOGGABLE_ANNOTATION).append(System.lineSeparator());
        sb.append(GOOGLE_INJECT_SINGLETON_ANNOTATION).append(System.lineSeparator());
        sb.append("public class CommonPage extends Page {").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    static Logger log;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * select2 is javascript librairy.").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    public final PageElement select2 = new PageElement(\"-select2\");").append(System.lineSeparator());
        sb.append("    public final PageElement select2Value = new PageElement(\"-select2_value_\");").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    public CommonPage() {").append(System.lineSeparator());
        sb.append("        super();").append(System.lineSeparator());
        sb.append("        this.application = " + applicationName.toUpperCase() + SUFFIX_KEY).append(System.lineSeparator());
        sb.append("        this.pageKey = \"" + applicationName.toUpperCase() + SUFFIX_COMMON).append(System.lineSeparator());
        sb.append("        this.callBack = Context.getCallBack(" + noraRobotName + "Context.RESTART_WEB_DRIVER_AND_SWITCH_TO_" + applicationName.toUpperCase() + "_HOME);")
                .append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public boolean checkPage(Object... elements) {").append(System.lineSeparator());
        sb.append("        return true;").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(pagePath.substring(0, pagePath.lastIndexOf(File.separator))));
            File newSelector = new File(pagePath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    log.info("File [{}] created with success.", pagePath);
                }
            } else {
                if (verbose) {
                    log.info("File [{}] already exist.", pagePath);
                }
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param applicationName
     *            name of application removed.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeApplicationPages(String applicationName, Class<?> robotContext, boolean verbose) {
        String applicationPagePath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/")
                .replace("utils", "application/pages/" + applicationName).replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), "");
        try {
            FileUtils.forceDelete(new File(applicationPagePath));
            if (verbose) {
                log.info("{} removed with success.", applicationPagePath);
            }
        } catch (IOException e) {
            log.debug("{} not revove because do not exist.", applicationPagePath);
        }
    }

    /**
     * @param applicationName
     *            name of application added.
     * @param noraRobotName
     *            Name of your robot generated by NoraUi Maven archetype.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addApplicationSteps(String applicationName, String noraRobotName, Class<?> robotContext, boolean verbose) {
        addHomeSteps(applicationName, noraRobotName, robotContext, verbose);
        addPortalSteps(applicationName, noraRobotName, robotContext, verbose);
        addCommonSteps(applicationName, noraRobotName, robotContext, verbose);
    }

    private void addHomeSteps(String applicationName, String noraRobotName, Class<?> robotContext, boolean verbose) {
        String stepsPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace("utils", "application/steps/" + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "HomeSteps")
                + ".java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(noraRobotName)).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace("utils", "application.steps." + applicationName) + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(IMPORT_SLF4J_LOGGER).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import " + robotContext.getCanonicalName().replace("utils", "application.pages." + applicationName).replaceAll(robotContext.getSimpleName(),
                applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "HomePage;")).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import com.github.noraui.application.steps.Step;").append(System.lineSeparator());
        sb.append("import com.github.noraui.exception.FailureException;").append(System.lineSeparator());
        sb.append("import com.github.noraui.exception.Result;").append(System.lineSeparator());
        sb.append("import com.github.noraui.log.annotation.Loggable;").append(System.lineSeparator());
        sb.append("import com.github.noraui.utils.Messages;").append(System.lineSeparator());
        sb.append("import com.google.inject.Inject;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import io.cucumber.java.en.Then;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(NORAUI_LOGGABLE_ANNOTATION).append(System.lineSeparator());
        sb.append("public class " + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "HomeSteps extends Step {").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    static Logger log;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    @Inject").append(System.lineSeparator());
        sb.append("    private " + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "HomePage " + applicationName + "HomePage;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * Check home page.").append(System.lineSeparator());
        sb.append("     *").append(System.lineSeparator());
        sb.append("     * @throws FailureException").append(System.lineSeparator());
        sb.append("     *             if the scenario encounters a functional error.").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Then(\"The " + applicationName.toUpperCase() + " home page is displayed\")").append(System.lineSeparator());
        sb.append("    public void check" + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "HomePage() throws FailureException {").append(System.lineSeparator());
        sb.append("        log.debug(\"check " + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + " home page is displayed\");").append(System.lineSeparator());
        sb.append("        if (!" + applicationName + "HomePage.checkPage()) {").append(System.lineSeparator());
        sb.append("            new Result.Failure<>(" + applicationName + "HomePage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_HOME_PAGE_NOT_FOUND), true, " + applicationName
                + "HomePage.getCallBack());").append(System.lineSeparator());
        sb.append("        }").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(stepsPath.substring(0, stepsPath.lastIndexOf(File.separator))));
            File newSelector = new File(stepsPath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    log.info("File [{}] created with success.", stepsPath);
                }
            } else {
                if (verbose) {
                    log.info("File [{}] already exist.", stepsPath);
                }
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    private void addPortalSteps(String applicationName, String noraRobotName, Class<?> robotContext, boolean verbose) {
        String stepsPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace("utils", "application/steps/" + applicationName)
                .replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "Steps")
                + ".java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(noraRobotName)).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace("utils", "application.steps." + applicationName) + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(IMPORT_SLF4J_LOGGER).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import " + robotContext.getCanonicalName().replace("utils", "application.pages." + applicationName).replaceAll(robotContext.getSimpleName(),
                applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "Page;")).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import com.github.noraui.application.steps.Step;").append(System.lineSeparator());
        sb.append("import com.github.noraui.exception.FailureException;").append(System.lineSeparator());
        sb.append("import com.github.noraui.exception.Result;").append(System.lineSeparator());
        sb.append("import com.github.noraui.log.annotation.Loggable;").append(System.lineSeparator());
        sb.append("import com.github.noraui.utils.Messages;").append(System.lineSeparator());
        sb.append("import com.google.inject.Inject;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import io.cucumber.java.en.Then;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(NORAUI_LOGGABLE_ANNOTATION).append(System.lineSeparator());
        sb.append("public class " + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "Steps extends Step {").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    static Logger log;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    @Inject").append(System.lineSeparator());
        sb.append("    private " + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "Page " + applicationName + "Page;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * Check home page.").append(System.lineSeparator());
        sb.append("     *").append(System.lineSeparator());
        sb.append("     * @throws FailureException").append(System.lineSeparator());
        sb.append("     *             if the scenario encounters a functional error.").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Then(\"The " + applicationName.toUpperCase() + " home page is displayed\")").append(System.lineSeparator());
        sb.append("    public void check" + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "HomePage() throws FailureException {").append(System.lineSeparator());
        sb.append("        log.debug(\"check " + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + " home page is displayed\");").append(System.lineSeparator());
        sb.append("        if (!" + applicationName + "Page.checkPage()) {").append(System.lineSeparator());
        sb.append("            new Result.Failure<>(" + applicationName + "Page.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_HOME_PAGE_NOT_FOUND), true, " + applicationName
                + "Page.getCallBack());").append(System.lineSeparator());
        sb.append("        }").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(stepsPath.substring(0, stepsPath.lastIndexOf(File.separator))));
            File newSelector = new File(stepsPath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    log.info("File [{}] created with success.", stepsPath);
                }
            } else {
                if (verbose) {
                    log.info("File [{}] already exist.", stepsPath);
                }
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    private void addCommonSteps(String applicationName, String noraRobotName, Class<?> robotContext, boolean verbose) {
        String stepsPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace("utils", "application/steps/" + applicationName)
                .replace(robotContext.getSimpleName(), "").replace("/", Matcher.quoteReplacement(File.separator)) + "CommonSteps.java";
        StringBuilder sb = new StringBuilder();
        sb.append(getJavaClassHeaders(noraRobotName)).append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replace("utils", "application.steps." + applicationName) + ";").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(IMPORT_SLF4J_LOGGER).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import " + robotContext.getCanonicalName().replace("utils", "application.pages." + applicationName).replaceAll(robotContext.getSimpleName(), "CommonPage;"))
                .append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import com.github.noraui.application.page.Page;").append(System.lineSeparator());
        sb.append("import com.github.noraui.application.steps.Step;").append(System.lineSeparator());
        sb.append("import com.github.noraui.exception.FailureException;").append(System.lineSeparator());
        sb.append("import com.github.noraui.exception.Result;").append(System.lineSeparator());
        sb.append("import com.github.noraui.exception.TechnicalException;").append(System.lineSeparator());
        sb.append("import com.github.noraui.log.annotation.Loggable;").append(System.lineSeparator());
        sb.append("import com.github.noraui.utils.Context;").append(System.lineSeparator());
        sb.append("import com.google.inject.Inject;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("import io.cucumber.java.en.Then;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(NORAUI_LOGGABLE_ANNOTATION).append(System.lineSeparator());
        sb.append("public class CommonSteps extends Step {").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("     static Logger log;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("     @Inject").append(System.lineSeparator());
        sb.append("     private CommonPage commonPage;").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("     /**").append(System.lineSeparator());
        sb.append("      * select2 is javascript librairy.").append(System.lineSeparator());
        sb.append("      *").append(System.lineSeparator());
        sb.append("      * @param id").append(System.lineSeparator());
        sb.append("      *            of select2.").append(System.lineSeparator());
        sb.append("      * @param value").append(System.lineSeparator());
        sb.append("      *            selected in drop-down list.").append(System.lineSeparator());
        sb.append("      * @param page").append(System.lineSeparator());
        sb.append("      *            on which is the drop-down list.").append(System.lineSeparator());
        sb.append("      * @throws FailureException").append(System.lineSeparator());
        sb.append("      *             if the scenario encounters a functional error.").append(System.lineSeparator());
        sb.append("      * @throws TechnicalException").append(System.lineSeparator());
        sb.append("      *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.").append(System.lineSeparator());
        sb.append("      */").append(System.lineSeparator());
        sb.append("     @Then(\"I update select2 list {string} with {string} on {string}\")").append(System.lineSeparator());
        sb.append("     public void updateSelect2(String id, String value, String page) throws FailureException, TechnicalException {").append(System.lineSeparator());
        sb.append("         String realValue = Context.getValue(value) != null ? Context.getValue(value) : value;").append(System.lineSeparator());
        sb.append("         log.debug(\"updateSelect2 {} with {}\", id, realValue);").append(System.lineSeparator());
        sb.append("         try {").append(System.lineSeparator());
        sb.append("             clickOn(commonPage.select2, id);").append(System.lineSeparator());
        sb.append("             clickOn(commonPage.select2Value, id, realValue);").append(System.lineSeparator());
        sb.append("         } catch (Exception e) {").append(System.lineSeparator());
        sb.append(
                "             new Result.Failure<>(Page.getInstance(page).getApplication(), \"Error when \\\"I update select2 '\" + id + \"' with '\" + realValue + \"' on '\" + page + \"': \" + e.getMessage(), true,")
                .append(System.lineSeparator());
        sb.append("                    Page.getInstance(page).getCallBack());").append(System.lineSeparator());
        sb.append("        }").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(stepsPath.substring(0, stepsPath.lastIndexOf(File.separator))));
            File newSelector = new File(stepsPath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                if (verbose) {
                    log.info("File [{}] created with success.", stepsPath);
                }
            } else {
                if (verbose) {
                    log.info("File [{}] already exist.", stepsPath);
                }
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param applicationName
     *            name of application removed.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeApplicationSteps(String applicationName, Class<?> robotContext, boolean verbose) {
        String applicationStepsPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/")
                .replace("utils", "application/steps/" + applicationName).replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), "");
        try {
            FileUtils.forceDelete(new File(applicationStepsPath));
            if (verbose) {
                log.info("{} removed with success.", applicationStepsPath);
            }
        } catch (IOException e) {
            log.debug("{} not revove because do not exist.", applicationStepsPath);
        }
    }

    /**
     * Remove application models (with unit tests).
     * 
     * @param applicationName
     *            name of application removed.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeApplicationModels(String applicationName, Class<?> robotContext, boolean verbose) {
        String applicationModelPath = mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/")
                .replace("utils", "application/model/" + applicationName).replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), "");
        String applicationModelUTPath = testPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/")
                .replace("utils", "application/model/" + applicationName).replace("/", Matcher.quoteReplacement(File.separator)).replaceAll(robotContext.getSimpleName(), "");
        try {
            FileUtils.forceDelete(new File(applicationModelPath));
            if (verbose) {
                log.info("{} removed with success.", applicationModelPath);
            }
        } catch (IOException e) {
            log.debug("{} not revove because do not exist.", applicationModelPath);
        }
        try {
            FileUtils.forceDelete(new File(applicationModelUTPath));
            if (verbose) {
                log.info("{} removed with success.", applicationModelUTPath);
            }
        } catch (IOException e) {
            log.debug("{} not revove because do not exist.", applicationModelUTPath);
        }
    }

    /**
     * @param applicationName
     *            name of application added.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addApplicationContext(String applicationName, Class<?> robotContext, boolean verbose) {
        manageApplicationContext(true, robotContext, applicationName, verbose);
    }

    /**
     * @param robotContext
     *            Context class from robot.
     * @param applicationName
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeApplicationContext(Class<?> robotContext, String applicationName, boolean verbose) {
        manageApplicationContext(false, robotContext, applicationName, verbose);
    }

    /**
     * @param addMode
     * @param robotContext
     *            Context class from robot.
     * @param applicationName
     *            name of application.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void manageApplicationContext(boolean addMode, Class<?> robotContext, String applicationName, boolean verbose) {
        String contextPath = this.mainPath + File.separator + "java" + File.separator + robotContext.getCanonicalName().replaceAll("\\.", "/").replace("/", Matcher.quoteReplacement(File.separator))
                + ".java";
        if (verbose) {
            log.info("Add application named [{}] in context.", applicationName);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(contextPath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                if (!(("    public static final String " + applicationName.toUpperCase() + "_KEY = \"" + applicationName + "\";").equals(line)
                        || ("    public static final String " + applicationName.toUpperCase() + "_HOME = \"" + applicationName.toUpperCase() + SUFFIX_HOME).equals(line)
                        || ("    private String " + applicationName + "Home; // " + applicationName.toUpperCase() + " home url").equals(line)
                        || ("    public static final String GO_TO_" + applicationName.toUpperCase() + "_HOME = \"GO_TO_" + applicationName.toUpperCase() + SUFFIX_HOME).equals(line)
                        || ("    public static final String CLOSE_WINDOW_AND_SWITCH_TO_" + applicationName.toUpperCase() + "_HOME = \"CLOSE_WINDOW_AND_SWITCH_TO_" + applicationName.toUpperCase()
                                + SUFFIX_HOME).equals(line)
                        || ("    public static final String CLOSE_ALL_WINDOWS_AND_SWITCH_TO_" + applicationName.toUpperCase() + "_HOME = \"CLOSE_ALL_WINDOWS_AND_SWITCH_TO_"
                                + applicationName.toUpperCase() + SUFFIX_HOME).equals(line)
                        || ("    public static final String RESTART_WEB_DRIVER_AND_SWITCH_TO_" + applicationName.toUpperCase() + "_HOME = \"RESTART_WEB_DRIVER_AND_SWITCH_TO_"
                                + applicationName.toUpperCase() + SUFFIX_HOME).equals(line)
                        || ("        " + applicationName + "Home = getProperty(" + applicationName.toUpperCase() + "_KEY, applicationProperties);").equals(line)
                        || ("        initApplicationDom(clazz.getClassLoader(), selectorsVersion, " + applicationName.toUpperCase() + "_KEY);").equals(line)
                        || ("        exceptionCallbacks.put(GO_TO_" + applicationName.toUpperCase() + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, GO_TO_URL_METHOD_NAME, "
                                + applicationName.toUpperCase() + "_HOME);").equals(line)
                        || ("        exceptionCallbacks.put(CLOSE_WINDOW_AND_SWITCH_TO_" + applicationName.toUpperCase()
                                + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, CLOSE_WINDOW_AND_SWITCH_TO, " + applicationName.toUpperCase() + "_KEY, " + applicationName.toUpperCase()
                                + "_HOME);").equals(line)
                        || ("        exceptionCallbacks.put(CLOSE_ALL_WINDOWS_AND_SWITCH_TO_" + applicationName.toUpperCase()
                                + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, CLOSE_ALL_WINDOWS_AND_SWITCH_TO, " + applicationName.toUpperCase() + "_KEY);").equals(line)
                        || ("        exceptionCallbacks.put(RESTART_WEB_DRIVER_AND_SWITCH_TO_" + applicationName.toUpperCase()
                                + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, RESTART_WEB_DRIVER_AND_SWITCH_TO, " + applicationName.toUpperCase() + "_HOME);").equals(line)
                        || ("        applications.put(" + applicationName.toUpperCase() + "_KEY, new Application(" + applicationName.toUpperCase() + "_HOME, " + applicationName + "Home));")
                                .equals(line))) {

                    if (("    public String get" + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "Home() {").equals(line)) {
                        line = br.readLine();
                        line = br.readLine();
                        line = br.readLine();
                    }
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    if (addMode) {
                        if ("    // applications".equals(line)) {
                            sb.append("    public static final String " + applicationName.toUpperCase() + "_KEY = \"" + applicationName + "\";");
                            sb.append(System.lineSeparator());
                            sb.append("    public static final String " + applicationName.toUpperCase() + "_HOME = \"" + applicationName.toUpperCase() + SUFFIX_HOME);
                            sb.append(System.lineSeparator());
                            sb.append("    private String " + applicationName + "Home; // " + applicationName.toUpperCase() + " home url");
                            sb.append(System.lineSeparator());
                        }
                        if ("    // targets".equals(line)) {
                            sb.append("    public static final String GO_TO_" + applicationName.toUpperCase() + "_HOME = \"GO_TO_" + applicationName.toUpperCase() + SUFFIX_HOME);
                            sb.append(System.lineSeparator());
                            sb.append("    public static final String CLOSE_WINDOW_AND_SWITCH_TO_" + applicationName.toUpperCase() + "_HOME = \"CLOSE_WINDOW_AND_SWITCH_TO_"
                                    + applicationName.toUpperCase() + SUFFIX_HOME);
                            sb.append(System.lineSeparator());
                            sb.append("    public static final String CLOSE_ALL_WINDOWS_AND_SWITCH_TO_" + applicationName.toUpperCase() + "_HOME = \"CLOSE_ALL_WINDOWS_AND_SWITCH_TO_"
                                    + applicationName.toUpperCase() + SUFFIX_HOME);
                            sb.append(System.lineSeparator());
                            sb.append("    public static final String RESTART_WEB_DRIVER_AND_SWITCH_TO_" + applicationName.toUpperCase() + "_HOME = \"RESTART_WEB_DRIVER_AND_SWITCH_TO_"
                                    + applicationName.toUpperCase() + SUFFIX_HOME);
                            sb.append(System.lineSeparator());
                        }
                        if ("        // Urls configuration".equals(line)) {
                            sb.append("        " + applicationName + "Home = getProperty(" + applicationName.toUpperCase() + "_KEY, applicationProperties);");
                            sb.append(System.lineSeparator());
                        }
                        if ("        // Selectors configuration".equals(line)) {
                            sb.append("        initApplicationDom(clazz.getClassLoader(), selectorsVersion, " + applicationName.toUpperCase() + "_KEY);");
                            sb.append(System.lineSeparator());
                        }
                        if ("        // Exception Callbacks".equals(line)) {
                            sb.append("        exceptionCallbacks.put(GO_TO_" + applicationName.toUpperCase() + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, GO_TO_URL_METHOD_NAME, "
                                    + applicationName.toUpperCase() + "_HOME);");
                            sb.append(System.lineSeparator());
                            sb.append("        exceptionCallbacks.put(CLOSE_WINDOW_AND_SWITCH_TO_" + applicationName.toUpperCase()
                                    + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, CLOSE_WINDOW_AND_SWITCH_TO, " + applicationName.toUpperCase() + "_KEY, " + applicationName.toUpperCase()
                                    + "_HOME);");
                            sb.append(System.lineSeparator());
                            sb.append("        exceptionCallbacks.put(CLOSE_ALL_WINDOWS_AND_SWITCH_TO_" + applicationName.toUpperCase()
                                    + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, CLOSE_ALL_WINDOWS_AND_SWITCH_TO, " + applicationName.toUpperCase() + "_KEY);");
                            sb.append(System.lineSeparator());
                            sb.append("        exceptionCallbacks.put(RESTART_WEB_DRIVER_AND_SWITCH_TO_" + applicationName.toUpperCase()
                                    + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, RESTART_WEB_DRIVER_AND_SWITCH_TO, " + applicationName.toUpperCase() + "_HOME);");
                            sb.append(System.lineSeparator());
                        }
                        if ("        // applications mapping".equals(line)) {
                            sb.append("        applications.put(" + applicationName.toUpperCase() + "_KEY, new Application(" + applicationName.toUpperCase() + "_HOME, " + applicationName + "Home));");
                            sb.append(System.lineSeparator());
                        }
                        if ("    // home getters".equals(line)) {
                            sb.append("    public String get" + applicationName.toUpperCase().charAt(0) + applicationName.substring(1) + "Home() {");
                            sb.append(System.lineSeparator());
                            sb.append("        return " + applicationName + "Home;");
                            sb.append(System.lineSeparator());
                            sb.append("    }");
                            sb.append(System.lineSeparator());
                        }
                    }
                }
                line = br.readLine();
            }
            FileWriter fw = new FileWriter(contextPath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString().substring(0, sb.toString().length() - System.lineSeparator().length()));
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param applicationName
     *            name of application added.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addApplicationSelector(String applicationName, boolean verbose) {
        String selectorsPath = this.mainPath + File.separator + RESOURCES + File.separator + "selectors";
        String[] versions = new File(selectorsPath).list();
        StringBuilder sb = new StringBuilder();
        sb.append("[" + applicationName.toUpperCase() + "_HOME-pageElementSample]");
        sb.append(System.lineSeparator());
        sb.append("xpath=//*[@id='page-element-sample']");
        try {
            for (String version : versions) {
                String iniFilePath = selectorsPath + File.separator + version + File.separator + applicationName + ".ini";
                File newSelector = new File(iniFilePath);
                if (!newSelector.exists()) {
                    Files.asCharSink(newSelector, StandardCharsets.UTF_8).write(sb.toString());
                    if (verbose) {
                        log.info("File [{}] created with success.", iniFilePath);
                    }
                } else {
                    if (verbose) {
                        log.info("File [{}] already exist.", iniFilePath);
                    }
                }
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param applicationName
     *            name of application removed.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeApplicationSelector(String applicationName, boolean verbose) {
        String selectorsPath = this.mainPath + File.separator + RESOURCES + File.separator + "selectors";
        String[] versions = new File(selectorsPath).list();
        try {
            for (String version : versions) {
                FileUtils.forceDelete(new File(selectorsPath + File.separator + version + File.separator + applicationName + ".ini"));
                if (verbose) {
                    log.info("{} removed with success.", selectorsPath);
                }
            }
        } catch (IOException e) {
            log.debug("{} not revove because do not exist.", selectorsPath);
        }
    }

    /**
     * @param applicationName
     *            name of application added.
     * @param noraRobotName
     *            Name of your robot generated by NoraUi Maven archetype.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addApplicationInPropertiesFile(String applicationName, String noraRobotName, boolean verbose) {
        manageApplicationInPropertiesFile(true, applicationName, noraRobotName, verbose);
    }

    /**
     * @param applicationName
     *            name of application removed.
     * @param noraRobotName
     *            Name of your robot generated by NoraUi Maven archetype.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeApplicationInPropertiesFile(String applicationName, String noraRobotName, boolean verbose) {
        manageApplicationInPropertiesFile(false, applicationName, noraRobotName, verbose);
    }

    /**
     * @param addMode
     * @param applicationName
     *            name of application.
     * @param noraRobotName
     *            Name of your robot generated by NoraUi Maven archetype.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void manageApplicationInPropertiesFile(boolean addMode, String applicationName, String noraRobotName, boolean verbose) {
        String propertiesfilePath = this.mainPath + File.separator + RESOURCES + File.separator + noraRobotName + ".properties";
        if (verbose) {
            log.info("Add application named [{}] in this properties file: {}]", applicationName, propertiesfilePath);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (!(applicationName + "=${" + applicationName + "}").equals(line)) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    if (addMode && "# application list".equals(line)) {
                        sb.append(applicationName + "=${" + applicationName + "}");
                        sb.append(System.lineSeparator());
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        updateFile(propertiesfilePath, sb);
    }

    /**
     * @param applicationName
     *            name of application added.
     * @param url
     *            of target application.
     * @param env
     *            is dev, ci, prod, ...
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void addApplicationInEnvPropertiesFile(String applicationName, String url, String env, boolean verbose) {
        manageApplicationInEnvPropertiesFile(true, applicationName, url, env, verbose);
    }

    /**
     * @param applicationName
     *            name of application removed.
     * @param url
     *            of target application.
     * @param env
     *            is dev, ci, prod, ...
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void removeApplicationInEnvPropertiesFile(String applicationName, String env, boolean verbose) {
        manageApplicationInEnvPropertiesFile(false, applicationName, "", env, verbose);
    }

    /**
     * @param addMode
     * @param applicationName
     *            name of application.
     * @param url
     *            of target application.
     * @param env
     *            is dev, ci, prod, ...
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void manageApplicationInEnvPropertiesFile(boolean addMode, String applicationName, String url, String env, boolean verbose) {
        String propertiesfilePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + ENVIRONMENTS + File.separator + env;
        if (verbose) {
            log.info("Add application named [{}] in this properties file: [{}]", applicationName, propertiesfilePath);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (!line.startsWith(applicationName + "=")) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    if (addMode && "# application list".equals(line)) {
                        sb.append(applicationName + "=" + url);
                        sb.append(System.lineSeparator());
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        updateFile(propertiesfilePath, sb);
    }

}
