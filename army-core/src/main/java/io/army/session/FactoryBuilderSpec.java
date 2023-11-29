package io.army.session;

import io.army.advice.FactoryAdvice;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGeneratorFactory;
import io.army.session.executor.ExecutorFactoryProvider;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>This interface representing the builder spec of {@link SessionFactory} .
 * <p>This interface is base interface of all factory builder.
 *
 * @param <B> factory builder java type,it is the sub interface of this interface
 * @param <R> sync session factory or Mono
 * @since 1.0
 */
public interface FactoryBuilderSpec<B, R> {


    /**
     * <p>Required.
     *
     * @param sessionFactoryName non-empty
     * @return <strong>this</strong>
     */
    B name(String sessionFactoryName);

    /**
     * <p>Required.
     *
     * @return <strong>this</strong>
     */
    B environment(ArmyEnvironment environment);

    /**
     * <p>Required.
     *
     * @return <strong>this</strong>
     */
    B datasource(Object dataSource);

    /**
     * <p>Required.
     *
     * @return <strong>this</strong>
     */
    B packagesToScan(List<String> packageList);

    /**
     * <p>Optional.
     *
     * @param catalog catalog or empty
     * @param schema  schema or empty
     * @return <strong>this</strong>
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
     *
     * @return <strong>this</strong>
     */
    B factoryAdvice(@Nullable Collection<FactoryAdvice> factoryAdvices);


    /**
     * <p>Optional.
     *
     * @return <strong>this</strong>
     */
    B fieldGeneratorFactory(@Nullable FieldGeneratorFactory factory);


    /**
     * <p>Optional.
     * See
     * <ul>
     *     <li>{@link ExecutorFactoryProvider#createServerMeta(Dialect, Function)}</li>
     *     <li>{@link Database#mapToDatabase(String, Function)}</li>
     * </ul>
     *
     * @return <strong>this</strong>
     */
    B nameToDatabaseFunc(@Nullable Function<String, Database> function);

    /**
     * <p>Optional.
     * <p>Set a consumer for validating {@link ExecutorFactoryProvider} is the instance which you want.
     * <p>See {@code io.army.env.SyncKey#EXECUTOR_PROVIDER} and  see {@code io.army.env.ReactiveKey#EXECUTOR_PROVIDER}
     *
     * @return <strong>this</strong>
     */
    B executorFactoryProviderValidator(@Nullable Consumer<ExecutorFactoryProvider> consumer);

    /**
     * <p>Optional.
     * <p>See {@link io.army.session.record.DataRecord#get(int, Class)}
     *
     * @return <strong>this</strong>
     */
    B columnConverterFunc(@Nullable Function<Class<?>, Function<?, ?>> converterFunc);

    <T> B dataSourceOption(Option<T> option, @Nullable T value);

    /**
     * <p>Create {@link SessionFactory} instance
     *
     * @return <ul>
     * <li>sync api : {@link SessionFactory} instance</li>
     * <li>reactive api : Mono of {@link SessionFactory} instance</li>
     * </ul>
     * @throws SessionFactoryException throw (emit) when
     *                                 <ul>
     *                                     <li>required properties absent</li>
     *                                     <li>name duplication</li>
     *                                     <li>access database occur error</li>
     *                                     <li>properties error</li>
     *                                 </ul>
     */
    R build();

}
