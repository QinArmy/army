package io.army.boot.reactive;

import io.army.lang.Nullable;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;
import io.army.session.FactoryBuilderSupport;

import java.util.Collection;

abstract class GenericReactiveSessionFactoryBuilderImpl<T extends GenericReactiveSessionFactoryBuilder<T>>
        extends FactoryBuilderSupport
        implements GenericReactiveSessionFactoryBuilder<T> {


    Collection<ReactiveDomainInsertAdvice> domainInsertAdvices;

    Collection<ReactiveDomainUpdateAdvice> domainUpdateAdvices;

    Collection<ReactiveDomainDeleteAdvice> domainDeleteAdvices;

    int waitCreateSeconds = 30;


    GenericReactiveSessionFactoryBuilderImpl(boolean springApplication) {
        //super(springApplication);
    }


    @Nullable
    final Collection<ReactiveDomainInsertAdvice> domainInsertAdvices() {
        return this.domainInsertAdvices;
    }

    @Nullable
    final Collection<ReactiveDomainUpdateAdvice> domainUpdateAdvices() {
        return this.domainUpdateAdvices;
    }

    @Nullable
    final Collection<ReactiveDomainDeleteAdvice> domainDeleteAdvices() {
        return this.domainDeleteAdvices;
    }
}
