package noraui.application.steps.demo;

import java.util.Map;

import com.google.inject.Inject;

import cucumber.api.java.en.Then;
import cucumber.api.java.fr.Alors;
import noraui.application.page.demo.DemoPage;
import noraui.application.steps.Step;
import noraui.exception.FailureException;
import noraui.exception.TechnicalException;

public class DemoSteps extends Step {

    @Inject
    private DemoPage demoPage;

    @Alors("Je mets à jour les checkboxes et vérifie la liste radio 'DEMO_HOME(.*)' avec '(.*)':")
    @Then("I update checkboxes and check radio list 'DEMO_HOME(.*)' with '(.*)':")
    public void selectCheckbox(String elementKey, String valueKey, Map<String, Boolean> values) throws TechnicalException, FailureException {
        selectCheckbox(this.demoPage.getPageElementByKey(elementKey), true);
        selectCheckbox(this.demoPage.getPageElementByKey(elementKey), valueKey, values);
        checkRadioList(this.demoPage.getPageElementByKey(elementKey), valueKey);
    }

}
