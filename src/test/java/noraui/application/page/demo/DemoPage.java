package noraui.application.page.demo;

import static noraui.utils.Context.DEMO_KEY;

import org.apache.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;

import noraui.application.page.Page;
import noraui.exception.Callbacks;
import noraui.utils.Context;
import noraui.utils.Utilities;

public class DemoPage extends Page {

    private static Logger logger = Logger.getLogger(DemoPage.class.getName());

    public final PageElement bigTitle = new PageElement("-big_title", "This is a demo for NORAUI (Non-Regression Automation for User Interfaces).");
    public final PageElement inputSelect = new PageElement("-input_select_field", "Input Select field");
    public final PageElement rateRadio = new PageElement("-rate", "Input radio rate");
    public final PageElement agreeCheckbox = new PageElement("-agree", "Input checkbox agree");
    public final PageElement submit = new PageElement("-submit", "Submit button");
    public final PageElement smilejs = new PageElement("-smilejs", "link a html balise with onclick by js (smile)");
    public final PageElement smile = new PageElement("-smile", "link a html balise with onclick (smile)");
    public final PageElement noExistElement = new PageElement("-noExistElement", "no exist element");
    public final PageElement message = new PageElement("-message", "message");

    private static final String TITLE_PAGE = "NoraUi Demo";

    public DemoPage() {
        super();
        this.application = DEMO_KEY;
        this.pageKey = "DEMO_HOME";
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