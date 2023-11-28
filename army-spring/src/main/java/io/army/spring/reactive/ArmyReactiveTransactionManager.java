package io.army.spring.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.*;
import org.springframework.transaction.reactive.GenericReactiveTransaction;
import org.springframework.transaction.reactive.TransactionContextManager;
import org.springframework.transaction.reactive.TransactionSynchronization;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

abstract class ArmyReactiveTransactionManager implements ReactiveTransactionManager {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public final Mono<ReactiveTransaction> getReactiveTransaction(final @Nullable TransactionDefinition definition) {

        // Use defaults if no transaction definition given.
        final TransactionDefinition def;
        if (definition == null) {
            def = TransactionDefinition.withDefaults();
        } else {
            def = definition;
        }

        return TransactionSynchronizationManager.forCurrentTransaction()
                .flatMap(manager -> getTransaction(manager, def));
    }


    @Override
    public final Mono<Void> commit(final ReactiveTransaction transaction) {
        return null;
    }

    @Override
    public final Mono<Void> rollback(final ReactiveTransaction transaction) {
        return null;
    }

    /*-------------------below protected template methods -------------------*/

    /**
     * Return whether to use a savepoint for a nested transaction.
     * <p>Default is {@code true}, which causes delegation to DefaultTransactionStatus
     * for creating and holding a savepoint. If the transaction object does not implement
     * the SavepointManager interface, a NestedTransactionNotSupportedException will be
     * thrown. Else, the SavepointManager will be asked to create a new savepoint to
     * demarcate the start of the nested transaction.
     * <p>Subclasses can override this to return {@code false}, causing a further
     * call to {@code doBegin} - within the context of an already existing transaction.
     * The {@code doBegin} implementation needs to handle this accordingly in such
     * a scenario. This is appropriate for JTA, for example.
     *
     * @see DefaultTransactionStatus#createAndHoldSavepoint
     * @see DefaultTransactionStatus#rollbackToHeldSavepoint
     * @see DefaultTransactionStatus#releaseHeldSavepoint
     * @see #doBegin
     */
    protected boolean useSavepointForNestedTransaction() {
        return true;
    }

    protected abstract Object doGetTransaction(TransactionSynchronizationManager manager);

    /**
     * Check if the given transaction object indicates an existing transaction
     * (that is, a transaction which has already started).
     * <p>The result will be evaluated according to the specified propagation
     * behavior for the new transaction. An existing transaction might get
     * suspended (in case of PROPAGATION_REQUIRES_NEW), or the new transaction
     * might participate in the existing one (in case of PROPAGATION_REQUIRED).
     * <p>The default implementation returns {@code false}, assuming that
     * participating in existing transactions is generally not supported.
     * Subclasses are of course encouraged to provide such support.
     *
     * @param transaction the transaction object returned by doGetTransaction
     * @return if there is an existing transaction
     * @see #doGetTransaction
     */
    protected abstract boolean isExistingTransaction(Object transaction);

    /**
     * Begin a new transaction with semantics according to the given transaction
     * definition. Does not have to care about applying the propagation behavior,
     * as this has already been handled by this abstract manager.
     * <p>This method gets called when the transaction manager has decided to actually
     * start a new transaction. Either there wasn't any transaction before, or the
     * previous transaction has been suspended.
     * <p>A special scenario is a nested transaction: This method will be called to
     * start a nested transaction when necessary. In such a context, there will be an
     * active transaction: The implementation of this method has to detect this and
     * start an appropriate nested transaction.
     *
     * @param manager     the synchronization manager bound to the new transaction
     * @param transaction the transaction object returned by {@code doGetTransaction}
     * @param definition  a TransactionDefinition instance, describing propagation
     *                    behavior, isolation level, read-only flag, timeout, and transaction name
     * @throws org.springframework.transaction.NestedTransactionNotSupportedException if the underlying transaction does not support nesting (e.g. through savepoints)
     */
    protected abstract Mono<Void> doBegin(TransactionSynchronizationManager manager,
                                          Object transaction, TransactionDefinition definition);

