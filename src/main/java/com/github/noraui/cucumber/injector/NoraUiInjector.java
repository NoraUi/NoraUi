/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.injector;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.utils.Messages;
import com.google.inject.Injector;

public class NoraUiInjector {

    public static final String TECHNICAL_ERROR_MESSAGE_NORAUI_INJECTOR_SOURCE_ALREADY_EXISTS = "TECHNICAL_ERROR_MESSAGE_NORAUI_INJECTOR_SOURCE_ALREADY_EXISTS";

    /**
     * Instance of Guice injector. ({@link com.github.noraui.cucumber.injector.NoraUiInjectorSource}).
     */
    private static volatile Injector noraUiInjectorSource = null;

    private NoraUiInjector() {
    }

    public static Injector getNoraUiInjectorSource() {
        return noraUiInjectorSource;
    }

    public static void createInjector(Injector injector) throws TechnicalException {
        if (noraUiInjectorSource == null) {
            noraUiInjectorSource = injector;
        } else {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + Messages.getMessage(TECHNICAL_ERROR_MESSAGE_NORAUI_INJECTOR_SOURCE_ALREADY_EXISTS));
        }
    }

    public static void resetInjector() {
        noraUiInjectorSource = null;
    }

}
