package com.github.noraui.log.slf4j;

import java.lang.reflect.Field;

import org.slf4j.Logger;

import com.github.noraui.log.annotation.InjectLogger;
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
        System.err.println("hear...");
        Class<?> clazz = typeLiteral.getRawType();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == Logger.class && field.isAnnotationPresent(InjectLogger.class)) {
                    typeEncounter.register(new Slf4JMembersInjector<T>(field));
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
