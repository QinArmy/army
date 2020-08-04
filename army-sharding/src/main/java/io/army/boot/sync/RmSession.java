package io.army.boot.sync;

import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.sync.GenericSyncRmSession;
import io.army.sync.Session;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.XaTransactionOption;

import java.util.Set;

/**
 * {@code Rm}  representing Resource Manager.
 *
 * @see Session
 */
interface RmSession extends GenericSyncRmSession {

    void valueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet, Visible visible);

    @Override
    RmSessionFactory sessionFactory();

    /**
     * xa transaction start when {@link RmSession} initialize.
     *
     * @return a started {@link XATransaction}
     * @see RmSessionFactory#build(XaTransactionOption)
     */
    @Override
    XATransaction sessionTransaction() throws NoSessionTransactionException;

}
