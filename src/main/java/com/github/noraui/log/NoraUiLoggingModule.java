package com.github.noraui.log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.log.annotation.Loggable;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.inject.Binder;
import com.google.inject.Module;

public class NoraUiLoggingModule implements Module {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NoraUiLoggingModule.class);

    private String packageName;

    public NoraUiLoggingModule(String packageName) {
        this.packageName = packageName;
    }

    /**
     * {@inheritDoc}
     * This configure method does not use Guice binding by far as it only injects static loggers in classes annotated with @see com.github.noraui.log.annotation.Loggable
     */
    @Override
    public void configure(Binder binder) {
        LOGGER.debug("NORAUI logging listeners binding");
        // @formatter:off
        try {
            ClassPath.from(getClass().getClassLoader()).getTopLevelClassesRecursive(packageName).stream()
            .map(ClassInfo::load)
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
                        field.set(null, LoggerFactory.getLogger(field.getDeclaringClass()));
                    } catch (IllegalAccessException iae) {
                        LOGGER.error("NoraUiLoggingModule.configure(Class<?>: " + clazz + ")", iae);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

}
