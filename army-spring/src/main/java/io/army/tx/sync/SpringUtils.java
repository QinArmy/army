package io.army.tx.sync;

import io.army.session.Isolation;
import io.army.session.SessionException;
import io.army.session.TransactionTimeOutException;
import org.springframework.transaction.*;

import javax.annotation.Nullable;

public abstract class SpringUtils {

    private SpringUtils() {
        throw new UnsupportedOperationException();
    }


    public static org.springframework.dao.DataAccessException convertSessionException(SessionException ex) {
        return new org.springframework.dao.DataAccessException(ex.getMessage(), ex) {

        };
    }


    public static org.springframework.transaction.TransactionException wrapTransactionError(
            final io.army.session.TransactionException ex) {

        final org.springframework.transaction.TransactionException e;
        if (ex instanceof io.army.tx.CannotCreateTransactionException) {
            e = new CannotCreateTransactionException(ex.getMessage(), ex);
        } else if (ex instanceof io.army.session.TransactionSystemException) {
            e = new TransactionSystemException(ex.getMessage(), ex);
        } else if (ex instanceof io.army.tx.IllegalTransactionStateException) {
            e = new IllegalTransactionStateException(ex.getMessage(), ex);
        } else if (ex instanceof TransactionTimeOutException) {
            e = new TransactionTimedOutException(ex.getMessage(), ex);
        } else {
            e = new TransactionException(ex.getMessage(), ex) {

            };
        }
        return e;
    }


    @Nullable
    public static Isolation toArmyIsolation(final int springIsolation) {
        final Isolation isolation;
        switch (springIsolation) {
            case TransactionDefinition.ISOLATION_DEFAULT:
                isolation = null;
                break;
            case TransactionDefinition.ISOLATION_READ_COMMITTED:
                isolation = Isolation.READ_COMMITTED;
                break;
            case TransactionDefinition.ISOLATION_REPEATABLE_READ:
                isolation = Isolation.REPEATABLE_READ;
                break;
            case TransactionDefinition.ISOLATION_SERIALIZABLE:
                isolation = Isolation.SERIALIZABLE;
                break;
            case TransactionDefinition.ISOLATION_READ_UNCOMMITTED:
                isolation = Isolation.READ_UNCOMMITTED;
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown springIsolation[%s]", springIsolation));
        }
        return isolation;
    }

}
