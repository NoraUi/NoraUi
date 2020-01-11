/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps.githubapi;

import org.slf4j.Logger;

import com.github.noraui.application.page.githubapi.GithubapiPage;
import com.github.noraui.application.steps.Step;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Messages;
import com.google.inject.Inject;

import io.cucumber.java.en.Then;
import io.cucumber.java.fr.Alors;

@Loggable
public class GithubapiSteps extends Step {

    static Logger log;

    @Inject
    private GithubapiPage githubapiPage;

    @Alors("Le portail GITHUBAPI est affiché")
    @Then("The GITHUBAPI portal is displayed")
    public void checkGithubapi() throws FailureException {
        if (!githubapiPage.checkPage()) {
            new Result.Failure<>("GITHUBAPI", Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, this.githubapiPage.getCallBack());
        }
        log.debug("The GITHUBAPI portal is displayed.");
    }

}
