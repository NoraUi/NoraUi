package noraui.cucumber.injector;

import com.google.inject.Injector;

import noraui.exception.TechnicalException;

public class NoraUiInjector {

    /**
     * Instance of Guice injector. ({@link noraui.cucumber.injector.NoraUiInjectorSource}).
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
            throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE + TechnicalException.TECHNICAL_ERROR_MESSAGE_NORAUI_INJECTOR_SOURCE_ALREADY_EXISTS);
        }
    }

}
