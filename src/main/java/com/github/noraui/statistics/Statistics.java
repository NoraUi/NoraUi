package com.github.noraui.statistics;

import java.util.Map;

/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
public class Statistics {

    /**
     * Name of NoraUi version use by the bot.
     */
    private String norauiVersion;

    /**
     * Name of bot.
     */
    private String name;

    /**
     * Maven groupId of bot.
     */
    private String groupId;

    /**
     * Maven artifactId of bot.
     */
    private String artifactId;

    /**
     * Maven version of bot.
     */
    private String version;

    /**
     * Target aplications use by bot.
     */
    private Map<String, String> applications;

    /**
     * All java methods mapped by cucumber annotations.
     */
    private Map<String, String> cucumberMethods;

    public String getNorauiVersion() {
        return norauiVersion;
    }

    public void setNorauiVersion(String norauiVersion) {
        this.norauiVersion = norauiVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getApplications() {
        return applications;
    }

    public void setApplications(Map<String, String> applications) {
        this.applications = applications;
    }

    public Map<String, String> getCucumberMethods() {
        return cucumberMethods;
    }

    public void setCucumberMethods(Map<String, String> cucumberMethods) {
        this.cucumberMethods = cucumberMethods;
    }

}
