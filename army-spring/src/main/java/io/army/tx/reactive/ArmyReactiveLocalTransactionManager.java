package io.army.tx.reactive;


import io.army.reactive.ReactiveLocalSession;
import io.army.reactive.ReactiveSessionFactory;
import io.army.session.*;
import io.army.tx.sync.SpringUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.transaction.reactive.AbstractReactiveTransactionManager;
import org.springframework.transaction.reactive.GenericReactiveTransaction;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public final class ArmyReactiveLocalTransactionManager extends AbstractReactiveTransactionManager {

    public static ArmyReactiveLocalTransactionManager create(ReactiveSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        return new ArmyReactiveLocalTransactionManager(sessionFactory);
    }


    private final ReactiveSessionFactory sessionFactory;

    private boolean useReadOnlyTransaction = true;

    private boolean useTransactionName;


    /**
     * private constructor
     */
    private ArmyReactiveLocalTransactionManager(ReactiveSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean isUseReadOnlyTransaction() {
        return this.useReadOnlyTransaction;
    }

    public void setUseReadOnlyTransaction(boolean useReadOnlyTransaction) {
        this.useReadOnlyTransaction = useReadOnlyTransaction;
    }


    public boolean isUseTransactionName() {
        return this.useTransactionName;
    }

    public void setUseTransactionName(boolean useTransactionName) {
        this.useTransactionName = useTransactionName;
    }


    @Override
    protected Object doGetTransaction(final TransactionSynchronizationManager synchronizationManager)
            throws TransactionException {
        final LocalTransactionObject txObject = new LocalTransactionObject();

        final ReactiveLocalSession session;
        session = (ReactiveLocalSession) synchronizationManager.getResource(this.sessionFactory);
        if (session != null) {
            txObject.reset(session);
        }
        return txObject;
    }


    @Override
    protected boolean isExistingTransaction(final Object transaction) throws TransactionException {
        final ReactiveLocalSession session = ((LocalTransactionObject) transaction).session;
        return session != null && session.hasTransactionInfo(); // here , use hasTransactionInfo() not inTransaction(),  because perhaps pseudo transaction
    }

    @Override
    protected Mono<Void> doBegin(final TransactionSynchronizationManager synchronizationManager, final Object transaction,
                                 final TransactionDefinition definition) throws TransactionException {

        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;

        final ReactiveLocalSession currentSession = txObject.session;
        final Mono<Void> mono;
        if (currentSession == null) {
            final ReactiveSessionFactory sessionFactory = this.sessionFactory;

            mono = sessionFactory.localBuilder()
                    .name(definition.getName())
                    .readonly(definition.isReadOnly())
                    .build()
                    .flatMap(session -> {
                        txObject.reset(session);
                        synchronizationManager.bindResource(sessionFactory, session);
                        return startTransaction(session, definition);
                    });
        } else {
            mono = Mono.defer(() -> startTransaction(currentSession, definition));
        }

        return mono.onErrorMap(this::wrapErrorIfNeed);
    }


    @Override
    protected Mono<Void> doCommit(final TransactionSynchronizationManager synchronizationManager,
                                  final GenericReactiveTransaction status) throws TransactionException {

        final ReactiveLocalSession session = ((LocalTransactionObject) status.getTransaction()).session;

        final Mono<Void> mono;
        if (session == null) {
            mono = Mono.error(SpringUtils.transactionNoSession());
        } else if (session.hasTransactionInfo()) {
            mono = Mono.defer(session::commit)
                    .onErrorMap(this::wrapErrorIfNeed)
                    .then();
        } else {
            mono = Mono.error(SpringUtils.unexpectedTransactionEnd(session));
        }
        return mono;
    }

    @Override
    protected Mono<Void> doRollback(final TransactionSynchronizationManager synchronizationManager,
                                    final GenericReactiveTransaction status) throws TransactionException {

        final ReactiveLocalSession session = ((LocalTransactionObject) status.getTransaction()).session;

        final Mono<Void> mono;
        if (session == null) {
            mono = Mono.error(SpringUtils.transactionNoSession());
        } else if (session.hasTransactionInfo()) {
            mono = Mono.defer(session::rollback)
                    .onErrorMap(this::wrapErrorIfNeed)
                    .then();
        } else {
            mono = Mono.error(SpringUtils.unexpectedTransactionEnd(session));
        }
        return mono;
    }


    @Override
    protected Mono<Object> doSuspend(final TransactionSynchronizationManager synchronizationManager, final Object transaction)
            throws TransactionException {
        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;

        if (txObject.session == null) {
            return Mono.error(SpringUtils.transactionNoSession());
        }

        return Mono.defer(() -> {
            final ReactiveLocalSession session;
            session = txObject.suspend();
            synchronizationManager.unbindResource(this.sessionFactory);
            return Mono.just(session);
        });
    }

    @Override
    protected Mono<Void> doResume(TransactionSynchronizationManager synchronizationManager, @Nullable Object transaction,
                                  Object suspendedResources) throws TransactionException {
        ReactiveLocalSessionFactory sessionFactory = this.sessionFactory;
        if (synchronizationManager.hasResource(sessionFactory)) {
            synchronizationManager.unbindResource(sessionFactory);
        }
        ReactiveLocalSession session = (ReactiveLocalSession) suspendedResources;
        synchronizationManager.bindResource(session, session);
        return Mono.empty();
    }

    @Override
    protected Mono<Void> doSetRollbackOnly(TransactionSynchronizationManager synchronizationManager
            , GenericReactiveTransaction status) throws TransactionException {
        return Mono.empty();
    }

    @Override
    protected Mono<Void> doCleanupAfterCompletion(TransactionSynchronizationManager synchronizationManager
            , Object transaction) {
        LocalTransactionObject txObject = (LocalTransactionObject) transaction;
//        ReactiveSessionFactory sessionFactory = this.sessionFactory;
//        if (synchronizationManager.hasResource(sessionFactory)) {
//            synchronizationManager.unbindResource(sessionFactory);
//        }
//        return txObject.session
//                .close()
//                .onErrorMap(e -> SpringUtils.convertArmyAccessException((SessionException) e))
//                ;
        throw new UnsupportedOperationException();
    }

    @Override
    protected Mono<Void> prepareForCommit(TransactionSynchronizationManager synchronizationManager
            , GenericReactiveTransaction status) {
        return Mono.empty();
    }


    /*################################## blow private method ##################################*/

    private Mono<ReactiveLocalSession> obtainSession(TransactionSynchronizationManager synchronizationManager, final LocalTransactionObject txObject) {
        ReactiveLocalSession session;
        session = txObject.session;
        session = (ReactiveLocalSession) synchronizationManager.getResource(this.sessionFactory);
        if (session != null) {
            return Mono.just(session);
        }
        return this.sessionFactory.builder()
                .build();
    }


    /**
     * @see #doBegin(TransactionSynchronizationManager, Object, TransactionDefinition)
     */
    private Mono<Void> startTransaction(final ReactiveLocalSession session, final TransactionDefinition definition) {

        final String txLabel;
        txLabel = definition.getName();

        final boolean readOnly;
        readOnly = definition.isReadOnly();

        final int isolationLevel;
        isolationLevel = definition.getIsolationLevel();


        // create TransactionOption
        final TransactionOption.Builder builder;
        builder = TransactionOption.builder()
                .option(Option.READ_ONLY, readOnly);

        if (txLabel != null) {
            builder.option(Option.LABEL, txLabel);
            if (this.useTransactionName) {
                builder.option(Option.NAME, txLabel);
            }
        }

        final int timeoutSeconds;
        timeoutSeconds = definition.getTimeout();
        if (timeoutSeconds > 0) {
            final long timeoutMillis;
            timeoutMillis = timeoutSeconds * 1000L;
            if (timeoutMillis > Integer.MAX_VALUE) {
                throw new TransactionUsageException("timeout milliseconds greater than Integer.MAX_VALUE");
            }
            builder.option(Option.TIMEOUT_MILLIS, (int) timeoutMillis);
        }

        final Mono<TransactionInfo> mono;
        if (!readOnly
                || isolationLevel != TransactionDefinition.ISOLATION_DEFAULT
                || this.useReadOnlyTransaction) {

            if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
                builder.option(Option.ISOLATION, SpringUtils.toArmyIsolation(isolationLevel));
            }

            // start real transaction
            mono = session.startTransaction(builder.build(), HandleMode.ERROR_IF_EXISTS)
                    .doOnSuccess(info -> {
                        assert info.inTransaction();
                    });
        } else {
            builder.option(Option.ISOLATION, Isolation.PSEUDO);

            final TransactionInfo info;
            info = session.pseudoTransaction(builder.build(), HandleMode.ERROR_IF_EXISTS);

            assert !info.inTransaction();
            assert info.isReadOnly();
            assert info.isolation() == Isolation.PSEUDO;

            mono = Mono.just(info);
        }
        return mono.then();

    }

    private Throwable wrapErrorIfNeed(final Throwable cause) {
        if (cause instanceof SessionException) {
            return SpringUtils.wrapSessionError((SessionException) cause);
        }
        return cause;
    }


    /*################################## blow static inner class ##################################*/

    private static final class LocalTransactionObject {

        private static final AtomicReferenceFieldUpdater<LocalTransactionObject, ReactiveLocalSession> SESSION =
                AtomicReferenceFieldUpdater.newUpdater(LocalTransactionObject.class, ReactiveLocalSession.class, "session");


        private volatile ReactiveLocalSession session;


        private ReactiveLocalSession suspend() {
            final ReactiveLocalSession reactiveSession;
            reactiveSession = SESSION.getAndSet(this, null);
            if (reactiveSession == null) {
                throw SpringUtils.transactionNoSession();
            }
            return reactiveSession;
        }

        private void reset(final ReactiveLocalSession newSession) {
            if (!SESSION.compareAndSet(this, null, newSession)) {
                throw new IllegalStateException("session not null,couldn't reset");
            }
        }


    }

}
