package io.army.boot;

import io.army.advice.GenericSessionFactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;

import java.util.Collection;

public interface GenericFactoryBuilder<T extends GenericFactoryBuilder<T>> {


    T fieldCodecs(Collection<FieldCodec> fieldCodecs);

    T name(String sessionFactoryName);

    T environment(ArmyEnvironment environment);

    T tableCountPerDatabase(int tableCountPerDatabase);

    T factoryAdvice(Collection<GenericSessionFactoryAdvice> factoryAdvices);

}
