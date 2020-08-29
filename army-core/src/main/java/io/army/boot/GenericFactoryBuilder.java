package io.army.boot;

import io.army.advice.GenericSessionFactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;

import java.util.Collection;
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
public interface GenericFactoryBuilder<T extends GenericFactoryBuilder<T>> {


    T fieldCodecs(Collection<FieldCodec> fieldCodecs);

    T name(String sessionFactoryName);

    T environment(ArmyEnvironment environment);

    T tableCountPerDatabase(int tableCountPerDatabase);

    T factoryAdvice(Collection<GenericSessionFactoryAdvice> factoryAdvices);

    T exceptionFunction(Function<RuntimeException, RuntimeException> exceptionFunction);

}
