package io.army.boot.reactive;

import io.army.DomainUpdateException;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerMultiDML;
import io.army.criteria.impl.inner.InnerSQL;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.criteria.impl.inner.InnerSingleDML;
import io.army.dialect.Dialect;
import io.army.meta.TableMeta;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;
import io.army.tx.GenericTransaction;
import io.army.tx.Isolation;
import io.army.wrapper.SQLWrapper;
import io.jdbd.StatelessDatabaseSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

abstract class AbstractGenericReactiveRmSession<S extends StatelessDatabaseSession, F extends InnerGenericRmSessionFactory>
        extends AbstractGenericReactiveSession implements InnerGenericRmSession {

    final F sessionFactory;

    final S databaseSession;

    final Dialect dialect;

    final ReactiveSelectSQLExecutor selectSQLExecutor;

    final ReactiveInsertSQLExecutor insertSQLExecutor;

    final ReactiveUpdateSQLExecutor updateSQLExecutor;

    AbstractGenericReactiveRmSession(F sessionFactory, S databaseSession, boolean readOnly) {
        super(sessionFactory, readOnly);
        this.sessionFactory = sessionFactory;
        this.databaseSession = databaseSession;

        this.dialect = sessionFactory.dialect();
        this.selectSQLExecutor = this.sessionFactory.selectSQLExecutor();
        this.insertSQLExecutor = this.sessionFactory.insertSQLExecutor();
        this.updateSQLExecutor = this.sessionFactory.updateSQLExecutor();
    }


    @Override
    public final <R> Flux<R> select(Select select, Class<R> resultClass, final Visible visible) {
        // 1. assert session active
        return this.assertSessionActive(false)
                // 2. parse select
                .then(Mono.defer(() -> Mono.just(this.dialect.select(select, visible))))
                // 3. execute select sql
                .flatMapMany(sqlWrapper -> this.selectSQLExecutor.select(this, sqlWrapper, resultClass))
                // if error convert exception for application developer
                .onErrorMap(this::composedExceptionFunction);
    }

    @Override
    public final <R> Flux<Optional<R>> selectOptional(Select select, Class<R> columnClass, Visible visible) {
        List<SelectPart> selectPartList = ((InnerSelect) select).selectPartList();
        if (selectPartList.size() != 1 || !(selectPartList.get(0) instanceof Selection)) {
            return Flux.error(new IllegalArgumentException(
                    "select isn't single column query,please use select method."));
        }
        // 1. assert session active
        return this.assertSessionActive(false)
                // 2. parse select
                .then(Mono.defer(() -> Mono.just(this.dialect.select(select, visible))))
                // 3. execute select sql
                .flatMapMany(sqlWrapper -> this.selectSQLExecutor.selectOptional(this, sqlWrapper, columnClass))
                // if error convert exception for application developer
                .onErrorMap(this::composedExceptionFunction);
    }


    @Override
    public final <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass, final Visible visible) {
        // 1. assert session active
        return this.assertSessionActive(true)
                //2.invoke insert before advice
                .then(Mono.defer(() -> this.invokeInsertBeforeAdvice(insert)))
                //3. parse returning insert sql
                .then(Mono.defer(() -> assertChildDomain(this.dialect.returningInsert(insert, visible))))
                //4. execute returning insert
                .flatMapMany(sqlWrapper -> this.insertSQLExecutor.returningInsert(this, sqlWrapper, resultClass))
                // if upstream error, execute insert throws advice
                .onErrorResume(ex -> this.invokeInsertThrowsAdvice(insert, ex))
                //5. execute insert after advice (concat empty)
                .concatWith(Mono.defer(() -> this.invokeInsertAfterAdvice(insert)))
                //6. clear insert
                .doOnTerminate(((InnerSQL) insert)::clear)
                // if error convert exception for application developer
                .onErrorMap(this::composedExceptionFunction);

    }


    @Override
    public final Mono<Integer> subQueryInsert(Insert insert, final Visible visible) {
        return internalSubQuery(insert, visible, Integer.class);
    }

    @Override
    public final Mono<Long> largeSubQueryInsert(Insert insert, Visible visible) {
        return internalSubQuery(insert, visible, Long.class);
    }

    @Override
    public final Mono<Integer> update(Update update, Visible visible) {
        return this.internalUpdate(update, visible, Integer.class);
    }

    @Override
    public final Mono<Long> largeUpdate(Update update, Visible visible) {
        return this.internalUpdate(update, visible, Long.class);
    }

    @Override
    public final <R> Flux<R> returningUpdate(Update update, Class<R> resultClass, Visible visible) {
        // 1. assert session active
        return this.assertSessionActive(true)
                //2.invoke update before advice
                .then(Mono.defer(() -> this.invokeUpdateBeforeAdvice(update)))
                //3. parse returning update sql
                .then(Mono.defer(() -> assertChildDomain(this.dialect.returningUpdate(update, visible))))
                //4. execute returning update
                .flatMapMany(sqlWrapper -> this.updateSQLExecutor.returningUpdate(this, sqlWrapper, resultClass))
                // if upstream error, execute update throws advice
                .onErrorResume(ex -> this.invokeUpdateThrowsAdvice(update, ex))
                //5. execute update after advice (concat empty)
                .concatWith(Mono.defer(() -> this.invokeUpdateAfterAdvice(update)))
                //6. clear update
                .doOnTerminate(((InnerSQL) update)::clear)
                // if error convert exception for application developer
                .onErrorMap(this::composedExceptionFunction);
    }

    @Override
    public final Mono<Integer> delete(Delete delete, Visible visible) {
        return this.internalDelete(delete, visible, Integer.class);
    }

    @Override
    public final Mono<Long> largeDelete(Delete delete, Visible visible) {
        return this.internalDelete(delete, visible, Long.class);
    }

    @Override
    public final <R> Flux<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible) {
        // 1. assert session active
        return this.assertSessionActive(true)
                //2.invoke delete before advice
                .then(Mono.defer(() -> this.invokeDeleteBeforeAdvice(delete)))
                //3. parse returning delete sql
                .then(Mono.defer(() -> assertChildDomain(this.dialect.returningDelete(delete, visible))))
                //4. execute returning delete
                .flatMapMany(sqlWrapper -> this.updateSQLExecutor.returningUpdate(this, sqlWrapper, resultClass))
                // if upstream error, execute delete throws advice
                .onErrorResume(ex -> this.invokeDeleteThrowsAdvice(delete, ex))
                //5. execute delete after advice (concat empty)
                .concatWith(Mono.defer(() -> this.invokeDeleteAfterAdvice(delete)))
                //6. clear delete
                .doOnTerminate(((InnerSQL) delete)::clear)
                // if error convert exception for application developer
                .onErrorMap(this::composedExceptionFunction);
    }

    /*################################## blow package method ##################################*/


    final Mono<Void> invokeInsertBeforeAdvice(Insert insert) {
        return invokeDomainAdvice(insert, this::doInvokeInsertBeforeAdvice);
    }

    final <R> Mono<R> invokeInsertAfterAdvice(Insert insert) {
        return invokeDomainAdvice(insert, this::doInvokeInsertAfterAdvice)
                .then(Mono.empty());
    }

    final <R> Mono<R> invokeInsertThrowsAdvice(Insert insert, Throwable ex) {
        return invokeDomainThrowsAdvice(insert, ex, this::doInvokeInsertThrowsAdvice)
                // must return ex
                .then(Mono.error(ex))
                ;
    }

    final Mono<Void> invokeUpdateBeforeAdvice(Update update) {
        return invokeDomainAdvice(update, this::doInvokeUpdateBeforeAdvice);
    }

    final <R> Mono<R> invokeUpdateAfterAdvice(Update update) {
        return invokeDomainAdvice(update, this::doInvokeUpdateAfterAdvice)
                .then(Mono.empty())
                ;
    }

    final <R> Mono<R> invokeUpdateThrowsAdvice(Update update, Throwable ex) {
        return invokeDomainThrowsAdvice(update, ex, this::doInvokeUpdateThrowsAdvice)
                // must return ex
                .then(Mono.error(ex))
                ;
    }

    final Mono<Void> invokeDeleteBeforeAdvice(Delete delete) {
        return invokeDomainAdvice(delete, this::doInvokeDeleteBeforeAdvice);
    }

    final <R> Mono<R> invokeDeleteAfterAdvice(Delete delete) {
        return invokeDomainAdvice(delete, this::doInvokeDeleteAfterAdvice)
                .then(Mono.empty())
                ;
    }

    final <R> Mono<R> invokeDeleteThrowsAdvice(Delete delete, Throwable ex) {
        return invokeDomainThrowsAdvice(delete, ex, this::doInvokeDeleteThrowsAdvice)
                // must return ex
                .then(Mono.error(ex))
                ;
    }

    final Mono<SQLWrapper> assertChildDomain(SQLWrapper sqlWrapper) {
        GenericTransaction tx = obtainTransaction();
        if (tx == null || tx.isolation().level < Isolation.READ_COMMITTED.level) {
            return Mono.error(new DomainUpdateException("Child domain update must in READ_COMMITTED(+) transaction."));
        }
        return Mono.just(sqlWrapper);
    }



    /*################################## blow private method ##################################*/

    /**
     * @param resultClass {@link Integer} or {@link Long}
     * @see ReactiveInsertSQLExecutor#subQueryInsert(InnerGenericRmSession, SQLWrapper, Class)
     */
    private <N extends Number> Mono<N> internalSubQuery(Insert insert, Visible visible, Class<N> resultClass) {
        // 1. assert session active
        return this.assertSessionActive(true)
                //2.invoke insert before advice
                .then(Mono.defer(() -> this.invokeInsertBeforeAdvice(insert)))
                //3. parse sub query insert sql
                .then(Mono.defer(() -> assertChildDomain(this.dialect.subQueryInsert(insert, visible))))
                //4. execute sub query insert sql
                .flatMap(sqlWrapper -> this.insertSQLExecutor.subQueryInsert(this, sqlWrapper, resultClass))
                // if upstream error, execute insert throws advice
                .onErrorResume(ex -> this.invokeInsertThrowsAdvice(insert, ex))
                //5. execute insert after advice (concat empty)
                .concatWith(Mono.defer(() -> this.invokeInsertAfterAdvice(insert)))
                .elementAt(0)
                //6. clear insert
                .doOnTerminate(((InnerSQL) insert)::clear)
                // if error convert exception for application developer
                .onErrorMap(this::composedExceptionFunction);
    }

    /**
     * @param resultClass {@link Integer} or {@link Long}
     * @see ReactiveUpdateSQLExecutor#update(InnerGenericRmSession, SQLWrapper, Class)
     */
    private <N extends Number> Mono<N> internalUpdate(Update update, final Visible visible, Class<N> resultClass) {
        // 1. assert session active
        return this.assertSessionActive(true)
                //2.invoke update before advice
                .then(Mono.defer(() -> this.invokeUpdateBeforeAdvice(update)))
                //3. parse update sql
                .then(Mono.defer(() -> assertChildDomain(this.dialect.update(update, visible))))
                //4. execute update sql
                .flatMap(sqlWrapper -> this.updateSQLExecutor.update(this, sqlWrapper, resultClass))
                // if upstream error, execute update throws advice
                .onErrorResume(ex -> this.invokeUpdateThrowsAdvice(update, ex))
                //5. execute update after advice (concat empty)
                .concatWith(Mono.defer(() -> this.invokeUpdateAfterAdvice(update)))
                .elementAt(0)
                //6. clear update
                .doOnTerminate(((InnerSQL) update)::clear)
                // if error convert exception for application developer
                .onErrorMap(this::composedExceptionFunction);
    }

    /**
     * @param resultClass {@link Integer} or {@link Long}
     * @see ReactiveUpdateSQLExecutor#update(InnerGenericRmSession, SQLWrapper, Class)
     */
    private <N extends Number> Mono<N> internalDelete(Delete delete, final Visible visible, Class<N> resultClass) {
        // 1. assert session active
        return this.assertSessionActive(true)
                //2.invoke delete before advice
                .then(Mono.defer(() -> this.invokeDeleteBeforeAdvice(delete)))
                //3. parse delete sql
                .then(Mono.defer(() -> assertChildDomain(this.dialect.delete(delete, visible))))
                //4. execute delete sql
                .flatMap(sqlWrapper -> this.updateSQLExecutor.update(this, sqlWrapper, resultClass))
                // if upstream error, execute delete throws advice
                .onErrorResume(ex -> this.invokeDeleteThrowsAdvice(delete, ex))
                //5. execute delete after advice (concat empty)
                .concatWith(Mono.defer(() -> this.invokeDeleteAfterAdvice(delete)))
                .elementAt(0)
                //6. clear delete
                .doOnTerminate(((InnerSQL) delete)::clear)
                // if error convert exception for application developer
                .onErrorMap(this::composedExceptionFunction);
    }


    private Mono<Void> invokeDomainAdvice(SQLStatement sqlStatement
            , Function<TableMeta<?>, Mono<Void>> function) {
        if (!(this.sessionFactory instanceof InnerReactiveApiSessionFactory)) {
            return Mono.empty();
        }
        Mono<Void> mono;
        if (sqlStatement instanceof InnerSingleDML) {
            mono = doInvokeInsertBeforeAdvice(((InnerSingleDML) sqlStatement).tableMeta());
        } else if (sqlStatement instanceof InnerMultiDML) {
            // 1. iterate tableWrapperList
            mono = Flux.fromIterable(((InnerMultiDML) sqlStatement).tableWrapperList())
                    // 2. filter TableMeta
                    .filter(tableWrapper -> tableWrapper.tableAble() instanceof TableMeta)
                    // map tableWrapper to table meta
                    .map(tableWrapper -> (TableMeta<?>) tableWrapper.tableAble())
                    // 3. invoke before domain insert
                    .flatMap(function)
                    .then();

        } else {
            return Mono.error(new ArmyCriteriaException("unknown insert[%s] criteria.", sqlStatement));
        }
        return mono;
    }

    private Mono<Void> invokeDomainThrowsAdvice(SQLStatement sqlStatement
            , Throwable ex, BiFunction<TableMeta<?>, Throwable, Mono<Void>> function) {
        if (!(this.sessionFactory instanceof InnerReactiveApiSessionFactory)) {
            return Mono.empty();
        }
        Mono<Void> mono;
        if (sqlStatement instanceof InnerSingleDML) {
            mono = doInvokeInsertBeforeAdvice(((InnerSingleDML) sqlStatement).tableMeta());
        } else if (sqlStatement instanceof InnerMultiDML) {
            // 1. iterate tableWrapperList
            mono = Flux.fromIterable(((InnerMultiDML) sqlStatement).tableWrapperList())
                    // 2. filter TableMeta
                    .filter(tableWrapper -> tableWrapper.tableAble() instanceof TableMeta)
                    // map tableWrapper to table meta
                    .map(tableWrapper -> (TableMeta<?>) tableWrapper.tableAble())
                    // 3. invoke before domain insert
                    .flatMap(tableMeta -> function.apply(tableMeta, ex))
                    .then();

        } else {
            return Mono.error(new ArmyCriteriaException("unknown insert[%s] criteria.", sqlStatement));
        }
        return mono;
    }

    private Mono<Void> doInvokeInsertBeforeAdvice(TableMeta<?> tableMeta) {
        InnerReactiveApiSessionFactory sessionFactory = ((InnerReactiveApiSessionFactory) this.sessionFactory);
        ReactiveDomainInsertAdvice domainInsertAdvice = sessionFactory.domainInsertAdviceComposite(tableMeta);
        return domainInsertAdvice == null
                ? Mono.empty()
                : domainInsertAdvice.beforeInsert(tableMeta, sessionFactory.proxySession());
    }

    private Mono<Void> doInvokeInsertAfterAdvice(TableMeta<?> tableMeta) {
        InnerReactiveApiSessionFactory sessionFactory = ((InnerReactiveApiSessionFactory) this.sessionFactory);
        ReactiveDomainInsertAdvice domainInsertAdvice = sessionFactory.domainInsertAdviceComposite(tableMeta);
        return domainInsertAdvice == null
                ? Mono.empty()
                : domainInsertAdvice.afterInsert(tableMeta, sessionFactory.proxySession());

    }

    private Mono<Void> doInvokeInsertThrowsAdvice(TableMeta<?> tableMeta, Throwable ex) {
        InnerReactiveApiSessionFactory sessionFactory = ((InnerReactiveApiSessionFactory) this.sessionFactory);
        ReactiveDomainInsertAdvice domainInsertAdvice = sessionFactory.domainInsertAdviceComposite(tableMeta);
        return domainInsertAdvice == null
                ? Mono.empty()
                : domainInsertAdvice.InsertThrows(tableMeta, sessionFactory.proxySession(), ex);

    }


    private Mono<Void> doInvokeUpdateBeforeAdvice(TableMeta<?> tableMeta) {
        InnerReactiveApiSessionFactory sessionFactory = ((InnerReactiveApiSessionFactory) this.sessionFactory);
        ReactiveDomainUpdateAdvice domainUpdateAdvice = sessionFactory.domainUpdateAdviceComposite(tableMeta);
        return domainUpdateAdvice == null
                ? Mono.empty()
                : domainUpdateAdvice.beforeUpdate(tableMeta, sessionFactory.proxySession());
    }

    private Mono<Void> doInvokeUpdateAfterAdvice(TableMeta<?> tableMeta) {
        InnerReactiveApiSessionFactory sessionFactory = ((InnerReactiveApiSessionFactory) this.sessionFactory);
        ReactiveDomainUpdateAdvice domainUpdateAdvice = sessionFactory.domainUpdateAdviceComposite(tableMeta);
        return domainUpdateAdvice == null
                ? Mono.empty()
                : domainUpdateAdvice.afterUpdate(tableMeta, sessionFactory.proxySession());

    }

    private Mono<Void> doInvokeUpdateThrowsAdvice(TableMeta<?> tableMeta, Throwable ex) {
        InnerReactiveApiSessionFactory sessionFactory = ((InnerReactiveApiSessionFactory) this.sessionFactory);
        ReactiveDomainUpdateAdvice domainUpdateAdvice = sessionFactory.domainUpdateAdviceComposite(tableMeta);
        return domainUpdateAdvice == null
                ? Mono.empty()
                : domainUpdateAdvice.updateThrows(tableMeta, sessionFactory.proxySession(), ex);

    }

    private Mono<Void> doInvokeDeleteBeforeAdvice(TableMeta<?> tableMeta) {
        InnerReactiveApiSessionFactory sessionFactory = ((InnerReactiveApiSessionFactory) this.sessionFactory);
        ReactiveDomainDeleteAdvice domainDeleteAdvice = sessionFactory.domainDeleteAdviceComposite(tableMeta);
        return domainDeleteAdvice == null
                ? Mono.empty()
                : domainDeleteAdvice.beforeDelete(tableMeta, sessionFactory.proxySession());
    }

    private Mono<Void> doInvokeDeleteAfterAdvice(TableMeta<?> tableMeta) {
        InnerReactiveApiSessionFactory sessionFactory = ((InnerReactiveApiSessionFactory) this.sessionFactory);
        ReactiveDomainDeleteAdvice domainDeleteAdvice = sessionFactory.domainDeleteAdviceComposite(tableMeta);
        return domainDeleteAdvice == null
                ? Mono.empty()
                : domainDeleteAdvice.afterDelete(tableMeta, sessionFactory.proxySession());

    }

    private Mono<Void> doInvokeDeleteThrowsAdvice(TableMeta<?> tableMeta, Throwable ex) {
        InnerReactiveApiSessionFactory sessionFactory = ((InnerReactiveApiSessionFactory) this.sessionFactory);
        ReactiveDomainDeleteAdvice domainDeleteAdvice = sessionFactory.domainDeleteAdviceComposite(tableMeta);
        return domainDeleteAdvice == null
                ? Mono.empty()
                : domainDeleteAdvice.deleteThrows(tableMeta, sessionFactory.proxySession(), ex);

    }

    private Throwable composedExceptionFunction(Throwable ex) {
        return this.sessionFactory instanceof InnerReactiveApiSessionFactory
                ? ((InnerReactiveApiSessionFactory) this.sessionFactory).composeExceptionFunction().apply(ex)
                : ex;
    }
}