    /**
     * Perform an actual commit of the given transaction.
     * <p>An implementation does not need to check the "new transaction" flag
     * or the rollback-only flag; this will already have been handled before.
     * Usually, a straight commit will be performed on the transaction object
     * contained in the passed-in status.
     *
     * @param manager the synchronization manager bound to the current transaction
     * @param status  the status representation of the transaction
     * @see GenericReactiveTransaction#getTransaction
     */
    protected abstract Mono<Void> doCommit(TransactionSynchronizationManager manager,
                                           GenericReactiveTransaction status);

    /**
     * Perform an actual rollback of the given transaction.
     * <p>An implementation does not need to check the "new transaction" flag;
     * this will already have been handled before. Usually, a straight rollback
     * will be performed on the transaction object contained in the passed-in status.
     *
     * @param manager the synchronization manager bound to the current transaction
     * @param status  the status representation of the transaction
     * @see GenericReactiveTransaction#getTransaction
     */
    protected abstract Mono<Void> doRollback(TransactionSynchronizationManager manager,
                                             GenericReactiveTransaction status);

    /**
     * Set the given transaction rollback-only. Only called on rollback
     * if the current transaction participates in an existing one.
     * <p>The default implementation throws an IllegalTransactionStateException,
     * assuming that participating in existing transactions is generally not
     * supported. Subclasses are of course encouraged to provide such support.
     *
     * @param manager the synchronization manager bound to the current transaction
     * @param status  the status representation of the transaction
     */
    protected Mono<Void> doSetRollbackOnly(TransactionSynchronizationManager manager,
                                           GenericReactiveTransaction status) {
        final String m;
        m = "Participating in existing transactions is not supported - when 'isExistingTransaction' " +
                "returns true, appropriate 'doSetRollbackOnly' behavior must be provided";
        throw new IllegalTransactionStateException(m);
    }

    /**
     * Suspend the resources of the current transaction.
     * Transaction synchronization will already have been suspended.
     * <p>The default implementation throws a TransactionSuspensionNotSupportedException,
     * assuming that transaction suspension is generally not supported.
     *
     * @param manager     the synchronization manager bound to the current transaction
     * @param transaction the transaction object returned by {@code doGetTransaction}
     * @return an object that holds suspended resources
     * (will be kept unexamined for passing it into doResume)
     * @throws org.springframework.transaction.TransactionSuspensionNotSupportedException if suspending is not supported by the transaction manager implementation
     * @see #doResume
     */
    protected Mono<Object> doSuspend(TransactionSynchronizationManager manager,
                                     Object transaction) {

        throw new TransactionSuspensionNotSupportedException(
                "Transaction manager [" + getClass().getName() + "] does not support transaction suspension");
    }

    /**
     * Resume the resources of the current transaction.
     * Transaction synchronization will be resumed afterwards.
     * <p>The default implementation throws a TransactionSuspensionNotSupportedException,
     * assuming that transaction suspension is generally not supported.
     *
     * @param manager            the synchronization manager bound to the current transaction
     * @param transaction        the transaction object returned by {@code doGetTransaction}
     * @param suspendedResources the object that holds suspended resources,
     *                           as returned by doSuspend
     * @throws org.springframework.transaction.TransactionSuspensionNotSupportedException if suspending is not supported by the transaction manager implementation
     * @see #doSuspend
     */
    protected Mono<Void> doResume(TransactionSynchronizationManager manager,
                                  @Nullable Object transaction, Object suspendedResources) {

        throw new TransactionSuspensionNotSupportedException(
                "Transaction manager [" + getClass().getName() + "] does not support transaction suspension");
    }

