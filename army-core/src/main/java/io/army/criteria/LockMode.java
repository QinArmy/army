package io.army.criteria;

/**
 * created  on 2018/10/21.
 */
public enum LockMode {

    /**
     * Synonymous with <code>OPTIMISTIC</code>.
     * <code>OPTIMISTIC</code> is to be preferred for new
     * applications.
     */
    READ,

    /**
     * Synonymous with <code>OPTIMISTIC_FORCE_INCREMENT</code>.
     * <code>OPTIMISTIC_FORCE_IMCREMENT</code> is to be preferred for new
     * applications.
     */
    WRITE,

    /**
     * Optimistic lock.
     *
     * @since Java Persistence 2.0
     */
    OPTIMISTIC,

    /**
     * Optimistic lock, with version singleUpdate.
     *
     * @since Java Persistence 2.0
     */
    OPTIMISTIC_FORCE_INCREMENT,

    /**
     * Pessimistic read lock.
     *
     * @since Java Persistence 2.0
     */
    PESSIMISTIC_READ,

    /**
     * Pessimistic write lock.
     *
     * @since Java Persistence 2.0
     */
    PESSIMISTIC_WRITE,

    /**
     * Pessimistic write lock, with version singleUpdate.
     *
     * @since Java Persistence 2.0
     */
    PESSIMISTIC_FORCE_INCREMENT,

    /**
     * No lock.
     *
     * @since Java Persistence 2.0
     */
    NONE

}
