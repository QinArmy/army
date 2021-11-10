package io.army.sync;

import io.army.advice.FactoryAdvice;
import io.army.advice.sync.DomainAdvice;
import io.army.lang.Nullable;
import io.army.session.FactoryMode;

import java.util.Collection;

abstract class AbstractSyncSessionFactoryBuilder {

    Collection<DomainAdvice> domainInterceptors;

    Collection<FactoryAdvice> factoryAdvices;

    int tableCountPerDatabase = 1;

    FactoryMode factoryMode = FactoryMode.NO_SHARDING;

    //  private CompositeSessionFactoryAdvice compositeSessionFactoryAdvice;

    AbstractSyncSessionFactoryBuilder(boolean springApplication) {

    }

    @Nullable
    public final Collection<DomainAdvice> domainInterceptors() {
        return domainInterceptors;
    }


//    final CompositeSessionFactoryAdvice getCompositeSessionFactoryAdvice() {
//        if (this.compositeSessionFactoryAdvice == null) {
//            this.compositeSessionFactoryAdvice = CompositeSessionFactoryAdvice.build(this.factoryAdvices);
//        }
//        return this.compositeSessionFactoryAdvice;
//    }


}
