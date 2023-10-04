package io.army.tx;


import io.army.lang.NonNull;
import io.army.session.Session;

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


}
