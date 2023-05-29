package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.criteria.TypeInfer;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.optional.CompositeTypeField;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.session.DataAccessException;
import io.army.session.ParamException;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.function.BiFunction;

public abstract class MappingType extends MappingSupport implements TypeMeta, TypeInfer {

    protected static final BiFunction<MappingType, Object, ArmyException> PARAM_ERROR_HANDLER_0 = MappingType::paramError0;

    protected static final BiFunction<MappingType, Object, ArmyException> DATA_ACCESS_ERROR_HANDLER_0 = MappingType::dataAccessError0;

    protected static final BiFunction<MappingType, ServerMeta, NotSupportDialectException> MAP_ERROR_HANDLER = MappingType::mapError;

    protected static final MappingSupport.ErrorHandler PARAM_ERROR_HANDLER = MappingType::paramError;

    protected static final MappingSupport.ErrorHandler ACCESS_ERROR_HANDLER = MappingType::dataAccessError;

    protected MappingType() {
    }

    @Override
    public final MappingType mappingType() {
        return this;
    }

    @Override
    public final TypeMeta typeMeta() {
        return this;
    }


    public abstract Class<?> javaType();


    public abstract SqlType map(ServerMeta meta) throws NotSupportDialectException;

    /**
     * @throws CriteriaException when this instance don't support array type.
     */
    public MappingType arrayTypeOfThis() throws CriteriaException {
        throw dontSupportArrayType(this);
    }

    /**
     * @param targetType never {@code  String.class}
     */
    public abstract <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException;


    /**
     * @return the instance of {@link #javaType()}.
     */
    public abstract Object convert(MappingEnv env, Object nonNull) throws CriteriaException;


    /**
     * @param type from {@link #map(ServerMeta)}
     * @return the instance of the type that {@link SqlType} allow.
     */
    public abstract Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException;

