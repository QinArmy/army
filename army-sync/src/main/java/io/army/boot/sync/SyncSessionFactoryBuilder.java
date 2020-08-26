package io.army.boot.sync;

import io.army.advice.GenericSessionFactoryAdvice;
import io.army.advice.sync.DomainAdvice;
import io.army.boot.GenericFactoryBuilder;

import java.util.Collection;

interface SyncSessionFactoryBuilder<T extends SyncSessionFactoryBuilder<T>> extends GenericFactoryBuilder<T> {

    T factoryAdvice(Collection<GenericSessionFactoryAdvice> factoryAdvices);

    T tableCountPerDatabase(int tableCountPerDatabase);

    T domainInterceptor(Collection<DomainAdvice> domainInterceptors);
}
