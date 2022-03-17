package io.army.session;

import io.army.SessionException;

/**
 * Throw when statement executor don't correctly execute {@link io.army.criteria.Statement}.
 */
public class ExecutorExecutionException extends SessionException {

    public ExecutorExecutionException(String message) {
        super(message);
    }

    public ExecutorExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
