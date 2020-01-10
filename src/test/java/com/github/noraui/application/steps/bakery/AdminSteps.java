/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps.bakery;

import org.slf4j.Logger;

import com.github.noraui.application.page.bakery.AdminPage;
import com.github.noraui.application.steps.Step;
import com.github.noraui.log.annotation.Loggable;
import com.google.inject.Inject;

@Loggable
public class AdminSteps extends Step {

    static Logger log;

    @Inject
    private AdminPage adminPage;

}
