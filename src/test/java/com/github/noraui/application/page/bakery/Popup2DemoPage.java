/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.page.bakery;

import static com.github.noraui.utils.Context.BAKERY_KEY;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import com.github.noraui.application.page.Page;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Utilities;
import com.google.inject.Singleton;

@Loggable
@Singleton
public class Popup2DemoPage extends Page {

    static Logger log;

    public final PageElement bigTitle = new PageElement("-big_title", "This is a popup demo for NORAUI (Non-Regression Automation for User Interfaces).");

    private static final String TITLE_PAGE = "NoraUi Demo Popup";

    public Popup2DemoPage() {
        super();
        this.application = BAKERY_KEY;
        this.pageKey = "DEMO_POP2";
        this.callBack = Context.getCallBack(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_BAKERY_HOME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPage(Object... elements) {
        try {
            Context.waitUntil(ExpectedConditions.not(ExpectedConditions.titleIs("")));
            if (!TITLE_PAGE.equals(getDriver().getTitle())) {
                log.error("HTML title is not good");
                return false;
            }
        } catch (Exception e) {
            log.error("HTML title Exception", e);
            return false;
        }
        try {
            if (bigTitle.getLabel().equals(Utilities.findElement(bigTitle).getText())) {
                return true;
            }
            log.error("Big title is not good");
            return false;
        } catch (Exception e) {
            log.error("Big title Exception", e);
            return false;
        }
    }

}