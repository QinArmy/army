package io.army.sync;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.lang.Nullable;
import io.army.session.CurrentRecord;
import io.army.session.Session;
import io.army.session.VisibleModeException;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>
 * This interface encapsulate blocking api than can access database.
 * </p>
 *
 * <p>
 * This interface is base interface of below interface:
 *     <ul>
 *         <li>{@link LocalSession}</li>
 *     </ul>
 * </p>
 */
public interface SyncSession extends Session {

    @Override
    SyncSessionFactory sessionFactory();


    /**
     * @param <R> representing select result Java Type.
     */
    @Nullable
    <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass);


    /**
     * @param <R> representing select result Java Type.
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    @Nullable
    <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass, Visible visible);


    @Nullable
    <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    @Nullable
    <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, Visible visible);


    @Nullable
    <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    @Nullable
    <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, Visible visible);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass);


    /**
     * @param <R> representing select result Java Type.
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, Visible visible);


    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor);


    /**
     * @param resultClass probably below type.
     *                    <ul>
     *                         <li>simple java type,eg: {@code java.lang.String} , {@code java.lange.Long}</li>
     *                         <li>java bean</li>
     *                         <li>{@link io.army.bean.FieldAccessBean}</li>
     *                    </ul>
     * @param <R>         representing select result Java Type.
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible);

    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor);


    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor);


    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Visible visible);

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
    <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor,
                            Visible visible);

    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function);


    <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function,
                            Supplier<List<R>> listConstructor);

    <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, Visible visible);

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
                            Supplier<List<R>> listConstructor, Visible visible);


    /*-------------------below queryStream -------------------*/

    <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, StreamOptions options);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, StreamOptions options, Visible visible);


    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> Stream<R> queryObjectStream(SimpleDqlStatement statement, Supplier<R> constructor, StreamOptions options);

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
    <R> Stream<R> queryObjectStream(SimpleDqlStatement statement, Supplier<R> constructor, StreamOptions options,
                                    Visible visible);

    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> Stream<R> queryRecardStream(SimpleDqlStatement statement, Function<CurrentRecord, R> function,
                                    StreamOptions options);

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
    <R> Stream<R> queryRecardStream(SimpleDqlStatement statement, Function<CurrentRecord, R> function,
                                    StreamOptions options, Visible visible);


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
    long update(SimpleDmlStatement statement, Visible visible);

    <T> long save(T domain);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <T> long save(T domain, Visible visible);

    <T> long batchSave(List<T> domainList);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <T> long batchSave(List<T> domainList, Visible visible);


    List<Long> batchUpdate(BatchDmlStatement statement);

    List<Long> batchUpdate(BatchDmlStatement statement, boolean useMultiStmt);


    List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor);

    List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, boolean useMultiStmt);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    List<Long> batchUpdate(BatchDmlStatement statement, Visible visible);


    List<Long> batchUpdate(BatchDmlStatement statement, boolean useMultiStmt, Visible visible);


    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, Visible visible);


    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, boolean useMultiStmt,
                           Visible visible);


    <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator);

    <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                           Supplier<List<R>> listConstructor);

    <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                           Supplier<List<R>> listConstructor, boolean useMultiStmt);


    <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator, Visible visible);

    <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                           Supplier<List<R>> listConstructor, Visible visible);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                           Supplier<List<R>> listConstructor, boolean useMultiStmt, Visible visible);


    <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator);

    <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                           Supplier<List<R>> listConstructor);

    <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                           Supplier<List<R>> listConstructor, boolean useMultiStmt);


    <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator, Visible visible);

    <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                           Supplier<List<R>> listConstructor, Visible visible);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */

    <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                           Supplier<List<R>> listConstructor, boolean useMultiStmt, Visible visible);


    <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                   StreamOptions options);

    <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                   StreamOptions options, boolean useMultiStmt);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                   StreamOptions options, Visible visible);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                   StreamOptions options, boolean useMultiStmt, Visible visible);


    <R> Stream<R> batchQueryObjectStream(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                         StreamOptions options);


    <R> Stream<R> batchQueryObjectStream(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                         StreamOptions options, boolean useMultiStmt);


    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> batchQueryObjectStream(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                         StreamOptions options, Visible visible);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> batchQueryObjectStream(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                         StreamOptions options, boolean useMultiStmt,
                                         Visible visible);


    <R> Stream<R> batchQueryRecordStream(BatchDqlStatement statement, Function<CurrentRecord, R> function, R terminator,
                                         StreamOptions options);


    <R> Stream<R> batchQueryRecordStream(BatchDqlStatement statement, Function<CurrentRecord, R> function, R terminator,
                                         StreamOptions options, boolean useMultiStmt);


    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> batchQueryRecordStream(BatchDqlStatement statement, Function<CurrentRecord, R> function, R terminator,
                                         StreamOptions options, Visible visible);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> batchQueryRecordStream(BatchDqlStatement statement, Function<CurrentRecord, R> function, R terminator,
                                         StreamOptions options, boolean useMultiStmt,
                                         Visible visible);

    MultiResult multiStmt(MultiResultStatement statement);

    MultiResult multiStmt(MultiResultStatement statement, StreamOptions options);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    MultiResult multiStmt(MultiResultStatement statement, Visible visible);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    MultiResult multiStmt(MultiResultStatement statement, StreamOptions options, Visible visible);


    MultiStream multiStmtStream(MultiResultStatement statement);

    MultiStream multiStmtStream(MultiResultStatement statement, StreamOptions options);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    MultiStream multiStmtStream(MultiResultStatement statement, Visible visible);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    MultiStream multiStmtStream(MultiResultStatement statement, StreamOptions options, Visible visible);


}
