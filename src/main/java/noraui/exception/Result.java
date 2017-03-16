package noraui.exception;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.junit.Assert;

import noraui.exception.Callbacks.Callback;
import noraui.utils.Context;
import noraui.utils.Utilities;

public abstract class Result {
    public static final int CONTINUE_SCENARIO = 0;
    public static final int BREAK_SCENARIO = 1;

    protected String message;
    protected boolean takeScreenshot;
    protected Callback callback;

    private Result() {
    }

    public static class Success<O> extends Result {
        private final O object;
        private static final Logger logger = Logger.getLogger(Success.class.getName());

        public Success(O object, String message) throws TechnicalException {
            this.object = object;
            this.message = message;
            for (Integer i : Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes()) {
                Context.getDataOutputProvider().writeSuccessResult(i);
            }
            logger.info(message + " [" + success() + "]");
        }

        public O success() {
            Optional<O> o = Optional.ofNullable(object);
            return o.isPresent() ? o.get() : null;
        }
    }

    public static class Failure<O> extends Result {
        private final O error;
        private static final Logger logger = Logger.getLogger(Failure.class.getName());

        public Failure(O error, String message, boolean takeScreenshot, Callback callback) throws FailureException {
            this.error = error;
            this.message = message;
            this.takeScreenshot = takeScreenshot;
            this.callback = callback;

            throw new FailureException(this);
        }

        public O failure() {
            Optional<O> o = Optional.ofNullable(error);
            return o.isPresent() ? o.get() : null;
        }

        public void fail() {
            for (Integer line : Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes()) {
                try {
                    Context.getDataOutputProvider().writeFailedResult(line, message);
                } catch (TechnicalException e) {
                    logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE + e.getMessage(), e);
                }
            }
            Context.addFailure();
            if (takeScreenshot) {
                logger.debug("Current scenario is " + Context.getCurrentScenario());
                Utilities.takeScreenshot(Context.getCurrentScenario());
            }
            if (callback != null) {
                callback.call();
            }
            if (Context.isStackTraceDisplayed()) {
                Assert.fail(message + " [" + failure() + "]");
            } else {
                throw new AssertError(message + " [" + failure() + "]");
            }
        }
    }
}