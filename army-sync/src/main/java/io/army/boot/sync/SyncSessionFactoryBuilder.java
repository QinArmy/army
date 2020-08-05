package io.army.boot.sync;

import io.army.boot.GenericFactoryBuilder;
import io.army.interceptor.DomainAdvice;
import io.army.sync.SessionFactoryAdvice;

import java.util.Collection;

public interface SyncSessionFactoryBuilder extends GenericFactoryBuilder {

    SyncSessionFactoryBuilder factoryAdvice(Collection<SessionFactoryAdvice> factoryAdvices);

    SyncSessionFactoryBuilder tableCountPerDatabase(int tableCountPerDatabase);

    SyncSessionFactoryBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors);
}
