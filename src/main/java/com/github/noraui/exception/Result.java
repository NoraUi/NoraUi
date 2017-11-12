package com.github.noraui.exception;

import java.util.Optional;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.exception.Callbacks.Callback;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;

public abstract class Result {

    public static final int CONTINUE_SCENARIO = 0;
    public static final int BREAK_SCENARIO = 1;

    protected String message;
    protected int nid;
    protected boolean takeScreenshot;
    protected Callback callback;

    private static final String PARTIAL_SUCCESS_MESSAGE = "PARTIAL_SUCCESS_MESSAGE";

    private Result() {
    }

    public static class Success<O> extends Result {

        /**
         * Specific logger
         */
        private static final Logger logger = LoggerFactory.getLogger(Success.class);

        private final O object;

        /**
         * @param object
         *            bonus information.
         * @param message
         *            success message.
         * @throws TechnicalException
         *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
         */
        public Success(O object, String message) throws TechnicalException {
            this.object = object;
            this.message = message;
            for (final Integer i : Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes()) {
                Context.getDataOutputProvider().writeSuccessResult(i);
            }
            logger.info("{} [{}]", message, success());
        }

        public O success() {
            final Optional<O> o = Optional.ofNullable(object);
            return o.isPresent() ? o.get() : null;
        }
    }

    public static class Warning<O> extends Result {

        /**
         * Specific logger
         */
        private static final Logger logger = LoggerFactory.getLogger(Warning.class);

        private final O object;

        /**
         * @param object
         *            bonus information.
         * @param message
         *            warning message.
         * @param takeScreenshot
         *            (true or false).
         * @param nid
         *            nora-ui technical id (0 or more).
         * @throws TechnicalException
         *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
         */
        public Warning(O object, String message, boolean takeScreenshot, int nid) throws TechnicalException {
            this.object = object;
            try {
                Context.getDataOutputProvider().writeWarningResult(Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes().get(nid),
                        Messages.getMessage(Messages.WARNING_MESSAGE_DEFAULT) + message);
            } catch (final TechnicalException e) {
                logger.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE), e);
            }
            if (!Context.scenarioHasWarning()) {
                Context.addWarning();
                Context.scenarioHasWarning(true);
            }
            if (takeScreenshot) {
                logger.debug("Current scenario is {}", Context.getCurrentScenario());
                Utilities.takeScreenshot(Context.getCurrentScenario());
            }
            Context.getCurrentScenario().write(Messages.getMessage(Messages.WARNING_MESSAGE_DEFAULT) + message);
            logger.info("{} [{}]", message, warning());
        }

        public O warning() {
            final Optional<O> o = Optional.ofNullable(object);
            return o.isPresent() ? o.get() : null;
        }
    }

    public static class Failure<O> extends Result {

        /**
         * Specific logger
         */
        private static final Logger logger = LoggerFactory.getLogger(Failure.class);

        private final O error;

        /**
         * @param error
         *            bonus information.
         * @param message
         *            failure message.
         * @param takeScreenshot
         *            (true or false).
         * @param nid
         *            nora-ui technical id (1 or more).
         * @param callback
         *            is noraui.exception.Callbacks.Callback of page.
         * @throws FailureException
         *             if the scenario encounters a functional error.
         */
        public Failure(O error, String message, boolean takeScreenshot, int nid, Callback callback) throws FailureException {
            this.error = error;
            this.message = message;
            this.nid = nid;
            this.takeScreenshot = takeScreenshot;
            this.callback = callback;

            throw new FailureException(this);
        }

        /**
         * @param error
         *            bonus information.
         * @param message
         *            failure message.
         * @param takeScreenshot
         *            (true or false).
         * @param callback
         *            is noraui.exception.Callbacks.Callback of page.
         * @throws FailureException
         *             if the scenario encounters a functional error.
         */
        public Failure(O error, String message, boolean takeScreenshot, Callback callback) throws FailureException {
            this.error = error;
            this.message = message;
            this.nid = 1;
            this.takeScreenshot = takeScreenshot;
            this.callback = callback;

            throw new FailureException(this);
        }

        public O failure() {
            final Optional<O> o = Optional.ofNullable(error);
            return o.isPresent() ? o.get() : null;
        }

        public void fail() {
            for (int i = 1; i <= Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes().size(); i++) {
                final Integer line = Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes().get(i - 1);
                try {
                    if (i < this.nid) {
                        Context.getDataOutputProvider().writeWarningResult(line, Messages.getMessage(PARTIAL_SUCCESS_MESSAGE));
                    } else if (i == this.nid) {
                        Context.getDataOutputProvider().writeFailedResult(line, Messages.getMessage(Messages.FAIL_MESSAGE_DEFAULT) + this.message);
                    } else if (i > this.nid) {
                        Context.getDataOutputProvider().writeWarningResult(line, Messages.getMessage(Messages.NOT_RUN_MESSAGE));
                    }
                } catch (final TechnicalException e) {
                    logger.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE), e);
                }
            }
            Context.addFailure();
            if (Context.scenarioHasWarning()) {
                Context.setNbWarning(Context.getNbWarning() - 1);
                Context.scenarioHasWarning(false);
            }
            if (takeScreenshot) {
                logger.debug("Current scenario is {}", Context.getCurrentScenario());
                Utilities.takeScreenshot(Context.getCurrentScenario());
            }
            if (callback != null) {
                callback.call();
            }
            if (Context.isStackTraceDisplayed()) {
                Assert.fail(Messages.getMessage(Messages.FAIL_MESSAGE_DEFAULT) + this.message + " [" + failure() + "]");
            } else {
                throw new AssertError(Messages.getMessage(Messages.FAIL_MESSAGE_DEFAULT) + this.message + " [" + failure() + "]");
            }
        }
    }
}
