package io.army.env;

import io.army.dialect.Database;
import io.army.util._Exceptions;
import io.qinarmy.env.convert.Converter;
import io.qinarmy.env.convert.ConverterManager;
import io.qinarmy.env.convert.ImmutableConverterManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class StandardEnvironment implements ArmyEnvironment {

    public static StandardEnvironment from(Map<String, Object> map) {
        final ConverterManager converterManager;
        converterManager = ImmutableConverterManager.create(consumer -> {
        });
        return new StandardEnvironment(converterManager, map);
    }

    public static StandardEnvironment create(ConverterManager converterManager, Map<String, Object> map) {
        return new StandardEnvironment(converterManager, map);
    }


    private final ConverterManager converterManager;

    private final Map<String, Object> map;

    private StandardEnvironment(ConverterManager converterManager, Map<String, Object> map) {
        this.converterManager = converterManager;
        this.map = Collections.unmodifiableMap(new HashMap<>(map));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final ArmyKey<T> key) {
        final Object userValue;
        userValue = this.map.get(key.name);

        final Class<T> javaType = key.javaType;
        final T value;
        if (userValue == null) {
            value = key.defaultValue;
        } else if (javaType.isInstance(userValue)) {
            value = (T) userValue;
        } else if (!(userValue instanceof String)) {
            throw _Exceptions.convertFail(key, userValue, null);
        } else if (key == ArmyKey.DIALECT) {
            final Database database;
            database = this.getRequired(ArmyKey.DATABASE);
            try {
                value = (T) database.dialectOf((String) userValue);
            } catch (IllegalArgumentException e) {
                String m = String.format("%s value error,couldn't get %s.", ArmyKey.DIALECT.name, ArmyKey.DIALECT);
                throw new IllegalStateException(m);
            }
        } else {
            final Converter<T> converter;
            converter = this.converterManager.getConverter(javaType);
            if (converter == null) {
                String m = String.format("Not found %s for key[%s]", Converter.class.getName(), key.name);
                throw new IllegalStateException(m);
            }

            try {
                value = converter.convert((String) userValue);
            } catch (IllegalArgumentException e) {
                throw _Exceptions.convertFail(key, userValue, e);
            }
        }

        return value;
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
        final T value;
        value = this.get(key);
        if (value == null) {
            String m = String.format("%s %s no default value.", ArmyKey.class.getName(), key.name);
            throw new IllegalArgumentException(m);
        }
        return value;
    }


}
