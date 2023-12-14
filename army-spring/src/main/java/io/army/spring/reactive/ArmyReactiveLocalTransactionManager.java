package io.army.spring.reactive;


import io.army.reactive.ReactiveLocalSession;
import io.army.reactive.ReactiveSessionFactory;
import io.army.session.*;
import io.army.spring.sync.SpringUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.reactive.AbstractReactiveTransactionManager;
import org.springframework.transaction.reactive.GenericReactiveTransaction;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

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
    protected Object doGetTransaction(final TransactionSynchronizationManager manager)
            throws TransactionException {
        final LocalTransactionObject txObject = new LocalTransactionObject();

        final ReactiveLocalSession session;
        session = (ReactiveLocalSession) manager.getResource(this.sessionFactory);
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
    protected Mono<Void> doBegin(final TransactionSynchronizationManager manager, final Object transaction,
                                 final TransactionDefinition definition) throws TransactionException {

        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;
        final ReactiveLocalSession currentSession = txObject.session;

        final Mono<Void> mono;
        if (currentSession != null && currentSession.hasTransactionInfo()) {
            String m = String.format("%s don't support %s.%s", getClass().getName(),
                    Propagation.class.getName(), Propagation.NESTED.name());
            mono = Mono.error(new TransactionUsageException(m));
        } else if (currentSession == null) {
            final ReactiveSessionFactory sessionFactory = this.sessionFactory;

            mono = sessionFactory.localBuilder()
                    .name(definition.getName())
                    .readonly(definition.isReadOnly())
                    .build()
                    .flatMap(session -> {
                        txObject.reset(session);
                        manager.bindResource(sessionFactory, session);
                        return startTransaction(session, definition);
                    });
        } else {
            mono = Mono.defer(() -> startTransaction(currentSession, definition));
        }

        return mono.onErrorMap(this::wrapErrorIfNeed);
    }


    @Override
    protected Mono<Void> doCommit(final TransactionSynchronizationManager manager,
                                  final GenericReactiveTransaction status) throws TransactionException {

        return commitOrRollback(status, true);
    }

    @Override
    protected Mono<Void> doRollback(final TransactionSynchronizationManager manager,
                                    final GenericReactiveTransaction status) throws TransactionException {
        return commitOrRollback(status, false);
    }


    @Override
    protected Mono<Object> doSuspend(final TransactionSynchronizationManager manager, final Object transaction)
            throws TransactionException {
        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;

        if (txObject.session == null) {
            return Mono.error(SpringUtils.transactionNoSession());
        }

        return Mono.defer(() -> {
            final ReactiveLocalSession session;
            session = txObject.suspend();
            manager.unbindResource(this.sessionFactory);
            return Mono.just(session);
        });
    }

    @Override
    protected Mono<Void> doResume(final TransactionSynchronizationManager manager,
                                  final @Nullable Object transaction, final Object suspendedResources)
            throws TransactionException {

        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;
        if (txObject == null) {
            // no bug never here
            return Mono.error(new IllegalTransactionStateException("no transaction object"));
        }
        return Mono.defer(() -> {
            final ReactiveSessionFactory sessionFactory = this.sessionFactory;
            if (manager.hasResource(sessionFactory)) {
                manager.unbindResource(sessionFactory);
            }
            final ReactiveLocalSession session = (ReactiveLocalSession) suspendedResources;
            txObject.reset(session);
            manager.bindResource(session, session);
            return Mono.empty();
        });
    }

    @Override
    protected Mono<Void> doSetRollbackOnly(final TransactionSynchronizationManager manager,
                                           final GenericReactiveTransaction status) throws TransactionException {
        final ReactiveLocalSession session;
        session = ((LocalTransactionObject) status.getTransaction()).session;

        if (session == null) {
            return Mono.error(SpringUtils.transactionNoSession());
        }
        return Mono.defer(() -> {
            session.markRollbackOnly();
            return Mono.empty();
        });
    }

    @Override
    protected Mono<Void> doCleanupAfterCompletion(final TransactionSynchronizationManager synchronizationManager,
                                                  final Object transaction) {
        final LocalTransactionObject txObject = (LocalTransactionObject) transaction;
        final ReactiveLocalSession session;
        session = txObject.getAndClear();
        if (session == null) {
            return Mono.empty();
        }
        final Mono<Void> mono;
        mono = session.close();
        return mono.onErrorMap(this::wrapErrorIfNeed);
    }



    /*################################## blow private method ##################################*/


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
                return Mono.error(new TransactionUsageException("timeout milliseconds greater than Integer.MAX_VALUE"));
            }
            builder.option(Option.TIMEOUT_MILLIS, (int) timeoutMillis);
        }

        final boolean pseudoTransaction;
        pseudoTransaction = readOnly
                && isolationLevel == TransactionDefinition.ISOLATION_DEFAULT
                && this.useReadOnlyTransaction;

        if (pseudoTransaction) {
            builder.option(Option.ISOLATION, Isolation.PSEUDO);
        } else if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
            builder.option(Option.ISOLATION, SpringUtils.toArmyIsolation(isolationLevel));
        }

        return session.startTransaction(builder.build(), HandleMode.ERROR_IF_EXISTS)
                .doOnSuccess(info -> {
                    if (pseudoTransaction) {
                        assert info.isolation() == Isolation.PSEUDO;
                    } else {
                        assert info.inTransaction();
                    }
                    assert info.isReadOnly() == readOnly;
                }).onErrorMap(this::wrapErrorIfNeed)
                .then();
    }


    /**
     * @see #doCommit(TransactionSynchronizationManager, GenericReactiveTransaction)
     * @see #doRollback(TransactionSynchronizationManager, GenericReactiveTransaction)
     */
    private Mono<Void> commitOrRollback(final GenericReactiveTransaction status, final boolean commit) {
        final ReactiveLocalSession session = ((LocalTransactionObject) status.getTransaction()).session;

        final Mono<Void> mono;
        if (session == null) {
            mono = Mono.error(SpringUtils.transactionNoSession());
        } else if (session.hasTransactionInfo()) {
            final Supplier<Mono<ReactiveLocalSession>> supplier;
            if (commit) {
                supplier = session::commit;
            } else {
                supplier = session::rollback;
            }
            mono = Mono.defer(supplier)
                    .onErrorMap(this::wrapErrorIfNeed)
                    .then();
        } else {
            mono = Mono.error(SpringUtils.unexpectedTransactionEnd(session));
        }
        return mono;
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

        @Nullable
        private ReactiveLocalSession getAndClear() {
            return SESSION.getAndSet(this, null);
        }

    } // LocalTransactionObject

}
