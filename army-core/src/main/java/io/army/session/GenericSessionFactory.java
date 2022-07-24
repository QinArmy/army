package io.army.session;

import io.army.ArmyException;
import io.army.env.ArmyEnvironment;
import io.army.lang.Nullable;
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
    <T> TableMeta<T> tableMeta(Class<T> domainClass);


    boolean supportSavePoints();

    /**
     * Is this factory already closed?
     *
     * @return True if this factory is already closed; false otherwise.
     */
    boolean factoryClosed();

    boolean readonly();


    boolean isReactive();

    boolean uniqueCache();


    Function<ArmyException, RuntimeException> exceptionFunction();

}
