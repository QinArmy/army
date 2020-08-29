package io.army;

import io.army.advice.GenericSessionFactoryAdvice;
import io.army.boot.GenericFactoryBuilder;
import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;
import io.army.lang.Nullable;
import io.army.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;

public abstract class GenericFactoryBuilderImpl<T extends GenericFactoryBuilder<T>>
        implements GenericFactoryBuilder<T> {

    private final boolean springApplication;

    protected String name;

    protected ArmyEnvironment environment;

    protected Collection<FieldCodec> fieldCodecs;

    protected ShardingMode shardingMode = ShardingMode.NO_SHARDING;

    protected int tableCountPerDatabase;

    protected Collection<GenericSessionFactoryAdvice> factoryAdvices;

    protected Function<RuntimeException, RuntimeException> exceptionFunction;

    private GenericSessionFactoryAdvice factoryAdviceComposite;

    protected GenericFactoryBuilderImpl(boolean springApplication) {
        this.springApplication = springApplication;
    }

    public final String name() {
        return this.name;
    }

    public final ArmyEnvironment environment() {
        return this.environment;
    }

    @Nullable
    public final Collection<FieldCodec> fieldCodecs() {
        return this.fieldCodecs;
    }

    @Nullable
    public final ShardingMode shardingMode() {
        return this.shardingMode;
    }

    public final int tableCountPerDatabase() {
        return this.tableCountPerDatabase;
    }

    public final boolean springApplication() {
        return this.springApplication;
    }

    @Nullable
    final Function<RuntimeException, RuntimeException> exceptionFunction() {
        return this.exceptionFunction;
    }

    @Nullable
    protected Function<RuntimeException, RuntimeException> springExceptionFunction() {
        return null;
    }

    protected final GenericSessionFactoryAdvice getFactoryAdviceComposite() {
        if (this.factoryAdviceComposite == null) {
            this.factoryAdviceComposite = GenericSessionFactoryAdviceComposite.build(this.factoryAdvices);
        }
        return this.factoryAdviceComposite;
    }


    private static final class GenericSessionFactoryAdviceComposite implements GenericSessionFactoryAdvice {

        private static GenericSessionFactoryAdviceComposite build(
                @Nullable Collection<GenericSessionFactoryAdvice> factoryAdvices) {
            List<GenericSessionFactoryAdvice> orderedAdviceList;

            if (CollectionUtils.isEmpty(factoryAdvices)) {
                orderedAdviceList = Collections.emptyList();
            } else {
                orderedAdviceList = new ArrayList<>(factoryAdvices);
                orderedAdviceList.sort(Comparator.comparingInt(GenericSessionFactoryAdvice::order));
                orderedAdviceList = Collections.unmodifiableList(orderedAdviceList);
            }
            return new GenericSessionFactoryAdviceComposite(orderedAdviceList);
        }

        private final List<GenericSessionFactoryAdvice> adviceList;

        private GenericSessionFactoryAdviceComposite(List<GenericSessionFactoryAdvice> adviceList) {
            this.adviceList = adviceList;
        }

        @Override
        public int order() {
            return Integer.MIN_VALUE;
        }


        @Override
        public void beforeInstance(ArmyEnvironment environment) {
            for (GenericSessionFactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.beforeInstance(environment);
            }
        }

        @Override
        public void beforeInitialize(GenericSessionFactory sessionFactory) {
            for (GenericSessionFactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.beforeInitialize(sessionFactory);
            }
        }

        @Override
        public void afterInitialize(GenericSessionFactory sessionFactory) {
            for (GenericSessionFactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.afterInitialize(sessionFactory);
            }
        }

    }

}
