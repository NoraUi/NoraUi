/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps.bakery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.bakery.ReferencerPage;
import com.github.noraui.application.steps.Step;
import com.google.inject.Inject;

public class RefererSteps extends Step {
    
    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BakerySteps.class);

    @Inject
    private ReferencerPage refererPage;
    
    

}
