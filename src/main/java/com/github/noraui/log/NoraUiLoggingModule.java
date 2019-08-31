package com.github.noraui.log;

import static com.google.inject.matcher.Matchers.any;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.log.slf4j.Slf4JTypeListener;
import com.google.inject.Binder;
import com.google.inject.Module;

public class NoraUiLoggingModule implements Module {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NoraUiLoggingModule.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(Binder binder) {
        LOGGER.info("NORAUI listeners binding");
        binder.bindListener(any(), new Slf4JTypeListener());
    }

}
