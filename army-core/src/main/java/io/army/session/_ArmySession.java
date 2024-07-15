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
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._MultiDml;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect.Database;
import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;
import io.army.mapping.MappingType;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.session.executor.ExecutorSupport;
import io.army.session.record.CurrentRecord;
import io.army.session.record.ResultStates;
import io.army.stmt.SingleSqlStmt;
import io.army.stmt.Stmt;
import io.army.stmt.TwoStmtQueryStmt;
import io.army.type.ImmutableSpec;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>This class is all implementation of {@link Session}.
 * <p>This class is direct base class of following :
 * <ul>
 *     <li>{@code  io.army.sync.ArmySyncSession}</li>
 *     <li>{@code io.army.reactive.ArmyReactiveSession}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public abstract class _ArmySession<F extends _ArmySessionFactory> implements Session {

    protected static final String PSEUDO_SAVE_POINT = "ARMY_PSEUDO_SAVE_POINT";

    protected static final Consumer<ResultStates> OPTIMISTIC_LOCK_VALIDATOR = _ArmySession::validateOptimisticLock;


    protected final F factory;

    protected final String name;

    protected final boolean readonly;
    protected final boolean allowQueryInsert;

    private final Visible visible;

    protected _ArmySession(_ArmySessionFactory.ArmySessionBuilder<F, ?, ?> builder) {

        this.name = builder.name;
        this.readonly = builder.readonly;
        this.visible = builder.visible;
        this.allowQueryInsert = builder.allowQueryInsert;

        assert _StringUtils.hasText(this.name);
        assert this.visible != null;
        this.factory = builder.factory;
    }


    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final boolean isReadonlySession() {
        return this.readonly;
    }

    @Override
    public final boolean isReadOnlyStatus() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        final boolean readOnlyStatus;
        final TransactionInfo info;
        if (this.readonly) {
            readOnlyStatus = true;
        } else if ((info = obtainTransactionInfo()) == null) {
            readOnlyStatus = false;
        } else {
            readOnlyStatus = info.inTransaction() && info.isReadOnly();
        }
        return readOnlyStatus;
    }

    @Override
    public final boolean hasTransactionInfo() {
        return obtainTransactionInfo() != null;
    }

    @Override
    public final boolean inPseudoTransaction() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        final TransactionInfo info;
        info = obtainTransactionInfo();
        return info != null && info.isolation() == Isolation.PSEUDO;
    }

    @Override
    public final boolean inAnyTransaction() throws SessionException {
        return inTransaction() || inPseudoTransaction();
    }

    @Override
    public final Visible visible() {
        return this.visible;
    }

    @Override
    public final boolean isQueryInsertAllowed() {
        return this.allowQueryInsert;
    }

    @Override
    public final Database serverDatabase() {
        return this.factory.serverDatabase;
    }

    @Override
    public final <T> TableMeta<T> tableMeta(Class<T> domainClass) {
        final TableMeta<T> table;
        table = this.factory.getTable(domainClass);
        if (table == null) {
            String m = String.format("Not found %s for %s.", TableMeta.class.getName(), domainClass.getName());
            throw new IllegalArgumentException(m);
        }
        return table;
    }

    @Nullable
    @Override
    public final Object getAttribute(Object key) {
        final Map<Object, Object> map;
        map = obtainAttributeMap();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    @Override
    public final void setAttribute(Object key, Object value) {
        obtainOrCreateAttributeMap().put(key, value);
    }

    @Override
    public final Set<Object> getAttributeKeys() {
        final Map<Object, Object> map;
        map = obtainAttributeMap();
        if (map == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(map.keySet());
    }

    @Nullable
    @Override
    public final Object removeAttribute(final Object key) {
        final Map<Object, Object> map;
        map = obtainAttributeMap();
        if (map == null) {
            return null;
        }
        return map.remove(key);
    }

    @Override
    public final int attributeSize() {
        final Map<Object, Object> map;
        map = obtainAttributeMap();
        return map == null ? 0 : map.size();
    }

    @Override
    public final Set<Map.Entry<Object, Object>> attributeEntrySet() {
        final Map<Object, Object> map;
        map = obtainAttributeMap();
        if (map == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(map.entrySet());
    }


    @Override
    public final <T> T nonNullOf(Option<T> option) {
        return Session.super.nonNullOf(option);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this;
    }


    @Override
    public final String toString() {
        return _StringUtils.builder(86)
                .append(getClass().getName())
                .append("[name:")
                .append(this.name)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(",factoryName:")
                .append(this.factory.name)
                .append(']')
                .toString();
    }

    /*-------------------below protected template methods -------------------*/

    protected abstract Logger getLogger();

    @Nullable
    protected abstract TransactionInfo obtainTransactionInfo();


    protected abstract void rollbackOnlyOnError(ChildUpdateException cause);


    @Nullable
    protected abstract Map<Object, Object> obtainAttributeMap();


    protected abstract Map<Object, Object> obtainOrCreateAttributeMap();

    protected final boolean isMultiTableDomainDml(final DmlStatement statement) {
        return this.factory.serverDatabase == Database.MySQL
                && statement instanceof _SingleDml._DomainDml
                && ((_SingleDml._DomainDml) statement).isChildDml();
    }


    protected final Stmt parseDqlStatement(final DqlStatement statement, final StmtOption option) {

        final SqlLogMode logMode;
        logMode = obtainSqlLogMode();

        final long startNanoSecond;
        if (logMode != SqlLogMode.OFF && this.factory.sqlParsingCostTime) {
            startNanoSecond = System.nanoTime();
        } else {
            startNanoSecond = -1L;
        }

        final Stmt stmt;
        if (statement instanceof SelectStatement) {
            stmt = this.factory.dialectParser.select((SelectStatement) statement, option.isParseBatchAsMultiStmt(), this);
        } else if (statement instanceof Values) {
            stmt = this.factory.dialectParser.values((Values) statement, this);
        } else if (!(statement instanceof DmlStatement)) {
            stmt = this.factory.dialectParser.dialectDql(statement, this);
        } else if (statement instanceof InsertStatement) {
            stmt = this.factory.dialectParser.insert((InsertStatement) statement, this);
        } else if (statement instanceof _Statement._ChildStatement) {
            throw new ArmyException("current api don't support child dml statement.");
        } else if (statement instanceof UpdateStatement) {
            stmt = this.factory.dialectParser.update((UpdateStatement) statement, option.isParseBatchAsMultiStmt(), this);
        } else if (statement instanceof DeleteStatement) {
            stmt = this.factory.dialectParser.delete((DeleteStatement) statement, option.isParseBatchAsMultiStmt(), this);
        } else {
            stmt = this.factory.dialectParser.dialectDml((DmlStatement) statement, this);
        }

        if (logMode != SqlLogMode.OFF) {
            printSql(logMode, stmt, startNanoSecond);
        }
        return stmt;
    }


    protected final Stmt parseInsertStatement(final InsertStatement statement) {
        final SqlLogMode logMode;
        logMode = obtainSqlLogMode();

        final long startNanoSecond;
        if (logMode != SqlLogMode.OFF && this.factory.sqlParsingCostTime) {
            startNanoSecond = System.nanoTime();
        } else {
            startNanoSecond = -1L;
        }

        final Stmt stmt;
        stmt = this.factory.dialectParser.insert(statement, this);
        if (logMode != SqlLogMode.OFF) {
            printSql(logMode, stmt, startNanoSecond);
        }
        return stmt;
    }

    protected final Stmt parseDmlStatement(final DmlStatement statement, final StmtOption option) {
        final SqlLogMode logMode;
        logMode = obtainSqlLogMode();

        final long startNanoSecond;
        if (logMode != SqlLogMode.OFF && this.factory.sqlParsingCostTime) {
            startNanoSecond = System.nanoTime();
        } else {
            startNanoSecond = -1L;
        }

        final Stmt stmt;
        if (statement instanceof UpdateStatement) {
            stmt = this.factory.dialectParser.update((UpdateStatement) statement, option.isParseBatchAsMultiStmt(), this);
        } else if (statement instanceof DeleteStatement) {
            stmt = this.factory.dialectParser.delete((DeleteStatement) statement, option.isParseBatchAsMultiStmt(), this);
        } else {
            stmt = this.factory.dialectParser.dialectDml(statement, this);
        }

        if (logMode != SqlLogMode.OFF) {
            printSql(logMode, stmt, startNanoSecond);
        }
        return stmt;
    }


    protected final void assertSession(final Statement statement) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        if (statement instanceof DmlStatement) {
            if (this.readonly) {
                throw _Exceptions.readOnlySession(this);
            } else if (isReadOnlyStatus()) {
                throw _Exceptions.readOnlyTransaction(this);
            } else if (statement instanceof _Statement._ChildStatement && !inTransaction()) {
                final TableMeta<?> domainTable;
                domainTable = ((_Statement._ChildStatement) statement).table();
                throw _Exceptions.childDmlNoTransaction(this, (ChildTableMeta<?>) domainTable);
            } else if (statement instanceof _Insert._QueryInsert && !this.allowQueryInsert) {
                throw _Exceptions.dontSupportSubQueryInsert(this);
            }
        }

    }

    protected final SqlLogMode obtainSqlLogMode() {
        SqlLogMode mode;
        final _ArmySessionFactory factory = this.factory;
        if (factory.sqlLogDynamic) {
            mode = factory.env.getOrDefault(ArmyKey.SQL_LOG_MODE);
        } else {
            mode = factory.sqlLogMode;
        }
        if (mode == SqlLogMode.OFF) {
            return mode;
        }
        final Logger logger;
        final boolean debug = mode.debug;
        logger = getLogger();
        if ((debug && !logger.isDebugEnabled()) || (!debug && !logger.isInfoEnabled())) {
            mode = SqlLogMode.OFF;
        }
        return mode;
    }

    protected final Function<Option<?>, ?> declareCursorOptionFunc() {
        final Map<Option<?>, Object> map;
        map = Collections.singletonMap(Option.ARMY_SESSION, this);
        return map::get;
    }

    /*-------------------below private methods -------------------*/


    private void printSql(final SqlLogMode mode, final Stmt stmt, final long startNanoSecond) {

        final StringBuilder builder = new StringBuilder(256);
        builder.append("session[name : ")
                .append(this.name)
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append("]\n\n");

        this.factory.dialectParser.printStmt(stmt, mode.beautify, builder::append);

        if (startNanoSecond > -1L) {
            final long costNano, millis, micro, nano;
            costNano = System.nanoTime() - startNanoSecond;

            millis = costNano / 1000_000L;
            micro = (costNano % 1000_000L) / 1000L;
            nano = costNano % 1000L;

            builder.append("\n\nsql parsing cost ")
                    .append(millis)
                    .append(" millis ")
                    .append(micro)
                    .append(" micro ")
                    .append(nano)
                    .append(" nano");
        }

        if (mode.debug) {
            getLogger().debug(builder.toString());
        } else {
            getLogger().info(builder.toString());
        }

    }


    protected final void printExecutionCostTimeLog(final Logger logger, final Stmt stmt, final SqlLogMode sqlLogMode,
                                                   final long startNanoSecond) {

        if (startNanoSecond < 1L || sqlLogMode == SqlLogMode.OFF) {
            return;
        }

        final long costNano, millis, micro, nano;
        costNano = System.nanoTime() - startNanoSecond;

        millis = costNano / 1000_000L;
        micro = (costNano % 1000_000L) / 1000L;
        nano = costNano % 1000L;


        final StringBuilder builder = new StringBuilder(256);

        builder.append("session[name : ")
                .append(this.name)
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append("]\n\n");

        this.factory.dialectParser.printStmt(stmt, sqlLogMode.beautify, builder::append);


        builder.append("\n\nsql execution cost ")
                .append(millis)
                .append(" millis ")
                .append(micro)
                .append(" micro ")
                .append(nano)
                .append(" nano");

        if (sqlLogMode.debug) {
            logger.debug(builder.toString());
        } else {
            logger.info(builder.toString());
        }

    }


    /*-------------------below static method -------------------*/

    protected static <R> ReaderFunction<R> constructorReaderFunc(final Supplier<R> constructor) {
        return (stmt, immutableMap) -> new ObjectReader<>(constructor, null, stmt, immutableMap)::readRow;
    }

    protected static <R> ReaderFunction<R> classReaderFunc(final Class<R> resultClass) {
        return (stmt, immutableMap) -> {
            final Function<CurrentRecord, R> rowFunc;
            if ((stmt instanceof TwoStmtQueryStmt && ((TwoStmtQueryStmt) stmt).maxColumnSize() == 1)
                    || stmt.selectionList().size() == 1) {
                rowFunc = record -> record.get(0, resultClass);
            } else {
                final ObjectAccessor accessor;
                accessor = ObjectAccessorFactory.forBean(resultClass);

                final ObjectReader<R> objectReader;
                objectReader = new ObjectReader<>(ObjectAccessorFactory.beanConstructor(resultClass), accessor, stmt, false);
                rowFunc = objectReader::readRow;
            }
            return rowFunc;
        };
    }


    protected static void assertTransactionInfo(final @Nullable TransactionInfo info, final TransactionOption option) {
        assert info != null;

        final Isolation isolation = option.isolation();

        assert info.inTransaction(); // fail,executor bug
        assert info.isReadOnly() == option.isReadOnly();
        assert isolation == null || isolation.equals(info.isolation());
        assert info.valueOf(Option.START_MILLIS) != null;

        assert (option.isolation() == null) == info.nonNullOf(Option.DEFAULT_ISOLATION);

        assert Objects.equals(info.valueOf(Option.TIMEOUT_MILLIS), option.valueOf(Option.TIMEOUT_MILLIS));
        assert Objects.equals(info.valueOf(Option.NAME), option.valueOf(Option.NAME));
        assert Objects.equals(info.valueOf(Option.LABEL), option.valueOf(Option.LABEL));

    }

    protected static void assertXaEndTransactionInfo(final TransactionInfo startInfo, int flags, final @Nullable TransactionInfo endInfo) {
        assert endInfo != null;

        assert endInfo.inTransaction(); // fail ,executor bug
        assert Objects.equals(endInfo.valueOf(Option.XID), startInfo.valueOf(Option.XID));  // use infoXid ; fail ,executor bug
        assert endInfo.valueOf(Option.XA_STATES) == XaStates.IDLE;  // fail ,executor bug
        assert endInfo.nonNullOf(Option.XA_FLAGS) == flags;  // fail ,executor bug

        assert Objects.equals(endInfo.valueOf(Option.START_MILLIS), startInfo.valueOf(Option.START_MILLIS));
        assert Objects.equals(endInfo.valueOf(Option.DEFAULT_ISOLATION), startInfo.valueOf(Option.DEFAULT_ISOLATION));
        assert Objects.equals(endInfo.valueOf(Option.TIMEOUT_MILLIS), startInfo.valueOf(Option.TIMEOUT_MILLIS));
        assert Objects.equals(endInfo.valueOf(Option.NAME), startInfo.valueOf(Option.NAME));

        assert Objects.equals(endInfo.valueOf(Option.LABEL), startInfo.valueOf(Option.LABEL));
    }


    @Nullable
    protected static TableMeta<?> getBatchUpdateDomainTable(final BatchDmlStatement statement) {
        final TableMeta<?> domainTable;
        if (statement instanceof _MultiDml || statement instanceof _Statement._WithDmlSpec) {
            domainTable = null;
        } else if (statement instanceof _Statement._ChildStatement) {
            domainTable = ((_Statement._ChildStatement) statement).table();
            assert domainTable instanceof ChildTableMeta;
        } else {
            domainTable = ((_SingleDml) statement).table();
        }
        return domainTable;
    }


    protected static ChildDmlNoTractionException updateChildNoTransaction() {
        return new ChildDmlNoTractionException("insert/update/delete child must in transaction.");
    }


    protected static SessionException wrapSessionError(final Exception cause) {
        if (cause instanceof SessionException) {
            throw (SessionException) cause;
        }
        return new SessionException("unknown session error," + cause.getMessage(), cause);
    }

    public static Throwable wrapIfNeed(final Throwable cause) {
        return _Exceptions.wrapIfNeed(cause);
    }


    private static boolean isUseStaticMultiStmt(StmtOption option) {
        final boolean use;
        switch (option.multiStmtMode()) {
            case DRIVER_SPI:
                use = false;
                break;
            case DEFAULT:
            case STATIC:
            default:
                use = true;
        }
        return use;
    }

    private static void validateOptimisticLock(final ResultStates states) {
        if (states.affectedRows() > 0L) {
            return;
        }
        if (states.batchSize() > 0) {
            throw _Exceptions.batchOptimisticLock(null, states.batchNo(), states.affectedRows());
        } else {
            throw _Exceptions.optimisticLock();
        }
    }


    @FunctionalInterface
    protected interface ReaderFunction<R> {

        Function<CurrentRecord, R> apply(SingleSqlStmt stmt, boolean immutableMap);
    }


    private static abstract class SecondRecordReader<R> {

        private final Session session;

        private final ChildTableMeta<?> childTable;

        private final TwoStmtQueryStmt stmt;

        private final List<R> firstList;

        private final ObjectAccessor accessor;

        private final Class<?>[] columnClassArray;

        private final String[] columnLabelArray;

        private final int idSelectionIndex;

        private final Map<Object, R> idToRowMap;


        private SecondRecordReader(Session session, ChildTableMeta<?> childTable,
                                   final TwoStmtQueryStmt stmt, final List<R> firstList) {
            this.session = session;
            this.childTable = childTable;
            this.stmt = stmt;
            this.firstList = firstList;

            final R row = firstList.get(0);
            final ObjectAccessor accessor;
            if (row instanceof Map || stmt.maxColumnSize() > 1) {
                this.accessor = accessor = ObjectAccessorFactory.fromInstance(row);
            } else {
                this.accessor = accessor = ExecutorSupport.SINGLE_COLUMN_PSEUDO_ACCESSOR;
            }

            final List<? extends Selection> selectionList = stmt.selectionList();
            final int selectionSize = selectionList.size();
            final Class<?>[] columnClassArray;
            final String[] columnLabelArray;
            this.columnClassArray = columnClassArray = new Class<?>[selectionSize];
            this.columnLabelArray = columnLabelArray = new String[selectionSize];
            this.idSelectionIndex = stmt.idSelectionIndex();
            if (accessor == ExecutorSupport.SINGLE_COLUMN_PSEUDO_ACCESSOR) {
                columnClassArray[0] = row.getClass();
                columnLabelArray[0] = selectionList.get(0).label();
            } else if (row instanceof Map) {
                Selection selection;
                TypeMeta typeMeta;
                for (int i = 0; i < selectionSize; i++) {
                    selection = selectionList.get(i);
                    typeMeta = selection.typeMeta();
                    if (!(typeMeta instanceof MappingType)) {
                        typeMeta = typeMeta.mappingType();
                    }
                    columnClassArray[i] = ((MappingType) typeMeta).javaType();
                    columnLabelArray[i] = selection.label();
                }
            } else {
                String columnLabel;
                for (int i = 0; i < selectionSize; i++) {
                    columnLabel = selectionList.get(i).label();
                    columnClassArray[i] = accessor.getJavaType(columnLabel);
                    columnLabelArray[i] = columnLabel;
                }
            }
            // finally
            this.idToRowMap = createIdToRowMap();
        }


        @SuppressWarnings("unchecked")
        public final R readRecord(final CurrentRecord record) {
            final int columnCount = record.getColumnCount();
            final Class<?>[] columnClassArray = this.columnClassArray;
            if (columnCount != columnClassArray.length) {
                throw _Exceptions.columnCountAndSelectionCountNotMatch(columnCount, columnCount);
            }

            final Map<Object, R> idToRowMap = this.idToRowMap;
            final int idSelectionIndex = this.idSelectionIndex;

            final Object id;
            id = record.get(idSelectionIndex, columnClassArray[idSelectionIndex]);
            if (id == null) {
                throw _Exceptions.secondStmtIdIsNull(this.stmt.selectionList().get(idSelectionIndex));
            }
            final R row;
            if ((row = idToRowMap.get(id)) == null) {
                String m = String.format("Not found match row for %s(based 1) row id[%s] in first query ", record.rowNumber(), id);
                throw new DataAccessException(m);
            }

            final ObjectAccessor accessor = this.accessor;
            final String[] columnLabelArray = this.columnLabelArray;
            final boolean singleColumnRow;
            singleColumnRow = accessor == ExecutorSupport.SINGLE_COLUMN_PSEUDO_ACCESSOR;

            assert columnCount > 1 || singleColumnRow;
            for (int i = 0; i < columnCount; i++) {
                if (i == idSelectionIndex) {
                    continue;
                }
                accessor.set(row, columnLabelArray[i], record.get(i, columnClassArray[i]));
            }

            final R finalRow;
            if (row instanceof Map && row instanceof ImmutableSpec) {
                finalRow = (R) _Collections.unmodifiableMap((Map<String, Object>) row);
            } else {
                finalRow = row;
            }
            if (this instanceof _ArmySession.SyncSecondRecordReader<R>) {
                ((SyncSecondRecordReader<R>) this).rowCount++;
            } else {
                ReactiveSecondRecordReader.ROW_COUNT.getAndIncrement((ReactiveSecondRecordReader<R>) this);
            }
            return finalRow;
        }

        public final void validateRowCount() {
            final int firstListSize = this.firstList.size();
            final long rowCount;
            if (this instanceof _ArmySession.SyncSecondRecordReader<R>) {
                rowCount = ((SyncSecondRecordReader<R>) this).rowCount;
            } else {
                rowCount = ((ReactiveSecondRecordReader<R>) this).rowCount;
            }
            if (rowCount != firstListSize) {
                throw _Exceptions.parentChildRowsNotMatch(this.session, this.childTable, firstListSize, rowCount);
            }
        }

        private Map<Object, R> createIdToRowMap() {
            final ObjectAccessor accessor = this.accessor;
            final boolean singleColumnRow;
            singleColumnRow = accessor == ExecutorSupport.SINGLE_COLUMN_PSEUDO_ACCESSOR;

            final String idLabel;
            if (singleColumnRow) {
                idLabel = null;
            } else {
                idLabel = this.stmt.selectionList().get(this.idSelectionIndex).label();
            }

            final List<R> firstList = this.firstList;
            final int rowSize = firstList.size();
            final Map<Object, R> rowMap = _Collections.hashMapForSize(rowSize);

            Object id;
            R row;
            for (int i = 0; i < rowSize; i++) {

                row = firstList.get(i);
                if (row == null) {
                    // no bug,never here
                    throw new NullPointerException(String.format("%s row is null", i + 1));
                }

                if (singleColumnRow) {
                    id = row;
                } else {
                    id = accessor.get(row, idLabel);
                }

                if (id == null) {
                    // no bug,never here
                    throw new NullPointerException(String.format("%s row id is null", i + 1));
                }

                if (rowMap.putIfAbsent(id, row) != null) {
                    throw new CriteriaException(String.format("%s row id[%s] duplication", i + 1, id));
                }

            } // for loop

            return Collections.unmodifiableMap(rowMap);
        }


    } // SecondRecordReader

    protected static final class SyncSecondRecordReader<R> extends SecondRecordReader<R> {

        private long rowCount;

        public SyncSecondRecordReader(Session session, ChildTableMeta<?> childTable, TwoStmtQueryStmt stmt, List<R> firstList) {
            super(session, childTable, stmt, firstList);
        }

    } // SyncSecondRecordReader


    protected static final class ReactiveSecondRecordReader<R> extends SecondRecordReader<R> {

        @SuppressWarnings("unchecked")
        private static final AtomicLongFieldUpdater<ReactiveSecondRecordReader<?>> ROW_COUNT =
                AtomicLongFieldUpdater.newUpdater((Class<ReactiveSecondRecordReader<?>>) ((Class<?>) ReactiveSecondRecordReader.class), "rowCount");

        private volatile long rowCount = 0;

        public ReactiveSecondRecordReader(Session session, ChildTableMeta<?> childTable, TwoStmtQueryStmt stmt, List<R> firstList) {
            super(session, childTable, stmt, firstList);
        }


    } //ReactiveSecondRecordReader


    private static final class ObjectReader<R> {

        private final Supplier<R> constructor;

        private final boolean immutableMap;

        private final List<? extends Selection> selectionList;

        private final Class<?>[] columnClassArray;

        private final String[] columnLabelArray;

        private ObjectAccessor accessor;

        private ObjectReader(Supplier<R> constructor, @Nullable ObjectAccessor accessor, SingleSqlStmt stmt, boolean immutableMap) {
            this.constructor = constructor;
            this.accessor = accessor;
            final List<? extends Selection> selectionList;
            this.selectionList = selectionList = stmt.selectionList();
            this.immutableMap = immutableMap;

            final int selectionSize = selectionList.size();
            this.columnClassArray = new Class<?>[selectionSize];

            final String[] columnLabelArray;
            this.columnLabelArray = columnLabelArray = new String[selectionSize];
            for (int i = 0; i < selectionSize; i++) {
                columnLabelArray[i] = selectionList.get(i).label();
            }
        }


        @SuppressWarnings("unchecked")
        private R readRow(final CurrentRecord record) {
            final int columnCount = record.getColumnCount();
            final String[] columnLabelArray = this.columnLabelArray;

            if (columnCount != columnLabelArray.length) {
                throw _Exceptions.columnCountAndSelectionCountNotMatch(columnCount, columnLabelArray.length);
            }

            final R row;
            row = this.constructor.get();
            if (row == null) {
                throw _Exceptions.objectConstructorError();
            }
            ObjectAccessor accessor = this.accessor;
            if (accessor == null) {
                this.accessor = accessor = ObjectAccessorFactory.fromInstance(row);
            }


            final Class<?>[] columnClassArray = this.columnClassArray;
            final List<? extends Selection> selectionList = this.selectionList;
            TypeMeta typeMeta;

            String propertyName;
            Class<?> clumnClass;
            Object value;
            for (int i = 0; i < columnCount; i++) {
                propertyName = columnLabelArray[i];
                clumnClass = columnClassArray[i];

                if (clumnClass == null) {
                    if (row instanceof Map) {
                        typeMeta = selectionList.get(i).typeMeta();
                        if (!(typeMeta instanceof MappingType)) {
                            typeMeta = typeMeta.mappingType();
                        }
                        clumnClass = ((MappingType) typeMeta).javaType();
                    } else {
                        clumnClass = accessor.getJavaType(propertyName);
                    }
                    columnClassArray[i] = clumnClass;
                }

                value = record.get(i, clumnClass);

                accessor.set(row, propertyName, value);
            }

            final R finalRow;
            if (row instanceof Map && row instanceof ImmutableSpec && this.immutableMap) {
                finalRow = (R) _Collections.unmodifiableMap((Map<String, Object>) row);
            } else {
                finalRow = row;
            }
            return finalRow;
        }


    } // ObjectReader


}
