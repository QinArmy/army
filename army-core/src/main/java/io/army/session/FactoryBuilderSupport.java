package io.army.session;

import io.army.ArmyException;
import io.army.advice.FactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;
import io.army.lang.Nullable;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class FactoryBuilderSupport {

    protected String name;

    protected ArmyEnvironment environment;

    protected Collection<FieldCodec> fieldCodecs;

    protected FactoryMode factoryMode = FactoryMode.NO_SHARDING;

    protected int tableCountPerDatabase;

    protected Collection<FactoryAdvice> factoryAdvices;

    protected Function<ArmyException, RuntimeException> exceptionFunction;

    private FactoryAdvice factoryAdviceComposite;


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
    public final FactoryMode shardingMode() {
        return this.factoryMode;
    }

    public final int tableCountPerDatabase() {
        return this.tableCountPerDatabase;
    }

    @Nullable
    final Function<RuntimeException, RuntimeException> exceptionFunction() {
        return this.exceptionFunction;
    }

    @Nullable
    protected Function<RuntimeException, RuntimeException> springExceptionFunction() {
        return null;
    }

    protected final FactoryAdvice getFactoryAdviceComposite() {
        if (this.factoryAdviceComposite == null) {
            this.factoryAdviceComposite = GenericSessionFactoryAdviceComposite.build(this.factoryAdvices);
        }
        return this.factoryAdviceComposite;
    }


    private static final class GenericSessionFactoryAdviceComposite implements FactoryAdvice {

        private static GenericSessionFactoryAdviceComposite build(
                @Nullable Collection<FactoryAdvice> factoryAdvices) {
            List<FactoryAdvice> orderedAdviceList;

            if (CollectionUtils.isEmpty(factoryAdvices)) {
                orderedAdviceList = Collections.emptyList();
            } else {
                orderedAdviceList = new ArrayList<>(factoryAdvices);
                // orderedAdviceList.sort(Comparator.comparingInt(FactoryAdvice::order));
                orderedAdviceList = Collections.unmodifiableList(orderedAdviceList);
            }
            return new GenericSessionFactoryAdviceComposite(orderedAdviceList);
        }

        private final List<FactoryAdvice> adviceList;

        private GenericSessionFactoryAdviceComposite(List<FactoryAdvice> adviceList) {
            this.adviceList = adviceList;
        }


        @Override
        public void beforeInstance(ArmyEnvironment environment) {
            for (FactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.beforeInstance(environment);
            }
        }

        @Override
        public void beforeInitialize(GenericSessionFactory sessionFactory) {
            for (FactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.beforeInitialize(sessionFactory);
            }
        }

        @Override
        public void afterInitialize(GenericSessionFactory sessionFactory) {
            for (FactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.afterInitialize(sessionFactory);
            }
        }

    }

}
