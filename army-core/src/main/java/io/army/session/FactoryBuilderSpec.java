package io.army.session;

import io.army.ArmyException;
import io.army.advice.FactoryAdvice;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGeneratorFactory;
import io.army.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * <p>This interface is base interface of all factory builder.
 *
 * @param <B> factory builder java type
 * @param <R> sync session factory or Mono
 * @since 1.0
 */
public interface FactoryBuilderSpec<B, R> {


    /**
     * <p>Required.
     *
     * @param sessionFactoryName non-empty
     */
    B name(String sessionFactoryName);

    /**
     * <p>Required.
     */
    B environment(ArmyEnvironment environment);

    /**
     * <p>Required.
     */
    B datasource(Object dataSource);

    /**
     * <p>Required.
     */
    B packagesToScan(List<String> packageList);

    /**
     * <p>Optional.
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
     * <p>Optional.
     */
    B factoryAdvice(Collection<FactoryAdvice> factoryAdvices);

    /**
     * <p>Optional.
     */
    B exceptionFunction(Function<ArmyException, RuntimeException> exceptionFunction);

    /**
     * <p>Optional.
     */
    B fieldGeneratorFactory(FieldGeneratorFactory factory);


    /**
     * <p>Optional.
     * See
     * <ul>
     *     <li>{@link io.army.session.executor.StmtExecutorFactoryProviderSpec#createServerMeta(Dialect, Function)}</li>
     *     <li>{@link Database#mapToDatabase(String, Function)}</li>
     * </ul>
     */
    B nameToDatabaseFunc(@Nullable Function<String, Database> function);

    R build();

}
