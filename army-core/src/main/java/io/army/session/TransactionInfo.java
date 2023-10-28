package io.army.session;


import io.army.lang.NonNull;

import java.util.function.Function;

/**
 * <p>This interface representing the transaction info of session.
 *
 * @since 1.0
 */
public interface TransactionInfo extends TransactionOption {


    /**
     * <p>
     * {@link io.army.session.Session}'s transaction isolation level.
     * <br/>
     *
     * @return non-null
     */
    @NonNull
    @Override
    Isolation isolation();

    /**
     * <p>session whether in transaction block.
     * <p><strong>NOTE</strong> : for XA transaction {@link XaStates#PREPARED} always return false.
     *
     * @return true : {@link Session} in transaction block.
     */
    boolean inTransaction();

    /**
     * <p>
     * Application developer can get
     *     <ul>
     *         <li>{@link XaStates} with {@link Option#XA_STATES}</li>
     *         <li>{@link Xid} with {@link Option#XID}</li>
     *         <li>{@code flag} of last phase with {@link Option#XA_FLAGS}</li>
     *     </ul>
     *     when {@link RmSession} in XA transaction block.
     * <br/>
     */
    @Override
    <T> T valueOf(Option<T> option);

    static TransactionInfo info(boolean inTransaction, Isolation isolation, boolean readOnly,
                                Function<Option<?>, ?> optionFunc) {
        return ArmyTransactionInfo.create(inTransaction, isolation, readOnly, optionFunc);
    }


}
