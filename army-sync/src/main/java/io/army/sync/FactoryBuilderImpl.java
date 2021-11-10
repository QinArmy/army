package io.army.sync;

import io.army.ArmyException;
import io.army.SessionFactoryException;
import io.army.advice.FactoryAdvice;
import io.army.advice.sync.DomainAdvice;
import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.session.FactoryBuilderSupport;
import io.army.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

final class FactoryBuilderImpl extends FactoryBuilderSupport implements FactoryBuilder {

    Map<TableMeta<?>, DomainAdvice> domainAdviceMap;

    Object dataSource;


    @Override
    public FactoryBuilder name(String sessionFactoryName) {
        if (!StringUtils.hasText(sessionFactoryName)) {
            throw new IllegalArgumentException("sessionFactoryName must have text.");
        }
        this.name = sessionFactoryName;
        return this;
    }

    @Override
    public FactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public FactoryBuilder environment(ArmyEnvironment environment) {
        this.environment = Objects.requireNonNull(environment);
        return this;
    }

    @Override
    public FactoryBuilder tableCountPerDatabase(final int tableCountPerDatabase) {
        if (tableCountPerDatabase < 1) {
            throw new IllegalArgumentException("tableCountPerDatabase must be great than 0 .");
        }
        this.tableCountPerDatabase = tableCountPerDatabase;
        return this;
    }

    @Override
    public FactoryBuilder factoryAdvice(Collection<FactoryAdvice> factoryAdvices) {
        this.factoryAdvices = factoryAdvices;
        return this;
    }

    @Override
    public FactoryBuilder exceptionFunction(Function<ArmyException, RuntimeException> exceptionFunction) {
        this.exceptionFunction = exceptionFunction;
        return this;
    }

    @Override
    public FactoryBuilder domainAdvice(Map<TableMeta<?>, DomainAdvice> domainAdviceMap) {
        this.domainAdviceMap = domainAdviceMap;
        return this;
    }

    @Override
    public FactoryBuilder fieldGenerator(Map<FieldMeta<?, ?>, FieldGenerator> generatorMap) {
        return this;
    }

    @Override
    public FactoryBuilder datasource(Object dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public SessionFactory build() throws SessionFactoryException {

        return null;
    }


}
