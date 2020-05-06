package io.army;

import io.army.boot.FieldValuesGenerator;
import io.army.codec.FieldCodec;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public interface GenericSessionFactory {


    String name();

    Environment environment();

    SQLDialect actualSQLDialect();

    ZoneId zoneId();

    boolean supportZoneId();

    SchemaMeta schemaMeta();

    Map<Class<?>, TableMeta<?>> tableMetaMap();

    @Nullable
    FieldGenerator fieldGenerator(FieldMeta<?, ?> fieldMeta);

    Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain();

    List<FieldMeta<?, ?>> generatorChain(TableMeta<?> tableMeta);

    Map<TableMeta<?>, Map<FieldMeta<?, ?>, FieldCodec>> tableFieldCodecMap();

    Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap(TableMeta<?> tableMeta);

    FieldValuesGenerator fieldValuesGenerator();

    @Nullable
    FieldCodec fieldCodec(FieldMeta<?, ?> fieldMeta);

    ShardingMode shardingMode();

    /**
     * Is this factory already closed?
     *
     * @return True if this factory is already closed; false otherwise.
     */
    boolean closed();

    boolean hasCurrentSession();

    boolean readonly();

    boolean showSQL();

    boolean formatSQL();

    boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass);
}
