package io.army.boot.reactive;

import io.army.lang.Nullable;
import io.army.tx.*;
import io.army.tx.reactive.ReactiveTransaction;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

abstract class AbstractReactiveTransaction extends AbstractGenericTransaction implements ReactiveTransaction {

    private final AtomicBoolean rollbackOnly = new AtomicBoolean(false);

    final AtomicReference<TransactionStatus> status = new AtomicReference<>(TransactionStatus.NOT_ACTIVE);

    AbstractReactiveTransaction(TransactionOption option) {
        super(option);
    }


    @Override
    public final TransactionStatus status() {
        return this.status.get();
    }

    @Override
    public final boolean nonActive() {
        return this.status.get() != TransactionStatus.ACTIVE;
    }

    @Override
    public final boolean transactionEnded() {
        return TransactionStatus.END_STATUS_SET.contains(this.status.get());
    }


    @Override
    public boolean rollbackOnly() {
        return this.rollbackOnly.get();
    }

    @Override
    public final void markRollbackOnly() throws TransactionException {
        TransactionStatus currentStatus = this.status.get();
        if (!TransactionStatus.ROLL_BACK_ONLY_ABLE_SET.contains(currentStatus)) {
            throw new IllegalTransactionStateException("TransactionStatus[%S] can't mark rollback only.", currentStatus);
        }
        if (this.rollbackOnly.compareAndSet(false, true)) {
            this.status.compareAndSet(TransactionStatus.ACTIVE, TransactionStatus.MARKED_ROLLBACK);
        }
    }


    final Mono<Void> checkNonReadOnly(String command) {
        return this.readOnly
                ? Mono.error(new ReadOnlyTransactionException("read only transaction can't %s.", command))
                : Mono.empty();
    }

    @Nullable
    final String obtainSavePointName(Savepoint savepoint) {
        String savePointName;
        try {
            savePointName = savepoint.getSavepointName();
        } catch (SQLException e) {
            savePointName = null;
        }
        return savePointName;
    }


    static io.jdbd.session.Isolation convertToDatabaseIsolation(io.army.tx.Isolation arnyIsolation) {
//        io.jdbd.Isolation databaseOption;
//        switch (arnyIsolation) {
//            case READ_COMMITTED:
//                databaseOption = io.jdbd.Isolation.READ_COMMITTED;
//                break;
//            case REPEATABLE_READ:
//                databaseOption = io.jdbd.Isolation.REPEATABLE_READ;
//                break;
//            case SERIALIZABLE:
//                databaseOption = io.jdbd.Isolation.SERIALIZABLE;
//                break;
//            case DEFAULT:
//                databaseOption = io.jdbd.Isolation.DEFAULT;
//                break;
//            case READ_UNCOMMITTED:
//                databaseOption = io.jdbd.Isolation.READ_UNCOMMITTED;
//                break;
//            default:
//                throw new IllegalArgumentException(String.format("unknown io.army.tx.Isolation[%s]"
//                        , arnyIsolation));
//        }
//        return databaseOption;
        return null;
    }

    static io.army.tx.Isolation convertToArmyIsolation(Isolation databaseIsolation) {
        io.army.tx.Isolation databaseOption;
        switch (databaseIsolation) {
            case READ_COMMITTED:
                databaseOption = io.army.tx.Isolation.READ_COMMITTED;
                break;
            case REPEATABLE_READ:
                databaseOption = io.army.tx.Isolation.REPEATABLE_READ;
                break;
            case SERIALIZABLE:
                databaseOption = io.army.tx.Isolation.SERIALIZABLE;
                break;
            case DEFAULT:
                databaseOption = io.army.tx.Isolation.DEFAULT;
                break;
            case READ_UNCOMMITTED:
                databaseOption = io.army.tx.Isolation.READ_UNCOMMITTED;
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown io.jdbd.Isolation[%s]"
                        , databaseIsolation));
        }
        return databaseOption;
    }

}
