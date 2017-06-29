package noraui.exception;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.log4j.Logger;

import noraui.exception.Callbacks.Callback;
import noraui.utils.Context;

public class Callbacks extends HashMap<String, Callback> {

    /**
     *
     */
    private static final long serialVersionUID = -8116045885450166607L;

    /**
     * Specific logger
     */
    private static final Logger logger = Logger.getLogger(Callback.class);

    public static final String CLOSE_WINDOW_AND_SWITCH_TO_DEMO_HOME = "CLOSE_WINDOW_AND_SWITCH_TO_DEMO_HOME";
    public static final String CLOSE_WINDOW_AND_SWITCH_TO_LOGOGAME_HOME = "CLOSE_WINDOW_AND_SWITCH_TO_LOGOGAME_HOME";
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
                logger.debug("ExceptionCallback with full name of class: " + strClass);
                this.objectClass = Class.forName(strClass);

                Class<?>[] paramClasses = new Class<?>[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    paramClasses[i] = parameters[i].getClass();
                }
                this.method = objectClass.getDeclaredMethod(strMethod, paramClasses);
                this.parameters = parameters;
            } catch (Exception e) {
                logger.error(e);
            }
        }

        /**
         * Invokes the callback method
         */
        public void call() {
            try {
                method.invoke(objectClass.newInstance(), parameters);
            } catch (InvocationTargetException ite) {
                logger.error(ite.getTargetException(), ite);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

}
