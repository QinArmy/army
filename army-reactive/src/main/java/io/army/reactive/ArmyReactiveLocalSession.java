package io.army.reactive;

import io.army.reactive.executor.LocalStmtExecutor;
import io.army.session.Option;
import io.army.session.SessionException;
import io.army.tx.TransactionInfo;
import io.army.tx.TransactionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * This class is a implementation of {@link ReactiveLocalSession}.
 */
final class ArmyReactiveLocalSession extends ArmyReactiveSession implements ReactiveLocalSession {

    private static final Logger LOG = LoggerFactory.getLogger(ArmyReactiveLocalSession.class);


    private final ArmyReactiveLocalSessionFactory factory;

    private final LocalStmtExecutor stmtExecutor;
    private final AtomicBoolean sessionClosed = new AtomicBoolean(false);

    private final boolean readonly;

    private final AtomicReference<TransactionOption> sessionTransaction = new AtomicReference<>(null);

    ArmyReactiveLocalSession(ArmyReactiveLocalSessionFactory.LocalSessionBuilder builder) {
        super(builder);
        this.factory = builder.sessionFactory;
        this.stmtExecutor = builder.stmtExecutor;
        this.readonly = builder.readOnly;
    }


    @Override
    public ReactiveLocalSessionFactory sessionFactory() {
        return this.factory;
    }


    @Override
    public long sessionIdentifier() throws SessionException {
        return this.stmtExecutor.sessionIdentifier();
    }


    @Override
    public boolean isClosed() {
        // just test sessionClosed
        return this.sessionClosed.get();
    }

    @Override
    public boolean inTransaction() {
        // currently, jdbd-mysql and jdbd-postgre support
        return this.stmtExecutor.inTransaction();
    }

    @Override
    public boolean isReadOnlyStatus() {
        final boolean readOnlyStatus;
        final TransactionOption option;
        if (this.readonly) {
            readOnlyStatus = true;
        } else if ((option = this.sessionTransaction.get()) == null) {
            readOnlyStatus = false;
        } else {
            readOnlyStatus = option.isReadOnly();
        }
        return readOnlyStatus;
    }



    /*-------------------below statement methods -------------------*/


    /*-------------------below local transaction methods -------------------*/

    @Override
    public Mono<TransactionInfo> transactionInfo() {
        return this.stmtExecutor.transactionInfo();
    }

    @Override
    public Mono<ReactiveLocalSession> setTransactionCharacteristics(TransactionOption option) {
        return this.stmtExecutor.setTransactionCharacteristics(option)
                .thenReturn(this);
    }


    @Override
    public Mono<ReactiveLocalSession> startTransaction() {
        return this.startTransaction(TransactionOption.option(null, false));
    }

    @Override
    public Mono<ReactiveLocalSession> startTransaction(final TransactionOption option) {
        return this.stmtExecutor.startTransaction(option)
                .doOnSuccess(v -> this.sessionTransaction.set(option))
                .thenReturn(this);
    }

    @Override
    public Mono<ReactiveLocalSession> commit() {
        return this.commit(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> commit(Function<Option<?>, ?> optionFunc) {
        return this.stmtExecutor.commit(optionFunc)
                .thenReturn(this);
    }

    @Override
    public Mono<ReactiveLocalSession> rollback() {
        return this.rollback(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> rollback(Function<Option<?>, ?> optionFunc) {
        return this.stmtExecutor.rollback(optionFunc)
                .thenReturn(this);
    }

    @Override
    public Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc) {
        return this.stmtExecutor.setSavePoint(optionFunc);
    }

    @Override
    public Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint) {
        return this.releaseSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        return this.stmtExecutor.releaseSavePoint(savepoint, optionFunc)
                .thenReturn(this);
    }

    @Override
    public Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint) {
        return this.rollbackToSavePoint(savepoint, Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public Mono<ReactiveLocalSession> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        return this.stmtExecutor.rollbackToSavePoint(savepoint, optionFunc)
                .thenReturn(this);
    }

    @Override
    public <T> Mono<T> close() {
        return Mono.defer(this::closeSession);
    }

    /*-------------------below package methods -------------------*/

    @Override
    ReactiveOption defaultOption() {
        return null;
    }

    @Override
    protected String transactionName() {
        return "unnamed";
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    /*-------------------below private methods -------------------*/


    /**
     * @see #close()
     */
    private <T> Mono<T> closeSession() {
        Mono<T> mono;
        if (this.sessionClosed.compareAndSet(false, true)) {
            mono = this.stmtExecutor.close();
        } else {
            mono = Mono.empty();
        }
        return mono;
    }


}
