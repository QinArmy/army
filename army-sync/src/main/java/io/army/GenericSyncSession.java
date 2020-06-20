package io.army;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;

public interface GenericSyncSession extends GenericSession {

    SessionFactory sessionFactory();

    @Nullable
    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id);

    @Nullable
    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id, Visible visible);

    @Nullable
    <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList, List<Object> valueList);

    @Nullable
    <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible);

    @Nullable
    <T> T selectOne(Select select, Class<T> resultClass);

    @Nullable
    <T> T selectOne(Select select, Class<T> resultClass, Visible visible);

    <T> List<T> select(Select select, Class<T> resultClass);

    <T> List<T> select(Select select, Class<T> resultClass, Visible visible);

    /**
     * @param update will start singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    int update(Update update);

    int update(Update update, Visible visible);

    void updateOne(Update update);

    void updateOne(Update update, Visible visible);

    <T> List<T> returningUpdate(Update update, Class<T> resultClass);

    <T> List<T> returningUpdate(Update update, Class<T> resultClass, Visible visible);

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

    <T> List<T> returningInsert(Insert insert, Class<T> resultClass);

    <T> List<T> returningInsert(Insert insert, Class<T> resultClass, Visible visible);

    int delete(Delete delete);

    int delete(Delete delete, Visible visible);

    <T> List<T> returningDelete(Delete delete, Class<T> resultClass);

    <T> List<T> returningDelete(Delete delete, Class<T> resultClass, Visible visible);

    int[] batchDelete(Delete delete);

    int[] batchDelete(Delete delete, Visible visible);

    long largeDelete(Delete delete);

    long largeDelete(Delete delete, Visible visible);

    long[] batchLargeDelete(Delete delete);

    long[] batchLargeDelete(Delete delete, Visible visible);

}
