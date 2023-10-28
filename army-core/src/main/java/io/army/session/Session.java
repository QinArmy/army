package io.army.session;


import io.army.criteria.Visible;
import io.army.meta.TableMeta;

/**
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
     * @return session hold one  {@link TransactionInfo} instance.
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean hasTransaction();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isReadOnlyStatus();


    /**
     * <p>The status will clear after rollback or start new transaction.
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     *
     * @return true : current session support only rollback.
     * @see #markRollbackOnly()
     */
    boolean isRollbackOnly();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isReactiveSession();

    /**
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    boolean isSyncSession();

    /**
     * <p>Mark current session don't support rollback even if {@link #hasTransaction()} return false.
     * <p>The status will clear after rollback.
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     *
     * @see #isRollbackOnly()
     */
    void markRollbackOnly();


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


        boolean isSameRm(XaTransactionSupportSpec s);

    }


}
