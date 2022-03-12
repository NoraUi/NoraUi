/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli.model;

import java.util.List;

public class NoraUiCliFile {

    private List<NoraUiApplicationFile> applicationFiles;
    private List<NoraUiScenarioFile> scenarioFiles;

    public List<NoraUiApplicationFile> getApplicationFiles() {
        return applicationFiles;
    }

    public void setApplicationFiles(List<NoraUiApplicationFile> applicationFiles) {
        this.applicationFiles = applicationFiles;
    }

    public List<NoraUiScenarioFile> getScenarioFiles() {
        return scenarioFiles;
    }

    public void setScenarioFiles(List<NoraUiScenarioFile> scenarioFiles) {
        this.scenarioFiles = scenarioFiles;
    }

    public List<NoraUiApplicationFile> addApplication(NoraUiApplicationFile noraUiApplicationFile) {
        List<NoraUiApplicationFile> r = getApplicationFiles();
        r.add(noraUiApplicationFile);
        return r;
    }

    /**
     * @param applicationName
     *            name of application removed.
     * @return list of NoraUiApplicationFile Object contain all application data from CLI Files.
     */
    public List<NoraUiApplicationFile> removeApplication(String applicationName) {
        List<NoraUiApplicationFile> r = getApplicationFiles();
        for (int i = 0; i < r.size(); i++) {
            NoraUiApplicationFile e = r.get(i);
            if (applicationName.equals(e.getName())) {
                e.setStatus(false);
                r.set(i, e);
                break;
            }
        }
        return r;
    }

    /**
     * @param noraUiScenarioFile
     *            Object contain all scenario data added.
     * @return list of NoraUiScenarioFile Object contain all scenario data from CLI Files.
     */
    public List<NoraUiScenarioFile> addScenario(NoraUiScenarioFile noraUiScenarioFile) {
        List<NoraUiScenarioFile> r = getScenarioFiles();
        r.add(noraUiScenarioFile);
        return r;
    }

    /**
     * @param scenarioName
     *            name of scenario removed.
     * @return list of NoraUiScenarioFile Object contain all scenario data from CLI Files.
     */
    public List<NoraUiScenarioFile> removeScenario(String scenarioName) {
        List<NoraUiScenarioFile> r = getScenarioFiles();
        for (int i = 0; i < r.size(); i++) {
            NoraUiScenarioFile e = r.get(i);
            if (scenarioName.equals(e.getName())) {
                e.setStatus(false);
                r.set(i, e);
                break;
            }
        }
        return r;
    }

    /**
     * @param applicationName
     *            name of application.
     * @param model
     *            name of model added.
     * @return list of NoraUiApplicationFile Object contain all application data from CLI Files.
     */
    public List<NoraUiApplicationFile> addModel(String applicationName, NoraUiModel model) {
        List<NoraUiApplicationFile> r = getApplicationFiles();
        for (int i = 0; i < r.size(); i++) {
            NoraUiApplicationFile e = r.get(i);
            if (applicationName.equals(e.getName())) {
                List<NoraUiModel> models = e.getModels();
                models.add(model);
                e.setModels(models);
                r.set(i, e);
                break;
            }
        }
        return r;
    }

    /**
     * @param applicationName
     *            name of application.
     * @param modelName
     *            name of model removed.
     * @return list of NoraUiApplicationFile Object contain all application data from CLI Files.
     */
    public List<NoraUiApplicationFile> removeModel(String applicationName, String modelName) {
        List<NoraUiApplicationFile> r = getApplicationFiles();
        for (int i = 0; i < r.size(); i++) {
            NoraUiApplicationFile e = r.get(i);
            if (applicationName.equals(e.getName())) {
                List<NoraUiModel> models = e.getModels();
                for (int j = 0; j < models.size(); j++) {
                    NoraUiModel m = models.get(j);
                    if (m.getName().equals(modelName)) {
                        models.remove(j);
                        break;
                    }
                }
            }
            r.set(i, e);
        }
        return r;
    }

}
