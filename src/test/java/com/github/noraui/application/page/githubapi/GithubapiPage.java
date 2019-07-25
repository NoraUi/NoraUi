/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.page.githubapi;

import static com.github.noraui.utils.Context.GITHUBAPI_HOME;
import static com.github.noraui.utils.Context.GITHUBAPI_KEY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.Page;
import com.github.noraui.application.steps.ExpectSteps;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.utils.Context;
import com.google.inject.Singleton;

@Singleton
public class GithubapiPage extends Page {

    /**
     * Specific LOGGER
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(GithubapiPage.class);

    public GithubapiPage() {
        super();
        this.application = GITHUBAPI_KEY;
        this.pageKey = GITHUBAPI_HOME;
        this.callBack = Context.getCallBack(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_GITHUBAPI_HOME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPage(Object... elements) {
        try {
            Context.waitUntil(ExpectSteps.waitForLoad());
            LOGGER.debug("GitHub API loaded.");
            return true;
        } catch (Exception e) {
            LOGGER.error("GitHub API not loaded.", e);
            return false;
        }
    }

}