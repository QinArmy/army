package io.army.boot;

import io.army.*;
import io.army.beans.BeanWrapper;
import io.army.beans.MapWrapper;
import io.army.beans.ObjectAccessorFactory;
import io.army.codec.FieldCodec;
import io.army.codec.FieldCodecReturnException;
import io.army.codec.StatementType;
import io.army.criteria.CriteriaException;
import io.army.criteria.Selection;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.PrimaryFieldMeta;
import io.army.wrapper.GenericSimpleWrapper;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class GenericSQLExecutorSupport {

    private final GenericSessionFactory genericSessionFactory;

    protected GenericSQLExecutorSupport(GenericSessionFactory genericSessionFactory) {
        this.genericSessionFactory = genericSessionFactory;
    }


    protected final Object doEncodeParam(StatementType statementType, FieldMeta<?, ?> fieldMeta, final Object value) {
        FieldCodec fieldCodec = this.genericSessionFactory.fieldCodec(fieldMeta);
        Object paramValue;
        if (fieldCodec == null) {
            throw createNoFieldCodecException(fieldMeta);
        } else {
            // encode filed param
            paramValue = fieldCodec.encode(fieldMeta, value, statementType);
            if (!fieldMeta.javaType().isInstance(paramValue)) {
                throw createCodecReturnTypeException(fieldCodec, fieldMeta);
            }
        }
        return paramValue;
    }

    protected final Object doDecodeResult(StatementType statementType, FieldMeta<?, ?> fieldMeta
            , final Object resultFromDB) {
        FieldCodec fieldCodec = this.genericSessionFactory.fieldCodec(fieldMeta);
        Object result;
        if (fieldCodec == null) {
            throw createNoFieldCodecException(fieldMeta);
        } else {
            // decode result
            result = fieldCodec.decode(fieldMeta, resultFromDB, statementType);
            if (!fieldMeta.javaType().isInstance(result)) {
                throw GenericSQLExecutorSupport.createCodecReturnTypeException(fieldCodec, fieldMeta);
            }
        }
        return result;
    }

    protected static PrimaryFieldMeta<?, ?> obtainPrimaryField(List<Selection> selectionList) {
        PrimaryFieldMeta<?, ?> primaryField;
        Selection selection = selectionList.get(0);
        if (selection instanceof PrimaryFieldMeta) {
            primaryField = (PrimaryFieldMeta<?, ?>) selection;
        } else {
            throw new CriteriaException("Domain update/insert,first selection must be PrimaryFieldMeta");
        }
        return primaryField;
    }

    protected static List<Selection> subSelectionListForSecondSQL(List<Selection> selectionList) {
        List<Selection> subSelectionList;
        if (selectionList.size() == 1) {
            subSelectionList = Collections.emptyList();
        } else {
            subSelectionList = Collections.unmodifiableList(selectionList.subList(1, selectionList.size()));
        }
        return subSelectionList;
    }

    protected static CriteriaException createDomainFirstReturningNoIdException() {
        return new CriteriaException("Domain returning insert/update/delete id value is null.");
    }

    protected static CriteriaException createDomainSecondReturningNoIdException() {
        return new CriteriaException("Domain returning insert/update/delete id value is null.");
    }


    protected static CriteriaException convertExceptionWithSQL(Throwable ex, GenericSimpleWrapper simpleWrapper) {
        return new CriteriaException(ex, "execute sql[%s] error.", simpleWrapper.sql());
    }

    protected static BeanWrapper createObjectWrapper(Class<?> resultClass, GenericSession session) {
        BeanWrapper beanWrapper;
        if (Map.class.isAssignableFrom(resultClass)) {
            if (!session.hasTransaction() || !session.sessionTransaction().readOnly()) {
                throw new UnSupportedResultClassException(resultClass
                        , "not support java.util.Map result type in non-readonly transaction.");
            }
            beanWrapper = ObjectAccessorFactory.forMapAccess(resultClass);
        } else {
            beanWrapper = ObjectAccessorFactory.forBeanPropertyAccess(resultClass);
        }
        return beanWrapper;
    }

    @SuppressWarnings("unchecked")
    protected static <T> T getWrapperInstance(BeanWrapper beanWrapper) {
        T result;
        if (beanWrapper instanceof MapWrapper) {
            result = (T) ((MapWrapper) beanWrapper).getUnmodifiableMap();
        } else {
            result = (T) beanWrapper.getWrappedInstance();
        }
        return result;
    }


    protected static boolean singleType(List<Selection> selectionList, Class<?> resultClass) {
        return selectionList.size() == 1
                && resultClass.isAssignableFrom(selectionList.get(0).mappingMeta().javaType());

    }

    protected static FieldCodecReturnException createCodecReturnTypeException(FieldCodec fieldCodec
            , FieldMeta<?, ?> fieldMeta) {
        return new FieldCodecReturnException("FieldCodec[%s] return  error,FieldMeta[%s],"
                , fieldCodec, fieldMeta);
    }

    protected static DataAccessException convertSQLException(SQLException e, String sql) {
        return new DataAccessException(ErrorCode.ACCESS_ERROR, e, "army execute sql occur error ,sql[%s]", sql);
    }

    protected static MetaException createNoFieldCodecException(FieldMeta<?, ?> fieldMeta) {
        return new MetaException("FieldMeta[%s] not found FieldCodec.", fieldMeta);
    }

    protected static OptimisticLockException createOptimisticLockException(GenericSessionFactory sessionFactory, String sql) {
        return new OptimisticLockException(
                "SessionFactory[%s] record maybe be updated or deleted by transaction,sql:%s"
                , sessionFactory.name(), sql);
    }
}
