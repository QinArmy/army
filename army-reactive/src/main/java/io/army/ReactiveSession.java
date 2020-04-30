package io.army;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.tx.Isolation;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReactiveSession extends GenericSession {


    Mono<Void> save(IDomain entity);

    <T extends IDomain> Mono<T> get(TableMeta<T> tableMeta, Object id);

    <T extends IDomain> Mono<T> get(TableMeta<T> tableMeta, Object id, Visible visible);

    <T extends IDomain> Mono<T> getByUnique(TableMeta<T> tableMeta, List<String> propNameList, List<Object> valueList);

    <T extends IDomain> Mono<T> getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible);

    <T extends IDomain> Mono<List<T>> select(Select select);

    <T extends IDomain> Mono<List<T>> select(Select select, Visible visible);

    /**
     * @param update will execute singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    Mono<List<Integer>> update(Update update);

    Mono<List<Integer>> update(Update update, Visible visible);

    Mono<Void> insert(Insert insert);

    Mono<List<Integer>> delete(Delete delete);

    ReactiveTransaction sessionTransaction();

    SessionTransactionBuilder builder();

    Mono<Void> close() throws SessionException;

    Mono<Void> flush() throws SessionException;

    interface SessionTransactionBuilder {

        SessionTransactionBuilder readOnly(boolean readOnly);

        SessionTransactionBuilder isolation(Isolation isolation);

        SessionTransactionBuilder timeout(int seconds);

        SessionTransactionBuilder name(@Nullable String txName);

        Mono<ReactiveTransaction> build();
    }
}
