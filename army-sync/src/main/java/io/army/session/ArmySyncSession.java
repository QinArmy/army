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
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.executor.DataAccessException;
import io.army.executor.DriverSpiHolder;
import io.army.executor.SyncExecutor;
import io.army.function.PageConstructor;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
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
        return query(statement, resultClass, SyncStmtOptions.DEFAULT)
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
        return queryObject(statement, constructor, SyncStmtOptions.DEFAULT)
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
        return queryRecord(statement, function, SyncStmtOptions.DEFAULT)
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
        return queryList(statement, resultClass, _Collections::arrayList, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return queryList(statement, resultClass, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return queryList(statement, resultClass, listConstructor, SyncStmtOptions.DEFAULT);
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
        return this.queryObjectList(statement, constructor, _Collections::arrayList, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return this.queryObjectList(statement, constructor, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor) {
        return this.queryObjectList(statement, constructor, listConstructor, SyncStmtOptions.DEFAULT);
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
        return this.queryRecordList(statement, function, _Collections::arrayList, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return this.queryRecordList(statement, function, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor) {
        return this.queryRecordList(statement, function, listConstructor, SyncStmtOptions.DEFAULT);
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
        return this.query(statement, resultClass, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Stream<R> query(DqlStatement statement, final Class<R> resultClass, SyncStmtOption option) {
        return executeQuery(statement, option, classReaderFunc(resultClass));
    }


    @Override
    public final <R> Stream<R> queryObject(DqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Stream<R> queryObject(DqlStatement statement, final Supplier<R> constructor, SyncStmtOption option) {
        return executeQuery(statement, option, constructorReaderFunc(constructor));
    }


    @Override
    public final <R> Stream<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function) {
        return queryRecord(statement, function, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <R> Stream<R> queryRecord(DqlStatement statement, final Function<CurrentRecord, R> function, SyncStmtOption option) {
        if (statement instanceof _Statement._ChildStatement) {
            throw new SessionException("queryRecord api don't support two statement mode");
        }
        return executeQuery(statement, option, (stmt, immutableMap) -> function);
    }

    @Override
    public final <T, R> R paging(PagingPair pagingPair, Class<T> rowClass, PageConstructor<T, R> pageConstructor) {
        return executePaging(pagingPair, classRowFunc(rowClass), pageConstructor, _Collections::arrayList, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T, R> R paging(PagingPair pagingPair, Class<T> rowClass, PageConstructor<T, R> pageConstructor, Supplier<List<T>> listConstructor) {
        return executePaging(pagingPair, classRowFunc(rowClass), pageConstructor, listConstructor, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T, R> R paging(PagingPair pagingPair, Class<T> rowClass, PageConstructor<T, R> pageConstructor, Supplier<List<T>> listConstructor, SyncStmtOption option) {
        return executePaging(pagingPair, classRowFunc(rowClass), pageConstructor, listConstructor, option);
    }

    @Override
    public final <T, R> R pagingObject(PagingPair pagingPair, Supplier<T> constructor, PageConstructor<T, R> pageConstructor) {
        return executePaging(pagingPair, constructorRowFunc(constructor), pageConstructor, _Collections::arrayList, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T, R> R pagingObject(PagingPair pagingPair, Supplier<T> constructor, PageConstructor<T, R> pageConstructor, Supplier<List<T>> listConstructor) {
        return executePaging(pagingPair, constructorRowFunc(constructor), pageConstructor, listConstructor, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T, R> R pagingObject(PagingPair pagingPair, Supplier<T> constructor, PageConstructor<T, R> pageConstructor, Supplier<List<T>> listConstructor, SyncStmtOption option) {
        return executePaging(pagingPair, constructorRowFunc(constructor), pageConstructor, listConstructor, option);
    }

    @Override
    public final <T, R> R pagingRecord(PagingPair pagingPair, Function<? super CurrentRecord, T> function, PageConstructor<T, R> pageConstructor) {
        return executePaging(pagingPair, recordRowFunc(function), pageConstructor, _Collections::arrayList, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T, R> R pagingRecord(PagingPair pagingPair, Function<? super CurrentRecord, T> function, PageConstructor<T, R> pageConstructor, Supplier<List<T>> listConstructor) {
        return executePaging(pagingPair, recordRowFunc(function), pageConstructor, listConstructor, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T, R> R pagingRecord(PagingPair pagingPair, Function<? super CurrentRecord, T> function, PageConstructor<T, R> pageConstructor, Supplier<List<T>> listConstructor, SyncStmtOption option) {
        return executePaging(pagingPair, recordRowFunc(function), pageConstructor, listConstructor, option);
    }

    @Override
    public final long update(SimpleDmlStatement statement) {
        return updateAsStates(statement, SyncStmtOptions.DEFAULT).affectedRows();
    }

    @Override
    public final long update(SimpleDmlStatement statement, SyncStmtOption option) {
        return updateAsStates(statement, option).affectedRows();
    }


    @Override
    public final ResultStates updateAsStates(SimpleDmlStatement statement) {
        return updateAsStates(statement, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final ResultStates updateAsStates(final SimpleDmlStatement statement, final SyncStmtOption option) {
        try {
            if (statement instanceof _BatchStatement) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);

            final SyncStmtOption finalOption;
            finalOption = replaceIfNeed(option);

            final ResultStates states;
            if (statement instanceof InsertStatement) {
                states = executeInsert((InsertStatement) statement, finalOption);
            } else {
                states = executeUpdate(statement, finalOption);
            }
            return states;
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
    public final <T> int save(T domain) {
        return save(domain, LiteralMode.DEFAULT, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T> int save(T domain, SyncStmtOption option) {
        return save(domain, LiteralMode.DEFAULT, option);
    }

    @Override
    public final <T> int save(T domain, LiteralMode literalMode) {
        return save(domain, literalMode, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T> int save(T domain, LiteralMode literalMode, SyncStmtOption option) {
        final long rowCount;
        rowCount = updateAsStates(SQLStmts.insertStmt(this, literalMode, domain), option)
                .affectedRows();
        if (rowCount > 1) { // TODO 有些方言在冲突可能大于 1
            throw new DataAccessException(String.format("insert row count[%s] great than one ", rowCount));
        }
        return (int) rowCount;
    }

    @Override
    public final <T> int batchSave(List<T> domainList) {
        return batchSave(domainList, LiteralMode.DEFAULT, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final <T> int batchSave(List<T> domainList, LiteralMode literalMode) {
        return batchSave(domainList, literalMode, SyncStmtOptions.DEFAULT);
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
        rowCount = updateAsStates(SQLStmts.batchInsertStmt(this, literalMode, domainList), option)
                .affectedRows();

        if (rowCount > domainList.size()) {
            String m = String.format("insert row count[%s] and expected row count[%s] not match.",
                    rowCount, domainList.size());
            throw new DataAccessException(m);
        }
        return (int) rowCount;
    }


    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement) {
        return this.batchUpdate(statement, _Collections::arrayList, SyncStmtOptions.DEFAULT);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, SyncStmtOption option) {
        return this.batchUpdate(statement, _Collections::arrayList, option);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor) {
        return this.batchUpdate(statement, listConstructor, SyncStmtOptions.DEFAULT);
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
        return batchUpdateAsStates(statement, SyncStmtOptions.DEFAULT);
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
                final Function<? super CurrentRecord, R> function, rowFunc;
                function = readerFunc.apply((SingleSqlStmt) stmt, true);
                if (stmt instanceof GeneratedKeyStmt) {
                    rowFunc = insertRowFunc((GeneratedKeyStmt) stmt, function);
                } else {
                    rowFunc = function;
                }
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

    private ResultStates executeInsertWithOnlyReturnId(final GeneratedKeyStmt stmt, final SyncStmtOption option,
                                                       final Function<Option<?>, ?> sessionFunc) {
        final Function<? super CurrentRecord, Void> idRowFunc;
        idRowFunc = insertRowFunc(stmt, null);
        final ResultStates[] statesHolder = new ResultStates[1];
        final Consumer<ResultStates> consumer = states -> {
            statesHolder[0] = states;
        };
        this.executor.query(stmt, idRowFunc, SyncStmtOptions.replaceStateConsumer(option, consumer, true), sessionFunc)
                .forEach(StreamFunctions::ignore);

        final ResultStates states;
        states = statesHolder[0];
        assert states != null;
        return states;
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

        final Function<? super CurrentRecord, R> userFunc;

        if (firstStmtIsQuery) {
            userFunc = readerFunc.apply(firstStmt, false);
        } else {
            userFunc = readerFunc.apply(secondStmt, true);  // here use secondStmt not firstStmt
        }


        ResultStates states = null;
        List<R> resultList = null;
        if (firstStmtIsQuery) {
            final Function<? super CurrentRecord, R> firstRowFunc;
            if (firstStmt instanceof GeneratedKeyStmt) {
                firstRowFunc = insertRowFunc((GeneratedKeyStmt) firstStmt, userFunc);
            } else {
                firstRowFunc = userFunc;
            }
            resultList = this.executor.query(firstStmt, firstRowFunc, option, Option.EMPTY_FUNC)
                    .collect(Collectors.toCollection(_Collections::arrayList));
            if (resultList.size() == 0) {
                // exists conflict clause
                return Stream.empty();
            }
        } else if (firstStmt instanceof GeneratedKeyStmt && firstStmt.selectionList().size() > 0) {
            states = executeInsertWithOnlyReturnId((GeneratedKeyStmt) firstStmt, option, Option.EMPTY_FUNC);
        } else {
            states = this.executor.update(firstStmt, option, Option.EMPTY_FUNC);
            if (states.affectedRows() == 0L) {
                // exists conflict clause
                return Stream.empty();
            }
        }

        final ChildTableMeta<?> childTable;
        childTable = (ChildTableMeta<?>) ((_Insert._ChildInsert) statement).table();

        try {
            final Consumer<ResultStates> statesConsumer;
            final Function<Option<?>, ?> sessionFunc;
            final Stream<R> stream;
            if (firstStmtIsQuery) {
                sessionFunc = Option.singleFunc(Option.SECOND_DML_QUERY_STATES, Boolean.TRUE);
                final SyncSecondRecordReader<R> recordReader;
                recordReader = new SyncSecondRecordReader<>(this, childTable, (TwoStmtQueryStmt) secondStmt, resultList);
                statesConsumer = childStates -> {
                    if (childStates.isLastStates()) {
                        recordReader.validateRowCount();
                    }
                };
                stream = this.executor.query(secondStmt, recordReader::readRecord,
                        SyncStmtOptions.replaceStateConsumer(option, statesConsumer, false),
                        sessionFunc);
            } else {
                final ResultStates parentStates = states;

                statesConsumer = childStates -> {
                    if (childStates.isLastStates() && childStates.rowCount() != parentStates.affectedRows()) {
                        throw _Exceptions.parentChildRowsNotMatch(this, childTable, parentStates.affectedRows(), childStates.rowCount());
                    }
                };
                sessionFunc = Option.singleFunc(Option.FIRST_DML_STATES, parentStates);
                stream = this.executor.query(secondStmt, userFunc,
                        SyncStmtOptions.replaceStateConsumer(option, statesConsumer, false),
                        sessionFunc
                );
            }
            return stream;
        } catch (Throwable e) {
            throw _Exceptions.childInsertError(this, childTable, e);
        }
    }


    /**
     * @param option the instance is returned by {@link #replaceIfNeed(SyncStmtOption)}
     * @see #updateAsStates(SimpleDmlStatement, SyncStmtOption)
     */
    private ResultStates executeInsert(final InsertStatement statement, final SyncStmtOption option)
            throws ArmyException {

        final Stmt stmt;
        stmt = parseInsertStatement(statement);

        final Function<? super CurrentRecord, Void> rowFunc;

        final Stmt effectiveFirstStmt;
        if (stmt instanceof PairStmt) {
            effectiveFirstStmt = ((PairStmt) stmt).firstStmt();
        } else {
            effectiveFirstStmt = stmt;
        }

        final boolean firstIsQueryInsert;
        firstIsQueryInsert = effectiveFirstStmt instanceof GeneratedKeyStmt
                && ((GeneratedKeyStmt) effectiveFirstStmt).selectionList().size() > 0;

        final long executionStartNanoSecond;
        executionStartNanoSecond = getExecutionStartNanoSecond();

        final ResultStates states;

        if (stmt instanceof SimpleStmt) {
            if (firstIsQueryInsert) {
                states = executeInsertWithOnlyReturnId((GeneratedKeyStmt) stmt, option, Option.EMPTY_FUNC);
            } else {
                states = this.executor.update((SimpleStmt) stmt, option, Option.EMPTY_FUNC);
            }
        } else if (!(stmt instanceof PairStmt)) {
            throw _Exceptions.unexpectedStmt(stmt);
        } else if (inTransaction()) {
            final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_Insert) statement).table();
            final PairStmt pairStmt = (PairStmt) stmt;

            final SimpleStmt firstStmt;
            firstStmt = pairStmt.firstStmt();

            final ResultStates parentStates;

            if (firstIsQueryInsert) {
                parentStates = executeInsertWithOnlyReturnId((GeneratedKeyStmt) firstStmt, option, Option.EMPTY_FUNC);
            } else {
                parentStates = this.executor.update(firstStmt, option, Option.EMPTY_FUNC);
            }
            try {
                final Function<Option<?>, ?> optionFunc;
                optionFunc = Option.singleFunc(Option.FIRST_DML_STATES, parentStates);
                states = this.executor.update(pairStmt.secondStmt(), option, optionFunc); // here use option not finalOption
            } catch (Throwable e) {
                throw _Exceptions.childInsertError(this, domainTable, e);
            }

            final long childRows, parentRows;
            childRows = states.affectedRows();
            parentRows = parentStates.affectedRows();

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
     * @see #updateAsStates(SimpleDmlStatement, SyncStmtOption)
     */
    private ResultStates executeUpdate(final SimpleDmlStatement statement, final SyncStmtOption userOption)
            throws ArmyException {

        final SyncStmtOption option;
        option = replaceIfNeed(userOption);

        final Stmt stmt;
        stmt = parseDmlStatement(statement, option);

        final long executionStartNanoSecond;
        executionStartNanoSecond = getExecutionStartNanoSecond();

        final ResultStates states;
        if (stmt instanceof SimpleStmt) {
            final Function<Option<?>, ?> optionFunc;
            if (stmt instanceof DeclareCursorStmt) {
                optionFunc = declareCursorOptionFunc();
            } else {
                optionFunc = Option.EMPTY_FUNC;
            }

            states = this.executor.update((SimpleStmt) stmt, option, optionFunc);

            if (stmt.hasOptimistic() && states.affectedRows() == 0) {
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

            final ResultStates childStates;
            childStates = this.executor.update(pairStmt.firstStmt(), option, Option.EMPTY_FUNC);

            if (stmt.hasOptimistic() && obtainAffectedRows(childStates) == 0) {
                throw _Exceptions.optimisticLock();
            }
            try {
                final Function<Option<?>, ?> sessionFunc = Option.singleFunc(Option.FIRST_DML_STATES, childStates);
                states = this.executor.update(pairStmt.secondStmt(), option, sessionFunc);
            } catch (Throwable e) {
                throw _Exceptions.childUpdateError(this, domainTable, e);
            }

            if (states.affectedRows() != childStates.affectedRows()) {
                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, states.affectedRows(),
                        childStates.affectedRows());
            }
        } else { // eg : MySQL LOAD DATA statement
            final PairStmt pairStmt = (PairStmt) stmt;
            final ResultStates firstStates;
            firstStates = this.executor.update(pairStmt.firstStmt(), option, Option.EMPTY_FUNC);
            final Function<Option<?>, ?> sessionFunc = Option.singleFunc(Option.FIRST_DML_STATES, firstStates);
            states = this.executor.update(pairStmt.secondStmt(), option, sessionFunc);
        }
        if (executionStartNanoSecond > 0L) {
            printExecutionCostTimeLog(getLogger(), stmt, executionStartNanoSecond);
        }
        return states;
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

    private <T, R> R executePaging(PagingPair pagingPair, RowFunction<T> readerFun,
                                   PageConstructor<T, R> pageConstructor, Supplier<List<T>> listConstructor,
                                   SyncStmtOption optionOfUser) {
        throw new UnsupportedOperationException();
    }


    private SyncStmtOption replaceIfNeed(final SyncStmtOption option) {
        final TransactionInfo info;

        final SyncStmtOption newOption;
        if (option instanceof SyncStmtOptions.TransactionOverrideOption
                || (info = obtainTransactionInfo()) == null) {
            newOption = option;
        } else {
            newOption = SyncStmtOptions.overrideOptionIfNeed(option, info, null);
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
            newOption = SyncStmtOptions.overrideOptionIfNeed(option, obtainTransactionInfo(), consumer);
        } else if (option instanceof SyncStmtOptions.TransactionOverrideOption
                || (info = obtainTransactionInfo()) == null) {
            newOption = option;
        } else {
            newOption = SyncStmtOptions.overrideOptionIfNeed(option, info, null);
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
