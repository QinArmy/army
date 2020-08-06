package io.army.tx.sync;

import io.army.DataAccessException;
import io.army.SessionException;
import io.army.tx.Isolation;
import io.army.tx.TransactionException;
import org.springframework.transaction.TransactionDefinition;

public abstract class SpringTxUtils {

    protected SpringTxUtils() {
        throw new UnsupportedOperationException();
    }


    public static org.springframework.dao.DataAccessException convertSessionException(SessionException ex) {
        return new org.springframework.dao.DataAccessException(ex.getMessage(), ex) {

        };
    }

    public static org.springframework.dao.DataAccessException convertArmyAccessException(DataAccessException ex) {
        return new org.springframework.dao.DataAccessException(ex.getMessage(), ex) {

        };
    }

    public static org.springframework.transaction.TransactionException convertTransactionException(
            TransactionException ex) {
        throw new UnsupportedOperationException();
    }


    public static Isolation convertTotArmyIsolation(int springIsolation) {
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
