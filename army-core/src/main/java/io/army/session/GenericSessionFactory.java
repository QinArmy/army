package io.army.session;

import io.army.codec.FieldCodec;
import io.army.criteria.NotFoundRouteException;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.sharding.TableRoute;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

public interface GenericSessionFactory {

    String name();

    ArmyEnvironment environment();

    ZoneOffset zoneOffset();

    SchemaMeta schemaMeta();

    Map<Class<?>, TableMeta<?>> tableMetaMap();

    boolean supportZone();

    @Nullable
    <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass);

    @Nullable
    FieldGenerator fieldGenerator(FieldMeta<?, ?> fieldMeta);

    @Deprecated
    Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain();

    @Deprecated
    List<FieldMeta<?, ?>> generatorChain(TableMeta<?> tableMeta);

    @Nullable
    FieldCodec fieldCodec(FieldMeta<?, ?> fieldMeta);

    FactoryMode shardingMode();

    boolean supportSessionCache();

    boolean shardingSubQueryInsert();

    /**
     * Is this factory already closed?
     *
     * @return True if this factory is already closed; false otherwise.
     */
    boolean factoryClosed();

    boolean readonly();

    boolean showSQL();

    boolean formatSQL();

    boolean allowSpanSharding();

    TableRoute tableRoute(TableMeta<?> tableMeta) throws NotFoundRouteException;
}
