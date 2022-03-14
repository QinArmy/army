package io.army.session;

import io.army.ArmyException;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.function.Function;

public interface GenericSessionFactory {

    String name();

    ArmyEnvironment environment();

    ZoneOffset zoneOffset();

    SchemaMeta schemaMeta();

    ServerMeta serverMeta();

    Map<Class<?>, TableMeta<?>> tableMap();

    @Nullable
    <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass);

    @Nullable
    FieldGenerator fieldGenerator(FieldMeta<?> fieldMeta);

    boolean supportSessionCache();

    /**
     * Is this factory already closed?
     *
     * @return True if this factory is already closed; false otherwise.
     */
    boolean factoryClosed();

    boolean readonly();

    boolean showSQL();

    boolean formatSQL();


    Function<ArmyException, RuntimeException> exceptionFunction();

}
