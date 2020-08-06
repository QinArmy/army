package io.army.tx.reactive;


import io.army.SessionException;
import io.army.reactive.ReactiveSession;
import io.army.reactive.ReactiveSessionFactory;
import io.army.tx.sync.SpringTxUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.reactive.AbstractReactiveTransactionManager;
import org.springframework.transaction.reactive.GenericReactiveTransaction;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

public class ArmyReactiveTransactionManager extends AbstractReactiveTransactionManager {

    private ReactiveSessionFactory sessionFactory;


    @Override
    protected Object doGetTransaction(TransactionSynchronizationManager synchronizationManager)
            throws TransactionException {
        ArmyTransactionObject txObject = new ArmyTransactionObject();
        ReactiveSession session = (ReactiveSession) synchronizationManager.getResource(this.sessionFactory);
        if (session != null) {
            txObject.reset(session);
        }
        return txObject;
    }

    @Override
    protected Mono<Void> doBegin(TransactionSynchronizationManager synchronizationManager, final Object transaction
            , TransactionDefinition definition) throws TransactionException {
        return obtainSession(synchronizationManager)
                .flatMap(session -> this.startSessionTransaction(session, transaction, definition));
    }


    @Override
    protected Mono<Void> doCommit(TransactionSynchronizationManager synchronizationManager
            , GenericReactiveTransaction status) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) status.getTransaction();
        if (txObject.session == null || !txObject.session.hasTransaction()) {
            return Mono.error(new NoTransactionException("cannot commit,no transaction"));
        }
        return txObject.session
                .flush()
                .then(Mono.defer(() -> txObject.session.sessionTransaction().commit()))
                .onErrorMap((e) -> SpringTxUtils.convertArmyAccessException((io.army.tx.TransactionException) e))
                ;
    }

    @Override
    protected Mono<Void> doRollback(TransactionSynchronizationManager synchronizationManager
            , GenericReactiveTransaction status) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) status.getTransaction();
        if (txObject.session == null || !txObject.session.hasTransaction()) {
            return Mono.error(new NoTransactionException("cannot rollback,no transaction"));
        }
        return txObject.session.sessionTransaction()
                .rollback()
                .onErrorMap((e) -> SpringTxUtils.convertArmyAccessException((io.army.tx.TransactionException) e))
                ;
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        return txObject.session != null
                && txObject.session.hasTransaction();
    }

    @Override
    protected Mono<Object> doSuspend(TransactionSynchronizationManager synchronizationManager, Object transaction)
            throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        ReactiveSessionFactory sessionFactory = this.sessionFactory;
        synchronizationManager.unbindResource(sessionFactory);
        return txObject.suspend();
    }

    @Override
    protected Mono<Void> doResume(TransactionSynchronizationManager synchronizationManager
            , @Nullable Object transaction, Object suspendedResources) throws TransactionException {
        ReactiveSessionFactory sessionFactory = this.sessionFactory;
        if (synchronizationManager.hasResource(sessionFactory)) {
            synchronizationManager.unbindResource(sessionFactory);
        }
        ReactiveSession session = (ReactiveSession) suspendedResources;
        synchronizationManager.bindResource(session, session);
        return Mono.empty();
    }

    @Override
    protected Mono<Void> doSetRollbackOnly(TransactionSynchronizationManager synchronizationManager
            , GenericReactiveTransaction status) throws TransactionException {
        ArmyTransactionObject txObject = (ArmyTransactionObject) status.getTransaction();
        if (txObject.session == null || !txObject.session.hasTransaction()) {
            return Mono.error(new NoTransactionException("cannot rollbackOnly,no transaction"));
        }
        txObject.session.sessionTransaction()
                .markRollbackOnly();
        return Mono.empty();
    }

    @Override
    protected Mono<Void> doCleanupAfterCompletion(TransactionSynchronizationManager synchronizationManager
            , Object transaction) {
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        ReactiveSessionFactory sessionFactory = this.sessionFactory;
        if (synchronizationManager.hasResource(sessionFactory)) {
            synchronizationManager.unbindResource(sessionFactory);
        }
        return txObject.session
                .close()
                .onErrorMap(e -> SpringTxUtils.convertArmyAccessException((SessionException) e))
                ;

    }

    public ReactiveSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(ReactiveSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Mono<ReactiveSession> obtainSession(TransactionSynchronizationManager synchronizationManager) {
        ReactiveSession session = (ReactiveSession) synchronizationManager.getResource(this.sessionFactory);
        if (session != null) {
            return Mono.just(session);
        }
        return this.sessionFactory.builder()
                .build();
    }

    /*################################## blow private method ##################################*/

    private IllegalTransactionStateException existsTransactionException() {
        return new IllegalTransactionStateException(
                "create transaction occur error,session haven transaction,suspend transaction not unbind.");
    }

    private Mono<Void> startSessionTransaction(ReactiveSession session, Object transaction
            , TransactionDefinition definition) {
        if (session.hasTransaction()) {
            return Mono.error(this::existsTransactionException);
        }
        ArmyTransactionObject txObject = (ArmyTransactionObject) transaction;
        return txObject.reset(session)
                .flatMap(sessionAfterRest -> this.createSessionTransaction(sessionAfterRest, definition))
                .flatMap(ReactiveTransaction::start)
                ;

    }

    private Mono<ReactiveTransaction> createSessionTransaction(ReactiveSession session
            , TransactionDefinition definition) {
        return session.builder()
                .isolation(SpringTxUtils.convertTotArmyIsolation(definition.getIsolationLevel()))
                .readOnly(definition.isReadOnly())
                .timeout(definition.getTimeout())
                .name(definition.getName())
                .build();

    }

    /*################################## blow static inner class ##################################*/

    private static final class ArmyTransactionObject {

        private ReactiveSession session;


        private Mono<Object> suspend() {
            ReactiveSession reactiveSession = session;
            if (reactiveSession == null) {
                return Mono.error(new IllegalStateException("ArmyTransactionObject no session."));
            }
            this.session = null;
            return Mono.just(reactiveSession);
        }

        private Mono<ReactiveSession> reset(ReactiveSession newSession) {
            if (this.session != null) {
                return Mono.error(new IllegalStateException("ArmyTransactionObject session not null,couldn't reset"));
            }
            this.session = newSession;
            return Mono.just(this.session);
        }
    }

}
