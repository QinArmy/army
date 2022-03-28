package io.army.tx.sync;

import io.army.DataAccessException_0;
import io.army.session.SessionException;
import io.army.tx.Isolation;
import io.army.tx.TransactionException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.UnexpectedRollbackException;

public abstract class SpringUtils {

    private SpringUtils() {
        throw new UnsupportedOperationException();
    }


    public static org.springframework.dao.DataAccessException convertSessionException(SessionException ex) {
        return new org.springframework.dao.DataAccessException(ex.getMessage(), ex) {

        };
    }

    public static org.springframework.dao.DataAccessException convertArmyAccessException(DataAccessException_0 ex) {
        return new org.springframework.dao.DataAccessException(ex.getMessage(), ex) {

        };
    }

    public static NestedRuntimeException convertToSpringException(RuntimeException ex) {
        return new org.springframework.dao.DataAccessException(ex.getMessage(), ex) {

        };
    }

    public static org.springframework.transaction.TransactionException convertTransactionException(
            TransactionException ex) {
        return new UnexpectedRollbackException(ex.getMessage(), ex);
    }


    public static Isolation toArmyIsolation(final int springIsolation) {
        Isolation isolation;
        switch (springIsolation) {
            case TransactionDefinition.ISOLATION_DEFAULT:
                isolation = Isolation.DEFAULT;
                break;
            case TransactionDefinition.ISOLATION_READ_UNCOMMITTED:
                isolation = Isolation.READ_UNCOMMITTED;
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
            default:
                throw new IllegalArgumentException(String.format("unknown springIsolation[%s]", springIsolation));
        }
        return isolation;
    }

}
