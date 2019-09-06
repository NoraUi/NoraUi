package com.github.noraui.log.slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * @author Nicolas HALLOUIN & Stéohane GRILLON
 *         Implementation of a custom logger injection.
 *         This TypeListener looks for Slf4J loggers to inject
 * @see https://github.com/google/guice/wiki/CustomInjections
 */
public class Slf4JTypeListener implements TypeListener {
    public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
        Class<?> clazz = typeLiteral.getRawType();
        while (clazz != null) {
            for (Field field : typeLiteral.getRawType().getDeclaredFields()) {
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
