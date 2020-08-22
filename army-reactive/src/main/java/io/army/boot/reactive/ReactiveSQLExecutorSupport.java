package io.army.boot.reactive;

import io.army.DomainUpdateException;
import io.army.beans.BeanWrapper;
import io.army.boot.GenericSQLExecutorSupport;
import io.army.codec.StatementType;
import io.army.criteria.FieldSelection;
import io.army.criteria.Selection;
import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.ParamMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.reactive.GenericReactiveRmSessionFactory;
import io.army.sqldatatype.SQLDataType;
import io.army.wrapper.BatchSimpleSQLWrapper;
import io.army.wrapper.GenericSimpleWrapper;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SimpleSQLWrapper;
import io.jdbd.PreparedStatement;
import io.jdbd.ReactiveSQLException;
import io.jdbd.ResultRow;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

abstract class ReactiveSQLExecutorSupport extends GenericSQLExecutorSupport {

    final GenericReactiveRmSessionFactory sessionFactory;

    final MappingContext mappingContext;

    ReactiveSQLExecutorSupport(GenericReactiveRmSessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
        this.mappingContext = sessionFactory.dialect().mappingContext();
    }


    /**
     * execute update method of {@link PreparedStatement},and assert optimistic lock .
     *
     * @param executeFunction {@link PreparedStatement}'s execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link PreparedStatement#executeUpdate()}</li>
     *                          <li>{@link PreparedStatement#executeLargeUpdate()}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Mono<Integer> or Mono<Long>}
     */
    protected final <N extends Number> Mono<N> doExecuteUpdate(InnerGenericRmSession session
            , SimpleSQLWrapper sqlWrapper, Function<PreparedStatement, Mono<N>> executeFunction) {
        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramList()))
                //3. execute sql
                .flatMap(executeFunction)
                //4.assert optimistic Lock
                .flatMap(updatedRows -> assertOptimisticLock(updatedRows, sqlWrapper))
                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                ;
    }

    /**
     * execute batch update method of {@link PreparedStatement},and assert optimistic lock .
     *
     * @param executeFunction {@link PreparedStatement}'s execute batch method ,must be below:
     *                        <ul>
     *                          <li>{@link PreparedStatement#executeBatch()}</li>
     *                          <li>{@link PreparedStatement#executeLargeBatch()}</li>
     *                        </ul>
     * @param <N>             result type of updated rows ,must be  {@link Integer} or {@link Long}
     * @return Mono of a unmodifiable list,{@code Mono<List<Integer>> or Mono<List<Long>>}
     */
    protected final <N extends Number> Mono<List<N>> doExecuteBatchUpdate(InnerGenericRmSession session
            , BatchSimpleSQLWrapper sqlWrapper, Function<PreparedStatement, Mono<List<N>>> executeFunction) {
        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamGroupList(st, sqlWrapper))
                //3. execute sql
                .flatMap(executeFunction)
                //4.assert optimistic Lock
                .flatMap(batchResult -> assertBatchOptimisticLock(batchResult, sqlWrapper))
                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                ;
    }


    protected final <T> Flux<T> doExecuteSimpleReturning(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper
            , Class<T> resultClass) {

        //1. decide map function
        Function<ResultRow, T> resultFunction;
        if (singleType(sqlWrapper.selectionList(), resultClass)) {
            resultFunction = row -> mapSingleResult(row, sqlWrapper);
        } else {
            resultFunction = row -> mapComplexTypeResult(session, row, sqlWrapper, resultClass);
        }
        //2. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //3. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramList()))
                //4. execute sql
                .flatMapMany(PreparedStatement::executeQuery)
                //5. map result
                .map(resultFunction)
                ;
    }


    protected final Mono<Map<Object, BeanWrapper>> doExecuteFirstSQLReturning(InnerGenericRmSession session
            , SimpleSQLWrapper sqlWrapper, Class<?> resultClass) {

        final List<Selection> selectionList = sqlWrapper.selectionList();
        final PrimaryFieldMeta<?, ?> primaryField = obtainPrimaryField(selectionList);

        //1. create statement
        return session.createPreparedStatement(sqlWrapper.sql())
                //2. bind param list
                .map(st -> bindParamList(st, sqlWrapper.statementType(), sqlWrapper.paramList()))
                //3. execute sql
                .flatMapMany(PreparedStatement::executeQuery)
                //4. assert optimistic lock
                .switchIfEmpty(assertOptimisticLockWhenEmpty(sqlWrapper))
                //5. map ResultRow to BeanWrapper
                .map(resultRow -> mapFirstSQLResult(session, resultRow, sqlWrapper, primaryField, resultClass))
                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                //6. collect bean wrapper to java.util.Map with primaryKeyValue as key.
                .collectMap(beanWrapper -> keyExtractor(beanWrapper, primaryField))
                // make java.util.Map unmodifiable.
                .map(Collections::unmodifiableMap)
                ;
    }

    protected final <T> Flux<T> doExecuteSecondSQLReturning(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper
            , Class<T> resultClass, Map<Object, BeanWrapper> wrapperMap) {
        //1. create statement
        PreparedStatement st = session.createPreparedStatement(sqlWrapper.sql());
        final StatementType statementType = sqlWrapper.statementType();
        //2. bind param list
        bindParamList(st, statementType, sqlWrapper.paramList());
        final List<Selection> subSelectionList = subSelectionListForSecondSQL(sqlWrapper.selectionList());
        final PrimaryFieldMeta<?, ?> primaryField = obtainPrimaryField(sqlWrapper.selectionList());

        //3. execute sql
        return st.executeQuery()
                //4. assert optimistic lock
                .switchIfEmpty(assertOptimisticLockWhenEmpty(sqlWrapper))
                //5. map ResultRow to T
                .map(resultRow -> mapSecondSQLResult(resultRow, wrapperMap, subSelectionList, primaryField
                        , statementType, resultClass))
                // convert exception for print sql
                .onErrorMap(ex -> convertExceptionWithSQL(ex, sqlWrapper))
                .collectList()
                // 6. assert first sql updated row and second sql updated rows match
                .flatMap(listOfSecond -> assertFirstSQLAndSecondSQLResultMatch(listOfSecond, wrapperMap, sqlWrapper))
                .flatMapMany(Flux::fromIterable)
                ;
    }


    private <T> Mono<List<T>> assertFirstSQLAndSecondSQLResultMatch(List<T> listOfSecond
            , Map<Object, BeanWrapper> wrapperMap, GenericSimpleWrapper simpleWrapper) {
        Mono<List<T>> mono;
        if (listOfSecond.size() == wrapperMap.size()) {
            mono = Mono.just(listOfSecond);
        } else {
            mono = Mono.error(new DomainUpdateException(
                    "%s updated rows[%s] and first sql[%s] not match.", simpleWrapper.sql()
                    , listOfSecond.size(), wrapperMap.size()));
        }
        return mono;
    }


    protected final BeanWrapper mapFirstSQLResult(InnerGenericRmSession session, ResultRow resultRow
            , SimpleSQLWrapper sqlWrapper, PrimaryFieldMeta<?, ?> primaryField, Class<?> resultClass) {

        BeanWrapper beanWrapper = createObjectWrapper(resultClass, session);
        for (Selection selection : sqlWrapper.selectionList()) {
            Object columnResult = extractColumnResult(resultRow, selection, sqlWrapper.statementType());
            if (columnResult == null) {
                continue;
            }
            // set columnResult to object
            beanWrapper.setPropertyValue(selection.alias(), columnResult);
        }
        if (beanWrapper.getPropertyValue(primaryField.alias()) == null) {
            throw createDomainFirstReturningNoIdException();
        }
        return beanWrapper;
    }

    @SuppressWarnings("unchecked")
    protected final <T> T mapSingleResult(ResultRow resultRow, SimpleSQLWrapper sqlWrapper) {
        return (T) extractColumnResult(resultRow, sqlWrapper.selectionList().get(0), sqlWrapper.statementType());
    }

    protected final <T> T mapComplexTypeResult(InnerGenericRmSession session, ResultRow resultRow
            , SimpleSQLWrapper sqlWrapper, Class<T> resultClass) {

        final BeanWrapper beanWrapper = createObjectWrapper(resultClass, session);

        final MappingContext mappingContext = this.mappingContext;
        for (Selection selection : sqlWrapper.selectionList()) {
            // 1. obtain column result
            Object columnResult = resultRow.getObject(selection.alias());
            if (columnResult == null) {
                continue;
            }
            MappingMeta mappingMeta = selection.mappingMeta();
            // 2.mappingMeta decode if need
            columnResult = mappingMeta.decodeForReactive(columnResult, mappingContext);
            assertDecodeForReactiveReturning(mappingMeta, columnResult);

            if (selection instanceof FieldSelection) {
                FieldMeta<?, ?> fieldMeta = ((FieldSelection) selection).fieldMeta();
                if (fieldMeta.codec()) {
                    // 3. decode value if need
                    columnResult = doDecodeResult(sqlWrapper.statementType(), fieldMeta, columnResult);
                }
            }
            // 4. set bean property value.
            beanWrapper.setPropertyValue(selection.alias(), columnResult);
        }
        return getWrapperInstance(beanWrapper);
    }

    /*################################## blow private method ##################################*/

    @SuppressWarnings("unchecked")
    private <T> T mapSecondSQLResult(ResultRow resultRow
            , Map<Object, BeanWrapper> wrapperMap, List<Selection> subSelectionList
            , PrimaryFieldMeta<?, ?> primaryField, StatementType statementType, Class<T> resultClass) {

        Object primaryKeyValue = resultRow.getObject(primaryField.alias());
        if (primaryKeyValue == null) {
            throw createDomainSecondReturningNoIdException();
        }
        primaryKeyValue = primaryField.mappingMeta().decodeForReactive(primaryKeyValue, this.mappingContext);
        final BeanWrapper beanWrapper = wrapperMap.get(primaryKeyValue);
        if (beanWrapper == null) {
            throw new IllegalArgumentException(String.format(
                    "wrapperMap error,not found value for key[%s]", primaryKeyValue));
        }
        for (Selection selection : subSelectionList) {
            Object columnResult = extractColumnResult(resultRow, selection, statementType);
            if (columnResult == null) {
                continue;
            }
            beanWrapper.setPropertyValue(selection.alias(), columnResult);
        }
        return (T) beanWrapper.getWrappedInstance();
    }

    @Nullable
    private Object extractColumnResult(ResultRow resultRow, Selection selection, StatementType statementType) {
        // 1. obtain column result
        Object columnResult = resultRow.getObject(selection.alias());
        if (columnResult == null) {
            return null;
        }

        MappingMeta mappingMeta = selection.mappingMeta();
        // 2. decode columnResult with mappingMeta
        columnResult = mappingMeta.decodeForReactive(columnResult, this.mappingContext);
        assertDecodeForReactiveReturning(mappingMeta, columnResult);

        if (selection instanceof FieldSelection) {
            FieldMeta<?, ?> fieldMeta = ((FieldSelection) selection).fieldMeta();
            if (fieldMeta.codec()) {
                // 3. decode cipher field
                columnResult = doDecodeResult(statementType, fieldMeta, columnResult);
            }
        }
        return columnResult;
    }

    private <T> Flux<T> assertOptimisticLockWhenEmpty(SimpleSQLWrapper sqlWrapper) {
        Flux<T> flux;
        if (sqlWrapper.hasVersion()) {
            // throw optimistic lock exception
            flux = Flux.defer(() -> Flux.error(createOptimisticLockException(this.sessionFactory, sqlWrapper.sql())));
        } else {
            flux = Flux.empty();
        }
        return flux;
    }


    private Object keyExtractor(BeanWrapper beanWrapper, PrimaryFieldMeta<?, ?> primaryField) {
        Object idValue = beanWrapper.getPropertyValue(primaryField.propertyName());
        if (idValue == null) {
            throw new IllegalStateException(String.format(
                    "%s not found property[%s] value.", beanWrapper, primaryField.propertyName()));
        }
        return idValue;
    }


    private void assertDecodeForReactiveReturning(MappingMeta mappingMeta, Object columnResult) {
        if (!mappingMeta.javaType().isInstance(columnResult)) {
            throw new MetaException("%s decodeForReactive return value isn't %s's instance."
                    , mappingMeta.getClass().getName()
                    , mappingMeta.javaType().getName());
        }
    }


    private <N extends Number> Mono<N> assertOptimisticLock(N updatedRows, GenericSimpleWrapper sqlWrapper) {
        final boolean hasVersion = sqlWrapper.hasVersion();
        if (!hasVersion) {
            return Mono.just(updatedRows);
        }
        Mono<N> mono = null;
        if (updatedRows instanceof Integer) {
            if (updatedRows.intValue() < 1) {
                mono = Mono.error(createOptimisticLockException(this.sessionFactory, sqlWrapper.sql()));
            }
        } else if (updatedRows instanceof Long) {
            if (updatedRows.longValue() < 1L) {
                mono = Mono.error(createOptimisticLockException(this.sessionFactory, sqlWrapper.sql()));
            }
        } else {
            throw new IllegalArgumentException("executeFunction error.");
        }
        return mono == null ? Mono.just(updatedRows) : mono;
    }

    private <N extends Number> Mono<List<N>> assertBatchOptimisticLock(List<N> list, BatchSimpleSQLWrapper sqlWrapper) {
        final boolean hasVersion = sqlWrapper.hasVersion();
        if (!hasVersion) {
            return Mono.just(list);
        }
        Mono<List<N>> mono = null;
        for (N rows : list) {
            if (rows instanceof Integer) {
                if (rows.intValue() < 1) {
                    mono = Mono.error(createOptimisticLockException(this.sessionFactory, sqlWrapper.sql()));
                    break;
                }
            } else if (rows instanceof Long) {
                if (rows.longValue() < 1L) {
                    mono = Mono.error(createOptimisticLockException(this.sessionFactory, sqlWrapper.sql()));
                    break;
                }
            } else {
                throw new IllegalArgumentException("executeFunction error.");
            }
        }
        return mono == null ? Mono.just(list) : mono;
    }


    private PreparedStatement bindParamGroupList(final PreparedStatement st, BatchSimpleSQLWrapper sqlWrapper) {
        final StatementType statementType = sqlWrapper.statementType();
        for (List<ParamWrapper> paramList : sqlWrapper.paramGroupList()) {
            bindParamList(st, statementType, paramList);
            st.addBatch();
        }
        return st;
    }


    private PreparedStatement bindParamList(final PreparedStatement st, StatementType statementType
            , List<ParamWrapper> paramList)
            throws ReactiveSQLException {

        final MappingContext mappingContext = this.mappingContext;
        final Database database = mappingContext.database();

        final int size = paramList.size();
        for (int i = 0; i < size; i++) {
            ParamWrapper paramWrapper = paramList.get(i);
            Object value = paramWrapper.value();
            ParamMeta paramMeta = paramWrapper.paramMeta();
            SQLDataType sqlDataType = paramMeta.mappingMeta().sqlDataType(database);

            if (value == null) {
                st.bindNull(i + 1, sqlDataType.typeName());
            } else {
                if (paramMeta instanceof FieldMeta) {
                    FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
                    if (fieldMeta.codec()) {
                        value = doEncodeParam(statementType, fieldMeta, value);
                    }
                }
                value = paramWrapper.paramMeta().mappingMeta().encodeForReactive(value, mappingContext);
                st.bind(i + 1, sqlDataType.typeName(), value);
            }
        }
        return st;
    }


}
