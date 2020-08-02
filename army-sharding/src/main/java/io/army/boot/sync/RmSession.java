package io.army.boot.sync;

import io.army.sync.GenericRmSession;
import io.army.sync.Session;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.XATransaction;

/**
 * {@code Rm}  representing Resource Manager.
 *
 * @see Session
 */
interface RmSession extends GenericRmSession {

    @Override
    RmSessionFactory sessionFactory();

    XATransaction sessionTransaction() throws NoSessionTransactionException;

}
