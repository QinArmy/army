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

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.executor.DataAccessException;
import io.army.executor.StmtExecutor;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.session.ParamException;
import io.army.sqltype.ArmyType;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.SQLType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util.ArrayUtils;
import io.army.util.ClassUtils;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.*;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.BitSet;
import java.util.List;
import java.util.function.BiFunction;

abstract sealed class AbstractMappingType extends MappingSupport implements MappingType permits _ArmyBuildInType, UserMappingType {


    @Deprecated
    protected static final BiFunction<MappingType, Object, ArmyException> PARAM_ERROR_HANDLER_0 = AbstractMappingType::paramError0;

    @Deprecated
    protected static final BiFunction<MappingType, Object, ArmyException> DATA_ACCESS_ERROR_HANDLER_0 = AbstractMappingType::dataAccessError0;

    protected static final BiFunction<MappingType, ServerMeta, UnsupportedDialectException> MAP_ERROR_HANDLER = AbstractMappingType::mapError;

    protected static final MappingSupport.ErrorHandler PARAM_ERROR_HANDLER = AbstractMappingType::paramError;

    protected static final MappingSupport.ErrorHandler ACCESS_ERROR_HANDLER = AbstractMappingType::dataAccessError;

    /**
     * package constructor
     */
    AbstractMappingType() {
    }

    @Override
    public final MappingType mappingType() {
        return this;
    }

    @Override
    public final TypeMeta typeMeta() {
        return this;
    }


