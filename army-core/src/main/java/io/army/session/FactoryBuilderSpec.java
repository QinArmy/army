package io.army.session;

import io.army.ArmyException;
import io.army.advice.FactoryAdvice;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGeneratorFactory;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * <p>This interface is base interface of all factory builder.
 *
 * @param <B> factory builder java type
 * @param <R> sync session factory or Mono
 */
public interface FactoryBuilderSpec<B, R> {


    /**
     * <p>
     * Session factory name,required.
     * </p>
     *
     * @param sessionFactoryName non-empty
     */
    B name(String sessionFactoryName);

    /**
     * <p>
     * Required.
     * </p>
     */
    B environment(ArmyEnvironment environment);

    /**
     * <p>
     * Required.
     * </p>
     */
    B datasource(Object dataSource);

    /**
     * <p>
     * Required.
     * </p>
     */
    B packagesToScan(List<String> packageList);

    /**
     * <p>
     * Optional.
     * </p>
     *
     * @param catalog catalog or empty
     * @param schema  schema or empty
     */
    B schema(String catalog, String schema);

    /*
     * <p>
     * Optional.
     * </p>
     */
    //   B fieldCodecs(Collection<FieldCodec> fieldCodecs);

    /**
     * <p>
     * Optional.
     * </p>
     */
    B factoryAdvice(Collection<FactoryAdvice> factoryAdvices);

    /**
     * <p>
     * Optional.
     * </p>
     */
    B exceptionFunction(Function<ArmyException, RuntimeException> exceptionFunction);

    /**
     * <p>
     * Optional.
     * </p>
     */
    B fieldGeneratorFactory(FieldGeneratorFactory factory);


    R build();

}
