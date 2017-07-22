package noraui.data.console;

import java.io.IOException;
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

public class OutputConsoleDataProviderUT {

    @Test
    public void testConstructorIsPublic() throws Exception {
        Constructor<OutputConsoleDataProvider> constructor = OutputConsoleDataProvider.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
    }

    @Test
    public void testWriteXxxxxResult() throws TechnicalException, IOException {
        OutputConsoleDataProvider outputConsoleDataProvider = new OutputConsoleDataProvider();
        outputConsoleDataProvider.prepare("hello");
        Assert.assertTrue(true);

        final TestAppender appender = new TestAppender();
        final Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
        outputConsoleDataProvider.writeFailedResult(1, "UT Failed Message");
        List<LoggingEvent> log = appender.getLog();
        Assert.assertEquals(log.get(0).getLevel(), Level.ERROR);
        Assert.assertTrue(log.get(0).getMessage().toString().endsWith("----- FAILED at line 1 >  UT Failed Message -----"));

        outputConsoleDataProvider.writeSuccessResult(2);
        log = appender.getLog();
        Assert.assertEquals(log.get(1).getLevel(), Level.INFO);
        Assert.assertTrue(log.get(1).getMessage().toString().endsWith("----- SUCCESS at line 2 -----"));

        outputConsoleDataProvider.writeWarningResult(3, "UT Warning Message");
        log = appender.getLog();
        Assert.assertEquals(log.get(2).getLevel(), Level.WARN);
        Assert.assertTrue(log.get(2).getMessage().toString().endsWith("----- WARNING at line 3 > UT Warning Message -----"));

        outputConsoleDataProvider.writeDataResult("title", 4, "UT title");
        log = appender.getLog();
        Assert.assertEquals(log.get(3).getLevel(), Level.INFO);
        Assert.assertTrue(log.get(3).getMessage().toString().endsWith("----- DATA RESULT at line 4 > [title] = UT title -----"));
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
