package io.army;

import io.army.boot.GenericFactoryBuilder;
import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;

import java.util.Collection;

public abstract class GenericFactoryBuilderImpl implements GenericFactoryBuilder {

    private final boolean springApplication;

    protected String name;

    protected ArmyEnvironment environment;

    protected Collection<FieldCodec> fieldCodecs;

    protected ShardingMode shardingMode = ShardingMode.NO_SHARDING;

    protected GenericFactoryBuilderImpl(boolean springApplication) {
        this.springApplication = springApplication;
    }

    public final String name() {
        return name;
    }

    public final ArmyEnvironment environment() {
        return environment;
    }

    public final Collection<FieldCodec> fieldCodecs() {
        return fieldCodecs;
    }


    ShardingMode shardingMode() {
        return shardingMode;
    }

    protected boolean springApplication() {
        return this.springApplication;
    }


}
