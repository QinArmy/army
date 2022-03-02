package io.army.session;

import io.army.ArmyException;
import io.army.SessionFactoryException;
import io.army.advice.FactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.criteria.impl._SchemaMetaFactory;
import io.army.criteria.impl._TableMetaFactory;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;

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


    /*################################## blow non-setter fields ##################################*/

    Map<Class<?>, TableMeta<?>> tableMap;


    protected final void scanSchema() {

        final List<String> packagesToScan = this.packagesToScan;
        if (packagesToScan == null || packagesToScan.isEmpty()) {
            throw new SessionFactoryException("No specified package to scan.");
        }
        SchemaMeta schemaMeta = this.schemaMeta;
        if (schemaMeta == null) {
            schemaMeta = _SchemaMetaFactory.getSchema("", "");
        }
        final Map<Class<?>, TableMeta<?>> tableMetaMap;
        tableMetaMap = _TableMetaFactory.getTableMetaMap(schemaMeta, packagesToScan);
        if (tableMetaMap.isEmpty()) {
            String m;
            if (schemaMeta.defaultSchema()) {
                m = String.format("Not found any %s for default schema.", TableMeta.class.getName());
            } else {
                m = String.format("Not found any %s for %s.", TableMeta.class.getName(), schemaMeta);
            }
            throw new SessionFactoryException(m);
        }
        this.tableMap = tableMetaMap;
    }


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
