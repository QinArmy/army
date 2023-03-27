package io.army.env;

import io.qinarmy.env.convert.Converter;
import io.qinarmy.env.convert.ConverterManager;
import io.qinarmy.env.convert.ImmutableConverterManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class StandardEnvironment implements ArmyEnvironment {

    public static StandardEnvironment from(Map<String, String> map) {
        final ConverterManager converterManager;
        converterManager = ImmutableConverterManager.create(consumer -> {
        });
        return new StandardEnvironment(converterManager, map);
    }

    public static StandardEnvironment create(ConverterManager converterManager, Map<String, String> map) {
        return new StandardEnvironment(converterManager, map);
    }


    private final ConverterManager converterManager;

    private final Map<String, String> map;

    private StandardEnvironment(ConverterManager converterManager, Map<String, String> map) {
        this.converterManager = converterManager;
        this.map = Collections.unmodifiableMap(new HashMap<>(map));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final ArmyKey<T> key) {
        final String textValue;
        textValue = this.map.get(key.name);
        if (textValue == null) {
            return null;
        }
        final Class<T> javaType = key.javaType;
        if (javaType == String.class) {
            return (T) textValue;
        }
        final Converter<T> converter;
        converter = this.converterManager.getConverter(javaType);
        if (converter == null) {
            String m = String.format("Not found %s for key[%s]", Converter.class.getName(), key.name);
            throw new IllegalStateException(m);
        }
        return converter.convert(textValue);
    }

    @Override
    public <T> T getRequired(final ArmyKey<T> key) {
        final T value;
        value = this.get(key);
        if (value == null) {
            String m = String.format("value of %s is null", key.name);
            throw new IllegalStateException(m);
        }
        return value;
    }

    @Override
    public <T> T getOrDefault(ArmyKey<T> key) {
        final T defaultValue;
        defaultValue = key.defaultValue;
        if (defaultValue == null) {
            String m = String.format("%s %s no default value.", ArmyKey.class.getName(), key.name);
            throw new IllegalArgumentException(m);
        }
        final T value;
        value = get(key);
        return value == null ? defaultValue : value;
    }


}
