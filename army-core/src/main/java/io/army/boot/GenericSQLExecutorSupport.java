package io.army.boot;

import io.army.*;
import io.army.beans.MapWrapper;
import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ObjectWrapper;
import io.army.codec.FieldCodec;
import io.army.codec.FieldCodecReturnException;
import io.army.codec.StatementType;
import io.army.criteria.CriteriaException;
import io.army.criteria.FieldSelection;
import io.army.criteria.Selection;
import io.army.dialect.InsertException;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.PrimaryFieldMeta;
import io.army.session.GenericSession;
import io.army.session.GenericSessionFactory;
import io.army.stmt.BatchSimpleStmt;
import io.army.stmt.GenericSimpleStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class GenericSQLExecutorSupport {

    private final GenericSessionFactory genericSessionFactory;

    protected GenericSQLExecutorSupport(GenericSessionFactory genericSessionFactory) {
        this.genericSessionFactory = genericSessionFactory;
    }


    protected final Object doEncodeParam(StatementType statementType, FieldMeta<?, ?> fieldMeta, final Object value) {
//        FieldCodec fieldCodec = this.genericSessionFactory.fieldCodec(fieldMeta);
//        Object paramValue;
//        if (fieldCodec == null) {
//            throw createNoFieldCodecException(fieldMeta);
//        } else {
//            // encode filed param
//            paramValue = fieldCodec.encode(fieldMeta, value, statementType);
//            if (!fieldMeta.javaType().isInstance(paramValue)) {
//                throw createCodecReturnTypeException(fieldCodec, fieldMeta);
//            }
//        }
//        return paramValue;
        return null;
    }

    protected final Object doDecodeResult(StatementType statementType, FieldMeta<?, ?> fieldMeta
            , final Object resultFromDB) {
//        FieldCodec fieldCodec = this.genericSessionFactory.fieldCodec(fieldMeta);
//        Object result;
//        if (fieldCodec == null) {
//            throw createNoFieldCodecException(fieldMeta);
//        } else {
//            // decode result
//            result = fieldCodec.decode(fieldMeta, resultFromDB, statementType);
//            if (result.getClass() != resultFromDB.getClass()) {
//                throw GenericSQLExecutorSupport.createCodecReturnTypeException(fieldCodec, fieldMeta);
//            }
//        }
//        return result;
        return null;
    }

    protected final InsertRowsNotMatchException createBatchChildInsertNotMatchException(
            Number parentRows, Number childRows, BatchSimpleStmt sqlWrapper) {
        return new InsertRowsNotMatchException("%s,batch child insert[%s] and parent[%s] not match.sql:\n%s"
                , this.genericSessionFactory, childRows, parentRows, sqlWrapper.sql());
    }

    protected final InsertRowsNotMatchException createChildSubQueryInsertNotMatchException(Number parentRows
            , Number childRows, SimpleStmt sqlWrapper) {
        return new InsertRowsNotMatchException("%s,child subQuery insert[%s] and parent[%s] not match,sql:\n%s"
                , this.genericSessionFactory, childRows, parentRows, sqlWrapper.sql());
    }

    protected final InsertRowsNotMatchException createParentUpdateNotMatchException(Number parentRows
            , Number childRows, GenericSimpleStmt sqlWrapper) {
        return new InsertRowsNotMatchException("%s,parent update/delete[%s] and child[%s] not match,sql:\n%s"
                , this.genericSessionFactory, parentRows, childRows, sqlWrapper.sql());
    }

    protected final InsertRowsNotMatchException createParentBatchUpdateNotMatchException(int parentBatch
            , int childBatch, GenericSimpleStmt sqlWrapper) {
        return new InsertRowsNotMatchException("%s,parent update/delete batch[%s] and child[%s] not match,sql:\n%s"
                , this.genericSessionFactory, parentBatch, childBatch, sqlWrapper.sql());
    }

    protected final IllegalArgumentException createUnSupportedSQLWrapperException(Stmt stmt
            , String methodName) {
        return new IllegalArgumentException(String.format("%s,%s unsupported by %s", this.genericSessionFactory
                , stmt, methodName));
    }

    protected final OptimisticLockException createOptimisticLockException(String sql) {
        return new OptimisticLockException(
                "%s record maybe be updated or deleted by transaction,sql:\n%s"
                , this.genericSessionFactory, sql);
    }

    protected final DomainUpdateException createChildReturningNotMatchException(int firstRows, int secondRows
            , SimpleStmt childWrapper) {
        return new DomainUpdateException("%s,first returning[%s] and second[%s] not match.sql:\n%s"
                , this.genericSessionFactory, firstRows, secondRows, childWrapper.sql());
    }

    protected final InsertException createValueInsertException(Integer insertRows, GenericSimpleStmt simpleWrapper) {
        return new InsertException("expected insert 1 row,but %s rows.sql:\n%s", insertRows, simpleWrapper.sql());
    }

    /*################################## blow protected static method ##################################*/

    protected static boolean onlyIdReturning(SimpleStmt parentWrapper, SimpleStmt childWrapper) {
        List<Selection> parentSelectionList = parentWrapper.selectionList();
        List<Selection> childSelectionList = childWrapper.selectionList();
        boolean yes = false;
        if (parentSelectionList.size() == 1 && childSelectionList.size() == 1) {
            Selection parentSelect = parentSelectionList.get(0);
            Selection childSelect = childSelectionList.get(0);
            if (parentSelect instanceof FieldSelection && childSelect instanceof FieldSelection) {
                FieldMeta<?, ?> parentFieldMeta = ((FieldSelection) parentSelect).fieldMeta();
                FieldMeta<?, ?> childFieldMeta = ((FieldSelection) childSelect).fieldMeta();
                yes = parentFieldMeta instanceof PrimaryFieldMeta && childFieldMeta instanceof PrimaryFieldMeta;
            }
        }
        return yes;
    }

    protected static Selection obtainPrimaryFieldForReturning(List<Selection> selectionList) {

        if (selectionList.isEmpty()) {
            throw new CriteriaException("Domain update/insert,first selection must be PrimaryFieldMeta");
        }
        Selection selection = selectionList.get(0);
        if (selection instanceof FieldSelection
                && ((FieldSelection) selection).fieldMeta() instanceof PrimaryFieldMeta) {
            return selection;
        }
        throw new CriteriaException("Domain update/insert,first selection must be PrimaryFieldMeta");
    }


    protected static CriteriaException createDomainFirstReturningNoIdException() {
        return new CriteriaException("Domain returning insert/update/delete id value is null.");
    }

    protected static CriteriaException createDomainSecondReturningNoIdException() {
        return new CriteriaException("Domain returning insert/update/delete id value is null.");
    }


    protected static CriteriaException convertExceptionWithSQL(Throwable ex, GenericSimpleStmt simpleWrapper) {
        return new CriteriaException(ex, "execute sql[%s] error.", simpleWrapper.sql());
    }

    protected static ObjectWrapper createObjectWrapper(Class<?> resultClass, GenericSession session) {
        ObjectWrapper beanWrapper;
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
    protected static <T> T getWrapperInstance(ObjectWrapper beanWrapper) {
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
                && resultClass.isAssignableFrom(selectionList.get(0).mappingType().javaType());

    }

    protected static FieldCodecReturnException createCodecReturnTypeException(FieldCodec fieldCodec
            , FieldMeta<?, ?> fieldMeta) {
        return new FieldCodecReturnException("FieldCodec[%s] return  error,FieldMeta[%s],"
                , fieldCodec, fieldMeta);
    }

    protected static DataAccessException_0 convertSQLException(SQLException e, String sql) {
        return new DataAccessException_0(ErrorCode.ACCESS_ERROR, e, "army execute sql occur error ,sql[%s]", sql);
    }

    protected static MetaException createNoFieldCodecException(FieldMeta<?, ?> fieldMeta) {
        return new MetaException("FieldMeta[%s] not found FieldCodec.", fieldMeta);
    }


}
