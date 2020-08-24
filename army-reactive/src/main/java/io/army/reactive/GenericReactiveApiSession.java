package io.army.reactive;

import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * <p>
 * this interface have four direct sub interfaces:
 *     <ul>
 *         <li>{@link ReactiveSession}</li>
 *         <li>{@link ProxyReactiveSession}</li>
 *         <li>{@code io.army.reactive.ReactiveTmSession}</li>
 *         <li>{@code io.army.reactive.ProxyReactiveTmSession}</li>
 *     </ul>
 * </p>
 *
 * @see ReactiveSession
 * @see ProxyReactiveSession
 */
public interface GenericReactiveApiSession extends GenericReactiveSession {

    /**
     * @param <R> representing select result Java Type
     */
    <R extends IDomain> Mono<R> get(TableMeta<R> tableMeta, Object id);

    /**
     * @param <R> representing select result Java Type.
     */
    <R extends IDomain> Mono<R> getByUnique(TableMeta<R> tableMeta, List<String> propNameList, List<Object> valueList);

    Mono<Void> valueInsert(Insert insert);

    Mono<Void> valueInsert(Insert insert, Visible visible);

    Mono<Void> flush();

}
