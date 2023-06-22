package io.army.session;

import io.army.ArmyException;
import io.army.criteria.Visible;
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

    AllowMode visibleMode();

    AllowMode queryInsertMode();


    boolean isSupportSavePoints();

    /**
     * Is this factory already closed?
     *
     * @return True if this factory is already closed; false otherwise.
     */
    boolean isClosed();

    boolean isReadonly();


    boolean isReactive();

    @Deprecated
    default boolean uniqueCache() {
        throw new UnsupportedOperationException();
    }


    @Deprecated
    default Function<ArmyException, RuntimeException> exceptionFunction() {
        throw new UnsupportedOperationException();
    }


    interface SessionBuilderSpec<B, S extends Session> {

        B name(@Nullable String name);

        /**
         * Optional,default is {@link SessionFactory#isReadonly()}
         */
        B readonly(boolean readonly);

        B allowQueryInsert(boolean allow);

        B visibleMode(Visible visible);

        S build() throws SessionException;


    }

}
