package io.army.tx;

import io.army.lang.Nullable;

public class TransactionOptionImpl implements TransactionOption {

    public static TransactionOptionImpl build(@Nullable String name, boolean readOnly
            , Isolation isolation, int timeout) {
        return new TransactionOptionImpl(name, readOnly, isolation, timeout);
    }

    public static XaTransactionOptionImpl build(@Nullable String name, boolean readOnly, Isolation isolation
            , int timeout, byte[] gtrid) {
        return new XaTransactionOptionImpl(name, readOnly, isolation, timeout, gtrid);
    }

    private final String name;

    private final boolean readOnly;

    private final Isolation isolation;

    private final int timeout;

    private TransactionOptionImpl(@Nullable String name, boolean readOnly, Isolation isolation, int timeout) {
        this.name = name;
        this.readOnly = readOnly;
        this.isolation = isolation;
        this.timeout = timeout;
    }

    @Nullable
    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final boolean readOnly() {
        return this.readOnly;
    }

    @Override
    public final Isolation isolation() {
        return this.isolation;
    }

    @Override
    public final int timeout() {
        return this.timeout;
    }

    private static final class XaTransactionOptionImpl extends TransactionOptionImpl implements XaTransactionOption {

        private final byte[] gtrid;

        private XaTransactionOptionImpl(@Nullable String name, boolean readOnly, Isolation isolation
                , int timeout, byte[] gtrid) {
            super(name, readOnly, isolation, timeout);
            this.gtrid = gtrid;
        }

        @Override
        public final byte[] globalTransactionId() {
            return this.gtrid;
        }
    }
}
