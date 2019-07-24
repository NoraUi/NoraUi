/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli.model;

public class NoraUiScenarioFile {

    /**
     * Scenario name.
     */
    private String name;
    
    /**
     * description of application.
     */
    private String description;
    
    /**
     * application name use by this scenario.
     */
    private String application;

    /**
     * Sets the status parameter to false when deleting to delete the corresponding file at the end of the algorithm
     */
    private boolean status;

    public NoraUiScenarioFile() {
        this.name = "";
        this.description = "";
        this.application = "";
        this.status = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
