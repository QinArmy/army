package io.army.session;

import io.army.criteria.Visible;
import io.army.env.ArmyEnvironment;
import io.army.lang.Nullable;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;

import java.time.ZoneOffset;
import java.util.Map;

/**
 * <p>This interface is base interface of following:
 * <ul>
 *     <li>{@code  io.army.sync.SyncSessionFactory}</li>
 *     <li>{@code io.army.reactive.ReactiveSessionFactory}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface SessionFactory {

    String name();

    ArmyEnvironment environment();

    ZoneOffset zoneOffset();

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


    interface SessionBuilderSpec<B, R> {

        B name(@Nullable String name);

        /**
         * Optional,default is {@link SessionFactory#isReadonly()}
         */
        B readonly(boolean readonly);

        B allowQueryInsert(boolean allow);

        B visibleMode(Visible visible);

        R build();

    }


}
