package com.github.noraui.cli.model;

import java.util.Scanner;

public class NoraUiCliParameters {

    private int featureCode;
    private String applicationName;
    private String scenarioName;
    private String modelName;
    private String url;
    private String description;
    String fields;
    private String results;
    private String cryptoKey;
    private Class<?> robotContext;
    private Class<?> robotCounter;
    private boolean verbose;
    private Scanner input;
    private boolean interactiveMode;

    /**
     * @param featureCode
     *            is -f arg ("1" is "add new application", "2" is "add new scenario", "3" is "add new model", "4" is
     *            "remove application", "5" is "remove scenario", "6" is "remove model", "7" is
     *            "encrypt data", "8" is "decrypt data", "9" is "status" and "0" is "exit NoraUi CLI").
     * @param applicationName
     *            name of application.
     * @param scenarioName
     *            name of scenario.
     * @param modelName
     *            name of model.
     * @param url
     *            is first(home or login page) url of application.
     * @param description
     *            is description of scenario.
     * @param fields
     *            is fields of model (String separated by a space).
     * @param results
     *            is results of model (String separated by a space).
     * @param cryptoKey
     *            is AES key (secret key).
     * @param robotContext
     *            Context class from robot.
     * @param robotCounter
     *            Counter class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     */
    public NoraUiCliParameters(int featureCode, String applicationName, String scenarioName, String modelName, String url, String description, String fields, String results, String cryptoKey,
            Class<?> robotContext, Class<?> robotCounter, boolean verbose, Scanner input, boolean interactiveMode) {
        this.featureCode = featureCode;
        this.applicationName = applicationName;
        this.scenarioName = scenarioName;
        this.modelName = modelName;
        this.url = url;
        this.description = description;
        this.fields = fields;
        this.results = results;
        this.cryptoKey = cryptoKey;
        this.robotContext = robotContext;
        this.robotCounter = robotCounter;
        this.verbose = verbose;
        this.input = input;
        this.interactiveMode = interactiveMode;
    }

    public int getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(int featureCode) {
        this.featureCode = featureCode;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getCryptoKey() {
        return cryptoKey;
    }

    public void setCryptoKey(String cryptoKey) {
        this.cryptoKey = cryptoKey;
    }

    public Class<?> getRobotContext() {
        return robotContext;
    }

    public void setRobotContext(Class<?> robotContext) {
        this.robotContext = robotContext;
    }

    public Class<?> getRobotCounter() {
        return robotCounter;
    }

    public void setRobotCounter(Class<?> robotCounter) {
        this.robotCounter = robotCounter;
    }

    public boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public Scanner getInput() {
        return input;
    }

    public void setInput(Scanner input) {
        this.input = input;
    }

    public boolean getInteractiveMode() {
        return interactiveMode;
    }

    public void setInteractiveMode(boolean interactiveMode) {
        this.interactiveMode = interactiveMode;
    }

}
