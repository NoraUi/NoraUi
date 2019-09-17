/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.utils.Messages;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

public class NoraUiLoggingInjector {

    public static final String TECHNICAL_ERROR_MESSAGE_NORAUI_LOGGING_INJECTOR_ALREADY_EXISTS = "TECHNICAL_ERROR_MESSAGE_NORAUI_LOGGING_INJECTOR_ALREADY_EXISTS";

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NoraUiLoggingInjector.class.getName());

    /**
     * Instance of Guice logging injector.
     */
    private static volatile Injector noraUiLoggingInjector = null;

    private NoraUiLoggingInjector() {
    }

    public static Injector getInjector() {
        return noraUiLoggingInjector;
    }

    public static void createInjector() {
        if (noraUiLoggingInjector == null) {
            noraUiLoggingInjector = Guice.createInjector(Stage.PRODUCTION, new NoraUiLoggingModule());
        } else {
            LOGGER.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + Messages.getMessage(TECHNICAL_ERROR_MESSAGE_NORAUI_LOGGING_INJECTOR_ALREADY_EXISTS));
        }
    }

    public static void resetInjector() {
        noraUiLoggingInjector = null;
    }

}