    /**
     * @param type from {@code io.army.sync.executor.StmtExecutor} or {@code io.army.reactive.executor.StmtExecutor}
     * @return the instance of {@link #javaType()}.
     */
    public abstract Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException;

    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

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
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getName())
                .append("[javaType:")
                .append(this.javaType().getName())
                .append(",hash:")
                .append(System.identityHashCode(this));

        if (this instanceof MappingType.GenericsMappingType) {
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


    /**
     * prevent default deserialization
     */
    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        throw new InvalidObjectException(String.format("can't deserialize %s", MappingType.class.getName()));
    }

    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException(String.format("can't deserialize %s", MappingType.class.getName()));
    }


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

    protected static DataAccessException errorJavaTypeForSqlType(SqlType sqlType, final Object nonNull) {
        String m = String.format("Statement executor passing error java type[%s] for %s.%s ."
                , nonNull.getClass().getName(), sqlType.getClass().getSimpleName(), sqlType.name());
        return new DataAccessException(m);
    }


    protected static DataAccessException errorValueForSqlType(SqlType sqlType, final Object nonNull
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


    protected static CriteriaException valueOutRange(SqlType sqlType, final Object nonNull, @Nullable Throwable cause) {
        return _Exceptions.valueOutRange(sqlType, nonNull, cause);
    }

    protected static CriteriaException outRangeOfSqlType(SqlType sqlType, final Object nonNull) {
        return _Exceptions.outRangeOfSqlType(sqlType, nonNull);
    }

    protected static CriteriaException outRangeOfSqlType(SqlType sqlType, final Object nonNull
            , @Nullable Throwable cause) {
        return _Exceptions.outRangeOfSqlType(sqlType, nonNull, cause);
    }

    protected static NoMatchMappingException noMatchCompatibleMapping(MappingType type, Class<?> targetJavaType) {
        String m = String.format("%s not found match %s for %s", type, MappingType.class.getName(),
                targetJavaType.getName());
        return new NoMatchMappingException(m);
    }

    protected static CriteriaException dontSupportArrayType(MappingType type) {
        String m = String.format("%s don't support array type.", type);
        return new CriteriaException(m);
    }


    @Deprecated
    protected final NotSupportDialectException noMappingError(ServerMeta serverMeta) {
        String m = String.format("No mapping from java type[%s] to Server[%s]", javaType(), serverMeta);
        return new NotSupportDialectException(m);
    }

    protected static IllegalArgumentException errorJavaType(
            Class<? extends MappingType> mappingMetaClass, Class<?> javaType) {
        return new IllegalArgumentException(
                String.format("%s don't support java type[%s].", mappingMetaClass.getName(), javaType.getName()));
    }

    protected static IllegalArgumentException valueOutOfMapping(final Object nonNull
            , Class<? extends MappingType> typeClass) {
        String m = String.format("value[%s] out of range of %s .", nonNull, typeClass.getName());
        return new IllegalArgumentException(m);
    }


    private static CriteriaException paramError0(final MappingType type, final Object nonNull) {
        return new CriteriaException(createConvertErrorMessage(type, nonNull));
    }

    private static CriteriaException paramError(final MappingType type, SqlType sqlType,
                                                final Object nonNull, final @Nullable Throwable cause) {
        final CriteriaException e;
        if (cause == null) {
            e = new CriteriaException(createConvertErrorMessage(type, nonNull));
        } else {
            e = new CriteriaException(createConvertErrorMessage(type, nonNull), cause);
        }
        return e;
    }

    private static DataAccessException dataAccessError(final MappingType type, SqlType sqlType,
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

    private static NotSupportDialectException mapError(final MappingType type, final ServerMeta meta) {
        String m = String.format("%s don't support %s", type, meta);
        return new NotSupportDialectException(m);
    }

    private static String createConvertErrorMessage(final MappingType type, final Object nonNull) {
        final StringBuilder builder = new StringBuilder();
        builder.append(_ClassUtils.safeClassName(nonNull));

        if (type instanceof _ArmyNoInjectionMapping
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


    public interface GenericsMappingType {

        Class<?> javaType();

    }

    public enum LengthType {

        TINY(1),
        SMALL(2),
        DEFAULT(3),
        MEDIUM(4),
        LONG(5),
        BIG_LONG(6);

        private final byte value;

        LengthType(int value) {
            this.value = (byte) value;
        }

        public final int compareWith(final LengthType o) {
            return this.value - o.value;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(LengthType.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }


    }//LengthType

    /**
     * <p>
     * This interface is base interface of below:
     *     <ul>
     *         <li>{@link SqlNumberType}</li>
     *         <li>{@link SqlStringType}</li>
     *     </ul>
     * </p>
     */
    public interface SqlNumberOrStringType {

    }

    /**
     * <p>
     * This interface is base interface of below:
     *     <ul>
     *         <li>{@link SqlNumberType}</li>
     *         <li>{@link SqlBitType}</li>
     *     </ul>
     * </p>
     */
    public interface SqlNumberOrBitType {

    }

    public interface SqlNumberType extends SqlNumberOrStringType, SqlNumberOrBitType {

    }

    public interface SqlFloatType extends SqlNumberType {

    }

    public interface SqlUnsignedNumberType extends SqlNumberType {

    }

    public interface SqlIntegerOrDecimalType extends SqlNumberType {

    }

    public interface SqlIntegerType extends SqlIntegerOrDecimalType {

        LengthType lengthType();

    }

    public interface SqlDecimalType extends SqlIntegerOrDecimalType {

    }

    /**
     * <p>
     * This interface is base interface of below:
     *     <ul>
     *         <li>{@link SqlStringOrBinaryType }</li>
     *         <li>{@link SqlBitType }</li>
     *     </ul>
     * </p>
     */
    public interface SqlSqlStringOrBinaryOrBitType {

    }

    /**
     * <p>
     * This interface is base interface of below:
     *     <ul>
     *         <li>{@link SqlStringType }</li>
     *         <li>{@link SqlBinaryType }</li>
     *     </ul>
     * </p>
     */
    public interface SqlStringOrBinaryType extends SqlSqlStringOrBinaryOrBitType {

        LengthType lengthType();
    }

    public interface SqlBinaryType extends SqlStringOrBinaryType {


    }

    public interface SqlStringType extends SqlStringOrBinaryType, SqlNumberOrStringType {


    }

    public interface SqlTextType extends SqlStringType {


    }

    public interface SqlBlobType extends SqlBinaryType {

    }

    public interface SqlBitType extends SqlSqlStringOrBinaryOrBitType, SqlNumberOrBitType {

    }

    /**
     * <p>
     * This interface is base interface of below:
     *     <ul>
     *         <li>{@link SqlJsonType}</li>
     *         <li>{@link SqlJsonbType}</li>
     *     </ul>
     * </p>
     */
    public interface SqlJsonDocumentType {

    }

    public interface SqlJsonType extends SqlJsonDocumentType {

    }

    public interface SqlJsonbType extends SqlJsonDocumentType {

    }

    public interface SqlJsonPathType {

    }

    public interface SqlTimeValueType {

    }

    public interface SqlTemporalType extends SqlTimeValueType {

    }

    public interface SqlLocalTemporalType extends SqlTemporalType {

    }

    public interface SqlOffsetTemporalType extends SqlTemporalType {

    }

    public interface SqlTemporalFieldType extends SqlTimeValueType {

    }

    public interface SqlTemporalAmountType extends SqlTemporalType {

    }

    public interface SqlDurationType extends SqlTemporalAmountType {

    }

    public interface SqlPeriodType extends SqlTemporalAmountType {

    }

    public interface SqlIntervalType extends SqlTemporalAmountType {

    }

    public interface SqlLocalTimeType extends SqlLocalTemporalType {

    }

    public interface SqlLocalDateType extends SqlLocalTemporalType {

    }

    public interface SqlLocalDateTimeType extends SqlLocalTemporalType {

    }

    public interface SqlOffsetTimeType extends SqlOffsetTemporalType {

    }

    public interface SqlOffsetDateTimeType extends SqlOffsetTemporalType {

    }

    public interface SqlArrayType {

        MappingType elementType();

    }

    public interface SqlGeometryType {

    }

    public interface SqlPointType extends SqlGeometryType {

    }

    public interface SqlCurveType extends SqlGeometryType {

    }

    public interface SqlLineStringType extends SqlCurveType {

    }

    public interface SqlLineType extends SqlLineStringType {

    }

    public interface SqlLinearRingType extends SqlLineStringType {

    }

    public interface SqlSurfaceType extends SqlGeometryType {

    }

    public interface SqlPolygonType extends SqlSurfaceType {

    }

    public interface SqlGeometryCollectionType extends SqlGeometryType {

    }

    public interface SqlMultiPointType extends SqlGeometryCollectionType {

    }

    public interface SqlMultiCurveType extends SqlGeometryCollectionType {

    }

    public interface SqlMultiLineStringType extends SqlMultiCurveType {

    }

    public interface SqlMultiSurfaceType extends SqlGeometryCollectionType {

    }

    public interface SqlMultiPolygonType extends SqlMultiSurfaceType {

    }


    /**
     * private interface
     */
    private interface TypeNameSpec {

        String sqlTypeName(ServerMeta meta);
    }

    public interface SqlUserDefinedType extends TypeNameSpec {


    }

    public interface SqlCompositeType extends TypeNameSpec {

        List<CompositeTypeField> fieldList();

    }




    /*-------------------below protected interfaces -------------------*/


}
