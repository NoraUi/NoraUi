/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package cucumber.runtime;

import io.cucumber.core.api.Scenario;

public interface StepDefinitionMatch {
    void runStep(Scenario scenario) throws Throwable;

    void dryRunStep(Scenario scenario) throws Throwable;

    String getCodeLocation();

}
