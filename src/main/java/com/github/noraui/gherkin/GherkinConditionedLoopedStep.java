/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.gherkin;

public class GherkinConditionedLoopedStep extends GherkinStepCondition {

    private String step;

    public GherkinConditionedLoopedStep(String key, String step, String expected, String actual) {
        super(key, expected, actual);
        this.step = step;
    }

    public String getStep() {
        return step;
    }

}
