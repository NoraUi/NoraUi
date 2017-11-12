package com.github.noraui.data.console;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.github.noraui.data.console.OutputConsoleDataProvider;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.utils.Messages;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class OutputConsoleDataProviderUT {

    @Test
    public void testConstructorIsPublic() throws Exception {
        final Constructor<OutputConsoleDataProvider> constructor = OutputConsoleDataProvider.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
    }

    @Test
    public void testWriteXxxxxResult() throws TechnicalException {
        final OutputConsoleDataProvider outputConsoleDataProvider = new OutputConsoleDataProvider();
        outputConsoleDataProvider.prepare("hello");
        Assert.assertTrue(true);

        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        final TestAppender<ILoggingEvent> appender = new TestAppender<>();
        appender.start();
        logger.addAppender(appender);
        outputConsoleDataProvider.writeFailedResult(1, "UT Failed Message");
        List<ILoggingEvent> log = appender.getLog();
        Assert.assertEquals(Level.ERROR, log.get(0).getLevel());
        Assert.assertTrue(log.get(0).getMessage().toString().endsWith(String.format(Messages.getMessage("OUTPUT_CONSOLE_DATA_PROVIDER_FAILED_AT_LINE"), 1, "UT Failed Message")));

        outputConsoleDataProvider.writeSuccessResult(2);
        log = appender.getLog();
        Assert.assertEquals(Level.INFO, log.get(1).getLevel());
        Assert.assertTrue(log.get(1).getMessage().toString().endsWith(String.format(Messages.getMessage("OUTPUT_CONSOLE_DATA_PROVIDER_SUCCESS_AT_LINE"), 2)));

        outputConsoleDataProvider.writeWarningResult(3, "UT Warning Message");
        log = appender.getLog();
        Assert.assertEquals(Level.WARN, log.get(2).getLevel());
        Assert.assertTrue(log.get(2).getMessage().toString().endsWith(String.format(Messages.getMessage("OUTPUT_CONSOLE_DATA_PROVIDER_WARNING_AT_LINE"), 3, "UT Warning Message")));

        outputConsoleDataProvider.writeDataResult("title", 4, "UT title");
        log = appender.getLog();
        Assert.assertEquals(Level.INFO, log.get(3).getLevel());
        Assert.assertTrue(log.get(3).getMessage().toString().endsWith(String.format(Messages.getMessage("OUTPUT_CONSOLE_DATA_PROVIDER_RESULT_AT_LINE"), 4, "title", "UT title")));
    }
}

class TestAppender<E> extends AppenderBase<E> {

    private final List<E> log = new ArrayList<>();

    public List<E> getLog() {
        return new ArrayList<>(log);
    }

    @Override
    protected void append(E arg0) {
        log.add(arg0);
    }

}
