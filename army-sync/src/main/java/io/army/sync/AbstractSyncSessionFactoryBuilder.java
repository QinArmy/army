package io.army.sync;

import io.army.ShardingMode;
import io.army.advice.GenericSessionFactoryAdvice;
import io.army.advice.sync.DomainAdvice;
import io.army.lang.Nullable;

import java.util.Collection;

abstract class AbstractSyncSessionFactoryBuilder {

    Collection<DomainAdvice> domainInterceptors;

    Collection<GenericSessionFactoryAdvice> factoryAdvices;

    int tableCountPerDatabase = 1;

    ShardingMode shardingMode = ShardingMode.NO_SHARDING;

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
