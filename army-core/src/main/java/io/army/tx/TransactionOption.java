package io.army.tx;

import io.army.lang.Nullable;
import io.army.session.OptionSpec;

public interface TransactionOption extends OptionSpec {

    @Nullable
    Isolation isolation();


    /**
     * @return true : transaction is read-only.
     */
    boolean isReadOnly();

    /**
     * <p>
     * override {@link Object#toString()}
     * <p>
     * <br/>
     *
     * @return transaction info, contain
     * <ul>
     *     <li>implementation class name</li>
     *     <li>transaction info</li>
     *     <li>{@link System#identityHashCode(Object)}</li>
     * </ul>
     */
    @Override
    String toString();

    static TransactionOption option(@Nullable Isolation isolation, boolean readOnly) {
        return ArmyTransactionOption.option(isolation, readOnly);
    }

}
