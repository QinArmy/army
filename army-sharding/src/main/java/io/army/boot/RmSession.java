package io.army.boot;

import io.army.GenericSyncSession;
import io.army.Session;
import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.XATransaction;

import java.io.Flushable;
import java.util.Map;
import java.util.Set;

/**
 * {@code Rm}  representing Resource Manager.
 *
 * @see Session
 */
interface RmSession extends GenericSyncSession, AutoCloseable, Flushable {

    @Override
    RmSessionFactory sessionFactory();

    XATransaction sessionTransaction() throws NoSessionTransactionException;

    /**
     * @return a unmodifiable map
     */
    <V extends Number> Map<Integer, V> batchUpdate(Update update, @Nullable Set<Integer> domainIndexSet
            , Class<V> valueType, Visible visible);

    void valueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet, Visible visible);

    /**
     * @return a unmodifiable map
     */
    <V extends Number> Map<Integer, V> batchDelete(Delete delete, @Nullable Set<Integer> domainIndexSet
            , Class<V> valueType, Visible visible);


}
