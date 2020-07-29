package io.army;

public final class DenyBatchInsertException extends SessionUsageException {

    public DenyBatchInsertException(String format, Object... args) {
        super(ErrorCode.DENY_BATCH_INSERT, format, args);
    }

}
