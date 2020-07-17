package io.army.boot;

import io.army.GenericSyncSession;
import io.army.Session;
import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.tx.GenericSyncTransaction;
import io.army.tx.NoSessionTransactionException;

import java.io.Flushable;
import java.util.List;
import java.util.Set;

/**
 * {@code Rm}  representing Resource Manager.
 *
 * @see Session
 */
interface RmSession extends GenericSyncSession, AutoCloseable, Flushable {

    GenericSyncTransaction sessionTransaction() throws NoSessionTransactionException;

    int update(Update update, @Nullable Set<Integer> domainIndexSet, Visible visible);

    <R> List<R> returningUpdate(Update update, Class<R> resultClass, @Nullable Set<Integer> domainIndexSet
            , Visible visible);

    int[] batchUpdate(Update update, @Nullable Set<Integer> domainIndexSet, Visible visible);

    long largeUpdate(Update update, @Nullable Set<Integer> domainIndexSet, Visible visible);

    long[] batchLargeUpdate(Update update, @Nullable Set<Integer> domainIndexSet, Visible visible);

    void valueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet, Visible visible);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass, @Nullable Set<Integer> domainIndexSet
            , Visible visible);

    int delete(Delete delete, @Nullable Set<Integer> domainIndexSet, Visible visible);

    <R> List<R> returningDelete(Delete delete, Class<R> resultClass, @Nullable Set<Integer> domainIndexSet
            , Visible visible);

    int[] batchDelete(Delete delete, @Nullable Set<Integer> domainIndexSet, Visible visible);

    long largeDelete(Delete delete, @Nullable Set<Integer> domainIndexSet, Visible visible);

    long[] batchLargeDelete(Delete delete, @Nullable Set<Integer> domainIndexSet, Visible visible);

}
