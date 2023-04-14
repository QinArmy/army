package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.criteria.TypeInfer;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util._StringUtils;


public interface MappingType extends TypeMeta, TypeInfer {

    /**
     * @return always return this
     */
    @Override
    TypeMeta typeMeta();


    /**
     * @return always return this
     */
    @Override
    MappingType mappingType();

    Class<?> javaType();

    SqlType map(ServerMeta meta) throws NotSupportDialectException;

    /**
     * @return the instance of {@link #javaType()}.
     */
    Object convert(MappingEnv env, Object nonNull) throws CriteriaException;


    /**
     * @param type from {@link #map(ServerMeta)}
     * @return the instance of the type that {@link SqlType} allow.
     */
    Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException;

    /**
     * @param type from {@code io.army.sync.executor.StmtExecutor} or {@code io.army.reactive.executor.StmtExecutor}
     * @return the instance of {@link #javaType()}.
     */
    Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException;


    enum LengthType {

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
    interface SqlNumberOrStringType extends MappingType {

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
    interface SqlNumberOrBitType extends MappingType {

    }


    interface SqlNumberType extends SqlNumberOrStringType, SqlNumberOrBitType {

    }

    interface SqlFloatType extends SqlNumberType {

    }

    interface SqlUnsignedNumberType extends SqlNumberType {

    }

    interface SqlIntegerOrDecimalType extends SqlNumberType {

    }


    interface SqlIntegerType extends SqlIntegerOrDecimalType {

        LengthType lengthType();

    }


    interface SqlDecimalType extends SqlIntegerOrDecimalType {

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
    interface SqlSqlStringOrBinaryOrBitType {

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
    interface SqlStringOrBinaryType extends SqlSqlStringOrBinaryOrBitType {

        LengthType lengthType();
    }

    interface SqlBinaryType extends SqlStringOrBinaryType {


    }


    interface SqlStringType extends SqlStringOrBinaryType, SqlNumberOrStringType {


    }

    interface SqlTextType extends SqlStringType {


    }


    interface SqlBlobType extends SqlBinaryType {

    }

    interface SqlBitType extends SqlSqlStringOrBinaryOrBitType, SqlNumberOrBitType {

    }

    interface SqlJsonType extends MappingType {

    }

    interface SqlJsonbType extends MappingType {

    }


    interface SqlTimeValueType extends MappingType {

    }

    interface SqlTemporalType extends SqlTimeValueType {

    }


    interface SqlLocalTemporalType extends SqlTemporalType {

    }

    interface SqlOffsetTemporalType extends SqlTemporalType {

    }

    interface SqlTemporalFieldType extends SqlTimeValueType {

    }

    interface SqlTemporalAmountType extends SqlTemporalType {

    }

    interface SqlDurationType extends SqlTemporalAmountType {

    }

    interface SqlPeriodType extends SqlTemporalAmountType {

    }

    interface SqlIntervalType extends SqlTemporalAmountType {

    }

    interface SqlLocalTimeType extends SqlLocalTemporalType {

    }

    interface SqlLocalDateType extends SqlLocalTemporalType {

    }

    interface SqlLocalDateTimeType extends SqlLocalTemporalType {

    }

    interface SqlOffsetTimeType extends SqlOffsetTemporalType {

    }

    interface SqlOffsetDateTimeType extends SqlOffsetTemporalType {

    }


    interface SqlGeometryType extends MappingType {

    }

    interface SqlPointType extends SqlGeometryType {

    }

    interface SqlCurveType extends SqlGeometryType {

    }

    interface SqlLineStringType extends SqlCurveType {

    }

    interface SqlLineType extends SqlLineStringType {

    }

    interface SqlLinearRingType extends SqlLineStringType {

    }

    interface SqlSurfaceType extends SqlGeometryType {

    }

    interface SqlPolygonType extends SqlSurfaceType {

    }

    interface SqlGeometryCollectionType extends SqlGeometryType {

    }

    interface SqlMultiPointType extends SqlGeometryCollectionType {

    }

    interface SqlMultiCurveType extends SqlGeometryCollectionType {

    }

    interface SqlMultiLineStringType extends SqlMultiCurveType {

    }

    interface SqlMultiSurfaceType extends SqlGeometryCollectionType {

    }

    interface SqlMultiPolygonType extends SqlMultiSurfaceType {

    }


}
