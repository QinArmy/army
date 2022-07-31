package io.army.sync;

import io.army.ArmyException;
import io.army.advice.FactoryAdvice;
import io.army.advice.sync.DomainAdvice;
import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGeneratorFactory;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.session.SessionFactoryException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface LocalFactoryBuilder {

    /**
     * <p>
     * Session factory name,required.
     * </p>
     *
     * @param sessionFactoryName non-empty
     */
    LocalFactoryBuilder name(String sessionFactoryName);

    /**
     * <p>
     * Required.
     * </p>
     */
    LocalFactoryBuilder environment(ArmyEnvironment environment);

    /**
     * <p>
     * Required.
     * </p>
     */
    LocalFactoryBuilder datasource(Object dataSource);

    /**
     * <p>
     * Required.
     * </p>
     */
    LocalFactoryBuilder packagesToScan(List<String> packageList);

    /**
     * <p>
     * Optional.
     * </p>
     *
     * @param catalog catalog or empty
     * @param schema  schema or empty
     */
    LocalFactoryBuilder schema(String catalog, String schema);

    /**
     * <p>
     * Optional.
     * </p>
     */
    LocalFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs);

    /**
     * <p>
     * Optional.
     * </p>
     */
    LocalFactoryBuilder factoryAdvice(Collection<FactoryAdvice> factoryAdvices);

    /**
     * <p>
     * Optional.
     * </p>
     */
    LocalFactoryBuilder exceptionFunction(Function<ArmyException, RuntimeException> exceptionFunction);

    /**
     * <p>
     * Optional.
     * </p>
     */
    LocalFactoryBuilder domainAdvice(Map<TableMeta<?>, DomainAdvice> domainAdviceMap);

    /**
     * <p>
     * Optional.
     * </p>
     */
    LocalFactoryBuilder fieldGeneratorFactory(@Nullable FieldGeneratorFactory factory);


    /**
     * (optional)
     */
    LocalFactoryBuilder currentSessionContext(SessionContext context);

    LocalSessionFactory build() throws SessionFactoryException;

    static LocalFactoryBuilder builder() {
        return new LocalSessionFactoryBuilder();
    }


}
