package noraui.application.steps;

import noraui.application.page.IPage;

public interface IStep {

    /**
     * checkStep call checkPage
     *
     * @param page
     *            is the target page
     */
    public void checkStep(IPage page);

}
