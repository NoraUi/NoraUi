package noraui.application.page.demo;

import static noraui.utils.Context.DEMO_KEY;

import org.apache.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;

import noraui.application.page.Page;
import noraui.exception.Callbacks;
import noraui.utils.Context;
import noraui.utils.Utilities;

public class Popup2DemoPage extends Page {

    private static Logger logger = Logger.getLogger(Popup2DemoPage.class.getName());

    public final PageElement bigTitle = new PageElement("-big_title", "This is a popup demo for NORAUI (Non-Regression Automation for User Interfaces).");

    private static final String TITLE_PAGE = "NoraUi Demo Popup";

    public Popup2DemoPage() {
        super();
        this.application = DEMO_KEY;
        this.pageKey = "DEMO_POP2";
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