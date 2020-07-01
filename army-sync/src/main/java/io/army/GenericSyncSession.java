package io.army;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.tx.GenericSyncTransaction;
import io.army.tx.NoSessionTransactionException;

import java.io.Flushable;
import java.sql.Connection;
import java.util.List;

public interface GenericSyncSession extends GenericSession, AutoCloseable, Flushable {

    GenericSyncSessionFactory sessionFactory();

    GenericSyncTransaction sessionTransaction() throws NoSessionTransactionException;

    @Nullable
    <R extends IDomain> R get(TableMeta<R> tableMeta, Object id);

    @Nullable
    <R extends IDomain> R get(TableMeta<R> tableMeta, Object id, Visible visible);

    @Nullable
    <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList, List<Object> valueList);

    @Nullable
    <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible);

    @Nullable
    <R> R selectOne(Select select, Class<R> resultClass);

    @Nullable
    <R> R selectOne(Select select, Class<R> resultClass, Visible visible);

    <R> List<R> select(Select select, Class<R> resultClass);

    <R> List<R> select(Select select, Class<R> resultClass, Visible visible);

    /**
     * @param update will start singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    int update(Update update);

    int update(Update update, Visible visible);

    void updateOne(Update update);

    void updateOne(Update update, Visible visible);

    <R> List<R> returningUpdate(Update update, Class<R> resultClass);

    <R> List<R> returningUpdate(Update update, Class<R> resultClass, Visible visible);

    int[] batchUpdate(Update update);

    int[] batchUpdate(Update update, Visible visible);

    long largeUpdate(Update update);

    long largeUpdate(Update update, Visible visible);

    long[] batchLargeUpdate(Update update);

    long[] batchLargeUpdate(Update update, Visible visible);

    void insert(Insert insert);

    void insert(Insert insert, Visible visible);

    int subQueryInsert(Insert insert);

    int subQueryInsert(Insert insert, Visible visible);

    long subQueryLargeInsert(Insert insert);

    long subQueryLargeInsert(Insert insert, Visible visible);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible);

    int delete(Delete delete);

    int delete(Delete delete, Visible visible);

    <R> List<R> returningDelete(Delete delete, Class<R> resultClass);

    <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible);

    int[] batchDelete(Delete delete);

    int[] batchDelete(Delete delete, Visible visible);

    long largeDelete(Delete delete);

    long largeDelete(Delete delete, Visible visible);

    long[] batchLargeDelete(Delete delete);

    long[] batchLargeDelete(Delete delete, Visible visible);


    /**
     * <o>
     * <li>invoke {@link io.army.context.spi.CurrentSessionContext#removeCurrentSession(Session)},if need</li>
     * <li>invoke {@link Connection#close()}</li>
     * </o>
     *
     * @throws SessionException close session occur error.
     */
    @Override
    void close() throws SessionException;


    @Override
    void flush() throws SessionException;

}