    /**
     * Make preparations for commit, to be performed before the
     * {@code beforeCommit} synchronization callbacks occur.
     * <p>Note that exceptions will get propagated to the commit caller
     * and cause a rollback of the transaction.
     *
     * @param manager the synchronization manager bound to the current transaction
     * @param status  the status representation of the transaction
     * @throws RuntimeException in case of errors; will be <b>propagated to the caller</b>
     *                          (note: do not throw TransactionException subclasses here!)
     */
    protected Mono<Void> prepareForCommit(TransactionSynchronizationManager manager,
                                          GenericReactiveTransaction status) {

        return Mono.empty();
    }


    /**
     * Register the given list of transaction synchronizations with the existing transaction.
     * <p>Invoked when the control of the Spring transaction manager and thus all Spring
     * transaction synchronizations end, without the transaction being completed yet. This
     * is for example the case when participating in an existing JTA or EJB CMT transaction.
     * <p>The default implementation simply invokes the {@code afterCompletion} methods
     * immediately, passing in "STATUS_UNKNOWN". This is the best we can do if there's no
     * chance to determine the actual outcome of the outer transaction.
     *
     * @param manager          the synchronization manager bound to the current transaction
     * @param transaction      the transaction object returned by {@code doGetTransaction}
     * @param synchronizations a List of TransactionSynchronization objects
     * @see #invokeAfterCompletion(TransactionSynchronizationManager, List, int)
     * @see TransactionSynchronization#afterCompletion(int)
     * @see TransactionSynchronization#STATUS_UNKNOWN
     */
    protected Mono<Void> registerAfterCompletionWithExistingTransaction(TransactionSynchronizationManager manager,
                                                                        Object transaction, List<TransactionSynchronization> synchronizations) {

        log.debug("Cannot register Spring after-completion synchronization with existing transaction - " +
                "processing Spring after-completion callbacks immediately, with outcome status 'unknown'");
        return invokeAfterCompletion(manager, synchronizations, TransactionSynchronization.STATUS_UNKNOWN);
    }

    /**
     * Cleanup resources after transaction completion.
     * <p>Called after {@code doCommit} and {@code doRollback} execution,
     * on any outcome. The default implementation does nothing.
     * <p>Should not throw any exceptions but just issue warnings on errors.
     *
     * @param manager     the synchronization manager bound to the current transaction
     * @param transaction the transaction object returned by {@code doGetTransaction}
     */
    protected Mono<Void> doCleanupAfterCompletion(TransactionSynchronizationManager manager, Object transaction) {

        return Mono.empty();
    }



    /*-------------------below private methods -------------------*/

