package io.army.boot.reactive;

import io.army.cache.SessionCacheFactory;
import io.army.context.spi.ReactiveCurrentSessionContext;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.reactive.ReactiveApiSessionFactory;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;

import java.util.function.Function;

interface InnerReactiveApiSessionFactory extends ReactiveApiSessionFactory {

    SessionCacheFactory sessionCacheFactory();

    Function<Throwable, RuntimeException> composeExceptionFunction();

    ReactiveCurrentSessionContext currentSessionContext();

    @Nullable
    ReactiveDomainInsertAdvice domainInsertAdviceComposite(TableMeta<?> tableMeta);

    @Nullable
    ReactiveDomainUpdateAdvice domainUpdateAdviceComposite(TableMeta<?> tableMeta);

    @Nullable
    ReactiveDomainDeleteAdvice domainDeleteAdviceComposite(TableMeta<?> tableMeta);

}
