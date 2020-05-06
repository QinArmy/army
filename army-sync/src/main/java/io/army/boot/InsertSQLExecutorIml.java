package io.army.boot;

import io.army.ArmyAccessException;
import io.army.ErrorCode;
import io.army.InsertRowsNotMatchException;
import io.army.UnKnownTypeException;
import io.army.beans.DomainReadonlyWrapper;
import io.army.codec.FieldCodec;
import io.army.criteria.CriteriaException;
import io.army.dialect.Dialect;
import io.army.dialect.InsertException;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.wrapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class InsertSQLExecutorIml implements InsertSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(InsertSQLExecutorIml.class);


    private final InnerSessionFactory sessionFactory;

    InsertSQLExecutorIml(InnerSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public final List<Integer> multiInsert(InnerSession session, List<SQLWrapper> sqlWrapperList)
            throws InsertException {

        List<Integer> sqlWrapperUpdateRowList = new ArrayList<>(sqlWrapperList.size());

        final boolean showSQL = session.sessionFactory().showSQL();
        final Dialect dialect = session.dialect();
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (showSQL) {
                LOG.info("{}", dialect.showSQL(sqlWrapper));
            }
            if (sqlWrapper instanceof ChildSQLWrapper) {
                sqlWrapperUpdateRowList.add(
                        doExecuteChild(session, (ChildSQLWrapper) sqlWrapper)
                );
            } else if (sqlWrapper instanceof DomainSQLWrapper) {
                sqlWrapperUpdateRowList.add(
                        doExecuteDomain(session, (DomainSQLWrapper) sqlWrapper)
                );
            } else if (sqlWrapper instanceof SimpleSQLWrapper) {
                sqlWrapperUpdateRowList.add(
                        doExecuteSimple(session, (SimpleSQLWrapper) sqlWrapper)
                );
            } else {
                throw new IllegalArgumentException(String.format("%s supported by multiInsert", sqlWrapper));
            }
        }
        return Collections.unmodifiableList(sqlWrapperUpdateRowList);
    }

    @Override
    public final List<Integer> batchInsert(InnerSession session, List<BatchSQLWrapper> sqlWrapperList) {
        List<Integer> sqlWrapperInsertRowList = new ArrayList<>(sqlWrapperList.size());

        final boolean showSQL = session.sessionFactory().showSQL();
        final Dialect dialect = session.dialect();

        for (BatchSQLWrapper sqlWrapper : sqlWrapperList) {
            if (showSQL) {
                LOG.info("{}", dialect.showSQL(sqlWrapper));
            }
            if (sqlWrapper instanceof ChildBatchSQLWrapper) {
                sqlWrapperInsertRowList.add(
                        doExecuteChildBatch(session, (ChildBatchSQLWrapper) sqlWrapper)
                );
            } else if (sqlWrapper instanceof DomainBatchSQLWrapper) {
                sqlWrapperInsertRowList.add(
                        doExecuteDomainBatch(session, (DomainBatchSQLWrapper) sqlWrapper)
                );
            } else if (sqlWrapper instanceof SimpleBatchSQLWrapper) {
                sqlWrapperInsertRowList.add(
                        doExecuteSimpleBatch(session, (SimpleBatchSQLWrapper) sqlWrapper)
                );
            } else {
                throw new IllegalArgumentException(String.format("%s supported by batchInsert", sqlWrapper));
            }
        }
        return Collections.unmodifiableList(sqlWrapperInsertRowList);
    }

    /*################################## blow private method ##################################*/

    private int doExecuteSimple(InnerSession session, SimpleSQLWrapper sqlWrapper) {
        int rows;
        rows = doExecute(session, sqlWrapper);
        if (rows != 1) {
            throw new InsertException(ErrorCode.INSERT_ERROR
                    , "sql[%s] multiInsert rows[%s] error.", sqlWrapper.sql(), rows);
        }
        return rows;
    }

    private int doExecuteDomain(InnerSession session, DomainSQLWrapper sqlWrapper) {
        int rows;
        rows = doExecute(session, sqlWrapper);
        if (rows != 1) {
            throw new InsertException(ErrorCode.INSERT_ERROR, "sql[%s] multiInsert rows[%s] error."
                    , sqlWrapper.sql(), rows);
        }
        return rows;
    }

    private int doExecuteChild(InnerSession session, ChildSQLWrapper childSQLWrapper) {

        final SimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
        final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();

        int childRows, parentRows;
        // firstly,execute parent multiInsert sql
        parentRows = doExecute(session, parentWrapper);

        // secondly, execute child multiInsert sql
        childRows = doExecute(session, childWrapper);

        if (parentRows != childRows || parentRows != 1) {
            throw new InsertRowsNotMatchException(
                    "child sql [%s] multiInsert rows[%s] and parent sql[%s] rows[%s] not match."
                    , childWrapper.sql(), childRows, parentWrapper.sql(), parentRows);
        }
        return childRows;
    }

    private int doExecuteSimpleBatch(InnerSession session, SimpleBatchSQLWrapper sqlWrapper) {
        return assertAndSumTotal(sqlWrapper
                , doExecuteBatch(session, sqlWrapper)
        );
    }

    private int doExecuteDomainBatch(InnerSession session, DomainBatchSQLWrapper sqlWrapper) {
        return assertAndSumTotal(sqlWrapper
                , doExecuteBatch(session, sqlWrapper)
        );
    }

    private int assertAndSumTotal(SimpleBatchSQLWrapper sqlWrapper, int[] domainRows) {
        if (domainRows.length != sqlWrapper.paramGroupList().size()) {
            throw new InsertRowsNotMatchException(
                    "TableMeta[%s] multiInsert batch count error,sql[%s]"
                    , sqlWrapper.tableMeta(), sqlWrapper.sql());
        }
        int row;
        for (int i = 0; i < domainRows.length; i++) {
            row = domainRows[i];
            if (row != 1) {
                throw new InsertRowsNotMatchException(
                        "TableMeta[%s] multiInsert index[%s] actual row count[%s] not 1 ."
                        , sqlWrapper.tableMeta(), i, row);
            }
        }
        return domainRows.length;
    }

    private int doExecuteChildBatch(InnerSession session, ChildBatchSQLWrapper sqlWrapper) {

        final SimpleBatchSQLWrapper parentWrapper = sqlWrapper.parentWrapper();
        final SimpleBatchSQLWrapper childWrapper = sqlWrapper.childWrapper();

        int[] parentRows, childRows;
        // firstly, parent multiInsert sql
        parentRows = doExecuteBatch(session, parentWrapper);

        // secondly,child multiInsert sql
        childRows = doExecuteBatch(session, childWrapper);

        return assertAndSumTotal(parentWrapper, childWrapper, parentRows, childRows);
    }

    private int assertAndSumTotal(SimpleBatchSQLWrapper parentWrapper, SimpleBatchSQLWrapper childWrapper
            , int[] parentRows, int[] childRows) {
        if (parentRows.length != childRows.length || childRows.length != childWrapper.paramGroupList().size()) {
            throw new InsertRowsNotMatchException(
                    "child sql[%s] multiInsert batch count[%s] and parent sql [%s] batch count[%s] not match."
                    , childWrapper.sql(), childRows.length, parentWrapper.sql(), parentRows.length);
        }
        int parentRow;
        for (int i = 0; i < parentRows.length; i++) {
            parentRow = parentRows[i];
            if (parentRow != childRows[i] || parentRow != 1) {
                throw new InsertRowsNotMatchException(
                        "child sql[%s] multiInsert batch[%s] rows[%s] and parent sql [%s] batch[%s] rows[%s] not match."
                        , childWrapper.sql(), i, childRows[i], parentWrapper.sql(), i, parentRows[i]);
            }
        }
        return childRows.length;
    }

    private int[] doExecuteBatch(InnerSession session, SimpleBatchSQLWrapper sqlWrapper) {

        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {

            for (List<ParamWrapper> paramList : sqlWrapper.paramGroupList()) {
                // 1. set params
                setParams(st, paramList);
                // 2. add to batch
                st.addBatch();
            }
            // 3. execute batch
            return st.executeBatch();
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e
                    , "army set param occur error ,sql[%s]", sqlWrapper.sql());
        }
    }

    private int doExecute(InnerSession session, SimpleSQLWrapper sqlWrapper) {

        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), false)) {
            // 1. set params
            setParams(st, sqlWrapper.paramList());
            // 2. execute
            return st.executeUpdate();
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e
                    , "army set param occur error ,sql[%s]", sqlWrapper.sql());
        }

    }

    private void setParams(PreparedStatement st, List<ParamWrapper> paramList) throws SQLException {

        ParamWrapper wrapper;
        final int size = paramList.size();
        for (int i = 0; i < size; i++) {
            wrapper = paramList.get(i);
            Object value = wrapper.value();
            if (value == null) {
                st.setNull(i + 1, ExecutorUtils.obtainVendorTypeNumber(wrapper.paramMeta()));
            } else {
                setNonNullValue(st, i + 1, wrapper.paramMeta(), value);

            }
        }

    }

    private void setNonNullValue(PreparedStatement st, final int index, ParamMeta paramMeta, Object value)
            throws SQLException {

        MappingMeta mappingMeta;
        if (paramMeta instanceof FieldMeta) {
            FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
            value = doEncodeFieldValue(fieldMeta, value);
            mappingMeta = fieldMeta.mappingType();
        } else if (paramMeta instanceof MappingMeta) {
            mappingMeta = (MappingMeta) paramMeta;
        } else {
            throw new UnKnownTypeException(paramMeta);
        }
        // set param
        mappingMeta.nonNullSet(st, value, index);
    }

    private Object doEncodeFieldValue(FieldMeta<?, ?> fieldMeta, final Object value) {

        FieldCodec fieldCodec = this.sessionFactory.fieldCodec(fieldMeta);
        Object encodedValue;
        if (fieldCodec != null) {
            if (!(value instanceof DomainReadonlyWrapper)) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "FieldMeta[%s] criteria error,value isn't DomainReadonlyWrapper.", fieldMeta);
            }
            // obtain encoded value before setParameter() method.
            DomainReadonlyWrapper readonlyWrapper = (DomainReadonlyWrapper) value;
            Object plantObject = readonlyWrapper.getPropertyValue(fieldMeta.propertyName());
            if (plantObject == null) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "FieldMeta[%s] property value is null,can't invoke FieldCodec.", fieldMeta);
            }
            // invoke fieldCodec
            encodedValue = fieldCodec.encode(fieldMeta, plantObject);
            if (!fieldMeta.javaType().isInstance(value)) {
                throw ExecutorUtils.createCodecReturnTypeException(fieldCodec, fieldMeta, value);
            }
        } else {
            encodedValue = value;
        }
        return encodedValue;
    }


}
