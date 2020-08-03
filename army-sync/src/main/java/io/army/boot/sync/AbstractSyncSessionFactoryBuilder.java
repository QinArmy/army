package io.army.boot.sync;

import io.army.GenericFactoryBuilderImpl;
import io.army.interceptor.DomainAdvice;
import io.army.lang.Nullable;
import io.army.sync.SessionFactoryAdvice;

import java.util.*;

abstract class AbstractSyncSessionFactoryBuilder extends GenericFactoryBuilderImpl {

    Collection<DomainAdvice> domainInterceptors;

    List<SessionFactoryAdvice> factoryAdviceList;

    AbstractSyncSessionFactoryBuilder() {

    }


    @Nullable
    public final Collection<DomainAdvice> domainInterceptors() {
        return domainInterceptors;
    }

    public final List<SessionFactoryAdvice> factoryAdviceList() {
        return factoryAdviceList;
    }


    List<SessionFactoryAdvice> createFactoryInterceptorList() {
        List<SessionFactoryAdvice> list = new ArrayList<>(this.factoryAdviceList);
        list.sort(Comparator.comparingInt(SessionFactoryAdvice::order));
        return Collections.unmodifiableList(list);
    }

}
