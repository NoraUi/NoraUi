package noraui.exception;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.junit.Assert;

import noraui.exception.Callbacks.Callback;
import noraui.utils.Context;
import noraui.utils.Messages;
import noraui.utils.Utilities;

public abstract class Result {
    public static final int CONTINUE_SCENARIO = 0;
    public static final int BREAK_SCENARIO = 1;

    protected String message;
    protected int wid;
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

    public static class Warning<O> extends Result {
        private final O object;
        private static final Logger logger = Logger.getLogger(Warning.class.getName());

        public Warning(O object, String message, boolean takeScreenshot, int wid) throws TechnicalException {
            this.object = object;
            try {
                Context.getDataOutputProvider().writeWarningResult(Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes().get(wid),
                        Messages.WARNING_MESSAGE_DEFAULT + message);
            } catch (TechnicalException e) {
                logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE + e.getMessage(), e);
            }
            if (!Context.scenarioHasWarning()) {
                Context.addWarning();
                Context.scenarioHasWarning(true);
            }
            if (takeScreenshot) {
                logger.debug("Current scenario is " + Context.getCurrentScenario());
                Utilities.takeScreenshot(Context.getCurrentScenario());
            }
            Context.getCurrentScenario().write(Messages.WARNING_MESSAGE_DEFAULT + message);
            logger.info(message + " [" + warning() + "]");
        }

        public O warning() {
            Optional<O> o = Optional.ofNullable(object);
            return o.isPresent() ? o.get() : null;
        }
    }

    public static class Failure<O> extends Result {
        private final O error;
        private static final Logger logger = Logger.getLogger(Failure.class.getName());

        public Failure(O error, String message, boolean takeScreenshot, int wid, Callback callback) throws FailureException {
            this.error = error;
            this.message = message;
            this.wid = wid;
            this.takeScreenshot = takeScreenshot;
            this.callback = callback;

            throw new FailureException(this);
        }

        public Failure(O error, String message, boolean takeScreenshot, Callback callback) throws FailureException {
            this.error = error;
            this.message = message;
            this.wid = 1;
            this.takeScreenshot = takeScreenshot;
            this.callback = callback;

            throw new FailureException(this);
        }

        public O failure() {
            Optional<O> o = Optional.ofNullable(error);
            return o.isPresent() ? o.get() : null;
        }

        public void fail() {
            for (int i = 1; i <= Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes().size(); i++) {
                Integer line = Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes().get(i - 1);
                try {
                    if (i < this.wid) {
                        Context.getDataOutputProvider().writeWarningResult(line, Messages.PARTIAL_SUCCESS_MESSAGE);
                    } else if (i == this.wid) {
                        Context.getDataOutputProvider().writeFailedResult(line, Messages.FAIL_MESSAGE_DEFAULT + this.message);
                    } else if (i > this.wid) {
                        Context.getDataOutputProvider().writeWarningResult(line, Messages.NOT_RUN_MESSAGE);
                    }
                } catch (TechnicalException e) {
                    logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE + e.getMessage(), e);
                }
            }
            Context.addFailure();
            if (Context.scenarioHasWarning()) {
                Context.setNbWarning(Context.getNbWarning() - 1);
                Context.scenarioHasWarning(false);
            }
            if (takeScreenshot) {
                logger.debug("Current scenario is " + Context.getCurrentScenario());
                Utilities.takeScreenshot(Context.getCurrentScenario());
            }
            if (callback != null) {
                callback.call();
            }
            if (Context.isStackTraceDisplayed()) {
                Assert.fail(Messages.FAIL_MESSAGE_DEFAULT + this.message + " [" + failure() + "]");
            } else {
                throw new AssertError(Messages.FAIL_MESSAGE_DEFAULT + this.message + " [" + failure() + "]");
            }
        }
    }
}