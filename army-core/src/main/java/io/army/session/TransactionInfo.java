package io.army.session;


import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * <p>This interface representing the transaction info of session.
 *
 * @since 1.0
 */
public interface TransactionInfo extends TransactionOption {


    /**
     * <p>{@link io.army.session.Session}'s transaction isolation level.
     * <ul>
     *     <li>{@link #inTransaction()} is true : the isolation representing current transaction isolation</li>
     *     <li>Session level transaction isolation</li>
     * </ul>
     *
     * @return non-null
     */
    @Nonnull
    @Override
    Isolation isolation();

    /**
     * <p>Session whether in transaction block or not.
     * <p><strong>NOTE</strong> : for XA transaction {@link XaStates#PREPARED} always return false.
     *
     * @return true : {@link Session} in transaction block.
     */
    boolean inTransaction();

    /**
     * @return true when
     * <ol>
     *     <li>{@link #inTransaction()} is true</li>
     *     <li>database server demand client rollback(eg: PostgreSQL) or {@link Session#isRollbackOnly()}</li>
     * </ol>
     */
    boolean isRollbackOnly();

    /**
     * <p>
     * Application developer can get
     *     <ul>
     *         <li>{@link XaStates} with {@link ArmyOption#XA_STATES}</li>
     *         <li>{@link Xid} with {@link ArmyOption#XID}</li>
     *         <li>{@code flag} of last phase with {@link ArmyOption#XA_FLAGS}</li>
     *     </ul>
     *     when this instance is returned by {@link RmSession}.
     * <br/>
     */
    @Override
    <T> T valueOf(ArmyOption<T> option);

    static TransactionInfo info(boolean inTransaction, Isolation isolation, boolean readOnly,
                                Function<ArmyOption<?>, ?> optionFunc) {
        return ArmyTransactionInfo.create(inTransaction, isolation, readOnly, optionFunc);
    }


}
