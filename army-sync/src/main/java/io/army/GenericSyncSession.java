package io.army;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;

/**
 * <p>
 * this interface have three direct sub interfaces:
 *     <ul>
 *         <li>{@link io.army.GenericSyncApiSession}</li>
 *         <li>{@code io.army.boot.RmSession}</li>
 *     </ul>
 * </p>
 *
 * @see GenericSyncApiSession
 */
public interface GenericSyncSession extends GenericSession {

    GenericSyncSessionFactory sessionFactory();

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

    int subQueryInsert(Insert insert);

    int subQueryInsert(Insert insert, Visible visible);

    long subQueryLargeInsert(Insert insert);

    long subQueryLargeInsert(Insert insert, Visible visible);

    /**
     * @param update will start singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    int update(Update update);

    int update(Update update, Visible visible);

    long largeUpdate(Update update);

    long largeUpdate(Update update, Visible visible);

    <R> List<R> returningUpdate(Update update, Class<R> resultClass);

    <R> List<R> returningUpdate(Update update, Class<R> resultClass, Visible visible);

    int delete(Delete delete);

    int delete(Delete delete, Visible visible);

    long largeDelete(Delete delete);

    long largeDelete(Delete delete, Visible visible);

    <R> List<R> returningDelete(Delete delete, Class<R> resultClass);

    <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible);

}
