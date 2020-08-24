package io.army.boot.reactive;

import io.army.AbstractGenericSession;
import io.army.NonUniqueException;
import io.army.ReadOnlySessionException;
import io.army.SessionUsageException;
import io.army.cache.DomainUpdateAdvice;
import io.army.cache.SessionCache;
import io.army.cache.UniqueKey;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.reactive.GenericReactiveSession;
import io.army.reactive.GenericReactiveSessionFactory;
import io.army.tx.GenericTransaction;
import io.army.tx.TransactionNotCloseException;
import io.army.tx.reactive.GenericReactiveTransaction;
import io.army.util.CriteriaUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

abstract class AbstractGenericReactiveSession extends AbstractGenericSession implements GenericReactiveSession {

    static final Function<Throwable, ? extends Throwable> DEFAULT_EXCEPTION_FUNCTION = throwable -> throwable;

    final boolean readOnly;

    final SessionCache sessionCache;

    AbstractGenericReactiveSession(GenericReactiveSessionFactory sessionFactory, boolean readOnly) {
        this.readOnly = readOnly;
        if (sessionFactory instanceof InnerReactiveApiSessionFactory
                && sessionFactory.supportSessionCache()) {
            this.sessionCache = ((InnerReactiveApiSessionFactory) sessionFactory)
                    .sessionCacheFactory().createSessionCache(this);
        } else {
            this.sessionCache = null;
        }
    }

    @Override
    public final boolean readonly() {
        GenericTransaction tx = obtainTransaction();
        return this.readOnly || (tx != null && tx.readOnly());
    }

