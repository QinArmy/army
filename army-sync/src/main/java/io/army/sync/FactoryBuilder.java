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

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;


/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@code io.army.boot.sync.SessionFactoryBuilder}</li>
 *         <li>{@code io.army.boot.sync.TmSessionFactionBuilder}</li>
 *         <li>{@code io.army.boot.reactive.ReactiveSessionFactoryBuilder}</li>
 *         <li>{@code io.army.boot.reactive.ReactiveTmSessionFactoryBuilder}</li>
 *     </ul>
 * </p>
 */
public interface FactoryBuilder {

    FactoryBuilder factoryName(String sessionFactoryName);

    FactoryBuilder schema(String catalog, String schema);

    FactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs);


    FactoryBuilder environment(ArmyEnvironment environment);

    FactoryBuilder tableCountPerDatabase(int tableCountPerDatabase);

    FactoryBuilder factoryAdvice(Collection<FactoryAdvice> factoryAdvices);

    FactoryBuilder exceptionFunction(Function<ArmyException, RuntimeException> exceptionFunction);

    FactoryBuilder domainAdvice(Map<TableMeta<?>, DomainAdvice> domainAdviceMap);

    FactoryBuilder fieldGenerator(Map<FieldMeta<?, ?>, FieldGenerator> generatorMap);

    FactoryBuilder datasource(Object dataSource);

    SessionFactory build() throws SessionFactoryException;

}
