package com.github.noraui.statistics;

/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
public class Statistics {

    private String norauiVersion;
    private String groupId;
    private String artifactId;
    private String version;

    public String getNorauiVersion() {
        return norauiVersion;
    }

    public void setNorauiVersion(String norauiVersion) {
        this.norauiVersion = norauiVersion;
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

}
