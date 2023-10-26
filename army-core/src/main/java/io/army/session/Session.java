package io.army.session;


import io.army.criteria.Visible;
import io.army.meta.TableMeta;

/**
 *
 */
public interface Session extends CloseableSpec, OptionSpec {

    boolean isReadOnlyStatus();

    boolean isReadonlySession();

    boolean inTransaction();

    boolean hasTransaction();


    String name();

    Visible visible();

    boolean isAllowQueryInsert();

    SessionFactory sessionFactory();

    /**
     * @throws IllegalArgumentException throw,when not found {@link TableMeta}.
     */
    <T> TableMeta<T> tableMeta(Class<T> domainClass);

    @Override
    String toString();


    /**
     * <p>This interface is base interface of following :
     * <ul>
     *     <li>{@link RmSession}</li>
     *     <li>RM {@link io.army.session.executor.StmtExecutor}</li>
     * </ul>
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


    }


}
