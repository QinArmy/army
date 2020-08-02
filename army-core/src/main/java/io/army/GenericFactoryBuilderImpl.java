package io.army;

import io.army.boot.GenericFactoryBuilder;
import io.army.codec.FieldCodec;
import io.army.env.Environment;

import java.util.Collection;

public abstract class GenericFactoryBuilderImpl implements GenericFactoryBuilder {

    protected String name;

    protected Environment environment;

    protected Collection<FieldCodec> fieldCodecs;

    protected GenericFactoryBuilderImpl() {
    }

    public final String name() {
        return name;
    }

    public final Environment environment() {
        return environment;
    }

    public final Collection<FieldCodec> fieldCodecs() {
        return fieldCodecs;
    }
}