    /**
     * @see #getReactiveTransaction(TransactionDefinition)
     */
    private Mono<ReactiveTransaction> getTransaction(final TransactionSynchronizationManager manager,
                                                     final TransactionDefinition def) {
        final Object transaction;
        transaction = doGetTransaction(manager);

        final Mono<ReactiveTransaction> mono;
        if (isExistingTransaction(transaction)) {
            mono = handleExistingTransaction(manager, def, transaction);
        } else if (def.getTimeout() < TransactionDefinition.TIMEOUT_DEFAULT) {  // Check definition settings for new transaction.
            mono = Mono.error(new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout()));
        } else switch (def.getPropagationBehavior()) {
            case TransactionDefinition.PROPAGATION_MANDATORY: {
                String m = "No existing transaction found for transaction marked with propagation 'mandatory'";
                mono = Mono.error(new IllegalTransactionStateException(m));
            }
            break;
            case TransactionDefinition.PROPAGATION_REQUIRED:
            case TransactionDefinition.PROPAGATION_REQUIRES_NEW:
            case TransactionDefinition.PROPAGATION_NESTED:
                mono = TransactionContextManager.currentContext()
                        .map(TransactionSynchronizationManager::new)
                        .flatMap(nestedManager -> startNewTransaction(nestedManager, def, transaction));
                break;
            default: {
                // Create "empty" transaction: no actual transaction, but potentially synchronization.
                if (log.isWarnEnabled() && def.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
                    log.warn("Custom isolation level specified but no actual transaction initiated;isolation level will effectively be ignored: {}", def);
                }
                mono = Mono.just(prepareReactiveTransaction(manager, def, null, true, log.isDebugEnabled(), null));

            } // default

        } // switch
        return mono;
    }

    /**
     * @see #getTransaction(TransactionSynchronizationManager, TransactionDefinition)
     */
    private Mono<ReactiveTransaction> handleExistingTransaction(final TransactionSynchronizationManager manager,
                                                                final TransactionDefinition def,
                                                                final Object transaction) {
        final Logger log = this.log;
        final boolean debugEnabled = log.isDebugEnabled();

        final Mono<ReactiveTransaction> mono;
        switch (def.getPropagationBehavior()) {
            case TransactionDefinition.PROPAGATION_NEVER: {
                String m = "Existing transaction found for transaction marked with propagation 'never'";
                mono = Mono.error(new IllegalTransactionStateException(m));
            }
            break;
            case TransactionDefinition.PROPAGATION_NOT_SUPPORTED: {
                if (debugEnabled) {
                    log.debug("Suspending current transaction");
                }
                mono = suspend(manager, transaction)
                        .map(suspendedResources -> prepareReactiveTransaction(manager, def, null, false, debugEnabled,
                                suspendedResources)
                        )
                        .switchIfEmpty(Mono.fromSupplier(() -> prepareReactiveTransaction(manager, def, null, false,
                                debugEnabled, null))
                        )
                        .cast(ReactiveTransaction.class);
            }
            break;
            case TransactionDefinition.PROPAGATION_REQUIRES_NEW: {
                if (debugEnabled) {
                    log.debug("Suspending current transaction, creating new transaction with name [{}]", def.getName());
                }
                mono = suspend(manager, transaction)
                        .flatMap(suspendedResourcesHolder -> {
                            final GenericReactiveTransaction status;
                            status = newReactiveTransaction(manager, def, transaction, true, debugEnabled,
                                    suspendedResourcesHolder);
                            return doBegin(manager, transaction, def).doOnSuccess(ignore ->
                                            prepareSynchronization(manager, status, def)).thenReturn(status)
                                    .onErrorResume(ErrorPredicates.RUNTIME_OR_ERROR, beginEx ->
                                            resumeAfterBeginException(manager, transaction, suspendedResourcesHolder, beginEx)
                                                    .then(Mono.error(beginEx))
                                    );
                        });
            }
            break;
            case TransactionDefinition.PROPAGATION_NESTED: {
                if (debugEnabled) {
                    log.debug("Creating nested transaction with name [{}]", def.getName());
                }

                final GenericReactiveTransaction status;
                status = newReactiveTransaction(manager, def, transaction, true, debugEnabled, null);

                if (useSavepointForNestedTransaction()) {
                    // TODO
                    throw new UnsupportedOperationException();
                } else {
                    mono = doBegin(manager, transaction, def)
                            .doOnSuccess(ignore -> prepareSynchronization(manager, status, def))
                            .thenReturn(status);
                }
            }
            break;
            default:
                throw new UnsupportedOperationException();
        }
        return mono;
    }


    /**
     * @see #getTransaction(TransactionSynchronizationManager, TransactionDefinition)
     */
    private Mono<ReactiveTransaction> startNewTransaction(final TransactionSynchronizationManager manager,
                                                          final TransactionDefinition def,
                                                          final Object transaction) {
        final boolean debugEnabled = log.isDebugEnabled();
        return suspend(manager, null)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(suspendedResources -> {
                    if (debugEnabled) {
                        log.debug("Creating new transaction with name [{}]: {}", def.getName(), def);
                    }


                    return Mono.defer(() -> {
                        final GenericReactiveTransaction status;
                        status = newReactiveTransaction(manager, def, transaction, true, debugEnabled,
                                suspendedResources.orElse(null)
                        );
                        return doBegin(manager, transaction, def)
                                .doOnSuccess(ignore -> prepareSynchronization(manager, status, def))
                                .thenReturn(status);
                    }).onErrorResume(ErrorPredicates.RUNTIME_OR_ERROR,
                            ex -> resume(manager, null, suspendedResources.orElse(null))
                                    .then(Mono.error(ex)));
                });
    }


    private GenericReactiveTransaction prepareReactiveTransaction(final TransactionSynchronizationManager manager,
                                                                  final TransactionDefinition def,
                                                                  final @Nullable Object transaction,
                                                                  final boolean newTransaction,
                                                                  final boolean debugEnabled,
                                                                  final @Nullable Object suspendedResources) {
        throw new UnsupportedOperationException();

    }

    /**
     * Suspend the given transaction. Suspends transaction synchronization first,
     * then delegates to the {@code doSuspend} template method.
     *
     * @param manager     the synchronization manager bound to the current transaction
     * @param transaction the current transaction object
     *                    (or {@code null} to just suspend active synchronizations, if any)
     * @return an object that holds suspended resources
     * (or {@code null} if neither transaction nor synchronization active)
     * @see #doSuspend
     * @see #resume
     */
    private Mono<SuspendedResourcesHolder> suspend(final TransactionSynchronizationManager manager,
                                                   final @Nullable Object transaction) {
        return Mono.empty();
    }


    /**
     * Resume the given transaction. Delegates to the {@code doResume}
     * template method first, then resuming transaction synchronization.
     *
     * @param manager         the synchronization manager bound to the current transaction
     * @param transaction     the current transaction object
     * @param resourcesHolder the object that holds suspended resources,
     *                        as returned by {@code suspend} (or {@code null} to just
     *                        resume synchronizations, if any)
     * @see #doResume
     * @see #suspend
     */
    private Mono<Void> resume(final TransactionSynchronizationManager manager,
                              final @Nullable Object transaction, final @Nullable SuspendedResourcesHolder resourcesHolder) {

        Mono<Void> resume = Mono.empty();

        if (resourcesHolder != null) {
            Object suspendedResources = resourcesHolder.suspendedResources;
            if (suspendedResources != null) {
                resume = doResume(manager, transaction, suspendedResources);
            }
            List<TransactionSynchronization> suspendedSynchronizations = resourcesHolder.suspendedSynchronizations;
            if (suspendedSynchronizations != null) {
                manager.setActualTransactionActive(resourcesHolder.wasActive);
                manager.setCurrentTransactionIsolationLevel(resourcesHolder.isolationLevel);
                manager.setCurrentTransactionReadOnly(resourcesHolder.readOnly);
                manager.setCurrentTransactionName(resourcesHolder.name);
                return resume.then(doResumeSynchronization(manager, suspendedSynchronizations));
            }
        }

        return resume;
    }

    /**
     * Reactivate transaction synchronization for the current transaction context
     * and resume all given synchronizations.
     *
     * @param manager                   the synchronization manager bound to the current transaction
     * @param suspendedSynchronizations a List of TransactionSynchronization objects
     */
    private Mono<Void> doResumeSynchronization(TransactionSynchronizationManager manager,
                                               List<TransactionSynchronization> suspendedSynchronizations) {

        manager.initSynchronization();
        return Flux.fromIterable(suspendedSynchronizations)
                .concatMap(synchronization -> synchronization.resume()
                        .doOnSuccess(ignore -> manager.registerSynchronization(synchronization))).then();
    }

    /**
     * Resume outer transaction after inner transaction begin failed.
     *
     * @see #handleExistingTransaction(TransactionSynchronizationManager, TransactionDefinition, Object)
     */
    private Mono<Void> resumeAfterBeginException(TransactionSynchronizationManager manager, Object transaction,
                                                 @Nullable SuspendedResourcesHolder suspendedResources, Throwable beginEx) {

        String exMessage = "Inner transaction begin exception overridden by outer transaction resume exception";
        return resume(manager, transaction, suspendedResources).doOnError(ErrorPredicates.RUNTIME_OR_ERROR,
                ex -> log.error(exMessage, beginEx));
    }


    /**
     * Initialize transaction synchronization as appropriate.
     */
    private void prepareSynchronization(final TransactionSynchronizationManager manager,
                                        final GenericReactiveTransaction status, final TransactionDefinition def) {

        if (status.isNewSynchronization()) {
            manager.setActualTransactionActive(status.hasTransaction());
            manager.setCurrentTransactionIsolationLevel(
                    def.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT ?
                            def.getIsolationLevel() : null);
            manager.setCurrentTransactionReadOnly(def.isReadOnly());
            manager.setCurrentTransactionName(def.getName());
            manager.initSynchronization();
        }
    }

    /**
     * Create a ReactiveTransaction instance for the given arguments.
     */
    private GenericReactiveTransaction newReactiveTransaction(
            TransactionSynchronizationManager synchronizationManager, TransactionDefinition definition,
            @Nullable Object transaction, boolean newTransaction, boolean debug, @Nullable Object suspendedResources) {

        return new GenericReactiveTransaction(transaction, newTransaction,
                !synchronizationManager.isSynchronizationActive(),
                definition.isReadOnly(), debug, suspendedResources);
    }

    /**
     * Actually invoke the {@code afterCompletion} methods of the
     * given TransactionSynchronization objects.
     * <p>To be called by this abstract manager itself, or by special implementations
     * of the {@code registerAfterCompletionWithExistingTransaction} callback.
     *
     * @param manager          the synchronization manager bound to the current transaction
     * @param synchronizations a List of TransactionSynchronization objects
     * @param completionStatus the completion status according to the
     *                         constants in the TransactionSynchronization interface
     * @see #registerAfterCompletionWithExistingTransaction(TransactionSynchronizationManager, Object, List)
     * @see TransactionSynchronization#STATUS_COMMITTED
     * @see TransactionSynchronization#STATUS_ROLLED_BACK
     * @see TransactionSynchronization#STATUS_UNKNOWN
     */
    private Mono<Void> invokeAfterCompletion(TransactionSynchronizationManager manager,
                                             List<TransactionSynchronization> synchronizations, int completionStatus) {

        // return TransactionSynchronizationUtils.invokeAfterCompletion(synchronizations, completionStatus);
        throw new UnsupportedOperationException();
    }


    /**
     * Holder for suspended resources.
     * Used internally by {@code suspend} and {@code resume}.
     */
    protected static final class SuspendedResourcesHolder {

        private final Object suspendedResources;

        private List<TransactionSynchronization> suspendedSynchronizations;

        private String name;

        private boolean readOnly;

        private Integer isolationLevel;

        private boolean wasActive;

        private SuspendedResourcesHolder(@Nullable Object suspendedResources) {
            this.suspendedResources = suspendedResources;
        }


    } // SuspendedResourcesHolder


    /**
     * Predicates for exception types that transactional error handling applies to.
     */
    private enum ErrorPredicates implements Predicate<Throwable> {

        /**
         * Predicate matching {@link RuntimeException} or {@link Error}.
         */
        RUNTIME_OR_ERROR {
            @Override
            public boolean test(Throwable throwable) {
                return throwable instanceof RuntimeException || throwable instanceof Error;
            }
        },

        /**
         * Predicate matching {@link TransactionException}.
         */
        TRANSACTION_EXCEPTION {
            @Override
            public boolean test(Throwable throwable) {
                return throwable instanceof TransactionException;
            }
        },

        /**
         * Predicate matching {@link UnexpectedRollbackException}.
         */
        UNEXPECTED_ROLLBACK {
            @Override
            public boolean test(Throwable throwable) {
                return throwable instanceof UnexpectedRollbackException;
            }
        };

        @Override
        public abstract boolean test(Throwable throwable);
    }

}
