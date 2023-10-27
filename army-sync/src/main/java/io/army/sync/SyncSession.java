package io.army.sync;

import io.army.criteria.BatchDmlStatement;
import io.army.criteria.SimpleDmlStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.criteria.Visible;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.lang.Nullable;
import io.army.session.CurrentRecord;
import io.army.session.Session;
import io.army.session.SessionException;
import io.army.session.VisibleModeException;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>This interface encapsulate blocking {@link Session}.
 *
 * <p>This interface is base interface of below interface:
 * <ul>
 *     <li>{@link SyncLocalSession}</li>
 *     <li>{@link SyncRmSession}</li>
 * </ul>
 */
public interface SyncSession extends Session, AutoCloseable {

    @Override
    SyncSessionFactory sessionFactory();


    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass);

    @Nullable
    <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option);


    @Nullable
    <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor);

    @Nullable
    <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option);

    @Nullable
    <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);

    @Nullable
    <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass);

    <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor);

    <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, SyncStmtOption option);


    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor);

    <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option);


    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor);

    <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor, SyncStmtOption option);

    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);

    <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);


    <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function,
                            Supplier<List<R>> listConstructor);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function,
                            Supplier<List<R>> listConstructor, SyncStmtOption option);


    /*-------------------below queryStream -------------------*/

    <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass);

    <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option);

    <R> Stream<R> queryObjectStream(SimpleDqlStatement statement, Supplier<R> constructor);

    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> Stream<R> queryObjectStream(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option);


    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> Stream<R> queryRecordStream(SimpleDqlStatement statement, Function<CurrentRecord, R> function);

    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     *
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> queryRecordStream(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);


    long update(SimpleDmlStatement statement);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    long update(SimpleDmlStatement statement, SyncStmtOption option);

    <T> long save(T domain);

    <T> long batchSave(List<T> domainList);

    List<Long> batchUpdate(BatchDmlStatement statement);

    List<Long> batchUpdate(BatchDmlStatement statement, SyncStmtOption option);

    List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor);

    List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, SyncStmtOption option);


    <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass);

    <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, SyncStmtOption option);

    <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor);

    <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, SyncStmtOption option);

    <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor);

    <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, SyncStmtOption option);

    <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor);

    <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor, SyncStmtOption option);


    <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function);


    <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);

    <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor);

    <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor, SyncStmtOption option);

    /*-------------------below batch stream stream -------------------*/


    <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass);

    <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass, SyncStmtOption option);


    <R> Stream<R> batchQueryObjectStream(BatchDqlStatement statement, Supplier<R> constructor);

    <R> Stream<R> batchQueryObjectStream(BatchDqlStatement statement, Supplier<R> constructor, SyncStmtOption option);


    <R> Stream<R> batchQueryRecordStream(BatchDqlStatement statement, Function<CurrentRecord, R> function);


    <R> Stream<R> batchQueryRecordStream(BatchDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);


    @Override
    void close() throws SessionException;


}