    /**
     * @throws CriteriaException when this instance don't support array type.
     */
    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        throw dontSupportArrayType(this);
    }

    /**
     * <p>Find compatible {@link AbstractMappingType} for targetType.
     *
     * @param dataType   from {@link StmtExecutor}, underlying api is one of following :
     *                   <ul>
     *                        <li>{@code  java.sql.ResultSetMetaData#getTableName(int)}</li>
     *                        <li>{@code io.jdbd.result.ResultRowMeta#getDataType(int)}</li>
     *                   </ul>
     *                   if dataType is {@link SQLType} instance,then dataType representing appropriate database build-in data type.
     * @param targetType the target type for application developer
     * @throws NoMatchMappingException throw when not found compatible {@link AbstractMappingType}
     */
    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType)
            throws NoMatchMappingException {
        if (!(dataType instanceof SQLType)) {
            throw noMatchCompatibleMapping(this, targetType);
        }

        final ArmyType armyType = ((SQLType) dataType).armyType();
        final MappingType type;
        switch (armyType) {
            case BOOLEAN:
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INTEGER:
            case BIGINT:

            case DECIMAL:
            case NUMERIC:
            case FLOAT:
            case DOUBLE: {

                if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else if (targetType == Integer.class || targetType == int.class) {
                    type = IntegerType.INSTANCE;
                } else if (targetType == Long.class || targetType == long.class) {
                    type = LongType.INSTANCE;
                } else if (targetType == Short.class || targetType == short.class) {
                    type = ShortType.INSTANCE;
                } else if (targetType == Byte.class || targetType == byte.class) {
                    type = ByteType.INSTANCE;
                } else if (targetType == BigDecimal.class) {
                    type = BigDecimalType.INSTANCE;
                } else if (targetType == BigInteger.class) {
                    type = BigIntegerType.INSTANCE;
                } else if (targetType == Float.class) {
                    type = FloatType.INSTANCE;
                } else if (targetType == Double.class) {
                    type = DoubleType.INSTANCE;
                } else if (targetType == YearMonth.class) {    // for example :  https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_period-add
                    switch (armyType) {
                        case BIGINT:
                        case INTEGER:
                            type = YearMonthType.INSTANCE;
                            break;
                        default:
                            throw noMatchCompatibleMapping(this, targetType);
                    }
                } else if (targetType == Year.class) {
                    switch (armyType) {
                        case SMALLINT:
                        case INTEGER:
                            type = YearType.INSTANCE;
                            break;
                        default:
                            throw noMatchCompatibleMapping(this, targetType);
                    }
                } else if (targetType == Instant.class && armyType == ArmyType.BIGINT) {
                    type = InstantType.INSTANCE;
                } else switch (armyType) {
                    case INTEGER:
                    case BIGINT: { // compute result
                        if (DayOfWeek.class.isAssignableFrom(targetType)) {
                            type = DayOfWeekType.DEFAULT;
                        } else if (Month.class.isAssignableFrom(targetType)) {
                            type = MonthType.DEFAULT;
                        } else if (CodeEnum.class.isAssignableFrom(targetType)) {
                            type = CodeEnumType.from(targetType);
                        } else {
                            throw noMatchCompatibleMapping(this, targetType);
                        }
                    }
                    break;
                    default:
                        throw noMatchCompatibleMapping(this, targetType);
                } // switch
            }
            break;
            case TINYINT_UNSIGNED:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT_UNSIGNED:
            case INTEGER_UNSIGNED:
            case BIGINT_UNSIGNED:
            case DECIMAL_UNSIGNED: {

                if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else if (targetType == Integer.class || targetType == int.class) {
                    type = UnsignedMediumIntType.INSTANCE;
                } else if (targetType == Long.class || targetType == long.class) {
                    type = UnsignedSqlIntType.INSTANCE;
                } else if (targetType == Short.class || targetType == short.class) {
                    type = UnsignedTinyIntType.INSTANCE;
                } else if (targetType == BigDecimal.class) {
                    type = UnsignedBigDecimalType.INSTANCE;
                } else if (targetType == BigInteger.class) {
                    type = UnsignedBigIntegerType.INSTANCE;
                } else if (targetType == Float.class) {
                    type = FloatType.INSTANCE;
                } else if (targetType == DoubleType.class) {
                    type = DoubleType.INSTANCE;
                } else {
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;
            case CHAR:
            case VARCHAR:

            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT: {
                if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else if (Number.class.isAssignableFrom(targetType)) {

                    if (targetType == Integer.class || targetType == int.class) {
                        type = IntegerType.INSTANCE;
                    } else if (targetType == Long.class || targetType == long.class) {
                        type = LongType.INSTANCE;
                    } else if (targetType == Short.class || targetType == short.class) {
                        type = ShortType.INSTANCE;
                    } else if (targetType == Byte.class || targetType == byte.class) {
                        type = ByteType.INSTANCE;
                    } else if (targetType == BigDecimal.class) {
                        type = BigDecimalType.INSTANCE;
                    } else if (targetType == BigInteger.class) {
                        type = BigIntegerType.INSTANCE;
                    } else if (targetType == Float.class) {
                        type = FloatType.INSTANCE;
                    } else if (targetType == DoubleType.class) {
                        type = DoubleType.INSTANCE;
                    } else {
                        throw noMatchCompatibleMapping(this, targetType);
                    }
                } else if (Temporal.class.isAssignableFrom(targetType)) {

                    if (targetType == LocalDateTime.class) {
                        type = LocalDateTimeType.INSTANCE;
                    } else if (targetType == OffsetDateTime.class || targetType == ZonedDateTime.class) {
                        type = OffsetDateTimeType.INSTANCE;
                    } else if (targetType == LocalTime.class) {
                        type = LocalTimeType.INSTANCE;
                    } else if (targetType == OffsetTime.class) {
                        type = OffsetTimeType.INSTANCE;
                    } else if (targetType == YearMonth.class) {
                        type = YearMonthType.INSTANCE;
                    } else if (targetType == Year.class) {
                        type = YearType.INSTANCE;
                    } else if (targetType == Instant.class) {
                        type = InstantType.INSTANCE;
                    } else {
                        throw noMatchCompatibleMapping(this, targetType);
                    }
                } else if (TemporalAmount.class.isAssignableFrom(targetType)) {
                    // TODO
                    throw noMatchCompatibleMapping(this, targetType);
                } else if (Enum.class.isAssignableFrom(targetType)) {
                    if (TextEnum.class.isAssignableFrom(targetType)) {
                        type = TextEnumType.from(targetType);
                    } else if (DayOfWeek.class.isAssignableFrom(targetType)) {
                        type = DayOfWeekType.DEFAULT;
                    } else if (Month.class.isAssignableFrom(targetType)) {
                        type = MonthType.DEFAULT;
                    } else {
                        type = NameEnumType.from(targetType);
                    }
                } else if (targetType == Path.class) {
                    type = PathType.INSTANCE;
                } else if (targetType == ZoneOffset.class) {
                    type = ZoneOffsetType.INSTANCE;
                } else if (targetType == ZoneId.class) {
                    type = ZoneIdType.INSTANCE;
                } else {
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;
            case ENUM: {
                if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else if (!Enum.class.isAssignableFrom(targetType)) {
                    throw noMatchCompatibleMapping(this, targetType);
                } else if (TextEnumType.class.isAssignableFrom(targetType)) {
                    type = TextEnumType.from(targetType);
                } else if (DayOfWeek.class.isAssignableFrom(targetType)) {
                    type = DayOfWeekType.DEFAULT;
                } else if (Month.class.isAssignableFrom(targetType)) {
                    type = MonthType.DEFAULT;
                } else {
                    type = NameEnumType.from(targetType);
                }
            }
            break;
            case JSON: {
                if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else {
                    type = JsonType.from(targetType);
                }
            }
            break;
            case JSONB: {
                if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else {
                    type = JsonbType.from(targetType);
                }
            }
            break;
            case XML:
                type = XmlType.from(targetType);
                break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case MEDIUMBLOB:
            case BLOB:
            case LONGBLOB: {
                if (targetType == byte[].class) {
                    type = VarBinaryType.from(targetType);
                } else if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else { // TODO
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;
            case YEAR: {
                if (targetType == Year.class) {
                    type = YearType.INSTANCE;
                } else if (targetType == Integer.class) {
                    type = IntegerType.INSTANCE;
                } else if (targetType == Short.class) {
                    type = ShortType.INSTANCE;
                } else if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else {
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;
            case MONTH_DAY: {
                if (targetType == MonthDay.class) {
                    type = MonthDayType.INSTANCE;
                } else if (targetType == LocalDate.class) {
                    type = LocalDateType.INSTANCE;
                } else if (targetType == Month.class) {
                    type = MonthType.DEFAULT;
                } else if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else {
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;
            case YEAR_MONTH: {
                if (targetType == YearMonth.class) {
                    type = YearMonthType.INSTANCE;
                } else if (targetType == LocalDate.class) {
                    type = LocalDateType.INSTANCE;
                } else if (targetType == Year.class) {
                    type = YearType.INSTANCE;
                } else if (targetType == Month.class) {
                    type = MonthType.DEFAULT;
                } else if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else {
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;
            case TIME: {

                if (targetType == LocalTime.class) {
                    type = LocalTimeType.INSTANCE;
                } else if (targetType == OffsetTime.class) {

                    switch (((SQLType) dataType).database()) {
                        case MySQL:
                            type = OffsetTimeType.INSTANCE;
                            break;
                        case SQLite:
                        case PostgreSQL:
                        default:
                            throw noMatchCompatibleMapping(this, targetType);
                    }

                } else if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else if (targetType != Duration.class) {
                    throw noMatchCompatibleMapping(this, targetType);
                } else if (dataType == MySQLType.TIME) {
                    type = DurationType.INSTANCE;
                } else {
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;
            case TIME_WITH_TIMEZONE: {

                if (targetType == OffsetTime.class) {
                    type = OffsetTimeType.INSTANCE;
                } else if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else {
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;
            case DATE: {

                if (targetType == LocalDate.class) {
                    type = LocalDateType.INSTANCE;
                } else if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else if (targetType == YearMonth.class) {
                    type = YearMonthType.INSTANCE;
                } else if (targetType == MonthDay.class) {
                    type = MonthDayType.INSTANCE;
                } else if (targetType == Year.class) {
                    type = YearType.INSTANCE;
                } else if (targetType == Month.class) {
                    type = MonthType.DEFAULT;
                } else if (targetType == DayOfWeek.class) {
                    type = DayOfWeekType.DEFAULT;
                } else {
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;
            case TIMESTAMP: {

                if (targetType == LocalDateTime.class) {
                    type = LocalDateTimeType.INSTANCE;
                } else if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else switch (((SQLType) dataType).database()) {
                    case MySQL: {
                        if (targetType == OffsetDateTime.class) {
                            type = OffsetDateTimeType.INSTANCE;
                        } else if (targetType == ZonedDateTime.class) {
                            type = ZonedDateTimeType.INSTANCE;
                        } else {
                            throw noMatchCompatibleMapping(this, targetType);
                        }
                    }
                    break;
                    case SQLite:
                    case PostgreSQL:
                    default:
                        throw noMatchCompatibleMapping(this, targetType);
                } // else switch
            }
            break;
            case TIMESTAMP_WITH_TIMEZONE: {

                if (targetType == OffsetDateTime.class) {
                    type = OffsetDateTimeType.INSTANCE;
                } else if (targetType == ZonedDateTime.class) {
                    type = ZonedDateTimeType.INSTANCE;
                } else if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else {
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;

            case BIT:
            case VARBIT: {
                if (targetType == BitSet.class) {
                    type = BitSetType.INSTANCE;
                } else if (targetType == String.class) {
                    type = StringType.INSTANCE;
                } else if (targetType == Long.class || targetType == long.class) {
                    type = LongType.INSTANCE;
                } else if (targetType == Integer.class || targetType == int.class) {
                    type = IntegerType.INSTANCE;
                } else {
                    throw noMatchCompatibleMapping(this, targetType);
                }
            }
            break;

            case DURATION:
            case PERIOD:
            case INTERVAL:

            case UNKNOWN:
            case ARRAY:
            case COMPOSITE:
            case ROWID:
            case REF_CURSOR:
            case GEOMETRY:
            case DIALECT_TYPE:
            default:
                throw noMatchCompatibleMapping(this, targetType);
        }
        return type;
    }

    @Override
    public boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (!(this instanceof SqlArrayType)) {
            match = this.getClass().isInstance(type);
        } else if (this instanceof UnaryGenericsMapping.ListMapping) {
            final Class<?> thisClass, typeClass;
            thisClass = this.getClass();
            typeClass = type.getClass();
            match = thisClass == typeClass || thisClass.getSuperclass() == typeClass;
        } else if (type instanceof UnaryGenericsMapping.ListMapping) {
            final Class<?> thisClass, typeClass;
            thisClass = this.getClass();
            typeClass = type.getClass();
            match = thisClass == typeClass || typeClass.getSuperclass() == thisClass;
        } else if (this.getClass().isInstance(type)) {
            final int thisDimension;
            thisDimension = ArrayUtils.dimensionOfArrayMapping(this.javaType());
            match = thisDimension == ArrayUtils.dimensionOfArrayMapping(type.javaType());
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    /**
     * @see #isSameType(MappingType)
     */
    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }


    /**
     * Throws CloneNotSupportedException.  This guarantees that MappingType
     * are never cloned
     *
     * @return (never returns)
     */
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getName())
                .append("[javaType:")
                .append(this.javaType().getName())
                .append(",hash:")
                .append(System.identityHashCode(this));

        if (this instanceof AbstractMappingType.GenericsMappingType) {
            if (this instanceof UnaryGenericsMapping) {
                builder.append(",unary generic type:")
                        .append(((UnaryGenericsMapping<?>) this).genericsType().getName());
            } else if (this instanceof DualGenericsMapping) {
                final DualGenericsMapping<?, ?> dual = (DualGenericsMapping<?, ?>) this;
                builder.append(",dual generics first type:")
                        .append(dual.firstGenericsType().getName())
                        .append(",dual generic second type:")
                        .append(dual.secondGenericsType().getName());
            } else if (this instanceof MultiGenericsMappingType) {
                final List<Class<?>> list = ((MultiGenericsMappingType) this).genericsTypeList();
                final int listSize = list.size();
                for (int i = 0; i < listSize; i++) {
                    builder.append(",multi generics[")
                            .append(i)
                            .append("]:")
                            .append(list.get(i).getName());
                }
            }
        }
        return builder.append(']')
                .toString();
    }


    /**
     * prevent default deserialization
     */
    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        throw new InvalidObjectException(String.format("can't deserialize %s", AbstractMappingType.class.getName()));
    }

    /**
     * prevent default deserialization
     */
    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException(String.format("can't deserialize %s", AbstractMappingType.class.getName()));
    }


    /*-------------------below private methods -------------------*/

    @Deprecated
    protected final ParamException notSupportConvertAfterGet(final Object nonNull) {
        String m = String.format("Not support convert from %s to %s.", nonNull, javaType().getName());
        return new ParamException(m);
    }


    @Deprecated
    protected final ParamException outRangeOfType(final Object nonNull, @Nullable final Throwable cause) {
        String m = String.format("Parameter[%s] value[%s] out of range %s"
                , nonNull.getClass().getName(), nonNull, this);
        return cause == null ? new ParamException(m) : new ParamException(m, cause);
    }

    @Deprecated
    protected static DataAccessException errorJavaTypeForSqlType(DataType sqlType, final Object nonNull) {
        String m = String.format("Statement executor passing error java type[%s] for %s.%s ."
                , nonNull.getClass().getName(), sqlType.getClass().getSimpleName(), sqlType.name());
        return new DataAccessException(m);
    }


    protected static DataAccessException errorValueForSqlType(DataType sqlType, final Object nonNull
            , @Nullable Throwable cause) {
        final String m = String.format("Statement executor passing error java type[%s] value for %s.%s ."
                , nonNull.getClass().getName(), sqlType.getClass().getSimpleName(), sqlType.name());
        final DataAccessException exception;
        if (cause == null) {
            exception = new DataAccessException(m);
        } else {
            exception = new DataAccessException(m, cause);
        }
        return exception;
    }


    protected static CriteriaException valueOutRange(DataType sqlType, final Object nonNull,
                                                     @Nullable Throwable cause) {
        return _Exceptions.valueOutRange(sqlType, nonNull, cause);
    }

    protected static CriteriaException outRangeOfSqlType(DataType sqlType, final Object nonNull) {
        return _Exceptions.outRangeOfSqlType(sqlType, nonNull);
    }

    protected static CriteriaException outRangeOfSqlType(DataType sqlType, final Object nonNull
            , @Nullable Throwable cause) {
        return _Exceptions.outRangeOfSqlType(sqlType, nonNull, cause);
    }

    protected static NoMatchMappingException noMatchCompatibleMapping(AbstractMappingType type, Class<?> targetJavaType) {
        String m = String.format("%s not found match %s for %s", type, AbstractMappingType.class.getName(),
                targetJavaType.getName());
        return new NoMatchMappingException(m);
    }

    protected static CriteriaException dontSupportArrayType(AbstractMappingType type) {
        String m = String.format("%s don't support array type.", type);
        return new CriteriaException(m);
    }


    @Deprecated
    protected final UnsupportedDialectException noMappingError(ServerMeta serverMeta) {
        String m = String.format("No mapping from java type[%s] to Server[%s]", javaType(), serverMeta);
        return new UnsupportedDialectException(m);
    }

    protected static IllegalArgumentException errorJavaType(
            Class<? extends AbstractMappingType> mappingMetaClass, Class<?> javaType) {
        return new IllegalArgumentException(
                String.format("%s don't support java type[%s].", mappingMetaClass.getName(), javaType.getName()));
    }

    protected static IllegalArgumentException valueOutOfMapping(final Object nonNull
            , Class<? extends AbstractMappingType> typeClass) {
        String m = String.format("value[%s] out of range of %s .", nonNull, typeClass.getName());
        return new IllegalArgumentException(m);
    }


    private static CriteriaException paramError0(final MappingType type, final Object nonNull) {
        return new CriteriaException(createConvertErrorMessage(type, nonNull));
    }

    private static CriteriaException paramError(final MappingType type, DataType sqlType,
                                                final Object nonNull, final @Nullable Throwable cause) {
        final CriteriaException e;
        if (cause == null) {
            e = new CriteriaException(createConvertErrorMessage(type, nonNull));
        } else {
            e = new CriteriaException(createConvertErrorMessage(type, nonNull), cause);
        }
        return e;
    }

    private static DataAccessException dataAccessError(final MappingType type, DataType sqlType,
                                                       final Object nonNull, final @Nullable Throwable cause) {
        final DataAccessException e;
        if (cause == null) {
            e = new DataAccessException(createConvertErrorMessage(type, nonNull));
        } else {
            e = new DataAccessException(createConvertErrorMessage(type, nonNull), cause);
        }
        return e;
    }

    private static DataAccessException dataAccessError0(final MappingType type, final Object nonNull) {
        return new DataAccessException(createConvertErrorMessage(type, nonNull));
    }

    private static UnsupportedDialectException mapError(final MappingType type, final ServerMeta meta) {
        String m = String.format("%s don't support %s", type, meta);
        return new UnsupportedDialectException(m);
    }

    private static String createConvertErrorMessage(final MappingType type, final Object nonNull) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ClassUtils.safeClassName(nonNull));

        if (type instanceof _ArmyNoInjectionType
                || nonNull instanceof Number
                || nonNull instanceof Enum
                || nonNull instanceof Temporal) {
            builder.append('[')
                    .append(nonNull)
                    .append(']');
        }
        return builder.append(" couldn't be converted by ")
                .append(type)
                .toString();
    }





    /*-------------------below protected interfaces -------------------*/


}
