package io.army.spring.reactive;


import io.army.reactive.ReactiveLocalSession;
import io.army.reactive.ReactiveSessionContext;
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

    private final ReactiveSessionContext sessionContext;

    private boolean pseudoTransactionAllowed;

    private boolean useTransactionName;

    private boolean useTransactionLabel;

    private boolean useDataSourceTimeout;

    private boolean useDatabaseSessionName;


    /**
     * private constructor
     */
    private ArmyReactiveLocalTransactionManager(ReactiveSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.sessionContext = SpringReactiveSessionContext.create(sessionFactory);
    }


    public ReactiveSessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public ReactiveSessionContext getSessionContext() {
        return this.sessionContext;
    }


    public boolean isPseudoTransactionAllowed() {
        return pseudoTransactionAllowed;
    }

    public ArmyReactiveLocalTransactionManager setPseudoTransactionAllowed(boolean pseudoTransactionAllowed) {
        this.pseudoTransactionAllowed = pseudoTransactionAllowed;
        return this;
    }

    public boolean isUseTransactionName() {
        return useTransactionName;
    }

    public ArmyReactiveLocalTransactionManager setUseTransactionName(boolean useTransactionName) {
        this.useTransactionName = useTransactionName;
        return this;
    }

    public boolean isUseTransactionLabel() {
        return useTransactionLabel;
    }

    public ArmyReactiveLocalTransactionManager setUseTransactionLabel(boolean useTransactionLabel) {
        this.useTransactionLabel = useTransactionLabel;
        return this;
    }

    public boolean isUseDataSourceTimeout() {
        return useDataSourceTimeout;
    }

    public ArmyReactiveLocalTransactionManager setUseDataSourceTimeout(boolean useDataSourceTimeout) {
        this.useDataSourceTimeout = useDataSourceTimeout;
        return this;
    }

    public boolean isUseDatabaseSessionName() {
        return useDatabaseSessionName;
    }

    public ArmyReactiveLocalTransactionManager setUseDatabaseSessionName(boolean useDatabaseSessionName) {
        this.useDatabaseSessionName = useDatabaseSessionName;
        return this;
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


        if (currentSession != null && currentSession.hasTransactionInfo()) {
            String m = String.format("%s don't support %s.%s", getClass().getName(),
                    Propagation.class.getName(), Propagation.NESTED.name());
            return Mono.error(new TransactionUsageException(m));
        }

        Mono<Void> mono;

        try {
            final String txLabel;
            txLabel = definition.getName();

            final int timeoutMillis;
            timeoutMillis = timeoutMillis(definition.getTimeout());

            if (currentSession == null) {
                final ReactiveSessionFactory sessionFactory = this.sessionFactory;

                mono = sessionFactory.localBuilder()
                        .name(txLabel)
                        .readonly(definition.isReadOnly())
                        .dataSourceOption(Option.NAME, this.useDatabaseSessionName ? txLabel : null)
                        .dataSourceOption(Option.TIMEOUT_MILLIS, (timeoutMillis > 0 && this.useDataSourceTimeout) ? timeoutMillis : null)
                        .build()
                        .flatMap(session -> {
                            txObject.reset(session);
                            manager.bindResource(sessionFactory, session);
                            return startTransaction(session, definition, timeoutMillis);
                        });
            } else {
                mono = Mono.defer(() -> startTransaction(currentSession, definition, timeoutMillis));
            }

            mono = mono.onErrorMap(this::wrapErrorIfNeed);
        } catch (Throwable e) {
            mono = Mono.error(wrapErrorIfNeed(e));
        }
        return mono;
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

    private int timeoutMillis(final int timeoutSeconds) {

        final long timeoutMillis;
        if (timeoutSeconds == TransactionDefinition.TIMEOUT_DEFAULT) {
            timeoutMillis = 0L;
        } else if (timeoutSeconds > 0) {
            timeoutMillis = timeoutSeconds * 1000L;
        } else {
            timeoutMillis = 0L;
        }

        if (timeoutMillis > Integer.MAX_VALUE) {
            throw new TransactionUsageException("timeout milliseconds greater than Integer.MAX_VALUE");
        }
        return (int) timeoutMillis;
    }


    /**
     * @see #doBegin(TransactionSynchronizationManager, Object, TransactionDefinition)
     */
    private Mono<Void> startTransaction(final ReactiveLocalSession session, final TransactionDefinition definition, final int timeoutMillis) {

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
            if (this.useTransactionLabel) {
                builder.option(Option.LABEL, txLabel);
            }
            if (this.useTransactionName) {
                builder.option(Option.NAME, txLabel);
            }
        }

        if (timeoutMillis > 0L) {
            builder.option(Option.TIMEOUT_MILLIS, timeoutMillis);
        }

        final boolean pseudoTransaction;
        pseudoTransaction = readOnly
                && isolationLevel == TransactionDefinition.ISOLATION_DEFAULT
                && this.pseudoTransactionAllowed;

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
