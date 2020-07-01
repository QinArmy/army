package io.army.boot;

import io.army.codec.FieldCodec;
import io.army.env.Environment;

import java.util.Collection;

abstract class GenericSessionFactoryParams {

    private String name;

    private Environment environment;

    private Collection<FieldCodec> fieldCodecs;

    public String getName() {
        return name;
    }

    public GenericSessionFactoryParams setName(String name) {
        this.name = name;
        return this;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public GenericSessionFactoryParams setEnvironment(Environment environment) {
        this.environment = environment;
        return this;
    }

    public Collection<FieldCodec> getFieldCodecs() {
        return fieldCodecs;
    }

    public GenericSessionFactoryParams setFieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }
}
