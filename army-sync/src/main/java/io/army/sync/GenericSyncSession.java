package io.army.sync;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.session.GenericSession;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * This interface encapsulate synchronous api than can access database.
 * </p>
 *
 * <p>
 * This interface is base interface of below interface:
 *     <ul>
 *         <li>{@link Session}</li>
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
    <R extends IDomain, F> R getByUnique(TableMeta<R> tableMeta, UniqueFieldMeta<R, F> fieldMeta, F fieldValue);

    @Nullable
    <R extends IDomain, F> R getByUnique(TableMeta<R> tableMeta, UniqueFieldMeta<R, F> fieldMeta, F fieldValue, Visible visible);

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

    Map<String, Object> selectOneAsMap(Select select);

    Map<String, Object> selectOneAsMap(Select select, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> select(Select select, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> select(Select select, Class<R> resultClass, Visible visible);

    List<Map<String, Object>> selectAsMap(Select select);

    List<Map<String, Object>> selectAsMap(Select select, Visible visible);

    void valueInsert(Insert insert);

    void valueInsert(Insert insert, Visible visible);


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
