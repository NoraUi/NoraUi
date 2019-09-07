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
