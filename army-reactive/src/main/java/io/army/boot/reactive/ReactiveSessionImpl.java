package io.army.boot.reactive;

import io.army.SessionException;
import io.army.SessionUsageException;
import io.army.ShardingMode;
import io.army.cache.UniqueKey;
import io.army.context.spi.ReactiveCurrentSessionContext;
import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerBatchDML;
import io.army.criteria.impl.inner.InnerSQL;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.reactive.ReactiveSession;
import io.army.reactive.ReactiveSessionFactory;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.reactive.GenericReactiveTransaction;
import io.army.tx.reactive.ReactiveTransaction;
import io.army.wrapper.SQLWrapper;
import io.jdbd.DatabaseSession;
import io.jdbd.PreparedStatement;
import io.jdbd.ReactiveSQLException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is a implementation of {@link ReactiveSession}.
 */
final class ReactiveSessionImpl extends AbstractGenericReactiveRmSession<DatabaseSession, InnerReactiveSessionFactory>
        implements InnerReactiveSession {

    private final DatabaseSession databaseSession;

    private final ReactiveCurrentSessionContext currentSessionContext;

    private final AtomicBoolean sessionClosed = new AtomicBoolean(false);

    private final boolean current;

    private final AtomicReference<ReactiveTransaction> sessionTransaction = new AtomicReference<>(null);

    ReactiveSessionImpl(InnerReactiveSessionFactory sessionFactory
            , DatabaseSession databaseSession, boolean readOnly, boolean current) {
        super(sessionFactory, databaseSession, readOnly);
        this.databaseSession = databaseSession;
        this.currentSessionContext = sessionFactory.currentSessionContext();
        this.current = current;
    }


    @Override
    public final <R extends IDomain> Mono<R> get(TableMeta<R> tableMeta, Object id) {
        //1. try obtain domain from cache
        return this.tryObtainCatch(tableMeta, id)
                // 2. if empty ,execute select and catch result domain
                .switchIfEmpty(Mono.defer(() -> doGet(tableMeta, id)))
                // if error convert exception for application developer
                .onErrorMap(this.sessionFactory.composeExceptionFunction())
                ;
    }

    @Override
    public final <R extends IDomain> Mono<R> getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        final UniqueKey uniqueKey = new UniqueKey(propNameList, valueList);
        //1. try obtain domain from cache
        return this.tryObtainCatch(tableMeta, uniqueKey)
                // 2. if empty ,execute select and catch result domain
                .switchIfEmpty(Mono.defer(() -> doGetByUnique(tableMeta, propNameList, valueList, uniqueKey)))
                // if error convert exception for application developer
                .onErrorMap(this.sessionFactory.composeExceptionFunction())
                ;
    }

    @Override
    public Mono<Void> valueInsert(Insert insert) {
        return this.valueInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public Mono<Void> valueInsert(Insert insert, final Visible visible) {
        //1.assert
        return this.assertForValueInsert(insert)
                //2.invoke insert before advice
                .then(Mono.defer(() -> this.invokeInsertBeforeAdvice(insert)))
                //3. parse value insert sql
                .thenMany(Flux.defer(() -> Flux.fromIterable(this.dialect.valueInsert(insert, null, visible))))
                // assert for child domain
                .flatMap(this::assertChildDomain)
                //4. execute value insert
                .flatMap(sqlWrapper -> this.insertSQLExecutor.valueInsert(this, sqlWrapper))
                // if upstream error, execute insert throws advice
                .onErrorResume(ex -> this.invokeInsertThrowsAdvice(insert, ex))
                //5. execute insert after advice (concat empty)
                .concatWith(Mono.defer(() -> this.invokeInsertAfterAdvice(insert)))
                //6. clear insert
                .doOnTerminate(((InnerSQL) insert)::clear)
                // if error convert exception for application developer
                .onErrorMap(this.sessionFactory.composeExceptionFunction())
                .then();
    }

    @Override
    public Flux<Integer> batchUpdate(Update update) {
        return this.batchUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public Flux<Integer> batchUpdate(Update update, final Visible visible) {
        return this.internalBatchUpdate(update, visible, Integer.class);
    }

    @Override
    public Flux<Long> batchLargeUpdate(Update update) {
        return this.batchLargeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public Flux<Long> batchLargeUpdate(Update update, final Visible visible) {
        return this.internalBatchUpdate(update, visible, Long.class);
    }

    @Override
    public Flux<Integer> batchDelete(Delete delete) {
        return this.batchDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public Flux<Integer> batchDelete(Delete delete, Visible visible) {
        return this.internalBatchDelete(delete, visible, Integer.class);
    }

    @Override
    public Flux<Long> batchLargeDelete(Delete delete) {
        return this.batchLargeDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public Flux<Long> batchLargeDelete(Delete delete, Visible visible) {
        return this.internalBatchDelete(delete, visible, Long.class);
    }

    @Override
    public ReactiveTransaction sessionTransaction() throws NoSessionTransactionException {
        ReactiveTransaction transaction = this.sessionTransaction.get();
        if (transaction == null) {
            throw new NoSessionTransactionException("%s not session transaction.", this);
        }
        return transaction;
    }

    @Override
    public Mono<Void> close() throws SessionException {
        return Mono.just(sessionClosed.get())
                // if closed is true ,then return empty.
                .filter(closed -> !closed)
                // 1. assert transaction end
                .flatMap(notClosed -> this.assertTransactionEnd())
                // 2. remove current session if need
                .then(Mono.defer(() -> this.currentSessionContext.removeCurrentSession(this.current, this)))
                //3. close database session
                .then(Mono.defer(this.databaseSession::close))
                //4. set session close status
                .then(Mono.defer(() -> Mono.just(this.sessionClosed.compareAndSet(false, true))))
                // if error convert exception for application developer
                .onErrorMap(this.sessionFactory.composeExceptionFunction())
                .then()
                ;
    }

    @Override
    public SessionTransactionBuilder builder() {
        return null;
    }

    @Override
    public Mono<Void> flush() {
        return this.internalFlush();
    }

    @Override
    public ReactiveSessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public boolean closed() {
        return this.sessionClosed.get();
    }

    @Override
    public boolean hasTransaction() {
        return this.sessionTransaction.get() != null;
    }

    /*################################## blow package method ##################################*/

    @Nullable
    @Override
    ReactiveTransaction obtainTransaction() {
        return this.sessionTransaction.get();
    }

    /*################################## blow InnerGenericRmSession method ##################################*/

    @Override
    public Mono<PreparedStatement> createPreparedStatement(String sql) throws ReactiveSQLException {
        return this.databaseSession.prepareStatement(sql);
    }

    @Override
    public Mono<Void> closeTransaction(GenericReactiveTransaction transaction) {
        ReactiveTransaction sessionTransaction = this.sessionTransaction.get();
        Mono<Void> mono;
        if (sessionTransaction == null) {
            mono = Mono.empty();
        } else if (transaction == sessionTransaction) {
            if (sessionTransaction.transactionEnded()) {
                this.sessionTransaction.compareAndSet(sessionTransaction, null);
                mono = Mono.empty();
            } else {
                mono = Mono.error(new SessionUsageException("%s not end,can't close.", transaction));
            }
        } else {
            mono = Mono.error(new SessionUsageException("%s and %s not match.", transaction, this));
        }
        return mono;
    }

    @Override
    public DatabaseSession databaseSession(ReactiveLocalTransaction sessionTransaction) {
        if (sessionTransaction != this.sessionTransaction.get()) {
            throw new IllegalArgumentException("sessionTransaction not match.");
        }
        return this.databaseSession;
    }

    /*################################## blow private method ##################################*/

    /**
     * @param resultClass {@link Integer} or {@link Long}
     * @see ReactiveUpdateSQLExecutor#batchUpdate(InnerGenericRmSession, SQLWrapper, Class)
     */
    private <N extends Number> Flux<N> internalBatchUpdate(Update update, final Visible visible, Class<N> resultClass) {
        // 1. assert session active
        return this.assertForBatch()
                //2.invoke update before advice
                .then(Mono.defer(() -> this.invokeUpdateBeforeAdvice(update)))
                //3. parse update sql
                .then(Mono.defer(() -> assertChildDomain(this.dialect.update(update, visible))))
                //4. execute update sql
                .flatMapMany(sqlWrapper -> this.updateSQLExecutor.batchUpdate(this, sqlWrapper, resultClass))
                // if upstream error, execute update throws advice
                .onErrorResume(ex -> this.invokeUpdateThrowsAdvice(update, ex))
                //5. execute update after advice (concat empty)
                .concatWith(Mono.defer(() -> this.invokeUpdateAfterAdvice(update)))
                //6. clear update
                .doOnTerminate(((InnerSQL) update)::clear)
                // if error convert exception for application developer
                .onErrorMap(this.sessionFactory.composeExceptionFunction());
    }

    /**
     * @param resultClass {@link Integer} or {@link Long}
     * @see ReactiveUpdateSQLExecutor#batchUpdate(InnerGenericRmSession, SQLWrapper, Class)
     */
    private <N extends Number> Flux<N> internalBatchDelete(Delete delete, final Visible visible, Class<N> resultClass) {
        // 1. assert session active
        return this.assertForBatch()
                //2.invoke delete before advice
                .then(Mono.defer(() -> this.invokeDeleteBeforeAdvice(delete)))
                //3. parse delete sql
                .then(Mono.defer(() -> assertChildDomain(this.dialect.delete(delete, visible))))
                //4. execute delete sql
                .flatMapMany(sqlWrapper -> this.updateSQLExecutor.batchUpdate(this, sqlWrapper, resultClass))
                // if upstream error, execute delete throws advice
                .onErrorResume(ex -> this.invokeDeleteThrowsAdvice(delete, ex))
                //5. execute delete after advice (concat empty)
                .concatWith(Mono.defer(() -> this.invokeDeleteAfterAdvice(delete)))
                //6. clear delete
                .doOnTerminate(((InnerSQL) delete)::clear)
                // if error convert exception for application developer
                .onErrorMap(this.sessionFactory.composeExceptionFunction());
    }


    private Mono<Void> assertForBatch() {
        return this.sessionFactory.shardingMode() == ShardingMode.NO_SHARDING
                ? this.assertSessionActive(true)
                : Mono.error(new SessionUsageException("not support batch operation in SHARDING mode."));
    }

    private Mono<Void> assertForValueInsert(Insert insert) {
        return insert instanceof InnerBatchDML
                ? assertForBatch()
                : this.assertSessionActive(true);
    }

    /*################################## blow private instance inner class ##################################*/


}
