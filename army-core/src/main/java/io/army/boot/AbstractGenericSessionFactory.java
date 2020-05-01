package io.army.boot;

import io.army.ErrorCode;
import io.army.GenericSessionFactory;
import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.codec.FieldCodec;
import io.army.env.Environment;
import io.army.generator.MultiGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractGenericSessionFactory implements GenericSessionFactory {

    private static final Map<String, AbstractGenericSessionFactory> FACTORY_MAP = new ConcurrentHashMap<>(3);


    final String name;

    final Environment env;

    final ZoneId zoneId;

    final SchemaMeta schemaMeta;

    final Map<Class<?>, TableMeta<?>> tableMetaMap;

    final Map<FieldMeta<?, ?>, MultiGenerator> fieldGeneratorMap;

    final Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain;

    final Map<TableMeta<?>, Map<FieldMeta<?, ?>, FieldCodec>> tableFieldCodecMap;

    final ShardingMode shardingMode;

    final boolean readOnly;


    AbstractGenericSessionFactory(String name, Environment env, Collection<FieldCodec> fieldCodecs) {
        Assert.hasText(name, "name required");
        Assert.notNull(env, "env required");

        if (FACTORY_MAP.putIfAbsent(name, this) != this) {
            throw new SessionFactoryException(ErrorCode.FACTORY_NAME_DUPLICATION, "factory name[%s] duplication", name);
        }
        this.name = name;
        this.env = env;
        this.schemaMeta = SessionFactoryUtils.obtainSchemaMeta(this.name, env);
        this.zoneId = SessionFactoryUtils.createZoneId(env, this.name);

        this.tableMetaMap = SessionFactoryUtils.scanPackagesForMeta(this.schemaMeta, env);
        SessionFactoryUtils.GeneratorWrapper generatorWrapper =
                SessionFactoryUtils.createGeneratorWrapper(this.tableMetaMap.values(), this.env);
        this.fieldGeneratorMap = generatorWrapper.getGeneratorChain();
        this.tableGeneratorChain = generatorWrapper.getTableGeneratorChain();
        this.shardingMode = SessionFactoryUtils.shardingMode(this.name, env);

        this.readOnly = SessionFactoryUtils.readOnly(this.name, this.env);
        this.tableFieldCodecMap = SessionFactoryUtils.createTableFieldCodecMap(fieldCodecs);
    }


    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Environment environment() {
        return this.env;
    }

    @Override
    public ZoneId zoneId() {
        return this.zoneId;
    }

    @Override
    public SchemaMeta schemaMeta() {
        return this.schemaMeta;
    }

    @Override
    public Map<Class<?>, TableMeta<?>> tableMetaMap() {
        return this.tableMetaMap;
    }

    @Override
    public Map<FieldMeta<?, ?>, MultiGenerator> fieldGeneratorMap() {
        return this.fieldGeneratorMap;
    }

    @Override
    public Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain() {
        return this.tableGeneratorChain;
    }

    @Override
    public Map<TableMeta<?>, Map<FieldMeta<?, ?>, FieldCodec>> tableFieldCodecMap() {
        return this.tableFieldCodecMap;
    }


    @Override
    public ShardingMode shardingMode() {
        return this.shardingMode;
    }


    @Override
    public boolean readonly() {
        return this.readOnly;
    }

}
