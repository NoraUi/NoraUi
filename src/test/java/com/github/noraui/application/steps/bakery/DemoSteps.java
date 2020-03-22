/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps.bakery;

import java.util.Map;

import com.github.noraui.application.page.bakery.DemoPage;
import com.github.noraui.application.steps.Step;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.TechnicalException;
import com.google.inject.Inject;

import io.cucumber.java.en.Then;
import io.cucumber.java.fr.Alors;

public class DemoSteps extends Step {

    @Inject
    private DemoPage demoPage;

    @Alors("Je mets à jour les checkboxes et vérifie la liste radio {string} avec {string}:")
    @Then("I update checkboxes and check radio list {string} with {string}:")
    public void selectCheckbox(String pageElement, String valueKey, Map<String, Boolean> values) throws TechnicalException, FailureException, IllegalArgumentException, IllegalAccessException {
        String elementName = pageElement.split("-")[1];
        // is it a sample with inject page by Guice, but you can use 'demo.DemoPage-agree' instead of 'BAKERY_DEMO-agree'.
        // String page = pageElement.split("-")[0];
        // selectCheckbox(Page.getInstance(page).getPageElementByKey('-' + elementName), true);
        selectCheckbox(this.demoPage.getPageElementByKey('-' + elementName), true);
        selectCheckbox(this.demoPage.getPageElementByKey('-' + elementName), valueKey, values);
        checkRadioList(this.demoPage.getPageElementByKey('-' + elementName), valueKey);
    }

}
