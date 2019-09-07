package com.github.noraui.log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.log.annotation.Loggable;
import com.google.common.reflect.ClassPath;
import com.google.inject.Binder;
import com.google.inject.Module;

public class NoraUiLoggingModule implements Module {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NoraUiLoggingModule.class);

    private static final String TOP_LEVEL_PACKAGE = "com.github.noraui";

    /**
     * {@inheritDoc}
     * This configure method does not use Guice binding by far as it only injects static loggers in classes annotated with @see com.github.noraui.log.annotation.Loggable
     */
    @Override
    public void configure(Binder binder) {
        LOGGER.debug("NORAUI logging listeners binding");

        // @formatter:off
        try {
            ClassPath.from(getClass().getClassLoader()).getTopLevelClassesRecursive(TOP_LEVEL_PACKAGE).stream()
            .map(ci -> ci.load())
            .filter(c -> !Modifier.isInterface(c.getModifiers()))
            .filter(c -> c.isAnnotationPresent(Loggable.class))
            .forEach(this::injectSlf4JLogger);
        } catch (IOException e) {
            LOGGER.error("NoraUiLoggingModule.configure(Binder: " + binder + ")", e);

        }
        // @formatter:on
    }

    private void injectSlf4JLogger(Class<?> clazz) {
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == Logger.class && Modifier.isStatic(field.getModifiers())) {
                    try {
                        field.setAccessible(true);
                        Logger logger = LoggerFactory.getLogger(field.getDeclaringClass());
                        field.set(null, logger);
                    } catch (IllegalAccessException iae) {
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

}
