package com.github.noraui.log.slf4j;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;

/**
 * @author Nicolas HALLOUIN & Stéohane GRILLON
 *         Implementation of a custom logger injection.
 * @see https://github.com/google/guice/wiki/CustomInjections
 * @param <T>
 *            Member type
 */
public class Slf4JMembersInjector<T> implements MembersInjector<T> {
    private final Field field;
    private final Logger logger;

    Slf4JMembersInjector(Field field) {
        System.err.println("Slf4JMembersInjector: " + field);
        this.field = field;
        this.logger = LoggerFactory.getLogger(field.getDeclaringClass());
        field.setAccessible(true);
    }

    public void injectMembers(T t) {
        try {
            field.set(t, logger);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}