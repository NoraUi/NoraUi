/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.page.demo;

import static com.github.noraui.utils.Context.DEMO_KEY;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.Page;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Utilities;

public class Popup1DemoPage extends Page {

    /**
     * Specific logger
     */
    protected static final Logger logger = LoggerFactory.getLogger(Popup1DemoPage.class);

    public final PageElement bigTitle = new PageElement("-big_title", "This is a popup demo for NORAUI (Non-Regression Automation for User Interfaces).");

    private static final String TITLE_PAGE = "NoraUi Demo Popup";

    public Popup1DemoPage() {
        super();
        this.application = DEMO_KEY;
        this.pageKey = "DEMO_POP1";
        this.callBack = Context.getCallBack(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_DEMO_HOME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPage(Object... elements) {
        try {
            Context.waitUntil(ExpectedConditions.not(ExpectedConditions.titleIs("")));
            if (!TITLE_PAGE.equals(getDriver().getTitle())) {
                logger.error("HTML title is not good");
                return false;
            }
        } catch (Exception e) {
            logger.error("HTML title Exception", e);
            return false;
        }
        try {
            if (bigTitle.getLabel().equals(Utilities.findElement(bigTitle).getText())) {
                return true;
            }
            logger.error("Big title is not good");
            return false;
        } catch (Exception e) {
            logger.error("Big title Exception", e);
            return false;
        }
    }

}