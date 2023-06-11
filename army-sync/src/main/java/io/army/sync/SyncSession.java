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
 * This interface encapsulate synchronous api than can access database.
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


    List<Map<String, Object>> queryAsMap(SimpleDqlStatement statement, Supplier<Map<String, Object>> mapConstructor,
                                         Supplier<List<Map<String, Object>>> listConstructor);

    /**
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

    <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, int fetchSize);


    <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, boolean serverStream,
                              int fetchSize, @Nullable Comparable<? super R> comparator);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, int fetchSize, Visible visible);


    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, boolean serverStream, int fetchSize,
                              @Nullable Comparable<? super R> comparator, Visible visible);


    /*-------------------below -------------------*/


    <R> Stream<R> queryParallelStream(SimpleDqlStatement statement, Class<R> resultClass, int fetchSize);


    <R> Stream<R> queryParallelStream(SimpleDqlStatement statement, Class<R> resultClass, boolean serverStream,
                                      int fetchSize, @Nullable Comparable<? super R> comparator);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> queryParallelStream(SimpleDqlStatement statement, Class<R> resultClass, int fetchSize,
                                      Visible visible);


    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    <R> Stream<R> queryParallelStream(SimpleDqlStatement statement, Class<R> resultClass, boolean serverStream,
                                      int fetchSize, @Nullable Comparable<? super R> comparator, Visible visible);



    /*-------------------below query map stream-------------------*/

    Stream<Map<String, Object>> queryMapStream(SimpleDqlStatement statement,
                                               Supplier<Map<String, Object>> mapConstructor, int fetchSize);


    Stream<Map<String, Object>> queryMapStream(SimpleDqlStatement statement,
                                               Supplier<Map<String, Object>> mapConstructor, boolean serverStream,
                                               int fetchSize, @Nullable Comparable<Map<String, Object>> comparator);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    Stream<Map<String, Object>> queryMapStream(SimpleDqlStatement statement,
                                               Supplier<Map<String, Object>> mapConstructor, int fetchSize,
                                               Visible visible);


    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    Stream<Map<String, Object>> queryMapStream(SimpleDqlStatement statement,
                                               Supplier<Map<String, Object>> mapConstructor, boolean serverStream,
                                               int fetchSize, @Nullable Comparable<Map<String, Object>> comparator,
                                               Visible visible);


    /*-------------------below -------------------*/


    Stream<Map<String, Object>> queryParallelMapStream(SimpleDqlStatement statement,
                                                       Supplier<Map<String, Object>> mapConstructor, int fetchSize);


    Stream<Map<String, Object>> queryParallelMapStream(SimpleDqlStatement statement,
                                                       Supplier<Map<String, Object>> mapConstructor,
                                                       boolean serverStream, int fetchSize,
                                                       @Nullable Comparable<Map<String, Object>> comparator);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    Stream<Map<String, Object>> queryParallelMapStream(SimpleDqlStatement statement,
                                                       Supplier<Map<String, Object>> mapConstructor,
                                                       int fetchSize, Visible visible);


    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    Stream<Map<String, Object>> queryParallelMapStream(SimpleDqlStatement statement,
                                                       Supplier<Map<String, Object>> mapConstructor,
                                                       boolean serverStream, int fetchSize,
                                                       @Nullable Comparable<Map<String, Object>> comparator,
                                                       Visible visible);


    long update(SimpleDmlStatement dml);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    long update(SimpleDmlStatement dml, Visible visible);

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

    /**
     * @return a unmodifiable list
     */
    List<Long> batchUpdate(BatchDmlStatement statement);

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

    QueryResult batchQuery(BatchDqlStatement statement);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    QueryResult batchQuery(BatchDqlStatement statement, Visible visible);


    MultiResult multiStmt(MultiStatement statement);

    /**
     * @throws VisibleModeException throw when satisfy all the following conditions :
     *                              <ul>
     *                                  <li>visible is {@link Visible#ONLY_NON_VISIBLE} or {@link Visible#BOTH}</li>
     *                                  <li>{@link Session#visible()} is don't support visible value</li>
     *                              </ul>
     * @see io.army.env.ArmyKey#VISIBLE_MODE
     * @see io.army.env.ArmyKey#VISIBLE_SESSION_WHITE_LIST
     */
    MultiResult multiStmt(MultiStatement statement, Visible visible);

    MultiResult call(CallableStatement callable);


}
