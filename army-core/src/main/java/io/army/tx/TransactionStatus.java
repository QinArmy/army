package io.army.tx;

/**
 * Enum representing the status in which Transaction is running.
 *
 * @see GenericTransaction#status()
 * @since 1.0
 */
public enum TransactionStatus {

    /**
     * The transaction has not yet been started.
     */
    NOT_ACTIVE,
    /**
     * The transaction has been started, but not yet completed.
     */
    ACTIVE,
    /**
     * The transaction has been completed successfully.
     */
    COMMITTED,
    /**
     * The transaction has been rolled back.
     */
    ROLLED_BACK,
    /**
     * The transaction has been marked for rollback only.
     */
    MARKED_ROLLBACK,
    /**
     * The transaction attempted to commit, but failed.
     */
    FAILED_COMMIT,
    /**
     * The transaction attempted to rollback, but failed.
     */
    FAILED_ROLLBACK,
    /**
     * Status code indicating a transaction that has begun the second
     * phase of the two-phase commit protocol, but not yet completed
     * this phase.
     */
    COMMITTING,
    /**
     * Status code indicating a transaction that is in the process of
     * rolling back.
     */
    ROLLING_BACK;
}
