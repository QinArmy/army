/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.DoubleArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;

/**
 * <p>
 * This class is mapping class of {@link Double}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte}</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Float}</li>
 *     <li>{@link Boolean},true:1d,false:0d</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link Double},if overflow,throw {@link io.army.ArmyException}
 *
 * @since 0.6.0
 */
public final class DoubleType extends _NumericType._FloatNumericType {


    public static DoubleType from(final Class<?> javaType) {
        if (javaType != Double.class) {
            throw errorJavaType(DoubleType.class, javaType);
        }
        return INSTANCE;
    }

    public static final DoubleType INSTANCE = new DoubleType();

    /**
     * private constructor
     */
    private DoubleType() {
    }

    @Override
    public Class<?> javaType() {
        return Double.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return DoubleArrayType.LINEAR;
    }

    @Override
    public DataType map(ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.DOUBLE;
                break;
            case PostgreSQL:
                type = PostgreType.FLOAT8;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public Object convert(MappingEnv env, final Object source) throws CriteriaException {
        return toDouble(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }


    @Override
    public Double beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toDouble(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Double afterGet(DataType dataType, MappingEnv env, Object source) {
        return toDouble(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    private static double toDouble(final MappingType type, final DataType dataType, final Object source,
                                   final ErrorHandler errorHandler) {
        final double value;
        if (source instanceof Double) {
            value = (Double) source;
        } else if (source instanceof Float
                || source instanceof Integer
                || source instanceof Short
                || source instanceof Byte) {
            value = ((Number) source).doubleValue();
        } else if (source instanceof String) {
            try {
                value = Double.parseDouble((String) source);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof Boolean) {
            value = ((Boolean) source) ? 1d : 0d;
        } else if (source instanceof BigDecimal) {
            try {
                value = Double.parseDouble(((BigDecimal) source).toPlainString());
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else {//TODO consider Long
            throw errorHandler.apply(type, dataType, source, null);
        }

        return value;
    }


}
