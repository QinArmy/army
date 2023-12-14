package io.army.session;


import io.army.util._Collections;

import javax.annotation.Nonnull;
import java.util.Map;
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
     *         <li>{@link XaStates} with {@link Option#XA_STATES}</li>
     *         <li>{@link Xid} with {@link Option#XID}</li>
     *         <li>{@code flag} of last phase with {@link Option#XA_FLAGS}</li>
     *     </ul>
     *     when this instance is returned by {@link RmSession}.
     * <br/>
     */
    @Override
    <T> T valueOf(Option<T> option);

    static TransactionInfo info(boolean inTransaction, Isolation isolation, boolean readOnly,
                                Function<Option<?>, ?> optionFunc) {
        return ArmyTransactionInfo.create(inTransaction, isolation, readOnly, optionFunc);
    }

    static TransactionInfo pseudo(TransactionOption option) {
        final Map<Option<?>, Object> map = _Collections.hashMap(8);

        map.put(Option.START_MILLIS, System.currentTimeMillis());

        final Integer timeoutMillis;
        timeoutMillis = option.valueOf(Option.TIMEOUT_MILLIS);
        if (timeoutMillis != null) {
            map.put(Option.TIMEOUT_MILLIS, timeoutMillis);
        }
        return ArmyTransactionInfo.create(false, Isolation.PSEUDO, true, map::get);
    }

    static <T> TransactionInfo replaceOption(TransactionInfo info, Option<T> option, T value) {
        return ArmyTransactionInfo.replaceOption(info, option, value);
    }


}
