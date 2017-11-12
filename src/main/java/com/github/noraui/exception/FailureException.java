package com.github.noraui.exception;

import com.github.noraui.exception.Result.Failure;

public class FailureException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 6056507236915536242L;

    final transient Failure<?> failure;

    public FailureException(Failure<?> failure) {
        this.failure = failure;
    }

    public Failure<?> getFailure() {
        return failure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return failure.message + " [" + failure.failure() + "]";
    }

}
