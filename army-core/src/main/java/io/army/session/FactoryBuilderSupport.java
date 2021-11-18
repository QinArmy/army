package io.army.session;

import io.army.ArmyException;
import io.army.advice.FactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.criteria.impl._SchemaMetaFactory;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;

import java.util.*;
import java.util.function.Function;

public abstract class FactoryBuilderSupport {

    protected String name;

    protected ArmyEnvironment environment;

    protected Collection<FieldCodec> fieldCodecs;

    protected FactoryMode factoryMode = FactoryMode.NO_SHARDING;

    protected int tableCountPerDatabase = 1;

    protected SchemaMeta schemaMeta = _SchemaMetaFactory.getSchema("", "");
    protected Function<ArmyException, RuntimeException> exceptionFunction;

    protected Map<FieldMeta<?, ?>, FieldGenerator> generatorMap;

    protected Collection<FactoryAdvice> factoryAdvices;

    protected List<String> packagesToScan;


    @Nullable
    protected static FactoryAdvice createFactoryAdviceComposite(Collection<FactoryAdvice> factoryAdvices) {
        if (factoryAdvices == null || factoryAdvices.isEmpty()) {
            return null;
        }
        List<FactoryAdvice> orderedAdviceList;
        orderedAdviceList = new ArrayList<>(factoryAdvices);
        orderedAdviceList.sort(Comparator.comparingInt(FactoryAdvice::order));
        orderedAdviceList = Collections.unmodifiableList(orderedAdviceList);
        return new SessionFactoryAdviceComposite(orderedAdviceList);
    }


    protected static final class SessionFactoryAdviceComposite implements FactoryAdvice {

        private final List<FactoryAdvice> adviceList;

        private SessionFactoryAdviceComposite(List<FactoryAdvice> adviceList) {
            this.adviceList = adviceList;
        }

        @Override
        public int order() {
            return 0;
        }

        @Override
        public void beforeInstance(ServerMeta serverMeta, ArmyEnvironment environment) {
            for (FactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.beforeInstance(serverMeta, environment);
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
