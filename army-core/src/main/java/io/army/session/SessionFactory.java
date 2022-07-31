package io.army.session;

import io.army.ArmyException;
import io.army.env.ArmyEnvironment;
import io.army.lang.Nullable;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;

import java.time.ZoneId;
import java.util.Map;
import java.util.function.Function;

public interface SessionFactory {

    String name();

    ArmyEnvironment environment();

    ZoneId zoneId();

    SchemaMeta schemaMeta();

    ServerMeta serverMeta();

    Map<Class<?>, TableMeta<?>> tableMap();

    @Nullable
    <T> TableMeta<T> getTable(Class<T> domainClass);


    boolean supportSavePoints();

    /**
     * Is this factory already closed?
     *
     * @return True if this factory is already closed; false otherwise.
     */
    boolean isClosed();

    boolean readonly();


    boolean isReactive();

    boolean uniqueCache();


    Function<ArmyException, RuntimeException> exceptionFunction();

}
