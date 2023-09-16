package io.army.reactive;

import io.army.lang.Nullable;
import io.army.tx.CannotCreateTransactionException;
import io.army.tx.Isolation;
import io.army.tx.TransactionOptions;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is a implementation of {@link Session}.
 */
final class ArmyReactiveLocalSession extends _ArmyReactiveSession implements Session {


    private final AtomicBoolean sessionClosed = new AtomicBoolean(false);

    private final boolean readonly;

    private final AtomicReference<Transaction> sessionTransaction = new AtomicReference<>(null);

    ArmyReactiveLocalSession(ArmyReactiveLocalSessionFactory.LocalSessionBuilder builder) {
        super(builder);
        this.readonly = builder.readOnly;
    }


    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean inTransaction() {
        return false;
    }

    @Override
    public boolean isReadOnlyStatus() {
        return false;
    }


    /*################################## blow private instance inner class ##################################*/

    static final class LocalTransactionBuilder extends TransactionOptions implements Session.TransactionBuilder {

        final ArmyReactiveLocalSession session;

        boolean readOnly;

        Isolation isolation;

        int timeout = -1;

        String name;

        private LocalTransactionBuilder(ArmyReactiveLocalSession session) {
            this.session = session;
        }

        @Override
        public Session.TransactionBuilder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        @Override
        public Session.TransactionBuilder isolation(Isolation isolation) {
            this.isolation = isolation;
            return this;
        }

        @Override
        public Session.TransactionBuilder timeout(int seconds) {
            this.timeout = seconds;
            return this;
        }

        @Override
        public Session.TransactionBuilder name(@Nullable String txName) {
            this.name = txName;
            return this;
        }

        @Override
        public Transaction build() throws CannotCreateTransactionException {
            if (this.isolation == null) {
                throw new CannotCreateTransactionException("not specified isolation.");
            }

            throw new UnsupportedOperationException();
        }
    }

}
