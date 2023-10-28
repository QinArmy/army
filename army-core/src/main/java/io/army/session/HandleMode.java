package io.army.session;

/**
 * <p>
 * the handle mode to exists transaction when invoke {@code  Session.startTransaction(TransactionOption, HandleMode)}.
 * </p>
 *
 * @since 1.0
 */
public enum HandleMode {

    ERROR_IF_EXISTS,
    ROLLBACK_IF_EXISTS,
    COMMIT_IF_EXISTS
}
