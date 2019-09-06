package com.github.noraui.log;

import static com.google.inject.matcher.Matchers.any;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.log.slf4j.Slf4JTypeListener;
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
     */
    @Override
    public void configure(Binder binder) {
        LOGGER.debug("NORAUI logging listeners binding");

        // @formatter:off
        try {
            ClassPath.from(getClass().getClassLoader()).getTopLevelClassesRecursive(TOP_LEVEL_PACKAGE).stream()
            .map(ci -> ci.load())
            .filter(c -> c.isAnnotationPresent(Loggable.class))
            .forEach(binder::bind);
        } catch (IOException e) {
            LOGGER.error("NoraUiLoggingModule.configure(Binder: " + binder + ")", e);

        }
        // @formatter:on

        binder.bindListener(any(), new Slf4JTypeListener());
    }

}
