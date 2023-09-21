package io.army.reactive;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is a implementation of {@link ReactiveLocalSession}.
 */
final class ArmyReactiveLocalSession extends _ArmyReactiveSession implements ReactiveLocalSession {


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


}
