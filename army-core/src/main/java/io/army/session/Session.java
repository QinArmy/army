package io.army.session;


import io.army.criteria.Visible;
import io.army.meta.TableMeta;

/**
 * <p>This interface representing database session.
 * <p>This interface is direct base interface of following :
 * <ul>
 *     <li>{@link LocalSession}</li>
 *     <li>{@link RmSession}</li>
 *     <li>{@code io.army.sync.SyncSession}</li>
 *     <li>{@code io.army.reactive.ReactiveSession}</li>
 * </ul>
 *
 * @see SessionFactory
 */
public interface Session extends CloseableSpec, OptionSpec {


    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    String name();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    SessionFactory sessionFactory();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isReadonlySession();

    /**
     * @return session in transaction block.
     * @throws SessionException throw when session have closed
     */
    boolean inTransaction() throws SessionException;

    /**
     * <p>Test session whether is rollback only or not.
     * <ul>
     *     <li>local transaction : the status will clear after rollback or start new transaction. see {@link LocalSession#markRollbackOnly()}</li>
     *     <li>XA transaction : the status will clear after prepare or one phase commit. see {@link RmSession#TM_FAIL}</li>
     * </ul>
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     *
     * @return true : current session support only rollback.
     * @see LocalSession#markRollbackOnly()
     * @see RmSession#TM_FAIL
     */
    boolean isRollbackOnly();

    /**
     * <p>Test session whether hold one  {@link TransactionInfo} instance or not.
     * <p><strong>NOTE</strong> :
     * <ol>
     *     <li>This method don't check whether session closed or not</li>
     *     <li>This method don't invoke {@link TransactionInfo#inTransaction()} method</li>
     * </ol>
     * <pre>The implementation of this method lke following
     *         <code><br/>
     *   &#64;Override
     *   public boolean hasTransactionInfo() {
     *       return this.transactionInfo != null;
     *   }
     *         </code>
     * </pre>
     *
     * @return true : session hold one  {@link TransactionInfo} instance.
     */
    boolean hasTransactionInfo();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isReadOnlyStatus();


    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isReactiveSession();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isSyncSession();


    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    Visible visible();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isAllowQueryInsert();


    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     *
     * @throws IllegalArgumentException throw,when not found {@link TableMeta}.
     */
    <T> TableMeta<T> tableMeta(Class<T> domainClass);

    /**
     * override {@link Object#toString()}
     *
     * @return driver info, contain : <ol>
     * <li>implementation class name</li>
     * <li>{@link #name()}</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


    /**
     * <p>This interface is base interface of following :
     * <ul>
     *     <li>{@link RmSession}</li>
     *     <li>RM {@link io.army.session.executor.StmtExecutor}</li>
     * </ul>
     * /**
     * <p><strong>NOTE</strong> : this interface never extends any interface.
     *
     * @since 1.0
     */
    interface XaTransactionSupportSpec {

        boolean isSupportForget();

        /**
         * @return the sub set of {@code  #start(Xid, int, TransactionOption)} support flags(bit set).
         */
        int startSupportFlags();

        /**
         * @return the sub set of {@code #end(Xid, int, Function)} support flags(bit set).
         */
        int endSupportFlags();

        /**
         * @return the sub set of {@code #recover(int, Function)} support flags(bit set).
         */
        int recoverSupportFlags();

        /**
         * @throws SessionException throw when underlying database session have closed.
         */
        boolean isSameRm(XaTransactionSupportSpec s) throws SessionException;

    }


}
