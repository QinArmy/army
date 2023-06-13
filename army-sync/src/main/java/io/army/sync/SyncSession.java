package io.army.sync;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.lang.Nullable;
import io.army.session.Session;
import io.army.session.VisibleModeException;

import java.util.List;
import java.util.Map;
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
    Map<String, Object> queryOneAsMap(SimpleDqlStatement statement);

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
    Map<String, Object> queryOneAsMap(SimpleDqlStatement statement, Visible visible);

    @Nullable
    Map<String, Object> queryOneAsMap(SimpleDqlStatement statement, Supplier<Map<String, Object>> mapConstructor);

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
    Map<String, Object> queryOneAsMap(SimpleDqlStatement statement, Supplier<Map<String, Object>> mapConstructor, Visible visible);

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
     *                         <li>{@link io.army.bean.PairBean}</li>
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


    List<Map<String, Object>> queryAsMap(SimpleDqlStatement statement);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    List<Map<String, Object>> queryAsMap(SimpleDqlStatement statement, Visible visible);


    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    List<Map<String, Object>> queryAsMap(SimpleDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                         Supplier<List<Map<String, Object>>> listConstructor);

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
    List<Map<String, Object>> queryAsMap(SimpleDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                         Supplier<List<Map<String, Object>>> listConstructor, Visible visible);



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
    Stream<Map<String, Object>> queryMapStream(SimpleDqlStatement statement,
                                               Supplier<Map<String, Object>> mapConstructor, StreamOptions options);

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
    Stream<Map<String, Object>> queryMapStream(SimpleDqlStatement statement,
                                               Supplier<Map<String, Object>> mapConstructor, StreamOptions options,
                                               Visible visible);


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


    List<Long> batchUpdate(BatchDmlStatement statement, Supplier<List<Long>> listConstructor);

    List<Long> batchUpdate(BatchDmlStatement statement, Supplier<List<Long>> listConstructor, boolean useMultiStmt);

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
    List<Long> batchUpdate(BatchDmlStatement statement, Supplier<List<Long>> listConstructor, Visible visible);


    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    List<Long> batchUpdate(BatchDmlStatement statement, Supplier<List<Long>> listConstructor, boolean useMultiStmt,
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


    List<Map<String, Object>> batchQueryAsMap(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                              Map<String, Object> terminator);

    List<Map<String, Object>> batchQueryAsMap(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                              Map<String, Object> terminator,
                                              Supplier<List<Map<String, Object>>> listConstructor);

    List<Map<String, Object>> batchQueryAsMap(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                              Map<String, Object> terminator,
                                              Supplier<List<Map<String, Object>>> listConstructor, boolean useMultiStmt);


    List<Map<String, Object>> batchQueryAsMap(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                              Map<String, Object> terminator, Visible visible);

    List<Map<String, Object>> batchQueryAsMap(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                              Map<String, Object> terminator,
                                              Supplier<List<Map<String, Object>>> listConstructor, Visible visible);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */

    List<Map<String, Object>> batchQueryAsMap(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                              Map<String, Object> terminator,
                                              Supplier<List<Map<String, Object>>> listConstructor, boolean useMultiStmt,
                                              Visible visible);


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


    Stream<Map<String, Object>> batchQueryMapStream(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                                    Map<String, Object> terminator, StreamOptions options);


    Stream<Map<String, Object>> batchQueryMapStream(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                                    Map<String, Object> terminator, StreamOptions options, boolean useMultiStmt);


    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    Stream<Map<String, Object>> batchQueryMapStream(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                                    Map<String, Object> terminator, StreamOptions options, Visible visible);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    Stream<Map<String, Object>> batchQueryMapStream(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                                    Map<String, Object> terminator, StreamOptions options, boolean useMultiStmt,
                                                    Visible visible);

    MultiResult multiStmt(MultiResultStatement statement);

    MultiResult multiStmt(MultiResultStatement statement, @Nullable StreamOptions options);

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
    MultiResult multiStmt(MultiResultStatement statement, @Nullable StreamOptions options, Visible visible);


    MultiResultStream multiStmtStream(MultiResultStatement statement);

    MultiResultStream multiStmtStream(MultiResultStatement statement, @Nullable StreamOptions options);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    MultiResultStream multiStmtStream(MultiResultStatement statement, Visible visible);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    MultiResultStream multiStmtStream(MultiResultStatement statement, @Nullable StreamOptions options, Visible visible);


}
