package io.army.boot.sync;

import io.army.GenericFactoryBuilderImpl;
import io.army.ShardingMode;
import io.army.env.Environment;
import io.army.interceptor.DomainAdvice;
import io.army.lang.Nullable;
import io.army.sync.GenericSyncApiSessionFactory;
import io.army.sync.SessionFactoryAdvice;
import io.army.util.CollectionUtils;

import java.util.*;

abstract class AbstractSyncSessionFactoryBuilder extends GenericFactoryBuilderImpl
        implements SyncSessionFactoryBuilder {

    Collection<DomainAdvice> domainInterceptors;

    Collection<SessionFactoryAdvice> factoryAdvices;

    int tableCountPerDatabase = 1;

    ShardingMode shardingMode = ShardingMode.NO_SHARDING;

    private CompositeSessionFactoryAdvice compositeSessionFactoryAdvice;

    AbstractSyncSessionFactoryBuilder() {

    }


    @Nullable
    public final Collection<DomainAdvice> domainInterceptors() {
        return domainInterceptors;
    }

    @Nullable
    ShardingMode shardingMode() {
        return shardingMode;
    }

    final CompositeSessionFactoryAdvice getCompositeSessionFactoryAdvice() {
        if (this.compositeSessionFactoryAdvice == null) {
            this.compositeSessionFactoryAdvice = CompositeSessionFactoryAdvice.build(this.factoryAdvices);
        }
        return this.compositeSessionFactoryAdvice;
    }


    static final class CompositeSessionFactoryAdvice implements SessionFactoryAdvice {

        private static CompositeSessionFactoryAdvice build(@Nullable Collection<SessionFactoryAdvice> factoryAdvices) {
            List<SessionFactoryAdvice> orderedAdviceList;

            if (CollectionUtils.isEmpty(factoryAdvices)) {
                orderedAdviceList = Collections.emptyList();
            } else {
                orderedAdviceList = new ArrayList<>(factoryAdvices);
                orderedAdviceList.sort(Comparator.comparingInt(SessionFactoryAdvice::order));
                orderedAdviceList = Collections.unmodifiableList(orderedAdviceList);
            }
            return new CompositeSessionFactoryAdvice(orderedAdviceList);
        }

        private final List<SessionFactoryAdvice> adviceList;

        private CompositeSessionFactoryAdvice(List<SessionFactoryAdvice> adviceList) {
            this.adviceList = adviceList;
        }

        @Override
        public int order() {
            return Integer.MIN_VALUE;
        }

        @Override
        public void beforeInstance(Environment environment) {
            for (SessionFactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.beforeInstance(environment);
            }
        }

        @Override
        public void beforeInitialize(GenericSyncApiSessionFactory sessionFactory) {
            for (SessionFactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.beforeInitialize(sessionFactory);
            }
        }

        @Override
        public void afterInitialize(GenericSyncApiSessionFactory sessionFactory) {
            for (SessionFactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.afterInitialize(sessionFactory);
            }
        }
    }

}
