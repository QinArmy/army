package io.army.session;

import javax.annotation.Nullable;

/**
 * <p>Package interface
 */
interface TransactionSpec extends OptionSpec {


    /**
     * <p>This transaction isolation.
     *
     * @see TransactionOption
     * @see TransactionInfo
     */
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


}
