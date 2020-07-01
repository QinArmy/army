package io.army.tx;

public enum XATransactionStatus {

    /**
     * The transaction has not yet been started.
     */
    NOT_ACTIVE,

    /**
     * The transaction has been started, but not yet completed.
     */
    ACTIVE,

    /**
     * The transaction has been idle, but not yet prepare.
     */
    IDLE,

    /**
     * The transaction has been prepare, but not yet commit or rollback.
     */
    PREPARED,

    /**
     * The transaction has been completed successfully.
     */
    COMMITTED,
    /**
     * The transaction has been rolled back.
     */
    ROLLED_BACK,

    FAILED_IDLE,

    FAILED_PREPARE,

    FAILED_COMMIT,
    /**
     * The transaction attempted to rollback, but failed.
     */
    FAILED_ROLLBACK,

    MARKED_ROLLBACK,

    IDLING,

    PREPARING,

    FORGOT,

    FORGETTING,
    FAILED_FORGET,

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
