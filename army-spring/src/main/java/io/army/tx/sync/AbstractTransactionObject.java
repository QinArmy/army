package io.army.tx.sync;


import io.army.DataAccessException;
import io.army.boot.sync.GenericSyncApiSession;
import io.army.sync.Session;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.transaction.support.SmartTransactionObject;

import java.sql.Savepoint;

abstract class AbstractTransactionObject<S extends GenericSyncApiSession> implements SavepointManager, SmartTransactionObject {

    S session;

    AbstractTransactionObject() {
    }

    /*################################## blow SavepointManager method ##################################*/

    @Override
    public final Object createSavepoint() throws TransactionException {
        try {
            if (this.session instanceof Session) {
                return ((Session) this.session).sessionTransaction().createSavepoint();
            } else {
                throw new TransactionUsageException(String.format("%s not support save point", this.session));
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }

    @Override
    public final void rollbackToSavepoint(Object savepoint) throws TransactionException {
        try {
            if (this.session instanceof Session) {
                ((Session) this.session).sessionTransaction().rollbackToSavepoint((Savepoint) savepoint);
            } else {
                throw new TransactionUsageException(String.format("%s not support save point", this.session));
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }

    @Override
    public final void releaseSavepoint(Object savepoint) throws TransactionException {
        try {
            if (this.session instanceof Session) {
                ((Session) this.session).sessionTransaction().releaseSavepoint((Savepoint) savepoint);
            } else {
                throw new TransactionUsageException(String.format("%s not support save point", this.session));
            }
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }

    /*################################## blow SmartTransactionObject method ##################################*/


    @Override
    public final void flush() {
        try {
            session.flush();
        } catch (DataAccessException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }



    /*################################## blow custom method ##################################*/
}
