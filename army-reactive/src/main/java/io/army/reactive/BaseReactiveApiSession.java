package io.army.reactive;

import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link Session}</li>
 *         <li>{@code io.army.boot.reactive.ReactiveTmSession}</li>
 *         <li>{@code io.army.reactive.ProxyReactiveSession}</li>
 *         <li>{@code io.army.boot.reactive.ProxyReactiveTmSession}</li>
 *     </ul>
 * </p>
 *
 * @see Session
 */
interface BaseReactiveApiSession extends BaseReactiveSession {

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
