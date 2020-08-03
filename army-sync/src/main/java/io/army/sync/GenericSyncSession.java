package io.army.sync;

import io.army.GenericSession;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;

/**
 * <p>
 * This interface encapsulate synchronous api than can access database.
 * </p>
 *
 * <p>
 * This interface is base interface of below interface:
 *     <ul>
 *         <li>{@link io.army.sync.Session}</li>
 *         <li>{@code io.army.boot.sync.RmSession}</li>
 *         <li>{@code io.army.sync.TmSession}</li>
 *     </ul>
 * </p>
 */
public interface GenericSyncSession extends GenericSession {

    GenericSyncSessionFactory sessionFactory();

    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R extends IDomain> R get(TableMeta<R> tableMeta, Object id);

    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R extends IDomain> R get(TableMeta<R> tableMeta, Object id, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList, List<Object> valueList);

    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R> R selectOne(Select select, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R> R selectOne(Select select, Class<R> resultClass, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> select(Select select, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> select(Select select, Class<R> resultClass, Visible visible);

    int subQueryInsert(Insert insert);

    int subQueryInsert(Insert insert, Visible visible);

    long subQueryLargeInsert(Insert insert);

    long largeSubQueryInsert(Insert insert, Visible visible);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible);

    /**
     * @param update will start singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    int update(Update update);

    int update(Update update, Visible visible);

    long largeUpdate(Update update);

    long largeUpdate(Update update, Visible visible);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningUpdate(Update update, Class<R> resultClass);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningUpdate(Update update, Class<R> resultClass, Visible visible);

    int delete(Delete delete);

    int delete(Delete delete, Visible visible);

    long largeDelete(Delete delete);

    long largeDelete(Delete delete, Visible visible);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningDelete(Delete delete, Class<R> resultClass);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible);

}
