package io.army.sync;

import io.army.SessionException;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.session.GenericSession;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>
 * This interface encapsulate synchronous api than can access database.
 * </p>
 *
 * <p>
 * This interface is base interface of below interface:
 *     <ul>
 *         <li>{@link Session}</li>
 *         <li>{@link CurrentSession}</li>
 *     </ul>
 * </p>
 */
public interface SyncSession extends GenericSession {

    @Override
    SessionFactory sessionFactory();

    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R extends IDomain> R get(TableMeta<R> tableMeta, Object id);

    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R extends IDomain> R get(TableMeta<R> table, Object id, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R extends IDomain> R getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value);

    @Nullable
    <R extends IDomain> R getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value, Visible visible);

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

    Map<String, Object> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor);

    Map<String, Object> selectOneAsMap(Select select, Visible visible);

    Map<String, Object> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> select(Select select, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> select(Select select, Class<R> resultClass, Supplier<List<R>> listConstructor);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> select(Select select, Class<R> resultClass, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> select(Select select, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible);


    List<Map<String, Object>> selectAsMap(Select select);

    List<Map<String, Object>> selectAsMap(Select select, Visible visible);


    List<Map<String, Object>> selectAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor);

    List<Map<String, Object>> selectAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor, Visible visible);

    long insert(Insert insert);

    long insert(Insert insert, Visible visible);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Supplier<List<R>> listConstructor);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible);

    long update(Update update);

    long update(Update update, Visible visible);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningUpdate(Update update, Class<R> resultClass);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningUpdate(Update update, Class<R> resultClass, Supplier<List<R>> listConstructor);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningUpdate(Update update, Class<R> resultClass, Visible visible);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningUpdate(Update update, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible);

    long delete(Delete delete);

    long delete(Delete delete, Visible visible);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningDelete(Delete delete, Class<R> resultClass);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Supplier<List<R>> listConstructor);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible);

    /**
     * @param <R> representing returning result Java Type.
     */
    <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible);


    List<Long> batchUpdate(Update update);

    /**
     * @return a unmodifiable list
     */
    List<Long> batchUpdate(Update update, Visible visible);

    /**
     * @return a unmodifiable list
     */
    List<Long> batchDelete(Delete delete);

    /**
     * @return a unmodifiable list
     */
    List<Long> batchDelete(Delete delete, Visible visible);

    void flush() throws SessionException;

}
