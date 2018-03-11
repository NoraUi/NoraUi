/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Application {

    /**
     * Add new target application to your robot.
     * Sample if you add google: -f 1 -n google -u http://www.google.com --verbose
     * 
     * @param name
     * @param url
     * @param robotContext
     * @param noraRobotName
     * @param verbose
     */
    public void add(String name, String url, Class robotContext, String noraRobotName, boolean verbose) {
        System.out.println("Add a new application named [" + name + "] with this url: " + url);
        addApplicationPages(name, noraRobotName, robotContext, verbose);
        addApplicationSteps(name, noraRobotName, robotContext, verbose);
        addApplicationContext(robotContext, name, verbose);
        addApplicationSelector(name, verbose);
        addApplicationInPropertiesFile(name, noraRobotName, verbose);
        addApplicationInEnvPropertiesFile(name, url, "ci", verbose);
        addApplicationInEnvPropertiesFile(name, url, "dev", verbose);
        addApplicationInEnvPropertiesFile(name, url, "prod", verbose);
    }

    /**
     * @param name
     * @param noraRobotName
     * @param robotContext
     * @param verbose
     */
    private void addApplicationPages(String name, String noraRobotName, Class robotContext, boolean verbose) {
        String pagePath = "src" + File.separator + "main" + File.separator + "java" + File.separator
                + robotContext.getCanonicalName().replaceAll("\\.", "/").replaceAll("utils", "application/pages/" + name).replaceAll("/", Matcher.quoteReplacement(File.separator))
                        .replaceAll(robotContext.getSimpleName(), name.toUpperCase().charAt(0) + name.substring(1) + "Page")
                + ".java";
        StringBuilder sb = new StringBuilder();
        sb.append("/**").append(System.lineSeparator());
        sb.append(" * " + noraRobotName + " generated free by NoraUi Oraganization https://github.com/NoraUi").append(System.lineSeparator());
        sb.append(" * " + noraRobotName + " is licensed under the license BSD.").append(System.lineSeparator());
        sb.append(" * ").append(System.lineSeparator());
        sb.append(" * CAUTION: " + noraRobotName + " use NoraUi library. This project is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE").append(System.lineSeparator());
        sb.append(" */").append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replaceAll("utils", "application.pages." + name) + ";").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import static " + robotContext.getCanonicalName() + "." + name.toUpperCase() + "_KEY;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import org.openqa.selenium.support.ui.ExpectedConditions;").append(System.lineSeparator());
        sb.append("import org.slf4j.Logger;").append(System.lineSeparator());
        sb.append("import org.slf4j.LoggerFactory;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import com.github.noraui.application.page.Page;").append(System.lineSeparator());
        sb.append("import com.github.noraui.utils.Context;").append(System.lineSeparator());
        sb.append("import " + robotContext.getCanonicalName() + ";").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("public class " + name.toUpperCase().charAt(0) + name.substring(1) + "Page extends Page {").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * Specific logger").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    private static final Logger logger = LoggerFactory.getLogger(" + name.toUpperCase().charAt(0) + name.substring(1) + "Page.class);").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    public final PageElement pageElementSample = new PageElement(\"-pageElementSample\", \"PageElement Sample\");").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    private static final String TITLE_PAGE = \"" + name.toUpperCase().charAt(0) + name.substring(1) + "\";").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    public " + name.toUpperCase().charAt(0) + name.substring(1) + "Page() {").append(System.lineSeparator());
        sb.append("        super();").append(System.lineSeparator());
        sb.append("        this.application = " + name.toUpperCase() + "_KEY;").append(System.lineSeparator());
        sb.append("        this.pageKey = \"" + name.toUpperCase() + "_HOM\";").append(System.lineSeparator());
        sb.append("        this.callBack = Context.getCallBack(" + noraRobotName + "Context.CLOSE_WINDOW_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME);").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * {@inheritDoc}").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Override").append(System.lineSeparator());
        sb.append("    public boolean checkPage(Object... elements) {").append(System.lineSeparator());
        sb.append("        try {").append(System.lineSeparator());
        sb.append("            Context.waitUntil(ExpectedConditions.not(ExpectedConditions.titleIs(\"\")));").append(System.lineSeparator());
        sb.append("            if (!TITLE_PAGE.equals(getDriver().getTitle())) {").append(System.lineSeparator());
        sb.append("                logger.error(\"HTML title is not good\");").append(System.lineSeparator());
        sb.append("                return false;").append(System.lineSeparator());
        sb.append("            }").append(System.lineSeparator());
        sb.append("            return true;").append(System.lineSeparator());
        sb.append("        } catch (Exception e) {").append(System.lineSeparator());
        sb.append("            logger.error(\"HTML title Exception\", e);").append(System.lineSeparator());
        sb.append("            return false;").append(System.lineSeparator());
        sb.append("        }").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(pagePath.substring(0, pagePath.lastIndexOf(File.separator))));
            File newSelector = new File(pagePath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, Charsets.UTF_8).write(sb.toString());
            }
        } catch (Exception e) {
            System.err.println("IOException " + e);
            System.exit(1);
        }
    }

    /**
     * @param name
     * @param noraRobotName
     * @param robotContext
     * @param verbose
     */
    private void addApplicationSteps(String name, String noraRobotName, Class robotContext, boolean verbose) {
        String pagePath = "src" + File.separator + "main" + File.separator + "java" + File.separator
                + robotContext.getCanonicalName().replaceAll("\\.", "/").replaceAll("utils", "application/steps/" + name).replaceAll("/", Matcher.quoteReplacement(File.separator))
                        .replaceAll(robotContext.getSimpleName(), name.toUpperCase().charAt(0) + name.substring(1) + "Steps")
                + ".java";
        StringBuilder sb = new StringBuilder();
        sb.append("/**").append(System.lineSeparator());
        sb.append(" * " + noraRobotName + " generated free by NoraUi Oraganization https://github.com/NoraUi").append(System.lineSeparator());
        sb.append(" * " + noraRobotName + " is licensed under the license BSD.").append(System.lineSeparator());
        sb.append(" * ").append(System.lineSeparator());
        sb.append(" * CAUTION: " + noraRobotName + " use NoraUi library. This project is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE").append(System.lineSeparator());
        sb.append(" */").append(System.lineSeparator());
        sb.append(robotContext.getPackage().toString().replaceAll("utils", "application.steps." + name) + ";").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import com.github.noraui.application.steps.Step;").append(System.lineSeparator());
        sb.append("import com.github.noraui.exception.FailureException;").append(System.lineSeparator());
        sb.append("import com.github.noraui.exception.Result;").append(System.lineSeparator());
        sb.append("import com.github.noraui.utils.Messages;").append(System.lineSeparator());
        sb.append("import com.google.inject.Inject;").append(System.lineSeparator());
        sb.append("import "
                + robotContext.getCanonicalName().replaceAll("utils", "application.pages." + name).replaceAll(robotContext.getSimpleName(), name.toUpperCase().charAt(0) + name.substring(1) + "Page;"))
                .append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("import cucumber.api.java.en.Then;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("public class " + name.toUpperCase().charAt(0) + name.substring(1) + "Steps extends Step {").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    @Inject").append(System.lineSeparator());
        sb.append("    private " + name.toUpperCase().charAt(0) + name.substring(1) + "Page " + name + "Page;").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    /**").append(System.lineSeparator());
        sb.append("     * Check Login page.").append(System.lineSeparator());
        sb.append("     *").append(System.lineSeparator());
        sb.append("     * @throws FailureException").append(System.lineSeparator());
        sb.append("     *             if the scenario encounters a functional error.").append(System.lineSeparator());
        sb.append("     */").append(System.lineSeparator());
        sb.append("    @Then(\"The " + name.toUpperCase() + " home page is displayed\")").append(System.lineSeparator());
        sb.append("    public void check" + name.toUpperCase().charAt(0) + name.substring(1) + "HomePage() throws FailureException {").append(System.lineSeparator());
        sb.append("        if (!" + name + "Page.checkPage()) {").append(System.lineSeparator());
        sb.append("            new Result.Failure<>(" + name + "Page.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, " + name + "Page.getCallBack());")
                .append(System.lineSeparator());
        sb.append("        }").append(System.lineSeparator());
        sb.append("    }").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        try {
            FileUtils.forceMkdir(new File(pagePath.substring(0, pagePath.lastIndexOf(File.separator))));
            File newSelector = new File(pagePath);
            if (!newSelector.exists()) {
                Files.asCharSink(newSelector, Charsets.UTF_8).write(sb.toString());
            }
        } catch (Exception e) {
            System.err.println("IOException " + e);
            System.exit(1);
        }
    }

    /**
     * @param robotContext
     * @param name
     * @param verbose
     */
    private void addApplicationContext(Class robotContext, String name, boolean verbose) {
        String contextPath = "src" + File.separator + "main" + File.separator + "java" + File.separator
                + robotContext.getCanonicalName().replaceAll("\\.", "/").replaceAll("/", Matcher.quoteReplacement(File.separator)) + ".java";
        if (verbose) {
            System.out.println("Add application named [" + name + "] in context.");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(contextPath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                if (!(("    public static final String " + name.toUpperCase() + "_KEY = \"" + name + "\";").equals(line)
                        || ("    public static final String " + name.toUpperCase() + "_HOME = \"" + name.toUpperCase() + "_HOME\";").equals(line)
                        || ("    private String " + name + "Home; // " + name.toUpperCase() + " home url").equals(line)
                        || ("    public static final String GO_TO_" + name.toUpperCase() + "_HOME = \"GO_TO_" + name.toUpperCase() + "_HOME\";").equals(line)
                        || ("    public static final String CLOSE_WINDOW_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME = \"CLOSE_WINDOW_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME\";").equals(line)
                        || ("    public static final String CLOSE_ALL_WINDOWS_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME = \"CLOSE_ALL_WINDOWS_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME\";")
                                .equals(line)
                        || ("        " + name + "Home = getProperty(" + name.toUpperCase() + "_KEY, applicationProperties);").equals(line)
                        || ("        initApplicationDom(clazz.getClassLoader(), selectorsVersion, " + name.toUpperCase() + "_KEY);").equals(line)
                        || ("        exceptionCallbacks.put(GO_TO_" + name.toUpperCase() + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, GO_TO_URL_METHOD_NAME, " + name.toUpperCase() + "_HOME);")
                                .equals(line)
                        || ("        exceptionCallbacks.put(CLOSE_WINDOW_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, \"closeWindowAndSwitchTo\", "
                                + name.toUpperCase() + "_KEY, " + name.toUpperCase() + "_HOME);").equals(line)
                        || ("        exceptionCallbacks.put(CLOSE_ALL_WINDOWS_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, \"closeAllWindowsAndSwitchTo\", "
                                + name.toUpperCase() + "_KEY);").equals(line)
                        || ("        applications.put(" + name.toUpperCase() + "_KEY, new Application(" + name.toUpperCase() + "_HOME, " + name + "Home));").equals(line))) {

                    if (("    public String get" + name.toUpperCase().charAt(0) + name.substring(1) + "Home() {").equals(line)) {
                        line = br.readLine();
                        line = br.readLine();
                        line = br.readLine();
                    }
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    if ("    // applications".equals(line)) {
                        sb.append("    public static final String " + name.toUpperCase() + "_KEY = \"" + name + "\";");
                        sb.append(System.lineSeparator());
                        sb.append("    public static final String " + name.toUpperCase() + "_HOME = \"" + name.toUpperCase() + "_HOME\";");
                        sb.append(System.lineSeparator());
                        sb.append("    private String " + name + "Home; // " + name.toUpperCase() + " home url");
                        sb.append(System.lineSeparator());
                    }
                    if ("    // targets".equals(line)) {
                        sb.append("    public static final String GO_TO_" + name.toUpperCase() + "_HOME = \"GO_TO_" + name.toUpperCase() + "_HOME\";");
                        sb.append(System.lineSeparator());
                        sb.append("    public static final String CLOSE_WINDOW_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME = \"CLOSE_WINDOW_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME\";");
                        sb.append(System.lineSeparator());
                        sb.append("    public static final String CLOSE_ALL_WINDOWS_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME = \"CLOSE_ALL_WINDOWS_AND_SWITCH_TO_" + name.toUpperCase()
                                + "_HOME\";");
                        sb.append(System.lineSeparator());
                    }
                    if ("        // Urls configuration".equals(line)) {
                        sb.append("        " + name + "Home = getProperty(" + name.toUpperCase() + "_KEY, applicationProperties);");
                        sb.append(System.lineSeparator());
                    }
                    if ("        // Selectors configuration".equals(line)) {
                        sb.append("        initApplicationDom(clazz.getClassLoader(), selectorsVersion, " + name.toUpperCase() + "_KEY);");
                        sb.append(System.lineSeparator());
                    }
                    if ("        // Exception Callbacks".equals(line)) {
                        sb.append("        exceptionCallbacks.put(GO_TO_" + name.toUpperCase() + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, GO_TO_URL_METHOD_NAME, " + name.toUpperCase()
                                + "_HOME);");
                        sb.append(System.lineSeparator());
                        sb.append("        exceptionCallbacks.put(CLOSE_WINDOW_AND_SWITCH_TO_" + name.toUpperCase() + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, \"closeWindowAndSwitchTo\", "
                                + name.toUpperCase() + "_KEY, " + name.toUpperCase() + "_HOME);");
                        sb.append(System.lineSeparator());
                        sb.append("        exceptionCallbacks.put(CLOSE_ALL_WINDOWS_AND_SWITCH_TO_" + name.toUpperCase()
                                + "_HOME, STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, \"closeAllWindowsAndSwitchTo\", " + name.toUpperCase() + "_KEY);");
                        sb.append(System.lineSeparator());
                    }
                    if ("        // applications mapping".equals(line)) {
                        sb.append("        applications.put(" + name.toUpperCase() + "_KEY, new Application(" + name.toUpperCase() + "_HOME, " + name + "Home));");
                        sb.append(System.lineSeparator());
                    }
                    if ("    // home getters".equals(line)) {
                        sb.append("    public String get" + name.toUpperCase().charAt(0) + name.substring(1) + "Home() {");
                        sb.append(System.lineSeparator());
                        sb.append("        return " + name + "Home;");
                        sb.append(System.lineSeparator());
                        sb.append("    }");
                        sb.append(System.lineSeparator());
                    }
                }
                line = br.readLine();
            }
            FileWriter fw = new FileWriter(contextPath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString().substring(0, sb.toString().length() - System.lineSeparator().length()));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.err.println("IOException " + e);
            System.exit(1);
        }
    }

    /**
     * @param name
     * @param verbose
     */
    private void addApplicationSelector(String name, boolean verbose) {
        File selectors = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "selectors");
        String[] versions = selectors.list();
        StringBuilder sb = new StringBuilder();
        sb.append("[" + name.toUpperCase() + "_HOM-pageElementSample]");
        sb.append(System.lineSeparator());
        sb.append("xpath=//*[@id='page-element-sample']");
        try {
            for (String version : versions) {
                File newSelector = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "selectors" + File.separator + version + File.separator + name + ".ini");
                if (!newSelector.exists()) {
                    Files.asCharSink(newSelector, Charsets.UTF_8).write(sb.toString());
                }
            }
        } catch (Exception e) {
            System.err.println("IOException " + e);
            System.exit(1);
        }
    }

    /**
     * @param name
     * @param verbose
     */
    private void addApplicationInPropertiesFile(String name, String noraRobotName, boolean verbose) {
        String propertiesfilePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + noraRobotName + ".properties";
        if (verbose) {
            System.out.println("Add application named [" + name + "] in this properties file: " + propertiesfilePath + "]");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                if (!(name + "=${" + name + "}").equals(line)) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    if ("# application list".equals(line)) {
                        sb.append(name + "=${" + name + "}");
                        sb.append(System.lineSeparator());
                    }
                }
                line = br.readLine();
            }
            FileWriter fw = new FileWriter(propertiesfilePath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString().substring(0, sb.toString().length() - System.lineSeparator().length()));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.err.println("IOException " + e);
            System.exit(1);
        }
    }

    /**
     * @param name
     * @param url
     * @param env
     * @param verbose
     */
    private void addApplicationInEnvPropertiesFile(String name, String url, String env, boolean verbose) {
        String propertiesfilePath = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "environments" + File.separator + env + ".properties";
        if (verbose) {
            System.out.println("Add application named [" + name + "] in this properties file: " + propertiesfilePath + "]");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                if (!line.startsWith(name + "=")) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    if ("# application list".equals(line)) {
                        sb.append(name + "=" + url);
                        sb.append(System.lineSeparator());
                    }
                }
                line = br.readLine();
            }
            FileWriter fw = new FileWriter(propertiesfilePath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString().substring(0, sb.toString().length() - System.lineSeparator().length()));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.err.println("IOException " + e);
            System.exit(1);
        }
    }

}
