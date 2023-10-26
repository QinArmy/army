package io.army.session;


import io.army.lang.NonNull;

import java.util.function.Function;

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
     * @return true : {@link Session} in transaction block.
     */
    boolean inTransaction();

    static TransactionInfo info(boolean inTransaction, Isolation isolation, boolean readOnly) {
        return SimpleTransactionOption.info(inTransaction, isolation, readOnly);
    }

    static TransactionInfo infoFrom(Function<Option<?>, ?> optionFunc) {
        return SimpleTransactionOption.infoFrom(optionFunc);
    }


}