    @Override
    public final <R> Mono<R> selectOne(Select select, Class<R> resultClass) {
        return this.selectOne(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Mono<R> selectOne(Select select, Class<R> resultClass, final Visible visible) {
        return this.select(select, resultClass, visible)
                .take(2L)
                .collectList()
                .flatMap(this::mapMono)
                ;
    }


    @Override
    public final Mono<Map<String, Object>> selectOneAsUnmodifiableMap(Select select) {
        return this.selectOneAsUnmodifiableMap(select, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Map<String, Object>> selectOneAsUnmodifiableMap(Select select, final Visible visible) {
        return this.selectOne(select, Map.class, visible)
                .map(this::castMap)
                ;
    }

    @Override
    public final <R> Flux<R> select(Select select, Class<R> resultClass) {
        return this.select(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Map<String, Object>> selectAsUnmodifiableMap(Select select) {
        return this.selectAsUnmodifiableMap(select, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Flux<Map<String, Object>> selectAsUnmodifiableMap(Select select, final Visible visible) {
        return this.select(select, Map.class, visible)
                .map(this::castMap)
                ;
    }


    @Override
    public final <R> Flux<R> returningInsert(Insert insert, Class<R> resultClass) {
        return this.returningInsert(insert, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Integer> subQueryInsert(Insert insert) {
        return this.subQueryInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Long> largeSubQueryInsert(Insert insert) {
        return this.largeSubQueryInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Integer> update(Update update) {
        return this.update(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Long> largeUpdate(Update update) {
        return this.largeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Flux<R> returningUpdate(Update update, Class<R> resultClass) {
        return this.returningUpdate(update, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Integer> delete(Delete delete) {
        return this.delete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Mono<Long> largeDelete(Delete delete) {
        return this.largeDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Flux<R> returningDelete(Delete delete, Class<R> resultClass) {
        return this.returningDelete(delete, resultClass, Visible.ONLY_VISIBLE);
    }

    /*################################## blow package method ##################################*/

    @Nullable
    abstract GenericReactiveTransaction obtainTransaction();

    /*################################## blow private method for api ##################################*/

    final <R extends IDomain> Mono<R> doGet(TableMeta<R> tableMeta, Object id) {
        // 1. parse  sql
        Select select = CriteriaUtils.createSelectDomainById(tableMeta, id);
        // 2. execute sql
        return this.selectOne(select, tableMeta.javaType())
                // 3. cache domain by id
                .flatMap(domain -> this.cacheDomainById(tableMeta, domain))
                ;
    }

    final <R extends IDomain> Mono<R> doGetByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, UniqueKey uniqueKey) {
        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainByUnique(tableMeta, propNameList, valueList);
        // 2. execute sql
        return this.selectOne(select, tableMeta.javaType())
                // 3. cache domain by unique
                .flatMap(domain -> this.cacheDomainByUnique(tableMeta, domain, uniqueKey))
                ;
    }

    final <R extends IDomain> Mono<R> cacheDomainById(TableMeta<R> tableMeta, R domain) {
        Mono<R> mono;
        if (this.sessionCache == null) {
            mono = Mono.just(domain);
        } else {
            mono = Mono.just(this.sessionCache.cacheDomainById(tableMeta, domain));
        }
        return mono;
    }

    private <R extends IDomain> Mono<R> cacheDomainByUnique(TableMeta<R> tableMeta, R domain, UniqueKey uniqueKey) {
        Mono<R> mono;
        if (this.sessionCache == null) {
            mono = Mono.just(domain);
        } else {
            mono = Mono.just(this.sessionCache.cacheDomainByUnique(tableMeta, domain, uniqueKey));
        }
        return mono;
    }


    final <R extends IDomain> Mono<R> tryObtainCatch(TableMeta<R> tableMeta, Object idOrUnique) {
        Mono<R> mono;
        if (this.sessionCache == null) {
            mono = Mono.empty();
        } else {
            R catchValue = this.sessionCache.getDomain(tableMeta, idOrUnique);
            mono = Mono.justOrEmpty(catchValue);
        }
        return mono;
    }

    final Mono<Void> assertSessionActive(final boolean write) {
        Mono<Void> mono = null;
        GenericTransaction tx = obtainTransaction();
        if (this.closed() || (tx != null && tx.nonActive())) {
            String txName = this.sessionTransaction().name();
            mono = Mono.error(new SessionUsageException("TmSession[%s] closed or Transaction[%s] not active."
                    , txName, txName));
        }
        if (write && this.readonly()) {
            mono = Mono.error(new ReadOnlySessionException("%s read only"));
        }
        if (mono == null) {
            mono = Mono.empty();
        }
        return mono;
    }

    final Mono<Void> internalFlush() {
        GenericReactiveSessionFactory sessionFactory = sessionFactory();
        if (!(sessionFactory instanceof InnerReactiveApiSessionFactory)) {
            return Mono.error(new IllegalStateException("not api session"));
        }
        return Mono.justOrEmpty(this.sessionCache)
                // 1. if this.sessionCache not null, assert session active
                .map(sessionCache -> assertSessionActive(true))
                // 2. iterate advices
                .thenMany(Flux.defer(() -> Flux.fromIterable(this.sessionCache.updateAdvices())))
                .filter(DomainUpdateAdvice::hasUpdate)
                // 3. only has updated domain, execute update
                .flatMap(this::doCacheDomainUpdate)
                // if error convert exception for application developer
                .onErrorMap(((InnerReactiveApiSessionFactory) sessionFactory).composeExceptionFunction())
                .then();
    }


    final Mono<Void> assertTransactionEnd() {
        GenericTransaction tx = obtainTransaction();
        return (tx == null || tx.transactionEnded())
                ? Mono.empty()
                : Mono.error(new TransactionNotCloseException("Session[%s] Transaction[%s] not close."
                , this, tx.name()));
    }



    /*################################## blow private method ##################################*/


    private Mono<Integer> doCacheDomainUpdate(DomainUpdateAdvice advice) {
        return update(CacheDomainUpdate.build(advice), Visible.ONLY_VISIBLE)
                .doOnNext(rows -> advice.updateFinish())
                ;
    }

    private <R> Mono<R> mapMono(List<R> list) {
        Mono<R> mono;
        if (list.size() > 1) {
            mono = Mono.error(new NonUniqueException("select result[%s] more than 1.", list.size()));
        } else if (list.size() == 1) {
            mono = Mono.just(list.get(0));
        } else {
            mono = Mono.empty();
        }
        return mono;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Map<?, ?> map) {
        return (Map<String, Object>) map;
    }


}
