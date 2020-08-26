package io.army.boot.reactive;

import io.army.boot.GenericFactoryBuilder;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;

import java.util.Collection;

interface GenericReactiveSessionFactoryBuilder<T extends GenericReactiveSessionFactoryBuilder<T>>
        extends GenericFactoryBuilder<T> {

    T waitCreateSeconds(int seconds);

    T domainInsertAdvice(Collection<ReactiveDomainInsertAdvice> insertAdvices);

    T domainUpdateInsertAdvice(Collection<ReactiveDomainUpdateAdvice> updateAdvices);

    T domainDeleteInsertAdvice(Collection<ReactiveDomainDeleteAdvice> deleteAdvices);

}
