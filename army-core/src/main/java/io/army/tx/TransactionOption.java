package io.army.tx;

import io.army.lang.Nullable;

@Deprecated
public interface TransactionOption {

    @Nullable
    String name();

    boolean readOnly();

    Isolation isolation();

    int timeout();

    /**
     * <ul>
     *     <li>negative:  Use the default timeout of the underlying transaction system,or none if timeouts are not supported.</li>
     *     <li>other : transaction end mills. {@link System#currentTimeMillis()} </li>
     * </ul>
     */
    long endMills();


}
