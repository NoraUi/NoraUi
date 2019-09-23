/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.log;

import java.util.HashMap;
import java.util.Map;

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
    private static volatile Map<String, Injector> logInjectors = new HashMap<>();

    private NoraUiLoggingInjector() {
    }

    public static Map<String, Injector> getInjector() {
        return logInjectors;
    }

    public static void addInjector(String packageName) {
        if (!logInjectors.containsKey(packageName)) {
            logInjectors.put(packageName, Guice.createInjector(Stage.PRODUCTION, new NoraUiLoggingModule(packageName)));
            LOGGER.info("Created injector: " + packageName);
        } else {
            LOGGER.warn(
                    Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + String.format(Messages.getMessage(TECHNICAL_ERROR_MESSAGE_NORAUI_LOGGING_INJECTOR_ALREADY_EXISTS), packageName));
        }
    }

    public static void resetInjector() {
        logInjectors.clear();
    }

}
