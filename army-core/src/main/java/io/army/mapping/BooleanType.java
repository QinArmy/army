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
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.array.BooleanArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

/**
 * <p>This class is mapping class of {@link Boolean}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte},non-zero is true</li>
 *     <li>{@link Short},non-zero is true</li>
 *     <li>{@link Integer},non-zero is true</li>
 *     <li>{@link Long},non-zero is true</li>
 *     <li>{@link java.math.BigInteger},non-zero is true</li>
 *     <li>{@link java.math.BigDecimal},non-zero is true</li>
 *     <li>{@link String} , true or false ,case insensitive</li>
 * </ul>
 *  to {@link Boolean},if overflow,throw {@link io.army.ArmyException}
 * * @since 0.6.0
 *
 * @see <a href="https://www.postgresql.org/docs/current/catalog-pg-type.html">Postgre pg_type table ,oid : 16</a>
 */
public final class BooleanType extends _ArmyNoInjectionMapping {


    public static BooleanType from(Class<?> fieldType) {
        if (fieldType != Boolean.class) {
            throw errorJavaType(BooleanType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final BooleanType INSTANCE = new BooleanType();


    public static final String TRUE = "TRUE";

    public static final String FALSE = "FALSE";

    /**
     * private constructor
     */
    private BooleanType() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }


    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return BooleanArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType sqlType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlType = MySQLType.BOOLEAN;
                break;
            case PostgreSQL:
                sqlType = PostgreType.BOOLEAN;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return sqlType;
    }


    @Override
    public Boolean convert(MappingEnv env, Object source) throws CriteriaException {
        return toBoolean(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Boolean beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toBoolean(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Boolean afterGet(DataType dataType, MappingEnv env, final Object source) {
        return toBoolean(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    public static boolean toBoolean(final MappingType type, final DataType dataType, final Object source,
                                    final ErrorHandler errorHandler) {
        final boolean value;
        if (source instanceof Boolean) {
            value = (Boolean) source;
        } else if (source instanceof Integer) {
            value = ((Integer) source) != 0;
        } else if (source instanceof Short || source instanceof Byte) {
            value = ((Number) source).intValue() != 0;
        } else if (source instanceof Long) {
            value = ((Long) source) != 0L;
        } else if (source instanceof String) {
            switch (((String) source).toLowerCase(Locale.ROOT)) {
                case "true":
                case "on":
                case "yes":
                case "1":
                case "y":
                case "t":
                    value = true;
                    break;
                case "false":
                case "off":
                case "no":
                case "n":
                case "f":
                case "0":
                    value = false;
                    break;
                default:
                    throw errorHandler.apply(type, dataType, source, null);
            }
        } else if (source instanceof BigDecimal) {
            value = BigDecimal.ZERO.compareTo((BigDecimal) source) != 0;
        } else if (source instanceof BigInteger) {
            value = BigInteger.ZERO.compareTo((BigInteger) source) != 0;
        } else if (source instanceof Double || source instanceof Float) {
            value = Double.compare(((Number) source).doubleValue(), 0.0D) != 0;
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }


}
