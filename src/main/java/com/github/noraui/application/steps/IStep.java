/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import org.openqa.selenium.WebDriver;

import com.github.noraui.application.page.IPage;

public interface IStep {

    /**
     * checkStep call checkPage
     *
     * @param page
     *            is the target page
     */
    public void checkStep(IPage page);

    /**
     * Quick getter to the global web driver
     * 
     * @return
     *         The global instance of web driver
     */
    WebDriver getDriver();

}
