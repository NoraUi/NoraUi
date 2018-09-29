/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.Page;
import com.github.noraui.exception.TechnicalException;

public class UnitTest4CLIContext extends Context {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(UnitTest4CLIContext.class);

    // applications

    // targets

    /**
     * Constructor is useless because all attributes are static
     */
    private UnitTest4CLIContext() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void initializeRobot(Class<?> clazz) throws TechnicalException {
        super.initializeRobot(clazz);
        logger.info("NoraRobotContext > initializeRobot()");

        // This line is here as an example to show how to do with internationalization using messages bundles.
        logger.info(Messages.format(Messages.getMessage("HELLO", "UnitTest4CLI"), "UnitTest4CLI"));

        // Urls configuration

        // Selectors configuration

        // Exception Callbacks

        // applications mapping

        Page.setPageMainPackage("com.your.company.application.pages.");
    }

    /**
     * Get context singleton.
     *
     * @return context instance
     */
    public static Context getInstance() {
        if (instance == null || !(instance instanceof UnitTest4CLIContext)) {
            instance = new UnitTest4CLIContext();
        }
        return instance;
    }

    // home getters

}