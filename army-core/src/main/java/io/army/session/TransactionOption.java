package io.army.session;


import javax.annotation.Nullable;

/**
 * @see TransactionInfo
 * @since 1.0
 */
public interface TransactionOption extends OptionSpec {

    /**
     * <p>This transaction isolation.
     *
     * @return <ul>
     * <li>null : use default isolation</li>
     * <li>non-null : use specified isolation for this transaction,but does not affect subsequent transactions</li>
     * </ul>
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

    static TransactionOption option(@Nullable Isolation isolation, boolean readOnly) {
        return SimpleTransactionOption.option(isolation, readOnly);
    }


    static Builder builder() {
        return SimpleTransactionOption.builder();
    }

    interface Builder {

        /**
         * set transaction option.
         *
         * @param option transaction option key,for example :
         *               <ul>
         *                    <li>{@link Option#ISOLATION}</li>
         *                    <li>{@link Option#READ_ONLY}</li>
         *                    <li>{@link Option#NAME} ,transaction name</li>
         *                    <li>{@code  Option#WITH_CONSISTENT_SNAPSHOT}</li>
         *                    <li>{@code Option#DEFERRABLE}</li>
         *                    <li>{@link Option#WAIT}</li>
         *                    <li>{@link Option#LOCK_TIMEOUT}</li>
         *               </ul>
         */
        <T> Builder option(Option<T> option, @Nullable T value);

        /**
         * @throws IllegalArgumentException throw when <ul>
         *                                  <li>{@link Option#IN_TRANSACTION} exists</li>
         *                                  </ul>
         */
        TransactionOption build() throws IllegalArgumentException;


    }//Builder

}
