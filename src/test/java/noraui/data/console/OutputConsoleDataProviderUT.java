package noraui.data.console;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;

import noraui.exception.TechnicalException;
import noraui.utils.Messages;

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

        final TestAppender appender = new TestAppender();
        final Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
        outputConsoleDataProvider.writeFailedResult(1, "UT Failed Message");
        List<LoggingEvent> log = appender.getLog();
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

class TestAppender extends AppenderSkeleton {
    private final List<LoggingEvent> log = new ArrayList<>();

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    protected void append(final LoggingEvent loggingEvent) {
        log.add(loggingEvent);
    }

    @Override
    public void close() {
    }

    public List<LoggingEvent> getLog() {
        return new ArrayList<>(log);
    }
}
