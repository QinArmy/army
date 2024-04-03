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
import io.army.mapping.array.FloatArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLiteType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * This class is mapping class of {@link Float}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte}</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Float}</li>
 *     <li>{@link Boolean},true:1f,false:0f</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link Float},if overflow,throw {@link io.army.ArmyException}
 * * @since 0.6.0
 */
public final class FloatType extends _NumericType._FloatNumericType {


    public static FloatType from(final Class<?> fieldType) {
        if (fieldType != Float.class) {
            throw errorJavaType(FloatType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final FloatType INSTANCE = new FloatType();

    /**
     * private constructor
     */
    private FloatType() {
    }

    @Override
    public Class<?> javaType() {
        return Float.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return FloatArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.FLOAT;
                break;
            case PostgreSQL:
                dataType = PostgreType.REAL;
                break;
            case SQLite:
                dataType = SQLiteType.FLOAT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }


    @Override
    public Float convert(MappingEnv env, Object source) throws CriteriaException {
        return convertToFloat(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Float beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return convertToFloat(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Float afterGet(DataType dataType, MappingEnv env, Object source) {
        return convertToFloat(this, dataType, source, ACCESS_ERROR_HANDLER);
    }

    private static float convertToFloat(final MappingType type, final DataType dataType, final Object source,
                                        final ErrorHandler errorHandler) {
        final float value;
        if (source instanceof Float) {
            value = (Float) source;
        } else if (source instanceof Short
                || source instanceof Byte) {
            value = ((Number) source).floatValue();
        } else if (source instanceof String) {
            try {
                value = Float.parseFloat((String) source);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof Boolean) {
            value = ((Boolean) source) ? 1f : 0f;
        } else if (source instanceof BigDecimal) {
            try {
                value = Float.parseFloat(((BigDecimal) source).toPlainString());
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof BigInteger) {
            try {
                value = Float.parseFloat(source.toString());
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }


}
