package com.github.noraui.exception;

import com.github.noraui.exception.Result.Warning;

public class WarningException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 6056507236915536242L;

    final transient Warning<?> warning;

    public WarningException(Warning<?> warning) {
        this.warning = warning;
    }

    public Warning<?> getWarning() {
        return warning;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return warning.message + " [" + warning.warning() + "]";
    }

}
