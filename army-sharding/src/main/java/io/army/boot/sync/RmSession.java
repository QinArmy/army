package io.army.boot.sync;

import io.army.sync.GenericRmSession;
import io.army.sync.Session;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.XaTransactionOption;

/**
 * {@code Rm}  representing Resource Manager.
 *
 * @see Session
 */
interface RmSession extends GenericRmSession {

    @Override
    RmSessionFactory sessionFactory();

    /**
     * xa transaction start when {@link RmSession} initialize.
     *
     * @return a started {@link XATransaction}
     * @see RmSessionFactory#build(XaTransactionOption)
     */
    XATransaction startedTransaction() throws NoSessionTransactionException;

}
