/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.exception;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.exception.Callbacks.Callback;
import com.github.noraui.utils.Context;

public class Callbacks extends HashMap<String, Callback> {

    /**
     *
     */
    private static final long serialVersionUID = -8116045885450166607L;

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Callback.class);

    /**
     * @deprecated (Only used for demo application for testing purposes, use {@link #CLOSE_WINDOW_AND_SWITCH_TO_GEOBEER_HOME} instead)
     */
    @Deprecated
    public static final String CLOSE_WINDOW_AND_SWITCH_TO_DEMO_HOME = "CLOSE_WINDOW_AND_SWITCH_TO_DEMO_HOME";

    /**
     * @deprecated (Only used for demo application for testing purposes, use {@link #CLOSE_WINDOW_AND_SWITCH_TO_GEOBEER_HOME} instead)
     */
    @Deprecated
    public static final String CLOSE_WINDOW_AND_SWITCH_TO_LOGOGAME_HOME = "CLOSE_WINDOW_AND_SWITCH_TO_LOGOGAME_HOME";

    public static final String CLOSE_WINDOW_AND_SWITCH_TO_GEOBEER_HOME = "CLOSE_WINDOW_AND_SWITCH_TO_GEOBEER_HOME";
    public static final String CLOSE_WINDOW_AND_SWITCH_TO_BAKERY_HOME = "CLOSE_WINDOW_AND_SWITCH_TO_BAKERY_HOME";
    public static final String CLOSE_WINDOW_AND_SWITCH_TO_GITHUBAPI_HOME = "CLOSE_WINDOW_AND_SWITCH_TO_GITHUBAPI_HOME";
    public static final String RESTART_WEB_DRIVER = "RESTART_WEB_DRIVER";

    public Callbacks() {
        super();
        put(RESTART_WEB_DRIVER, Context.STEPS_BROWSER_STEPS_CLASS_QUALIFIED_NAME, "restartWebDriver");
    }

    public void put(String key, String strClass, String strMethod, Object... parameters) {
        put(key, new Callback(strClass, strMethod, parameters));
    }

    public class Callback {

        /**
         *
         */
        private Class<?> objectClass;
        private Method method;
        private Object[] parameters;

        /**
         * Constructor
         *
         * @param strClass
         *            String full name of class (ex: "utils.CLASSNAME")
         * @param strMethod
         *            String method to call
         * @param parameters
         *            Method to call parameters
         */
        public Callback(String strClass, String strMethod, Object... parameters) {
            try {
                LOGGER.debug("ExceptionCallback with full name of class: {}", strClass);
                this.objectClass = Class.forName(strClass);

                final Class<?>[] paramClasses = new Class<?>[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    paramClasses[i] = parameters[i].getClass();
                }
                this.method = objectClass.getDeclaredMethod(strMethod, paramClasses);
                this.parameters = parameters;
            } catch (final Exception e) {
                LOGGER.error("error Callback()", e);
            }
        }

        /**
         * Invokes the callback method
         */
        public void call() {
            try {
                method.invoke(objectClass.newInstance(), parameters);
            } catch (final InvocationTargetException ite) {
                LOGGER.error("error InvocationTargetException", ite);
            } catch (final Exception e) {
                LOGGER.error("error Callback.call()", e);
            }
        }
    }

}
