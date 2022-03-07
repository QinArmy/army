package io.army.tx.reactive;


import io.army.reactive.ReactiveSessionFactory;
import io.army.reactive.Session;
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
        Session session = (Session) synchronizationManager.getResource(this.sessionFactory);
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
//        ArmyTransactionObject txObject = (ArmyTransactionObject) status.getTransaction();
//        if (txObject.session == null || !txObject.session.hasTransaction()) {
//            return Mono.error(new NoTransactionException("cannot commit,no transaction"));
//        }
//        return txObject.session
//                .flush()
//                .then(Mono.defer(() -> txObject.session.sessionTransaction().commit()))
//                .onErrorMap((e) -> SpringUtils.convertArmyAccessException((io.army.tx.TransactionException) e))
//                ;
        return Mono.empty();
    }

    @Override
    protected Mono<Void> doRollback(TransactionSynchronizationManager synchronizationManager
            , GenericReactiveTransaction status) throws TransactionException {
//        ArmyTransactionObject txObject = (ArmyTransactionObject) status.getTransaction();
//        if (txObject.session == null || !txObject.session.hasTransaction()) {
//            return Mono.error(new NoTransactionException("cannot rollback,no transaction"));
//        }
//        return txObject.session.sessionTransaction()
//                .rollback()
//                .onErrorMap((e) -> SpringUtils.convertArmyAccessException((io.army.tx.TransactionException) e))
//                ;
        throw new UnsupportedOperationException();
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
        Session session = (Session) suspendedResources;
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


    public ReactiveSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(ReactiveSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Mono<Session> obtainSession(TransactionSynchronizationManager synchronizationManager) {
        Session session = (Session) synchronizationManager.getResource(this.sessionFactory);
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

    private Mono<Void> startSessionTransaction(Session session, Object transaction
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

    private Mono<ReactiveTransaction> createSessionTransaction(Session session
            , TransactionDefinition definition) {
//        return session.builder()
//                .isolation(SpringUtils.convertTotArmyIsolation(definition.getIsolationLevel()))
//                .readOnly(definition.isReadOnly())
//                .timeout(definition.getTimeout())
//                .name(definition.getName())
//                .build();
        return Mono.empty();

    }

    /*################################## blow static inner class ##################################*/

    private static final class ArmyTransactionObject {

        private Session session;


        private Mono<Object> suspend() {
            Session reactiveSession = session;
            if (reactiveSession == null) {
                return Mono.error(new IllegalStateException("ArmyTransactionObject no session."));
            }
            this.session = null;
            return Mono.just(reactiveSession);
        }

        private Mono<Session> reset(Session newSession) {
            if (this.session != null) {
                return Mono.error(new IllegalStateException("ArmyTransactionObject session not null,couldn't reset"));
            }
            this.session = newSession;
            return Mono.just(this.session);
        }
    }

}
