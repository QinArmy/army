package io.army.boot.reactive;

import io.army.cache.SessionCacheFactory;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.reactive.GenericReactiveApiSessionFactory;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;

interface InnerReactiveApiSessionFactory extends GenericReactiveApiSessionFactory {

    SessionCacheFactory sessionCacheFactory();

    CurrentSessionContext currentSessionContext();

    @Nullable
    ReactiveDomainInsertAdvice domainInsertAdviceComposite(TableMeta<?> tableMeta);

    @Nullable
    ReactiveDomainUpdateAdvice domainUpdateAdviceComposite(TableMeta<?> tableMeta);

    @Nullable
    ReactiveDomainDeleteAdvice domainDeleteAdviceComposite(TableMeta<?> tableMeta);

    boolean springApplication();
}
