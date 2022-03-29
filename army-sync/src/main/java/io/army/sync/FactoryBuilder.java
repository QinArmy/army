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

public interface FactoryBuilder {

    /**
     * <p>
     * Session factory name,required.
     * </p>
     *
     * @param sessionFactoryName non-empty
     */
    FactoryBuilder name(String sessionFactoryName);

    /**
     * <p>
     * Required.
     * </p>
     */
    FactoryBuilder environment(ArmyEnvironment environment);

    /**
     * <p>
     * Required.
     * </p>
     */
    FactoryBuilder datasource(Object dataSource);

    /**
     * <p>
     * Required.
     * </p>
     */
    FactoryBuilder packagesToScan(List<String> packageList);

    /**
     * <p>
     * Optional.
     * </p>
     *
     * @param catalog catalog or empty
     * @param schema  schema or empty
     */
    FactoryBuilder schema(String catalog, String schema);

    /**
     * <p>
     * Optional.
     * </p>
     */
    FactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs);

    /**
     * <p>
     * Optional.
     * </p>
     */
    FactoryBuilder factoryAdvice(Collection<FactoryAdvice> factoryAdvices);

    /**
     * <p>
     * Optional.
     * </p>
     */
    FactoryBuilder exceptionFunction(Function<ArmyException, RuntimeException> exceptionFunction);

    /**
     * <p>
     * Optional.
     * </p>
     */
    FactoryBuilder domainAdvice(Map<TableMeta<?>, DomainAdvice> domainAdviceMap);

    /**
     * <p>
     * Optional.
     * </p>
     */
    FactoryBuilder fieldGeneratorFactory(@Nullable FieldGeneratorFactory factory);


    /**
     * (optional)
     */
    FactoryBuilder currentSessionContext(SessionContext context);

    SessionFactory build() throws SessionFactoryException;

    static FactoryBuilder builder() {
        return new LocalSessionFactoryBuilder();
    }


}
