package io.army.sync;

import io.army.criteria.*;
import io.army.session.*;

import javax.annotation.Nullable;
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


    /**
     * <p>
     * Session identifier(non-unique, for example : database server cluster),probably is following :
     *     <ul>
     *         <li>server process id</li>
     *         <li>server thread id</li>
     *         <li>other identifier</li>
     *     </ul>
     *     <strong>NOTE</strong>: identifier will probably be updated if reconnect.
     * </p>
     *
     * @return {@link io.army.env.SyncKey#SESSION_IDENTIFIER_ENABLE} : <ul>
     * <li>true :  session identifier </li>
     * <li>false (default) : always 0 , because JDBC spi don't support get server process id (or server thread id)</li>
     * </ul>
     * @throws SessionException throw when underlying database session have closed
     */
    @Override
    long sessionIdentifier() throws SessionException;

    @Override
    SyncSessionFactory sessionFactory();

    TransactionInfo transactionInfo();

    /**
     * <p>Set session level transaction characteristics:
     * <ul>
     *     <li>These characteristics applies to all subsequent transactions performed within the current session,if you use appropriate default characteristic.</li>
     *     <li>This method is permitted within transactions ,but does not affect the current ongoing transaction.</li>
     *     <li>If you don't use appropriate default value,then appropriate characteristic does not affect new transaction,for example : {@link TransactionOption#isolation()} not null.</li>
     * </ul>
     *
     * @see SyncLocalSession#startTransaction(TransactionOption, HandleMode)
     * @see SyncRmSession#start(Xid, int, TransactionOption)
     */
    void setTransactionCharacteristics(TransactionOption option);

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
    <R> List<R> query(DqlStatement statement, Class<R> resultClass);

    <R> List<R> query(DqlStatement statement, Class<R> resultClass, SyncStmtOption option);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> List<R> query(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor);

    <R> List<R> query(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, SyncStmtOption option);


    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> List<R> queryObject(DqlStatement statement, Supplier<R> constructor);

    <R> List<R> queryObject(DqlStatement statement, Supplier<R> constructor, SyncStmtOption option);


    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> List<R> queryObject(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor);

    <R> List<R> queryObject(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor, SyncStmtOption option);

    /**
     * <p>
     * <strong>NOTE</strong> : If constructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> List<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function);

    <R> List<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);


    <R> List<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function,
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
    <R> List<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function,
                            Supplier<List<R>> listConstructor, SyncStmtOption option);


    /*-------------------below queryStream -------------------*/

    <R> Stream<R> queryStream(DqlStatement statement, Class<R> resultClass);

    <R> Stream<R> queryStream(DqlStatement statement, Class<R> resultClass, SyncStmtOption option);

    <R> Stream<R> queryObjectStream(DqlStatement statement, Supplier<R> constructor);

    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> Stream<R> queryObjectStream(DqlStatement statement, Supplier<R> constructor, SyncStmtOption option);


    /**
     * <p>
     * <strong>NOTE</strong> : If mapConstructor return {@link java.util.concurrent.ConcurrentMap}
     * and column value is null ,army remove element not put element.
     * </p>
     */
    <R> Stream<R> queryRecordStream(DqlStatement statement, Function<CurrentRecord, R> function);

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
    <R> Stream<R> queryRecordStream(DqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option);


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

    <T> long save(T domain, SyncStmtOption option);

    <T> long batchSave(List<T> domainList);

    <T> long batchSave(List<T> domainList, SyncStmtOption option);

    List<Long> batchUpdate(BatchDmlStatement statement);

    List<Long> batchUpdate(BatchDmlStatement statement, SyncStmtOption option);

    List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor);

    List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, SyncStmtOption option);


    @Override
    void close() throws SessionException;


}
