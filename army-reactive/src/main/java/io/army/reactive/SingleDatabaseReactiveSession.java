package io.army.reactive;

import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import reactor.core.publisher.Flux;

interface SingleDatabaseReactiveSession extends GenericReactiveApiSession {

    Flux<Integer> batchUpdate(Update update);

    Flux<Integer> batchUpdate(Update update, Visible visible);

    Flux<Long> batchLargeUpdate(Update update);

    Flux<Long> batchLargeUpdate(Update update, Visible visible);

    Flux<Integer> batchDelete(Delete delete);

    Flux<Integer> batchDelete(Delete delete, Visible visible);

    Flux<Long> batchLargeDelete(Delete delete);

    Flux<Long> batchLargeDelete(Delete delete, Visible visible);
}
