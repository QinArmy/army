package io.army.session;

import io.army.ArmyException;
import io.army.advice.FactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.criteria.impl._SchemaMetaFactory;
import io.army.criteria.impl._TableMetaFactory;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.generator.FieldGeneratorFactory;
import io.army.lang.Nullable;
import io.army.meta.*;

import java.util.*;
import java.util.function.Function;

public abstract class FactoryBuilderSupport {

    protected String name;

    protected ArmyEnvironment environment;

    protected Collection<FieldCodec> fieldCodecs;

    protected SchemaMeta schemaMeta = _SchemaMetaFactory.getSchema("", "");
    protected Function<ArmyException, RuntimeException> exceptionFunction;

    protected Map<FieldMeta<?>, FieldGenerator> generatorMap = Collections.emptyMap();

    protected FieldGeneratorFactory fieldGeneratorFactory;

    protected Collection<FactoryAdvice> factoryAdvices;

    protected List<String> packagesToScan;

    protected DdlMode ddlMode;


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

        final FieldGeneratorFactory generatorFactory = this.fieldGeneratorFactory;
        List<FieldMeta<?>> fieldChain;
        GeneratorMeta meta;

        final Map<FieldMeta<?>, FieldGenerator> generatorMap = new HashMap<>();
        FieldGenerator generator;
        for (TableMeta<?> table : tableMetaMap.values()) {
            fieldChain = table.fieldChain();
            if (fieldChain.size() == 0) {
                continue;
            }
            for (FieldMeta<?> field : fieldChain) {
                meta = field.generator();
                assert meta != null;
                if (generatorFactory == null) {
                    throw notSpecifiedFieldGeneratorFactory(field);
                }
                generator = generatorFactory.get(field);
                if (!meta.javaType().isInstance(generator)) {
                    throw fieldGeneratorTypeError(meta, generator);
                }
                generatorMap.put(field, generator);
            }
        }
        if (generatorMap.size() > 0) {
            this.generatorMap = Collections.unmodifiableMap(generatorMap);
        }
        this.tableMap = tableMetaMap;
    }

    private SessionFactoryException fieldGeneratorTypeError(GeneratorMeta meta, @Nullable FieldGenerator generator) {
        String m = String.format("%s %s type %s isn't %s."
                , meta.field(), FieldGenerator.class.getName(), generator, meta.javaType().getName());
        throw new SessionFactoryException(m);
    }

    private SessionFactoryException notSpecifiedFieldGeneratorFactory(FieldMeta<?> field) {
        String m = String.format("%s has %s ,but not specified %s."
                , field, GeneratorMeta.class.getName(), FieldGeneratorFactory.class.getName());
        throw new SessionFactoryException(m);
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
        public void beforeInitialize(SessionFactory sessionFactory) {
            for (FactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.beforeInitialize(sessionFactory);
            }
        }

        @Override
        public void afterInitialize(SessionFactory sessionFactory) {
            for (FactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.afterInitialize(sessionFactory);
            }
        }

    }

}
