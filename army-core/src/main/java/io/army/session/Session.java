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
     * @throws SessionException throw when session have closed.
     */
    long sessionIdentifier() throws SessionException;

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
     * <p>Test session is whether rollback only or not.
     * <p> How to mark {@link Session}'s rollback only status ?
     * <ul>
     *     <li>local transaction  :
     *          <ol>
     *              <li>{@link LocalSession#markRollbackOnly()}</li>
     *              <li>throw {@link ChildUpdateException} when execute dml</li>
     *          </ol>
     *     </li>
     *     <li>XA transaction :
     *          <ol>
     *              <li>pass {@link RmSession#TM_FAIL} flag to {@link RmSession}'s end() method</li>
     *              <li>throw {@link ChildUpdateException} when execute dml</li>
     *          </ol>
     *     </li>
     * </ul>
     * <p> How to clear {@link Session}'s rollback only status ?
     * <ul>
     *     <li>local transaction  :
     *          <ol>
     *              <li>rollback transaction</li>
     *              <li>start new transaction</li>
     *          </ol>
     *     </li>
     *     <li>XA transaction :
     *          <ol>
     *              <li>prepare current transaction,but appropriate XA transaction is rollback only.</li>
     *              <li>start new transaction,but appropriate XA transaction is rollback only.</li>
     *          </ol>
     *     </li>
     * </ul>
     * <p><strong>NOTE</strong> : This method don't check session whether closed or not.
     *
     * @return true : session is rollback only.
     * @see LocalSession#markRollbackOnly()
     * @see RmSession#TM_FAIL
     */
    boolean isRollbackOnly();

    /**
     * <p>Test session whether hold one  {@link TransactionInfo} instance or not, the instance is current transaction info of this session.
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
