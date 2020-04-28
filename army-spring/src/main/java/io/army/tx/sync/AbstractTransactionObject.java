package io.army.tx.sync;


import io.army.ArmyAccessException;
import io.army.Session;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.SmartTransactionObject;

import java.sql.Savepoint;

abstract class AbstractTransactionObject implements SavepointManager, SmartTransactionObject {

    Session session;

    AbstractTransactionObject() {
    }

    /*################################## blow SavepointManager method ##################################*/

    @Override
    public final Object createSavepoint() throws TransactionException {
        try {
            return session.sessionTransaction().createSavepoint();
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }

    @Override
    public final void rollbackToSavepoint(Object savepoint) throws TransactionException {
        try {
            session.sessionTransaction().rollbackToSavepoint((Savepoint) savepoint);
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }

    @Override
    public final void releaseSavepoint(Object savepoint) throws TransactionException {
        try {
            session.sessionTransaction().releaseSavepoint((Savepoint) savepoint);
        } catch (io.army.tx.TransactionException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }

    /*################################## blow SmartTransactionObject method ##################################*/

    @Override
    public final boolean isRollbackOnly() {
        try {
            return session.sessionTransaction().rollbackOnly();
        } catch (ArmyAccessException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }

    @Override
    public final void flush() {
        try {
            session.flush();
        } catch (ArmyAccessException e) {
            throw SpringTxUtils.convertArmyAccessException(e);
        }
    }



    /*################################## blow custom method ##################################*/
}
