package io.army.sync;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.session.GenericSession;
import io.army.session.SessionException;

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
    <R extends IDomain> R get(TableMeta<R> table, Object id);

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
    <R> R queryOne(DqlStatement statement, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R> R queryOne(DqlStatement statement, Class<R> resultClass, Visible visible);

    @Nullable
    Map<String, Object> queryOneAsMap(DqlStatement statement);

    @Nullable
    Map<String, Object> queryOneAsMap(DqlStatement statement, Visible visible);

    @Nullable
    Map<String, Object> queryOneAsMap(DqlStatement statement, Supplier<Map<String, Object>> mapConstructor);

    @Nullable
    Map<String, Object> queryOneAsMap(DqlStatement statement, Supplier<Map<String, Object>> mapConstructor, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> query(DqlStatement statement, Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> query(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> query(DqlStatement statement, Class<R> resultClass, Visible visible);

    /**
     * @param resultClass probably below type.
     *                    <ul>
     *                         <li>simple java type,eg: {@code java.lang.String} , {@code java.lange.Long}</li>
     *                         <li>java bean</li>
     *                         <li>{@link io.army.bean.FieldAccessBean}</li>
     *                         <li>{@link io.army.bean.PairBean}</li>
     *                    </ul>
     * @param <R>         representing select result Java Type.
     */
    <R> List<R> query(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible);


    List<Map<String, Object>> queryAsMap(DqlStatement statement);

    List<Map<String, Object>> queryAsMap(DqlStatement statement, Visible visible);


    List<Map<String, Object>> queryAsMap(DqlStatement statement, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor);

    List<Map<String, Object>> queryAsMap(DqlStatement statement, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor, Visible visible);

    <T extends IDomain> void save(T domain);

    <T extends IDomain> void save(T domain, Visible visible);

    <T extends IDomain> void save(T domain, NullHandleMode mode);

    <T extends IDomain> void save(T domain, NullHandleMode mode, Visible visible);

    long update(DmlStatement dml);

    long update(DmlStatement dml, Visible visible);

    <R> List<R> returningUpdate(DmlStatement dml, Class<R> resultClass);

    <R> List<R> returningUpdate(DmlStatement dml, Class<R> resultClass, Visible visible);

    <R> List<R> returningUpdate(DmlStatement dml, Class<R> resultClass, Supplier<List<R>> listConstructor);

    /**
     * @param resultClass probably below java type.
     *                    <ul>
     *                         <li>simple java type,eg: {@link java.lang.String} , {@link java.lang.Long}</li>
     *                         <li>java bean</li>
     *                         <li>{@link io.army.bean.FieldAccessBean}</li>
     *                         <li>{@link io.army.bean.PairBean}</li>
     *                    </ul>
     * @param <R>         java type of result element
     */
    <R> List<R> returningUpdate(DmlStatement dml, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible);

    List<Map<String, Object>> returningUpdateAsMap(DmlStatement dml);

    List<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Visible visible);

    List<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor);

    List<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor, Visible visible);


    <T extends IDomain> void batchSave(List<T> domainList);

    <T extends IDomain> void batchSave(List<T> domainList, Visible visible);

    <T extends IDomain> void batchSave(List<T> domainList, NullHandleMode mode);

    <T extends IDomain> void batchSave(List<T> domainList, NullHandleMode mode, Visible visible);

    /**
     * @return a unmodifiable list
     */
    List<Long> batchUpdate(NarrowDmlStatement dml);

    /**
     * @return a unmodifiable list
     */
    List<Long> batchUpdate(NarrowDmlStatement dml, Visible visible);

    MultiResult multiStmt(List<Statement> statementList);

    MultiResult multiStmt(List<Statement> statementList, Visible visible);

    MultiResult call(CallableStatement callable);

    void flush() throws SessionException;

}
