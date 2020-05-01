package io.army;

import io.army.codec.FieldCodec;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import io.army.generator.MultiGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public interface GenericSessionFactory extends AutoCloseable {


    String name();

    Environment environment();

    SQLDialect actualSQLDialect();

    ZoneId zoneId();

    SchemaMeta schemaMeta();

    Map<Class<?>, TableMeta<?>> tableMetaMap();

    Map<FieldMeta<?, ?>, MultiGenerator> fieldGeneratorMap();

    Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain();

    Map<TableMeta<?>, Map<FieldMeta<?, ?>, FieldCodec>> tableFieldCodecMap();


    ShardingMode shardingMode();

    /**
     * Is this factory already closed?
     *
     * @return True if this factory is already closed; false otherwise.
     */
    boolean closed();

    boolean hasCurrentSession();

    boolean readonly();

    boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass);
}
