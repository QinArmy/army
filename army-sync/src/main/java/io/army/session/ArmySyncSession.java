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

package io.army.session;

import io.army.ArmyException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.executor.DataAccessException;
import io.army.executor.DriverSpiHolder;
import io.army.executor.SyncExecutor;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.ChildTableMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.option.Option;
import io.army.result.ChildUpdateException;
import io.army.result.CurrentRecord;
import io.army.result.ResultStates;
import io.army.stmt.*;
import io.army.transaction.TransactionInfo;
import io.army.transaction.TransactionOption;
import io.army.type.ImmutableSpec;
import io.army.util.SQLStmts;
import io.army.util.StreamFunctions;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link ArmySyncLocalSession}</li>
 *     <li>{@link ArmySyncRmSession}</li>
 * </ul>
 * <p>This class extends {@link ArmySession} and implements of {@link SyncSession}.
 *
 * @see ArmySyncSessionFactory
 * @since 0.6.0
 */
non-sealed abstract class ArmySyncSession extends ArmySession<ArmySyncSessionFactory> implements SyncSession {


    final SyncExecutor executor;

    private boolean sessionClosed;

    private Map<Object, Object> attributeMap;

    ArmySyncSession(ArmySyncSessionFactory.SyncBuilder<?, ?> builder) {
        super(builder);
        this.executor = builder.stmtExecutor;
        assert this.executor != null;
    }

    @Override
    public final boolean isReactive() {
        // always false
        return false;
    }


    @Override
    public final SyncSessionFactory sessionFactory() {
        return this.factory;
    }

    @Override
    public final long sessionIdentifier() throws SessionException {
        if (this.factory.sessionIdentifierEnable) {
            return this.executor.sessionIdentifier(Option.EMPTY_FUNC);
        }
        return 0L;
    }

    @Override
    public final boolean inTransaction() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        if (this.factory.jdbcDriver || !(this instanceof DriverSpiHolder)) {
            // JDBC don't support to get the state from database client protocol.
            final TransactionInfo info = obtainTransactionInfo();
            return info != null && info.inTransaction();
        }
        // due to session open driver spi to application developer, TransactionInfo probably error.
        return this.executor.inTransaction(Option.EMPTY_FUNC);
    }


    @Override
    public final TransactionInfo transactionInfo() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        final TransactionInfo info;
        info = obtainTransactionInfo();
        if (info != null) {
            return info;
        }
        try {
            return this.executor.transactionInfo(Option.EMPTY_FUNC);
        } catch (Exception e) {
            throw ArmySession.wrapSessionError(e);
        }
    }

    @Override
    public final TransactionInfo sessionTransactionCharacteristics() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        try {
            return this.executor.sessionTransactionCharacteristics(Option.EMPTY_FUNC, Option.EMPTY_FUNC);
        } catch (Exception e) {
            throw ArmySession.wrapSessionError(e);
        }
    }

    @Override
    public final Object setSavePoint() {
        return setSavePoint(Option.EMPTY_FUNC);
    }

    @Override
    public final Object setSavePoint(Function<Option<?>, ?> optionFunc) {
        if (inPseudoTransaction()) {
            return PSEUDO_SAVE_POINT;
        }
        try {
            return this.executor.setSavePoint(optionFunc, Option.EMPTY_FUNC);
        } catch (Exception e) {
            throw wrapSessionError(e);
        }
    }

    @Override
    public final void releaseSavePoint(Object savepoint) {
        releaseSavePoint(savepoint, Option.EMPTY_FUNC);
    }

    @Override
    public final void releaseSavePoint(final Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!inPseudoTransaction()) {
            try {
                this.executor.releaseSavePoint(savepoint, optionFunc, Option.EMPTY_FUNC);
            } catch (Exception e) {
                throw wrapSessionError(e);
            }
        } else if (!PSEUDO_SAVE_POINT.equals(savepoint)) {
            throw _Exceptions.unknownSavePoint(savepoint);
        }

    }

    @Override
    public final void rollbackToSavePoint(Object savepoint) {
        rollbackToSavePoint(savepoint, Option.EMPTY_FUNC);
    }

    @Override
    public final void rollbackToSavePoint(final Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!inPseudoTransaction()) {
            try {
                this.executor.rollbackToSavePoint(savepoint, optionFunc, Option.EMPTY_FUNC);
            } catch (Exception e) {
                throw wrapSessionError(e);
            }
        } else if (!PSEUDO_SAVE_POINT.equals(savepoint)) {
            throw _Exceptions.unknownSavePoint(savepoint);
        }

    }

    @Override
    public final void setTransactionCharacteristics(TransactionOption option) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        this.executor.setTransactionCharacteristics(option, Option.EMPTY_FUNC);
    }


    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass) {
        return query(statement, resultClass, ArmySyncStmtOptions.DEFAULT)
                .reduce(StreamFunctions::atMostOne)
                .orElse(null);
    }

    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return query(statement, resultClass, option)
                .reduce(StreamFunctions::atMostOne)
                .orElse(null);
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return queryObject(statement, constructor, ArmySyncStmtOptions.DEFAULT)
                .reduce(StreamFunctions::atMostOne)
                .orElse(null);
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return queryObject(statement, constructor, option)
                .reduce(StreamFunctions::atMostOne)
                .orElse(null);
    }


    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return queryRecord(statement, function, ArmySyncStmtOptions.DEFAULT)
                .reduce(StreamFunctions::atMostOne)
                .orElse(null);
    }

    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return queryRecord(statement, function, option)
                .reduce(StreamFunctions::atMostOne)
                .orElse(null);
    }

    @Override
    public final <R> List<R> queryList(DqlStatement statement, Class<R> resultClass) {
        return queryList(statement, resultClass, _Collections::arrayList, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return queryList(statement, resultClass, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return queryList(statement, resultClass, listConstructor, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        List<R> rowList;
        rowList = query(statement, resultClass, option)
                .collect(Collectors.toCollection(listConstructor));
        if (rowList instanceof ImmutableSpec) {
            rowList = _Collections.unmodifiableList(rowList);
        }
        return rowList;
    }


    @Override
    public final <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor) {
        return this.queryObjectList(statement, constructor, _Collections::arrayList, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return this.queryObjectList(statement, constructor, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor) {
        return this.queryObjectList(statement, constructor, listConstructor, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        List<R> rowList;
        rowList = queryObject(statement, constructor, option)
                .collect(Collectors.toCollection(listConstructor));
        if (rowList instanceof ImmutableSpec) {
            rowList = _Collections.unmodifiableList(rowList);
        }
        return rowList;
    }

    @Override
    public final <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecordList(statement, function, _Collections::arrayList, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return this.queryRecordList(statement, function, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor) {
        return this.queryRecordList(statement, function, listConstructor, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        List<R> rowList;
        rowList = queryRecord(statement, function, option)
                .collect(Collectors.toCollection(listConstructor));
        if (rowList instanceof ImmutableSpec) {
            rowList = _Collections.unmodifiableList(rowList);
        }
        return rowList;
    }

    @Override
    public final <R> Stream<R> query(DqlStatement statement, Class<R> resultClass) {
        return this.query(statement, resultClass, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Stream<R> query(DqlStatement statement, final Class<R> resultClass, SyncStmtOption option) {
        return executeQuery(statement, option, classReaderFunc(resultClass));
    }


    @Override
    public final <R> Stream<R> queryObject(DqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Stream<R> queryObject(DqlStatement statement, final Supplier<R> constructor, SyncStmtOption option) {
        return executeQuery(statement, option, constructorReaderFunc(constructor));
    }


    @Override
    public final <R> Stream<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function) {
        return queryRecord(statement, function, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Stream<R> queryRecord(DqlStatement statement, final Function<CurrentRecord, R> function, SyncStmtOption option) {
        if (statement instanceof _Statement._ChildStatement) {
            throw new SessionException("queryRecord api don't support two statement mode");
        }
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = parseDqlStatement(statement, option);
            if (!(stmt instanceof SingleSqlStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            }

            final SyncStmtOption newOption;
            if (this.factory.sqlExecutionCostTime) {
                newOption = replaceForQueryExecutionLogger(option, stmt);  // for transaction timeout and optimistic lock and execution log
            } else {
                newOption = replaceForQueryIfNeed(stmt.hasOptimistic(), option, null); // for transaction timeout and optimistic lock
            }
            return this.executor.query((SingleSqlStmt) stmt, function, newOption, Option.EMPTY_FUNC);
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
    }

    @Override
    public final <T> int save(T domain) {
        return save(domain, LiteralMode.DEFAULT, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T> int save(T domain, SyncStmtOption option) {
        return save(domain, LiteralMode.DEFAULT, option);
    }

    @Override
    public final <T> int save(T domain, LiteralMode literalMode) {
        return save(domain, literalMode, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T> int save(T domain, LiteralMode literalMode, SyncStmtOption option) {
        final long rowCount;
        rowCount = updateAsResult(SQLStmts.insertStmt(this, literalMode, domain), option, Long.class);
        if (rowCount > 1) { // TODO 有些方言在冲突可能大于 1
            throw new DataAccessException(String.format("insert row count[%s] great than one ", rowCount));
        }
        return (int) rowCount;
    }

    @Override
    public final long update(SimpleDmlStatement statement) {
        return updateAsResult(statement, ArmySyncStmtOptions.DEFAULT, Long.class);
    }

    @Override
    public final long update(SimpleDmlStatement statement, SyncStmtOption option) {
        return updateAsResult(statement, option, Long.class);
    }


    @Override
    public final ResultStates updateAsStates(SimpleDmlStatement statement) {
        return updateAsResult(statement, ArmySyncStmtOptions.DEFAULT, ResultStates.class);
    }

    @Override
    public final ResultStates updateAsStates(SimpleDmlStatement statement, SyncStmtOption option) {
        return updateAsResult(statement, option, ResultStates.class);
    }

    @Override
    public final <T> int batchSave(List<T> domainList) {
        return batchSave(domainList, LiteralMode.DEFAULT, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T> int batchSave(List<T> domainList, LiteralMode literalMode) {
        return batchSave(domainList, literalMode, ArmySyncStmtOptions.DEFAULT);
    }


    @Override
    public final <T> int batchSave(List<T> domainList, SyncStmtOption option) {
        return batchSave(domainList, LiteralMode.DEFAULT, option);
    }

    @Override
    public final <T> int batchSave(List<T> domainList, LiteralMode literalMode, SyncStmtOption option) {
        if (domainList.size() == 0) {
            throw new IllegalArgumentException("domainList must non-empty.");
        }
        final long rowCount;
        rowCount = updateAsResult(SQLStmts.batchInsertStmt(this, literalMode, domainList), option, Long.class);

        if (rowCount > domainList.size()) {
            String m = String.format("insert row count[%s] and expected row count[%s] not match.",
                    rowCount, domainList.size());
            throw new DataAccessException(m);
        }
        return (int) rowCount;
    }


    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement) {
        return this.batchUpdate(statement, _Collections::arrayList, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, SyncStmtOption option) {
        return this.batchUpdate(statement, _Collections::arrayList, option);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor) {
        return this.batchUpdate(statement, listConstructor, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, SyncStmtOption option) {
        final List<Long> resultList;
        if (this.factory.resultItemDriverSpi) {
            if (!(statement instanceof _BatchStatement)) {
                throw ArmySession.wrapSessionError(_Exceptions.unexpectedStatement(statement));
            }

            final int batchSize;
            batchSize = ((_BatchStatement) statement).paramList().size();

            // and execute
            try (Stream<ResultStates> stream = batchUpdateAsStates(statement, option)) {

                final List<Long> tempList;
                tempList = stream.map(ResultStates::affectedRows)
                        .collect(Collectors.toCollection(() -> listConstructor.apply(batchSize)));

                resultList = Collections.unmodifiableList(tempList);
            } catch (Exception e) {
                throw ArmySession.wrapSessionError(e);
            }
        } else {
            // JDBC driver here
            resultList = executeBatchUpdateAsLong(statement, listConstructor, option);
        }
        return resultList;
    }


    @Override
    public final Stream<ResultStates> batchUpdateAsStates(BatchDmlStatement statement) {
        return batchUpdateAsStates(statement, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final Stream<ResultStates> batchUpdateAsStates(BatchDmlStatement statement, SyncStmtOption option) {
        try {
            if (!(statement instanceof _BatchStatement)) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);

            final Stmt stmt;
            stmt = this.parseDmlStatement(statement, option);

            final TableMeta<?> domainTable;
            domainTable = getBatchUpdateDomainTable(statement);

            final Consumer<ResultStates> optimisticLockValidator;
            if (stmt.hasOptimistic()) {
                optimisticLockValidator = statesOptimisticLockValidator(domainTable);
            } else {
                optimisticLockValidator = null;
            }

            final Stream<ResultStates> stream, tempSteam;
            if (stmt instanceof BatchStmt) {
                tempSteam = this.executor.batchUpdate((BatchStmt) stmt, option, Option.EMPTY_FUNC);
                if (optimisticLockValidator == null) {
                    stream = tempSteam;
                } else {
                    stream = tempSteam.peek(optimisticLockValidator);
                }
            } else if (!(stmt instanceof PairBatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (!inTransaction()) {
                throw updateChildNoTransaction();
            } else {
                assert domainTable instanceof ChildTableMeta; // fail, bug.
                final PairBatchStmt pairStmt = (PairBatchStmt) stmt;

                tempSteam = this.executor.batchUpdate(pairStmt.firstStmt(), option, Option.EMPTY_FUNC);

                final List<ResultStates> childList;
                if (optimisticLockValidator == null) {
                    childList = tempSteam.collect(Collectors.toCollection(_Collections::arrayList));
                } else {
                    childList = tempSteam.peek(optimisticLockValidator)
                            .collect(Collectors.toCollection(_Collections::arrayList));
                }

                stream = this.executor.batchUpdate(pairStmt.secondStmt(), option, Option.EMPTY_FUNC)
                        .peek(batchUpdateStatesValidateParentRows(childList, (ChildTableMeta<?>) domainTable));
            }
            return stream;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
    }

    @Override
    public final <T> T valueOf(Option<T> option) {
        return this.executor.valueOf(option);
    }


    @Override
    public final boolean isClosed() {
        return this.sessionClosed;
    }

    @Override
    public final void close() throws SessionException {
        if (this.sessionClosed) {
            return;
        }

        synchronized (this) {
            if (this.sessionClosed) {
                return;
            }
            try {
                this.sessionClosed = true;
                this.executor.close();
            } catch (Exception e) {
                throw wrapSessionError(e);
            }
        }

    }

    /*-------------------below protected template methods -------------------*/

    @Nullable
    @Override
    protected final Map<Object, Object> obtainAttributeMap() {
        return this.attributeMap;
    }

    @Override
    protected final Map<Object, Object> obtainOrCreateAttributeMap() {
        Map<Object, Object> map = this.attributeMap;
        if (map == null) {
            this.attributeMap = map = _Collections.hashMap();
        }
        return map;
    }

    final void releaseSession() {
        this.sessionClosed = true;
    }



    /*-------------------below private methods -------------------*/


    private <R> Stream<R> executeQuery(final DqlStatement statement, final SyncStmtOption optionOfUser,
                                       final ReaderFunction<R> readerFunc) {
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = parseDqlStatement(statement, optionOfUser);

            final SyncStmtOption option;
            if (this.factory.sqlExecutionCostTime) {
                option = replaceForQueryExecutionLogger(optionOfUser, stmt);  // for transaction timeout and optimistic lock and execution log
            } else {
                option = replaceForQueryIfNeed(stmt.hasOptimistic(), optionOfUser, null); // for transaction timeout and optimistic lock
            }


            final Stream<R> stream;
            if (stmt instanceof SingleSqlStmt) {
                final Function<? super CurrentRecord, R> rowFunc;
                rowFunc = readerFunc.apply((SingleSqlStmt) stmt, true);
                stream = this.executor.query((SingleSqlStmt) stmt, rowFunc, option, Option.EMPTY_FUNC);
            } else if (!(stmt instanceof PairStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (!inTransaction()) {
                throw updateChildNoTransaction();
            } else if (statement instanceof InsertStatement) {
                stream = executePairInsertQuery((InsertStatement) statement, option, (PairStmt) stmt, readerFunc);
            } else {
                // TODO add for firebird
                throw _Exceptions.unexpectedStmt(stmt);
            }
            return stream;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }

    }


    /**
     * @param option the instance is returned by {@link #replaceIfNeed(SyncStmtOption)}
     * @see #executeQuery(DqlStatement, SyncStmtOption, ReaderFunction)
     */
    private <R> Stream<R> executePairInsertQuery(final InsertStatement statement, final SyncStmtOption option,
                                                 final PairStmt stmt, final ReaderFunction<R> readerFunc) {
        final _Insert._ChildInsert childInsert = (_Insert._ChildInsert) statement;
        final boolean firstStmtIsQuery = childInsert.parentStmt() instanceof _ReturningDml;


        final SimpleStmt firstStmt = stmt.firstStmt(), secondStmt = stmt.secondStmt();

        final Function<? super CurrentRecord, R> rowFunc;

        ResultStates states = null;
        List<R> resultList = null;
        if (firstStmtIsQuery) {
            rowFunc = readerFunc.apply(firstStmt, false);
            resultList = this.executor.query(firstStmt, rowFunc, option, Option.EMPTY_FUNC)
                    .collect(Collectors.toCollection(_Collections::arrayList));
            if (resultList.size() == 0) {
                // exists conflict clause
                return Stream.empty();
            }
        } else {
            rowFunc = readerFunc.apply(secondStmt, true);  // here use secondStmt not firstStmt
            states = this.executor.update(firstStmt, option, ResultStates.class, Option.EMPTY_FUNC);
            if (states.affectedRows() == 0L) {
                // exists conflict clause
                return Stream.empty();
            }
        }

        final ChildTableMeta<?> childTable;
        childTable = (ChildTableMeta<?>) ((_Insert._ChildInsert) statement).table();

        try {
            final Consumer<ResultStates> statesConsumer;
            final Stream<R> stream;
            if (firstStmtIsQuery) {
                final Function<Option<?>, ?> optionFunc;
                optionFunc = Option.singleFunc(Option.SECOND_DML_QUERY_STATES, Boolean.TRUE);
                final SyncSecondRecordReader<R> recordReader;
                recordReader = new SyncSecondRecordReader<>(this, childTable, (TwoStmtQueryStmt) secondStmt, resultList);
                // TODO 验证 行数
                stream = this.executor.query(secondStmt, recordReader::readRecord, option, optionFunc);
            } else {
                // TODO 验证 行数,fetch size
                final ResultStates parentStates = states;

                statesConsumer = childStates -> {
                    if (childStates.rowCount() != parentStates.affectedRows()) {
                        throw _Exceptions.parentChildRowsNotMatch(this, childTable, parentStates.affectedRows(), childStates.rowCount());
                    }
                };
                stream = this.executor.query(secondStmt, rowFunc,
                        ArmySyncStmtOptions.replaceStateConsumer(option, statesConsumer),
                        Option.singleFunc(Option.FIRST_DML_STATES, parentStates)
                );
            }
            return stream;
        } catch (Throwable e) {
            throw _Exceptions.childInsertError(this, childTable, e);
        }
    }


    /**
     * @param <R> the class of {@link Long} or {@link ResultStates}
     * @see #update(SimpleDmlStatement, SyncStmtOption)
     * @see #updateAsStates(SimpleDmlStatement, SyncStmtOption)
     */
    private <R> R updateAsResult(final SimpleDmlStatement statement, final SyncStmtOption optionOfUser, Class<R> resultClass) {
        try {
            if (statement instanceof _BatchStatement) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);

            final SyncStmtOption option;
            option = replaceIfNeed(optionOfUser);

            final R result;
            if (statement instanceof InsertStatement) {
                result = executeInsert((InsertStatement) statement, option, resultClass);
            } else {
                result = executeUpdate(statement, option, resultClass);
            }
            return result;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
    }


    static final class InsertQueryRowReader<R> {

        private final GeneratedKeyStmt stmt;

        private final List<? extends Selection> selectionList;

        private final Supplier<R> constructor;

        private final PrimaryFieldMeta<?> idField;

        private final MappingType idType;

        private final int rowSize;

        private final int idSelectionIndex;

        private final Class<?>[] classArray;

        private int rowIndex;

        private ObjectAccessor accessor;

        // private  final boolean oneRowWithConflict;

        private InsertQueryRowReader(GeneratedKeyStmt stmt, Supplier<R> constructor) {
            this.stmt = stmt;
            this.selectionList = stmt.selectionList();
            this.constructor = constructor;
            this.idField = stmt.idField();

            this.idType = this.idField.mappingType();
            this.idSelectionIndex = stmt.idSelectionIndex();
            this.classArray = new Class<?>[stmt.selectionList().size()];
            this.rowSize = stmt.rowSize();
            assert this.idSelectionIndex > -1;
            // this.oneRowWithConflict =   this.rowSize == 1 && stmt.hasConflictClause(); // query don't need
        }


        @Nullable
        R readOneRow(final CurrentRecord record) {
            final int idSelectionIndex = this.idSelectionIndex;

            final Object idValue;
            idValue = record.get(idSelectionIndex, this.idType);
            final int rowIndex = this.rowIndex;
            if (idValue == null) {
                throw _Exceptions.idValueIsNull(rowIndex, this.idField);
            }
            this.stmt.setGeneratedIdValue(rowIndex, idValue);
            this.rowIndex++;

            final Supplier<R> constructor = this.constructor;
            if (constructor == null) {
                return null;
            }
            final R row;
            row = constructor.get();
            if (row == null) {
                throw _Exceptions.objectConstructorError();
            }
            ObjectAccessor accessor = this.accessor;
            if (accessor == null) {
                this.accessor = accessor = ObjectAccessorFactory.fromInstance(row);
            }
            final Class<?>[] classArray = this.classArray;
            Class<?> columnClass;
            final int columnCount = record.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                if (i == idSelectionIndex) {
                    accessor.set(row, this.idField.fieldName(), idValue);
                    continue;
                }
                columnClass = classArray[i];
                if (columnClass == null) {
                    if (row instanceof Map) {
                        columnClass =
                    } else {

                    }
                }

            }
            return null;
        }


    } // InsertQueryRowReader


    /**
     * @param option the instance is returned by {@link #replaceIfNeed(SyncStmtOption)}
     * @see #updateAsResult(SimpleDmlStatement, SyncStmtOption, Class)
     */
    private <R> R executeInsert(InsertStatement statement, SyncStmtOption option, Class<R> resultClass)
            throws ArmyException {

        final Stmt stmt;
        stmt = parseInsertStatement(statement);

        final long executionStartNanoSecond;
        executionStartNanoSecond = getExecutionStartNanoSecond();

        final R states;

        if (stmt instanceof SimpleStmt) {
            if (stmt instanceof GeneratedKeyStmt && ((GeneratedKeyStmt) stmt).selectionList().size() > 0) {

                this.executor.query((SimpleStmt) stmt, null, option, Option.EMPTY_FUNC);
            } else {

            }
            states = this.executor.update((SimpleStmt) stmt, option, resultClass, Option.EMPTY_FUNC);
        } else if (!(stmt instanceof PairStmt)) {
            throw _Exceptions.unexpectedStmt(stmt);
        } else if (inTransaction()) {
            final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_Insert) statement).table();
            final PairStmt pairStmt = (PairStmt) stmt;

            final R parentStates;
            parentStates = this.executor.update(pairStmt.firstStmt(), option, resultClass, Option.EMPTY_FUNC);

            try {
                final Function<Option<?>, ?> optionFunc;
                if (parentStates instanceof ResultStates) {
                    optionFunc = Option.singleFunc(Option.FIRST_DML_STATES, (ResultStates) parentStates);
                } else {
                    optionFunc = Option.EMPTY_FUNC;
                }
                states = this.executor.update(pairStmt.secondStmt(), option, resultClass, optionFunc);
            } catch (Throwable e) {
                throw _Exceptions.childInsertError(this, domainTable, e);
            }

            final long childRows, parentRows;
            if (parentStates instanceof Long) {
                childRows = (Long) states;
                parentRows = (Long) parentStates;
            } else if (parentStates instanceof ResultStates) {
                childRows = ((ResultStates) states).affectedRows();
                parentRows = ((ResultStates) parentStates).affectedRows();
            } else {
                // no bug,never here
                throw new IllegalStateException();
            }
            if (childRows != parentRows
                    && !(statement instanceof _Insert._SupportConflictClauseSpec
                    && ((_Insert._SupportConflictClauseSpec) statement).hasConflictAction())) {
                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, parentRows, childRows);
            }
        } else {
            throw updateChildNoTransaction();
        }

        if (executionStartNanoSecond > 0) {
            printExecutionCostTimeLog(getLogger(), stmt, executionStartNanoSecond);
        }
        return states;
    }


    /**
     * @param userOption the instance is returned by {@link #replaceIfNeed(SyncStmtOption)}
     * @see #updateAsResult(SimpleDmlStatement, SyncStmtOption, Class)
     */
    private <R> R executeUpdate(SimpleDmlStatement statement, SyncStmtOption userOption, final Class<R> resultClass)
            throws ArmyException {

        final SyncStmtOption option;
        option = replaceIfNeed(userOption);

        final Stmt stmt;
        stmt = parseDmlStatement(statement, option);

        final long executionStartNanoSecond;
        executionStartNanoSecond = getExecutionStartNanoSecond();

        final R result;
        if (stmt instanceof SimpleStmt) {
            final Function<Option<?>, ?> optionFunc;
            if (stmt instanceof DeclareCursorStmt) {
                optionFunc = declareCursorOptionFunc();
            } else {
                optionFunc = Option.EMPTY_FUNC;
            }

            result = this.executor.update((SimpleStmt) stmt, option, resultClass, optionFunc);

            if (stmt.hasOptimistic() && obtainAffectedRows(result) == 0) {
                throw _Exceptions.optimisticLock();
            }
        } else if (!(stmt instanceof PairStmt)) {
            throw _Exceptions.unexpectedStmt(stmt);
        } else if (!inTransaction()) {
            throw updateChildNoTransaction();
        } else if (statement instanceof NarrowDmlStatement) {
            final ChildTableMeta<?> domainTable;
            if (statement instanceof _SingleDml._DomainDml) {
                domainTable = (ChildTableMeta<?>) ((_SingleDml._DomainDml) statement).table();
            } else { // example SQLite/H2 update child table
                domainTable = (ChildTableMeta<?>) ((_SingleUpdate._ChildUpdate) statement).table();
            }

            final PairStmt pairStmt = (PairStmt) stmt;

            final R childResult;
            childResult = this.executor.update(pairStmt.firstStmt(), option, resultClass, Option.EMPTY_FUNC);

            if (stmt.hasOptimistic() && obtainAffectedRows(childResult) == 0) {
                throw _Exceptions.optimisticLock();
            }
            try {
                result = this.executor.update(pairStmt.secondStmt(), option, resultClass, Option.EMPTY_FUNC);
            } catch (Throwable e) {
                throw _Exceptions.childUpdateError(this, domainTable, e);
            }

            if (obtainAffectedRows(result) != obtainAffectedRows(childResult)) {
                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, obtainAffectedRows(result),
                        obtainAffectedRows(childResult));
            }
        } else { // eg : MySQL LOAD DATA statement
            final PairStmt pairStmt = (PairStmt) stmt;
            this.executor.update(pairStmt.firstStmt(), option, resultClass, Option.EMPTY_FUNC);
            result = this.executor.update(pairStmt.secondStmt(), option, resultClass, Option.EMPTY_FUNC);
        }
        if (executionStartNanoSecond > 0L) {
            printExecutionCostTimeLog(getLogger(), stmt, executionStartNanoSecond);
        }
        return result;
    }


    /**
     * @see #batchUpdate(BatchDmlStatement, IntFunction, SyncStmtOption)
     */
    private List<Long> executeBatchUpdateAsLong(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, SyncStmtOption option) {
        try {

            Objects.requireNonNull(listConstructor);

            if (!(statement instanceof _BatchStatement)) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);

            final Stmt stmt;
            stmt = this.parseDmlStatement(statement, option);

            final TableMeta<?> domainTable;
            domainTable = getBatchUpdateDomainTable(statement);

            final LongConsumer optimisticLockValidator;
            if (stmt.hasOptimistic()) {
                optimisticLockValidator = rowsOptimisticLockValidator(domainTable);
            } else {
                optimisticLockValidator = null;
            }

            final List<Long> resultList;
            if (stmt instanceof BatchStmt) {
                resultList = this.executor.batchUpdateList((BatchStmt) stmt, listConstructor, option, optimisticLockValidator, Option.EMPTY_FUNC);
            } else if (!(stmt instanceof PairBatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (!inTransaction()) {
                throw updateChildNoTransaction();
            } else {
                assert domainTable instanceof ChildTableMeta; // fail, bug.
                final PairBatchStmt pairStmt = (PairBatchStmt) stmt;

                resultList = this.executor.batchUpdateList(pairStmt.firstStmt(), listConstructor, option, optimisticLockValidator, Option.EMPTY_FUNC);

                final LongConsumer parentValidator;
                parentValidator = batchUpdateValidateParentRows(resultList, (ChildTableMeta<?>) domainTable);
                this.executor.batchUpdateList(pairStmt.secondStmt(), null, option, parentValidator, Option.EMPTY_FUNC);
            }
            return resultList;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
    }


    private SyncStmtOption replaceIfNeed(final SyncStmtOption option) {
        final TransactionInfo info;

        final SyncStmtOption newOption;
        if (option instanceof ArmySyncStmtOptions.TransactionOverrideOption
                || (info = obtainTransactionInfo()) == null) {
            newOption = option;
        } else {
            newOption = ArmySyncStmtOptions.overrideOptionIfNeed(option, info, null);
        }
        return newOption;
    }

    private SyncStmtOption replaceForQueryIfNeed(final boolean hasOptimistic, final SyncStmtOption option,
                                                 @Nullable Consumer<ResultStates> consumer) {
        final TransactionInfo info;
        final SyncStmtOption newOption;
        if (hasOptimistic || consumer != null) {
            if (hasOptimistic) {
                if (consumer == null) {
                    consumer = OPTIMISTIC_LOCK_VALIDATOR;
                } else {
                    consumer = OPTIMISTIC_LOCK_VALIDATOR.andThen(consumer);
                }
            }
            newOption = ArmySyncStmtOptions.overrideOptionIfNeed(option, obtainTransactionInfo(), consumer);
        } else if (option instanceof ArmySyncStmtOptions.TransactionOverrideOption
                || (info = obtainTransactionInfo()) == null) {
            newOption = option;
        } else {
            newOption = ArmySyncStmtOptions.overrideOptionIfNeed(option, info, null);
        }
        return newOption;
    }

    /**
     * @see #executeQuery(DqlStatement, SyncStmtOption, ReaderFunction)
     */
    private SyncStmtOption replaceForQueryExecutionLogger(final SyncStmtOption optionOfUser, final Stmt stmt) {

        final long executionStartNanoSecond;
        executionStartNanoSecond = getExecutionStartNanoSecond();

        final Consumer<ResultStates> logConsumer;
        logConsumer = states -> {
            if (executionStartNanoSecond > 0 && states.isLastStates()) {
                printExecutionCostTimeLog(getLogger(), stmt, executionStartNanoSecond);
            }
        };
        return replaceForQueryIfNeed(stmt.hasOptimistic(), optionOfUser, logConsumer);
    }


    /*-------------------below private static method -------------------*/


    private static long obtainAffectedRows(final Object result) {
        if (result instanceof Long) {
            return (Long) result;
        }
        return ((ResultStates) result).affectedRows();
    }


    /**
     * @see #batchUpdate(BatchDmlStatement, IntFunction, SyncStmtOption)
     */
    private static LongConsumer rowsOptimisticLockValidator(final @Nullable TableMeta<?> domainTable) {
        final int[] indexHolder = new int[]{0};
        return rows -> {
            final int index = indexHolder[0]++;
            if (rows == 0L) {
                throw _Exceptions.batchOptimisticLock(domainTable, index + 1, rows);
            }
        };
    }


    /**
     * @see #batchUpdate(BatchDmlStatement, IntFunction, SyncStmtOption)
     */
    private static LongConsumer batchUpdateValidateParentRows(final List<Long> childList, final ChildTableMeta<?> domainTable) {
        final int batchSize = childList.size();

        final int[] indexHolder = new int[]{0};
        return parentRows -> {
            final int index = indexHolder[0]++;
            if (index >= batchSize) {
                throw _Exceptions.childBatchSizeError(domainTable, batchSize, index + 1);
            }
            if (parentRows != childList.get(index)) {
                throw _Exceptions.batchChildUpdateRowsError(domainTable, index + 1, childList.get(index),
                        parentRows);
            }

        };

    }

    /**
     * @see #batchUpdateAsStates(BatchDmlStatement, SyncStmtOption)
     */
    private static Consumer<ResultStates> statesOptimisticLockValidator(final @Nullable TableMeta<?> domainTable) {
        return states -> {
            if (states.affectedRows() == 0L) {
                throw _Exceptions.batchOptimisticLock(domainTable, states.resultNo(), states.affectedRows());
            }
        };
    }

    /**
     * @see #batchUpdateAsStates(BatchDmlStatement, SyncStmtOption)
     */
    private static Consumer<ResultStates> batchUpdateStatesValidateParentRows(final List<ResultStates> childList, final ChildTableMeta<?> domainTable) {
        final int batchSize = childList.size();

        return parentStates -> {
            final int batchNo = parentStates.resultNo();
            if (batchNo > batchSize || !parentStates.hasMoreResult()) {
                throw _Exceptions.childBatchSizeError(domainTable, batchSize, batchNo);
            }
            if (parentStates.affectedRows() != childList.get(batchNo - 1).affectedRows()) {
                throw _Exceptions.batchChildUpdateRowsError(domainTable, batchNo, childList.get(batchNo - 1).affectedRows(),
                        parentStates.affectedRows());
            }

        };

    }


}
