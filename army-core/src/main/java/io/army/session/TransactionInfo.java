package io.army.session;


import io.army.util._Collections;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

/**
 * <p>This interface representing the transaction info of session.
 *
 * @since 0.6.0
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

    static TransactionInfo pseudoLocal(final TransactionOption option) {
        final Map<Option<?>, Object> map = _Collections.hashMap(8);

        map.put(Option.START_MILLIS, System.currentTimeMillis());

        final Integer timeoutMillis;
        timeoutMillis = option.valueOf(Option.TIMEOUT_MILLIS);
        if (timeoutMillis != null) {
            map.put(Option.TIMEOUT_MILLIS, timeoutMillis);
        }
        map.put(Option.DEFAULT_ISOLATION, Boolean.TRUE);
        return ArmyTransactionInfo.create(false, Isolation.PSEUDO, true, map::get);
    }

    /**
     * <p>Create pseudo transaction info for XA transaction start method.
     */
    static TransactionInfo pseudoStart(final Xid xid, final int flags, final TransactionOption option) {
        if (option.isolation() != Isolation.PSEUDO || !option.isReadOnly()) {
            throw new IllegalArgumentException("non-pseudo transaction option");
        }

        final Map<Option<?>, Object> map = _Collections.hashMap(12);

        map.put(Option.START_MILLIS, System.currentTimeMillis());

        final Integer timeoutMillis;
        timeoutMillis = option.valueOf(Option.TIMEOUT_MILLIS);
        if (timeoutMillis != null) {
            map.put(Option.TIMEOUT_MILLIS, timeoutMillis);
        }
        map.put(Option.XID, xid);
        map.put(Option.XA_FLAGS, flags);
        map.put(Option.XA_STATES, XaStates.ACTIVE);
        map.put(Option.DEFAULT_ISOLATION, Boolean.TRUE);
        return ArmyTransactionInfo.create(false, Isolation.PSEUDO, true, map::get);
    }


    /**
     * <p>Create pseudo transaction info for XA transaction end method.
     */
    static TransactionInfo pseudoEnd(final TransactionInfo info, final int flags) {
        if (info.inTransaction() || info.isolation() != Isolation.PSEUDO || !info.isReadOnly()) {
            throw new IllegalArgumentException("non-pseudo transaction info");
        }
        final Map<Option<?>, Object> map = _Collections.hashMap(12);

        map.put(Option.START_MILLIS, info.nonNullOf(Option.START_MILLIS));

        final Integer timeoutMillis;
        timeoutMillis = info.valueOf(Option.TIMEOUT_MILLIS);
        if (timeoutMillis != null) {
            map.put(Option.TIMEOUT_MILLIS, timeoutMillis);
        }
        map.put(Option.XID, info.nonNullOf(Option.XID));
        map.put(Option.XA_FLAGS, flags);
        map.put(Option.XA_STATES, XaStates.IDLE);
        map.put(Option.DEFAULT_ISOLATION, info.nonNullOf(Option.DEFAULT_ISOLATION));
        return ArmyTransactionInfo.create(false, Isolation.PSEUDO, true, map::get);
    }


    static <T> TransactionInfo replaceOption(TransactionInfo info, Option<T> option, T value) {
        return ArmyTransactionInfo.replaceOption(info, option, value);
    }


}
