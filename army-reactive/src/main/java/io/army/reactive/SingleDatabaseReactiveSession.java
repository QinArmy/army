package io.army.reactive;

import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import reactor.core.publisher.Flux;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link Session}</li>
 *         <li>{@code io.army.reactive.ProxyReactiveSession}</li>
 *     </ul>
 * </p>
 */
interface SingleDatabaseReactiveSession extends BaseReactiveApiSession {

    @Override
    SessionFactory sessionFactory();

    Flux<Integer> batchUpdate(Update update);

    Flux<Integer> batchUpdate(Update update, Visible visible);

    Flux<Long> batchLargeUpdate(Update update);

    Flux<Long> batchLargeUpdate(Update update, Visible visible);

    Flux<Integer> batchDelete(Delete delete);

    Flux<Integer> batchDelete(Delete delete, Visible visible);

    Flux<Long> batchLargeDelete(Delete delete);

    Flux<Long> batchLargeDelete(Delete delete, Visible visible);
}
