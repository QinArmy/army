package io.army.boot.reactive;

import io.army.reactive.ReactiveSession;
import io.army.tx.IllegalTransactionStateException;
import io.army.tx.TransactionException;
import io.army.tx.TransactionOption;
import io.army.tx.TransactionStatus;
import io.army.tx.reactive.ReactiveTransaction;
import reactor.core.publisher.Mono;

import java.sql.Savepoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

final class ReactiveLocalTransaction extends AbstractReactiveTransaction implements ReactiveTransaction {

    private static final String SAVE_POINT_PREFIX = "army_reactive_";

    private final InnerTransactionSession session;

    private final AtomicInteger savePointCounter = new AtomicInteger(0);

    private final ConcurrentMap<String, Savepoint> savePointMap = new ConcurrentHashMap<>();


    ReactiveLocalTransaction(InnerTransactionSession session, TransactionOption option) {
        super(option);
        this.session = session;
    }

    @Override
    public ReactiveSession session() {
        return this.session;
    }

    @Override
    public Mono<Void> start() throws TransactionException {
        TransactionStatus currentStatus = this.status.get();
        if (currentStatus != TransactionStatus.NOT_ACTIVE) {
            return Mono.error(new IllegalTransactionStateException(
                    "transaction status[%s] isn't %s,can't start transaction."
                    , currentStatus, TransactionStatus.NOT_ACTIVE));
        }
        final io.jdbd.TransactionOption txOption = io.jdbd.TransactionOption.build(
                convertToDatabaseIsolation(this.isolation), this.readOnly);
        return this.session.databaseSession(this)
                .startTransaction(txOption)
                .then(Mono.defer(() -> setStatus(TransactionStatus.NOT_ACTIVE, TransactionStatus.ACTIVE)))
                ;
    }

    @Override
    public Mono<Savepoint> createSavepoint() throws TransactionException {
        TransactionStatus currentStatus = this.status.get();
        if (!TransactionStatus.SAVE_POINT_ABLE_SET.contains(currentStatus)) {
            return Mono.error(new IllegalTransactionStateException(
                    "transaction status[%s] isn't %s,can't create save point."
                    , currentStatus, TransactionStatus.ACTIVE));
        }
        final String savePointName = SAVE_POINT_PREFIX + this.savePointCounter.addAndGet(1);
        return checkNonReadOnly("createSavepoint")
                // set save point by database session
                .then(Mono.defer(() -> this.session.databaseSession(this).setSavepoint(savePointName)))
                // record save point
                .doOnNext(savepoint -> this.savePointMap.put(savePointName, savepoint))
                .cast(Savepoint.class)
                ;

    }

    @Override
    public Mono<Void> rollbackToSavepoint(Savepoint savepoint) throws TransactionException {
        TransactionStatus currentStatus = this.status.get();
        if (!TransactionStatus.SAVE_POINT_ABLE_SET.contains(currentStatus)) {
            return Mono.error(new IllegalTransactionStateException(
                    "transaction status[%s] isn't %s,can't rollback save point."
                    , currentStatus, TransactionStatus.ACTIVE));
        }

        final String savePointName = obtainSavePointName(savepoint);
        if (savePointName == null || !this.savePointMap.containsKey(savePointName)) {
            return Mono.error(new IllegalArgumentException("savepoint not match"));
        }
        return this.checkNonReadOnly("rollbackToSavepoint")
                // rollback save point by database session
                .then(Mono.defer(() -> this.session.databaseSession(this).rollbackToSavePoint(savepoint)));
    }

    @Override
    public Mono<Void> releaseSavepoint(Savepoint savepoint) throws TransactionException {
        TransactionStatus currentStatus = this.status.get();
        if (!TransactionStatus.SAVE_POINT_ABLE_SET.contains(currentStatus)) {
            return Mono.error(new IllegalTransactionStateException(
                    "transaction status[%s] isn't %s,can't release save point."
                    , currentStatus, TransactionStatus.ACTIVE));
        }

        final String savePointName = obtainSavePointName(savepoint);
        if (savePointName == null || !this.savePointMap.containsKey(savePointName)) {
            return Mono.error(new IllegalArgumentException("savepoint not match"));
        }
        return this.checkNonReadOnly("releaseSavepoint")
                // rollback save point by database session
                .then(Mono.defer(() -> this.session.databaseSession(this).releaseSavePoint(savepoint)))
                .doOnSuccess(v -> this.savePointMap.remove(savePointName))
                ;
    }

    @Override
    public Mono<Void> commit() throws TransactionException {
        TransactionStatus currentStatus = this.status.get();
        if (currentStatus != TransactionStatus.ACTIVE) {
            return Mono.error(new IllegalTransactionStateException(
                    "transaction status[%s] isn't %s,can't commit."
                    , currentStatus, TransactionStatus.ACTIVE));
        }
        return this.checkNonReadOnly("commit")
                .then(Mono.defer(() -> this.setStatus(TransactionStatus.ACTIVE, TransactionStatus.COMMITTING)))
                // commit by database session
                .then(Mono.defer(() -> this.session.databaseSession(this).commit()))
                // if error ,modify status to FAILED_COMMIT
                .doOnError(e -> this.setStatus(TransactionStatus.COMMITTING, TransactionStatus.FAILED_COMMIT))
                // if complete ,modify status to COMMITTED
                .then(Mono.defer(() -> this.setStatus(TransactionStatus.COMMITTING, TransactionStatus.COMMITTED)));
    }

    @Override
    public Mono<Void> rollback() throws TransactionException {
        final TransactionStatus currentStatus = this.status.get();
        if (!TransactionStatus.ROLL_BACK_ABLE_SET.contains(currentStatus)) {
            return Mono.error(new IllegalTransactionStateException(
                    "transaction status[%s] isn't %s,can't rollback."
                    , currentStatus, TransactionStatus.ROLL_BACK_ABLE_SET));
        }
        // 1. assert non read only
        return this.checkNonReadOnly("rollback")
                // 2. modify status to ROLLING_BACK
                .then(Mono.defer(() -> this.setStatus(currentStatus, TransactionStatus.ROLLING_BACK)))
                //3. rollback by database session
                .then(Mono.defer(() -> this.session.databaseSession(this).rollback()))
                // if error ,modify status to FAILED_ROLLBACK
                .doOnError(e -> this.setStatus(TransactionStatus.ROLLING_BACK, TransactionStatus.FAILED_ROLLBACK))
                //4. if complete ,modify status to ROLLED_BACK
                .then(Mono.defer(() -> this.setStatus(TransactionStatus.ROLLING_BACK, TransactionStatus.ROLLED_BACK)));
    }

    @Override
    public Mono<Void> flush() throws TransactionException {
        return this.session.flush();
    }


    @Override
    public Mono<Void> close() throws TransactionException {
        return this.session.closeTransaction(this);
    }

    /*################################## blow private method ##################################*/

    private Mono<Void> setStatus(TransactionStatus except, TransactionStatus update) {
        return this.status.compareAndSet(except, update)
                ? Mono.empty()
                : Mono.error(new IllegalTransactionStateException("except %s but %s ", except, this.status.get()));
    }
}
