package io.army.tx;

import io.army.ErrorCode;

/**
 * Exception thrown when save point not exists.
 * like on rollbackToSavepoint or releaseSavepoint
 */
public class UnKnownSavepointException extends TransactionUsageException {

    public UnKnownSavepointException(String format, Object... args) {
        super(ErrorCode.UNKNOWN_SAVE_POINT, format, args);
    }

}
