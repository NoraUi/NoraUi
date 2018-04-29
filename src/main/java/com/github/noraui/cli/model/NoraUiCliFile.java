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

    public List<NoraUiApplicationFile> removeApplication(String applicationName) {
        List<NoraUiApplicationFile> r = getApplicationFiles();
        for (int i = 0; i < r.size(); i++) {
            NoraUiApplicationFile e = r.get(i);
            if (applicationName.equals(e.getName())) {
                r.remove(i);
                break;
            }
        }
        return r;
    }

    public List<NoraUiScenarioFile> addScenario(NoraUiScenarioFile noraUiScenarioFile) {
        List<NoraUiScenarioFile> r = getScenarioFiles();
        r.add(noraUiScenarioFile);
        return r;
    }

    public List<NoraUiScenarioFile> removeScenario(String scenarioName) {
        List<NoraUiScenarioFile> r = getScenarioFiles();
        for (int i = 0; i < r.size(); i++) {
            NoraUiScenarioFile e = r.get(i);
            if (scenarioName.equals(e.getName())) {
                r.remove(i);
                break;
            }
        }
        return r;
    }

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

    public List<NoraUiApplicationFile> removeModel(String applicationName, NoraUiModel model) {
        List<NoraUiApplicationFile> r = getApplicationFiles();
        for (int i = 0; i < r.size(); i++) {
            NoraUiApplicationFile e = r.get(i);
            if (applicationName.equals(e.getName())) {
                List<NoraUiModel> models = e.getModels();
                for (int j = 0; j < models.size(); j++) {
                    NoraUiModel m = models.get(i);
                    if (m.getName().equals(model.getName())) {
                        models.remove(i);
                        break;
                    }
                }
                e.setModels(models);
            }
            r.set(i, e);
        }
        return r;
    }

}
