package io.army.boot;

import io.army.DomainUpdateException;
import io.army.ErrorCode;
import io.army.OptimisticLockException;
import io.army.beans.ObjectWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.criteria.CriteriaException;
import io.army.criteria.Selection;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.util.SQLExceptionUtils;
import io.army.wrapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

final class UpdateSQLExecutorImpl extends SQLExecutorSupport implements UpdateSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateSQLExecutorImpl.class);

    static UpdateSQLExecutorImpl build(InnerSessionFactory sessionFactory) {
        return new UpdateSQLExecutorImpl(sessionFactory);
    }

    UpdateSQLExecutorImpl(InnerSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public int update(InnerSession session, UpdateSQLWrapper sqlWrapper) {
        int updateRows;
        if (sqlWrapper instanceof ChildUpdateSQLWrapper) {
            updateRows = childUpdate(session, (ChildUpdateSQLWrapper) sqlWrapper);
        } else if (sqlWrapper instanceof SimpleUpdateSQLWrapper) {
            updateRows = doSimpleUpdate(session, (SimpleUpdateSQLWrapper) sqlWrapper);
        } else {
            throw new IllegalArgumentException(String.format("%s supported by update", sqlWrapper));
        }
        return updateRows;
    }

    @Override
    public <T> List<T> returningUpdate(InnerSession session, UpdateSQLWrapper sqlWrapper, Class<T> resultClass) {
        List<T> resultList;
        if (sqlWrapper instanceof ChildReturningUpdateSQLWrapper) {
            resultList = childReturningUpdate(session, (ChildReturningUpdateSQLWrapper) sqlWrapper, resultClass);
        } else if (sqlWrapper instanceof ReturningUpdateSQLWrapper) {
            resultList = doSimpleReturningUpdate(session, (ReturningUpdateSQLWrapper) sqlWrapper, resultClass);
        } else {
            throw new IllegalArgumentException(String.format("%s supported by returningUpdate", sqlWrapper));
        }
        return resultList;
    }

    @Override
    public List<Integer> batchUpdate(InnerSession session, BatchSQLWrapper sqlWrapper) {
        List<Integer> rowsList;
        if (sqlWrapper instanceof ChildBatchUpdateSQLWrapper) {
            rowsList = childBatchUpdate(session, (ChildBatchUpdateSQLWrapper) sqlWrapper);
        } else if (sqlWrapper instanceof BatchSimpleUpdateSQLWrapper) {
            rowsList = simpleBatchUpdate(session, (BatchSimpleUpdateSQLWrapper) sqlWrapper);
        } else {
            throw new IllegalArgumentException(String.format("%s supported by batchUpdate", sqlWrapper));
        }
        return Collections.unmodifiableList(rowsList);
    }

    /*################################## blow private method ##################################*/

    private List<Integer> childBatchUpdate(InnerSession session, ChildBatchUpdateSQLWrapper sqlWrapper) {
        int[] childRows, parentRows;
        // firstly, execute child update sql
        childRows = doBatchSimpleUpdate(session, sqlWrapper.childWrapper());
        // secondly,execute parent update sql
        parentRows = doBatchSimpleUpdate(session, sqlWrapper.parentWrapper());
        if (parentRows.length != childRows.length) {
            // check domain update batch match.
            throw new DomainUpdateException(
                    "Domain update,parent sql[%s] update batch[%s] and child sql[%s] update batch[%s] not match."
                    , sqlWrapper.parentWrapper().sql()
                    , parentRows.length
                    , sqlWrapper.childWrapper().sql()
                    , childRows.length
            );
        }
        final int len = childRows.length;
        for (int i = 0; i < len; i++) {
            if (parentRows[i] != childRows[i]) {
                throw new DomainUpdateException(
                        "Domain update,parent sql[%s] update index[%s] and child sql[%s] update index [%s] not match."
                        , sqlWrapper.parentWrapper().sql()
                        , parentRows[i]
                        , sqlWrapper.childWrapper().sql()
                        , childRows[i]
                );
            }
        }
        return convertAndCheckOptimisticLock(childRows, sqlWrapper.childWrapper());
    }

    private List<Integer> simpleBatchUpdate(InnerSession session, BatchSimpleUpdateSQLWrapper sqlWrapper) {
        return convertAndCheckOptimisticLock(doBatchSimpleUpdate(session, sqlWrapper), sqlWrapper);
    }

    private List<Integer> convertAndCheckOptimisticLock(int[] updateRows, BatchSimpleUpdateSQLWrapper sqlWrapper) {
        List<Integer> rowList = new ArrayList<>(updateRows.length);
        final boolean hasVersion = sqlWrapper.hasVersion();
        for (int updateRow : updateRows) {
            if (updateRow < 1 && hasVersion) {
                throw createOptimisticLockException(sqlWrapper.sql());
            }
            rowList.add(updateRow);
        }
        return rowList;
    }

    private int childUpdate(InnerSession session, ChildUpdateSQLWrapper sqlWrapper) {

        int childRows, parentRows;
        childRows = doSimpleUpdate(session, sqlWrapper.childWrapper());

        parentRows = doSimpleUpdate(session, sqlWrapper.parentWrapper());

        if (parentRows != childRows) {
            throw new DomainUpdateException("child sql[%s] and parent sql[%s] update rows not match."
                    , sqlWrapper.childWrapper().sql()
                    , sqlWrapper.parentWrapper().sql());
        }
        return childRows;
    }


    private <T> List<T> childReturningUpdate(InnerSession session, ChildReturningUpdateSQLWrapper sqlWrapper
            , Class<T> resultClass) {

        Map<Object, ObjectWrapper> wrapperMap;
        // firstly, execute child update sql.
        wrapperMap = doChildReturningUpdate(session, sqlWrapper.childWrapper(), resultClass);

        List<T> resultList;
        // secondly,execute parent update sql
        resultList = doParentReturningUpdate(session, sqlWrapper.parentWrapper(), wrapperMap);
        if (resultList.size() != wrapperMap.size()) {
            // check domain update row count match.
            throw new DomainUpdateException(
                    "Domain update,parent sql[%s] update rows[%s] and child sql[%s] update rows[%s] not match."
                    , sqlWrapper.parentWrapper().sql()
                    , resultList.size()
                    , sqlWrapper.childWrapper().sql()
                    , wrapperMap.size()
            );
        }
        return resultList;
    }

    private Map<Object, ObjectWrapper> extractChildResult(ResultSet resultSet, List<Selection> selectionList
            , Class<?> resultClass) throws SQLException {
        Map<Object, ObjectWrapper> map = new HashMap<>();
        // check selectionList
        obtainPrimaryField(selectionList.get(0));
        ObjectWrapper objectWrapper;
        while (resultSet.next()) {
            objectWrapper = PropertyAccessorFactory.forBeanPropertyAccess(resultClass);
            // extract one row
            extractRow(resultSet, selectionList, objectWrapper);
            Object idValue;
            if (objectWrapper.isReadableProperty(TableMeta.ID)) {
                idValue = objectWrapper.getPropertyValue(TableMeta.ID);
                if (idValue == null) {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Domain update id value is null.");
                }
            } else {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Domain update must have id property.");
            }
            // result add to result map
            if (map.putIfAbsent(idValue, objectWrapper) != null) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Domain update duplication row.");
            }
        }
        return Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> extractParentResult(ResultSet resultSet, List<Selection> selectionList
            , Map<Object, ObjectWrapper> wrapperMap) throws SQLException {

        final PrimaryFieldMeta<?, ?> primaryField = obtainPrimaryField(selectionList.get(0));
        List<Selection> subSelectionList;
        if (selectionList.size() == 1) {
            subSelectionList = Collections.emptyList();
        } else {
            subSelectionList = selectionList.subList(1, selectionList.size());
        }
        List<T> resultList = new ArrayList<>(wrapperMap.size());
        while (resultSet.next()) {
            Object idValue = primaryField.mappingMeta().nullSafeGet(resultSet, primaryField.alias());
            ObjectWrapper objectWrapper = wrapperMap.get(idValue);
            if (objectWrapper == null) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Domain update,parent criteria error.");
            }
            extractRow(resultSet, subSelectionList, objectWrapper);
            resultList.add((T) objectWrapper.getWrappedInstance());
        }
        return resultList;
    }

    private PrimaryFieldMeta<?, ?> obtainPrimaryField(Selection selection) {
        PrimaryFieldMeta<?, ?> primaryField;
        if (selection instanceof PrimaryFieldMeta) {
            primaryField = (PrimaryFieldMeta<?, ?>) selection;
        } else {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Domain update,first selection must be PrimaryFieldMeta");
        }
        return primaryField;
    }

    private Map<Object, ObjectWrapper> doChildReturningUpdate(InnerSession session, ReturningUpdateSQLWrapper sqlWrapper
            , Class<?> resultClass) {
        final String[] aliasArray = asSelectionAliasArray(sqlWrapper.selectionList());
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), aliasArray)) {
            // 2. set params
            setParams(st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                Map<Object, ObjectWrapper> wrapperMap;
                //4. extract result
                wrapperMap = extractChildResult(resultSet, sqlWrapper.selectionList(), resultClass);
                if (wrapperMap.isEmpty() && sqlWrapper.hasVersion()) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
                return Collections.unmodifiableMap(wrapperMap);
            }
        } catch (SQLException e) {
            throw SQLExceptionUtils.convert(e, sqlWrapper.sql());
        }
    }

    private <T> List<T> doParentReturningUpdate(InnerSession session, ReturningUpdateSQLWrapper sqlWrapper
            , Map<Object, ObjectWrapper> wrapperMap) {
        final String[] aliasArray = asSelectionAliasArray(sqlWrapper.selectionList());
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), aliasArray)) {
            if (session.sessionFactory().showSQL()) {
                LOG.info("army will execute update sql:\n{}", session.dialect().showSQL(sqlWrapper));
            }
            // 2. set params
            setParams(st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {

                List<T> resultList;
                //4. extract result
                resultList = extractParentResult(resultSet, sqlWrapper.selectionList(), wrapperMap);

                if (resultList.isEmpty() && sqlWrapper.hasVersion()) {
                    // check optimistic lock
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
                return resultList;
            }
        } catch (SQLException e) {
            throw SQLExceptionUtils.convert(e, sqlWrapper.sql());
        }
    }

    private <T> List<T> doSimpleReturningUpdate(InnerSession session, ReturningUpdateSQLWrapper sqlWrapper
            , Class<T> resultClass) {
        final String[] aliasArray = asSelectionAliasArray(sqlWrapper.selectionList());
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql(), aliasArray)) {
            if (session.sessionFactory().showSQL()) {
                LOG.info("army will execute update sql:\n{}", session.dialect().showSQL(sqlWrapper));
            }
            // 2. set params
            setParams(st, sqlWrapper.paramList());
            // 3. execute sql
            try (ResultSet resultSet = st.executeQuery()) {
                List<T> resultList;
                //4. extract result
                resultList = extractResult(resultSet, sqlWrapper.selectionList(), resultClass);
                if (resultList.isEmpty() && sqlWrapper.hasVersion()) {
                    throw createOptimisticLockException(sqlWrapper.sql());
                }
                return resultList;
            }
        } catch (SQLException e) {
            throw SQLExceptionUtils.convert(e, sqlWrapper.sql());
        }
    }

    private int doSimpleUpdate(InnerSession session, SimpleUpdateSQLWrapper sqlWrapper) {
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql())) {
            if (session.sessionFactory().showSQL()) {
                LOG.info("army will execute update sql:\n{}", session.dialect().showSQL(sqlWrapper));
            }
            // 2. set params
            setParams(st, sqlWrapper.paramList());
            int updateRows;
            // 3. execute sql
            updateRows = st.executeUpdate();
            if (updateRows < 1 && sqlWrapper.hasVersion()) {
                throw createOptimisticLockException(sqlWrapper.sql());
            }
            return updateRows;
        } catch (SQLException e) {
            throw SQLExceptionUtils.convert(e, sqlWrapper.sql());
        }
    }

    private int[] doBatchSimpleUpdate(InnerSession session, BatchSimpleUpdateSQLWrapper sqlWrapper) {
        // 1. create statement
        try (PreparedStatement st = session.createStatement(sqlWrapper.sql())) {
            if (session.sessionFactory().showSQL()) {
                LOG.info("army will execute update sql:\n{}", session.dialect().showSQL(sqlWrapper));
            }
            // 2. set params
            for (List<ParamWrapper> paramWrappers : sqlWrapper.paramGroupList()) {
                setParams(st, paramWrappers);
                st.addBatch();
            }
            // 3. execute sql
            return st.executeBatch();
        } catch (SQLException e) {
            throw SQLExceptionUtils.convert(e, sqlWrapper.sql());
        }
    }

    private static OptimisticLockException createOptimisticLockException(String sql) {
        return new OptimisticLockException("record maybe be updated or deleted by transaction,sql:%s", sql);
    }

}
