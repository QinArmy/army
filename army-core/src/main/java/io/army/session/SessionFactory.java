package io.army.session;

import io.army.criteria.Visible;
import io.army.env.ArmyEnvironment;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.session.executor.ExecutorFactory;

import javax.annotation.Nullable;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * <p>This interface is base interface of following:
 * <ul>
 *     <li>{@code  io.army.sync.SyncSessionFactory}</li>
 *     <li>{@code io.army.reactive.ReactiveSessionFactory}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface SessionFactory extends CloseableSpec {

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

    /**
     * See {@link ExecutorFactory#driverSpiVendor()}
     */
    String driverSpiVendor();


    boolean isSupportSavePoints();


    boolean isReadonly();


    boolean isReactive();

    boolean isSync();


    interface SessionBuilderSpec<B, R> {

        B name(@Nullable String name);

        /**
         * Optional,default is {@link SessionFactory#isReadonly()}
         */
        B readonly(boolean readonly);

        B allowQueryInsert(boolean allow);

        B visibleMode(Visible visible);

        <T> B dataSourceOption(Option<T> option, @Nullable T value);

        R build();

    }


}
