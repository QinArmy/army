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

package io.army.jdbd;

import io.army.ArmyException;
import io.army.criteria.SQLParam;
import io.army.criteria.Selection;
import io.army.env.SqlLogMode;
import io.army.executor.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.option.Option;
import io.army.result.CurrentRecord;
import io.army.result.ResultStates;
import io.army.result.Warning;
import io.army.session.ReactiveStmtOption;
import io.army.session.Session;
import io.army.session.TimeoutException;
import io.army.sqltype.ArmyType;
import io.army.sqltype.DataType;
import io.army.stmt.*;
import io.army.transaction.*;
import io.army.type.Blob;
import io.army.type.BlobPath;
import io.army.type.Clob;
import io.army.type.TextPath;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import io.army.util._TimeUtils;
import io.jdbd.JdbdException;
import io.jdbd.meta.JdbdType;
import io.jdbd.result.CurrentRow;
import io.jdbd.result.DataRow;
import io.jdbd.result.ResultRowMeta;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.LocalDatabaseSession;
import io.jdbd.session.RmDatabaseSession;
import io.jdbd.session.SavePoint;
import io.jdbd.statement.BindStatement;
import io.jdbd.statement.ParametrizedStatement;
import io.jdbd.util.SqlLogger;
import org.slf4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>This class is a abstract implementation of {@link ReactiveExecutor} with jdbd spi.
 * <p>This class is base class of following jdbd executor:
 * <ul>
 *     <li>{@link MySQLStmtExecutor}</li>
 *     <li>{@link PostgreStmtExecutor}</li>
 * </ul>
 * <p>Following is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @see JdbdStmtExecutorFactory
 * @see <a href="https://github.com/QinArmy/jdbd">jdbd-spi</a>
 */
abstract class JdbdExecutor extends JdbdExecutorSupport
        implements ReactiveExecutor,
        ReactiveExecutor.LocalTransactionSpec,
        ReactiveExecutor.XaTransactionSpec,
        Session.XaTransactionSupportSpec {


    final JdbdStmtExecutorFactory factory;

    final DatabaseSession session;

    final String sessionName;

    private final SqlLogger jdbdSqlLogger;

    private Set<Option<?>> optionSet;

    JdbdExecutor(JdbdStmtExecutorFactory factory, DatabaseSession session, String sessionName) {
        this.sessionName = sessionName;
        this.factory = factory;
        this.session = session;

        if (readSqlLogMode(factory) == SqlLogMode.OFF) {
            this.jdbdSqlLogger = null;
        } else {
            this.jdbdSqlLogger = this::jdbdSqlLogger;
        }

    }


    @Override
    public final long sessionIdentifier(Function<Option<?>, ?> sessionFunc) throws DataAccessException {
        try {
            return this.session.sessionIdentifier();
        } catch (Exception e) {
            throw wrapExecutingError(e);
        }
    }

    @Override
    public final boolean inTransaction(Function<Option<?>, ?> sessionFunc) throws DataAccessException {
        try {
            return this.session.inTransaction();
        } catch (JdbdException e) {
            throw wrapExecutingError(e);
        }
    }

    @Override
    public final boolean isSameFactory(StmtExecutor s) {
        return s instanceof JdbdExecutor && ((JdbdExecutor) s).factory == this.factory;
    }

    @Override
    public final boolean isDriverAssignableTo(Class<?> spiClass) {
        return spiClass.isAssignableFrom(this.session.getClass());
    }

    @Override
    public final <T> T getDriverSpi(Class<T> spiClass) {
        return spiClass.cast(this.session);
    }

    @Override
    public final Mono<TransactionInfo> transactionInfo() {
        final Function<io.jdbd.session.Option<?>, ?> optionFunc;
        optionFunc = addJdbdLogOptionIfNeed(io.jdbd.session.Option.EMPTY_OPTION_FUNC);

        return Mono.from(this.session.transactionInfo(optionFunc))
                .map(this::mapToArmyTransactionInfo)
                .onErrorMap(this::wrapExecuteIfNeed);
    }

    @Override
    public final Mono<TransactionInfo> sessionTransactionCharacteristics(final Function<Option<?>, ?> optionFunc) {
        return Mono.from(this.session.sessionTransactionCharacteristics(mapToJdbdOptionFunc(optionFunc)))
                .map(this::mapToArmyTransactionInfo)
                .onErrorMap(this::wrapExecuteIfNeed);
    }

    @Override
    public final Mono<Void> setTransactionCharacteristics(TransactionOption option) {
        final io.jdbd.session.TransactionOption jdbdOption;
        try {
            jdbdOption = mapToJdbdTransactionOption(option);
        } catch (Throwable e) {
            return Mono.error(wrapExecuteIfNeed(e));
        }
        return Mono.from(this.session.setTransactionCharacteristics(jdbdOption))
                .onErrorMap(this::wrapExecuteIfNeed)
                .then();
    }

    @Override
    public final Mono<?> setSavePoint(Function<Option<?>, ?> optionFunc) {
        return Mono.from(this.session.setSavePoint(mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed);
    }

    @Override
    public final Mono<Void> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!(savepoint instanceof SavePoint)) {
            return Mono.error(_Exceptions.unknownSavePoint(savepoint));
        }
        return Mono.from(this.session.releaseSavePoint((SavePoint) savepoint, mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed)
                .then();
    }


    @Override
    public final Mono<Void> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!(savepoint instanceof SavePoint)) {
            return Mono.error(_Exceptions.unknownSavePoint(savepoint));
        }
        return Mono.from(this.session.rollbackToSavePoint((SavePoint) savepoint, mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed)
                .then();
    }

    @Override
    public final Mono<ResultStates> insert(final SimpleStmt stmt, final ReactiveStmtOption option,
                                           final Function<Option<?>, ?> optionFunc) {

        final List<? extends Selection> selectionList = stmt.selectionList();
        final boolean returningId;
        returningId = selectionList.size() == 1 && selectionList.get(0) instanceof PrimaryFieldMeta;

        final AtomicReference<io.jdbd.result.ResultStates> jdbdStatesHolder;
        final Function<CurrentRow, Boolean> extractIdFunc;
        final Supplier<Mono<ResultStates>> monoSupplier;


        if (returningId) {
            final GeneratedKeyStmt keyStmt = (GeneratedKeyStmt) stmt;
            final MappingType type = keyStmt.idField().mappingType();
            final DataType dataType = type.map(this.factory.serverMeta);
            jdbdStatesHolder = new AtomicReference<>(null);

            final int rowSize = keyStmt.rowSize();
            final int[] rowIndexHolder = new int[]{0};

            extractIdFunc = row -> {
                Object idValue;
                idValue = get(row, 0, type, dataType);
                final int rowIndex = rowIndexHolder[0]++;
                if (idValue == null) {
                    throw _Exceptions.idValueIsNull(rowIndex, keyStmt.idField());
                }
                idValue = type.afterGet(dataType, this.factory.mappingEnv, idValue);
                keyStmt.setGeneratedIdValue(rowIndex, idValue);
                if (row.rowNumber() != rowIndexHolder[0]) {
                    String m = String.format("jdbd row index error,expected %s but %s", rowIndexHolder[0], row.rowNumber());
                    throw new DataAccessException(m);
                }
                return Boolean.TRUE;
            };

            monoSupplier = () -> {
                if (rowSize != rowIndexHolder[0]) {
                    return Mono.error(_Exceptions.insertedRowsAndGenerateIdNotMatch(rowSize, rowIndexHolder[0]));
                }
                return Mono.just(mapToArmyUpdateStates(jdbdStatesHolder.get(), optionFunc));
            };
        } else {
            extractIdFunc = null;
            jdbdStatesHolder = null;
            monoSupplier = null;
        }


        final BindStatement statement;
        try {
            statement = bindStatement(stmt, option);
        } catch (Throwable e) {
            return Mono.error(wrapExecuteIfNeed(e));
        }

        final Mono<ResultStates> mono;
        if (returningId) {
            mono = Flux.from(statement.executeQuery(extractIdFunc, jdbdStatesHolder::set))
                    .then(Mono.defer(monoSupplier));
        } else if (stmt instanceof GeneratedKeyStmt) {
            mono = Mono.from(statement.executeUpdate())
                    .map(states -> handleInsertStates(states, (GeneratedKeyStmt) stmt, optionFunc));
        } else {
            mono = Mono.from(statement.executeUpdate())
                    .map(states -> mapToArmyUpdateStates(states, optionFunc));
        }
        return mono.onErrorMap(this::wrapExecuteIfNeed);

    }


    @Override
    public final Mono<ResultStates> update(final SimpleStmt stmt, final ReactiveStmtOption option,
                                           final Function<Option<?>, ?> optionFunc) {
        Mono<ResultStates> mono;
        try {
            final BindStatement statement;
            statement = bindStatement(stmt, option);

            mono = Mono.from(statement.executeUpdate())
                    .map(states -> mapToArmyUpdateStates(states, optionFunc))
                    .onErrorMap(this::wrapExecuteIfNeed);
        } catch (Throwable e) {
            mono = Mono.error(wrapExecuteIfNeed(e));
        }
        return mono;
    }

    @Override
    public final Flux<ResultStates> batchUpdate(final BatchStmt stmt, final ReactiveStmtOption option,
                                                final Function<Option<?>, ?> optionFunc) {
        Flux<ResultStates> flux;
        try {
            final BindStatement statement;
            statement = bindStatement(stmt, option);

            flux = Flux.from(statement.executeBatchUpdate())
                    .map(states -> mapToArmyUpdateStates(states, optionFunc))
                    .onErrorMap(this::wrapExecuteIfNeed);
        } catch (Throwable e) {
            flux = Flux.error(wrapExecuteIfNeed(e));
        }
        return flux;
    }


    @Override
    public final <R> Flux<R> queryRecord(SingleSqlStmt stmt, Function<? super CurrentRecord, R> function,
                                         ReactiveStmtOption option, final Function<Option<?>, ?> optionFunc) {
        Flux<R> flux;
        try {

            final JdbdCurrentRecord<R> record;
            record = new JdbdCurrentRecord<>(this, stmt, option, function, optionFunc);

            final BindStatement statement;
            statement = bindStatement(stmt, option);

            // TODO 处理批量查询
            flux = Flux.from(statement.executeQuery(record::readOneRow, record::acceptStates))
                    .onErrorMap(this::wrapExecuteIfNeed);
        } catch (Throwable e) {
            flux = Flux.error(wrapExecuteIfNeed(e));
        }
        return flux;
    }






    /*-------------------below local transaction methods -------------------*/


    @Override
    public final Mono<TransactionInfo> startTransaction(final TransactionOption option, final HandleMode mode) {
        if (!(this instanceof ReactiveLocalExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }

        final io.jdbd.session.HandleMode jdbdMode;
        switch (mode) {
            case ERROR_IF_EXISTS:
                jdbdMode = io.jdbd.session.HandleMode.ERROR_IF_EXISTS;
                break;
            case ROLLBACK_IF_EXISTS:
                jdbdMode = io.jdbd.session.HandleMode.ROLLBACK_IF_EXISTS;
                break;
            case COMMIT_IF_EXISTS:
                jdbdMode = io.jdbd.session.HandleMode.COMMIT_IF_EXISTS;
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }


        final io.jdbd.session.TransactionOption jdbdOption;

        try {
            jdbdOption = mapToJdbdTransactionOption(option);
        } catch (Throwable e) {
            return Mono.error(wrapExecuteIfNeed(e));
        }

        return Mono.from(((LocalDatabaseSession) this.session).startTransaction(jdbdOption, jdbdMode))
                .map(this::mapToArmyTransactionInfo)
                .onErrorMap(this::wrapExecuteIfNeed);
    }

    @Override
    public final Mono<Optional<TransactionInfo>> commit(final Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveLocalExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }

        return Mono.from(((LocalDatabaseSession) this.session).commit(mapToJdbdOptionFunc(optionFunc)))
                .map(this::mapToArmyOptionalTransactionInfo)
                .onErrorMap(this::wrapExecuteIfNeed);
    }


    @Override
    public final Mono<Optional<TransactionInfo>> rollback(final Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveLocalExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }

        return Mono.from(((LocalDatabaseSession) this.session).rollback(mapToJdbdOptionFunc(optionFunc)))
                .map(this::mapToArmyOptionalTransactionInfo)
                .onErrorMap(this::wrapExecuteIfNeed);
    }


    /*-------------------below XA transaction methods -------------------*/


    @Override
    public final Mono<TransactionInfo> start(Xid xid, int flags, TransactionOption option) {
        if (!(this instanceof ReactiveRmExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }

        final io.jdbd.session.Xid jdbdXid;
        final io.jdbd.session.TransactionOption jdbdOption;
        try {
            jdbdXid = mapToJdbdXid(xid);
            jdbdOption = mapToJdbdTransactionOption(option);
        } catch (Throwable e) {
            return Mono.error(wrapExecuteIfNeed(e));
        }
        return Mono.from(((RmDatabaseSession) this.session).start(jdbdXid, flags, jdbdOption))
                .map(this::mapToArmyTransactionInfo)
                .onErrorMap(this::wrapExecuteIfNeed);
    }

    @Override
    public final Mono<TransactionInfo> end(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveRmExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }
        final io.jdbd.session.Xid jdbdXid;
        try {
            jdbdXid = mapToJdbdXid(xid);
        } catch (Throwable e) {
            return Mono.error(wrapExecuteIfNeed(e));
        }

        return Mono.from(((RmDatabaseSession) this.session).end(jdbdXid, flags, mapToJdbdOptionFunc(optionFunc)))
                .map(this::mapToArmyTransactionInfo)
                .onErrorMap(this::wrapExecuteIfNeed);
    }

    @Override
    public final Mono<Integer> prepare(Xid xid, Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveRmExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }
        final io.jdbd.session.Xid jdbdXid;
        try {
            jdbdXid = mapToJdbdXid(xid);
        } catch (Throwable e) {
            return Mono.error(wrapExecuteIfNeed(e));
        }

        return Mono.from(((RmDatabaseSession) this.session).prepare(jdbdXid, mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed);
    }

    @Override
    public final Mono<Void> commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveRmExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }
        final io.jdbd.session.Xid jdbdXid;
        try {
            jdbdXid = mapToJdbdXid(xid);
        } catch (Throwable e) {
            return Mono.error(wrapExecuteIfNeed(e));
        }

        return Mono.from(((RmDatabaseSession) this.session).commit(jdbdXid, flags, mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed)
                .then();
    }

    @Override
    public final Mono<Void> rollback(Xid xid, Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveRmExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }
        final io.jdbd.session.Xid jdbdXid;
        try {
            jdbdXid = mapToJdbdXid(xid);
        } catch (Throwable e) {
            return Mono.error(wrapExecuteIfNeed(e));
        }

        return Mono.from(((RmDatabaseSession) this.session).rollback(jdbdXid, mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed)
                .then();
    }


    @Override
    public final Mono<Void> forget(Xid xid, Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveRmExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }
        final io.jdbd.session.Xid jdbdXid;
        try {
            jdbdXid = mapToJdbdXid(xid);
        } catch (Throwable e) {
            return Mono.error(wrapExecuteIfNeed(e));
        }
        return Mono.from(((RmDatabaseSession) this.session).forget(jdbdXid, mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed)
                .then();
    }

    @Override
    public final Flux<Optional<Xid>> recover(int flags, Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveRmExecutor)) {
            return Flux.error(new UnsupportedOperationException());
        }
        return Flux.from(((RmDatabaseSession) this.session).recover(flags, mapToJdbdOptionFunc(optionFunc)))
                .map(this::mapToOptionalArmyXid)
                .onErrorMap(this::wrapExecuteIfNeed);
    }

    @Override
    public final boolean isSupportForget() {
        if (!(this instanceof ReactiveRmExecutor)) {
            throw new UnsupportedOperationException();
        }
        return ((RmDatabaseSession) this.session).isSupportForget();
    }

    @Override
    public final int startSupportFlags() {
        if (!(this instanceof ReactiveRmExecutor)) {
            throw new UnsupportedOperationException();
        }
        return ((RmDatabaseSession) this.session).startSupportFlags();
    }

    @Override
    public final int endSupportFlags() {
        if (!(this instanceof ReactiveRmExecutor)) {
            throw new UnsupportedOperationException();
        }
        return ((RmDatabaseSession) this.session).endSupportFlags();
    }

    @Override
    public final int commitSupportFlags() {
        if (!(this instanceof ReactiveRmExecutor)) {
            throw new UnsupportedOperationException();
        }
        return ((RmDatabaseSession) this.session).commitSupportFlags();
    }

    @Override
    public final int recoverSupportFlags() {
        if (!(this instanceof ReactiveRmExecutor)) {
            throw new UnsupportedOperationException();
        }
        return ((RmDatabaseSession) this.session).recoverSupportFlags();
    }

    @Override
    public final boolean isSameRm(final Session.XaTransactionSupportSpec s) {
        if (!(this instanceof ReactiveRmExecutor)) {
            throw new UnsupportedOperationException();
        }
        final boolean match;
        if (s == this) {
            match = true;
        } else if (s instanceof JdbdExecutor) {
            match = this.session.isSameFactory(((JdbdExecutor) s).session);
        } else {
            match = false;
        }
        return match;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T valueOf(final @Nullable Option<T> option) {
        final Object v;
        final T value;
        final io.jdbd.session.Option<?> jdbdOption;
        if (option == null) {
            value = null;
        } else if ((jdbdOption = this.factory.mapToJdbdOption(option)) == null) {
            value = null;
        } else if (option.javaType().isInstance(v = this.session.valueOf(jdbdOption))) {
            value = (T) v;
        } else {
            value = null;
        }
        return value;
    }


    @Override
    public final boolean isClosed() {
        return this.session.isClosed();
    }

    @Override
    public final <T> Mono<T> close() {
        return Mono.from(this.session.close());
    }

    @Override
    public final String toString() {
        return _StringUtils.builder(70)
                .append(getClass().getName())
                .append("[sessionName:")
                .append(this.sessionName)
                .append(",executorHash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }

    /*-------------------below package instance methods-------------------*/

    abstract Logger getLogger();


    abstract DataType getDataType(ResultRowMeta meta, int indexBasedZero);

    @Nullable
    abstract Object get(DataRow row, int indexBasedZero, MappingType type, DataType dataType);

    abstract void bind(ParametrizedStatement statement, int indexBasedZero, MappingType type, DataType dataType, @Nullable Object value);


    Isolation mapToArmyDialectIsolation(io.jdbd.session.Isolation jdbdIsolation) {
        throw unknownJdbdIsolation(jdbdIsolation);
    }

    io.jdbd.session.Isolation mapToJdbdDialectIsolation(Isolation isolation) {
        throw unsupportedIsolation(isolation);
    }


    /**
     * @see #bindParameters(ParametrizedStatement, List)
     */
    final void bindArmyType(ParametrizedStatement stmt, final int indexBasedZero, final MappingType type,
                            final DataType dataType, final ArmyType armyType, final @Nullable Object nullable) {

        final JdbdType jdbdType;

        try {
            jdbdType = JdbdType.valueOf(armyType.name());
        } catch (IllegalArgumentException e) {
            throw mapMethodError(type, dataType);
        }

        final Object value;
        if (nullable == null) {
            value = null;
        } else switch (armyType) {
            case BOOLEAN: {
                if (!(nullable instanceof Boolean)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case TINYINT: {
                if (!(nullable instanceof Byte)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case TINYINT_UNSIGNED:
            case SMALLINT: {
                if (!(nullable instanceof Short)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INTEGER: {
                if (!(nullable instanceof Integer)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case INTEGER_UNSIGNED:
            case BIGINT: {
                if (!(nullable instanceof Long)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case BIGINT_UNSIGNED: {
                if (!(nullable instanceof BigInteger || nullable instanceof BigDecimal)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case DECIMAL:
            case DECIMAL_UNSIGNED: {
                if (!(nullable instanceof BigDecimal)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case FLOAT: {
                if (!(nullable instanceof Float)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case DOUBLE: {
                if (!(nullable instanceof Double)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case TIME: {
                if (!(nullable instanceof LocalTime)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case YEAR_MONTH: {
                if (!(nullable instanceof LocalDate || nullable instanceof YearMonth)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case MONTH_DAY: {
                if (!(nullable instanceof LocalDate || nullable instanceof MonthDay)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case DATE: {
                if (!(nullable instanceof LocalDate)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case TIMESTAMP: {
                if (!(nullable instanceof LocalDateTime)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case TIME_WITH_TIMEZONE: {
                if (!(nullable instanceof OffsetTime)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case TIMESTAMP_WITH_TIMEZONE: {
                if (!(nullable instanceof OffsetDateTime)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case CHAR:
            case VARCHAR:
            case ENUM:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT: {
                if (!(nullable instanceof String)) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case JSON:
            case JSONB:
            case LONGTEXT:
                value = toJdbdLongTextValue(type, dataType, nullable);
                break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB: {
                if (!(nullable instanceof byte[])) {
                    throw beforeBindMethodError(type, dataType, nullable);
                }
                value = nullable;
            }
            break;
            case LONGBLOB:
                value = toJdbdLongBinaryValue(type, dataType, nullable);
                break;
            case GEOMETRY:
                value = toJdbdGeometry(type, dataType, nullable);
                break;
            default:
                throw mapMethodError(type, dataType);

        }


        stmt.bind(indexBasedZero, jdbdType, value);

    }

    @Nullable
    final Object toJdbdGeometry(final MappingType type, final DataType dataType, final @Nullable Object nullable) {
        final Object value;
        if (nullable == null || nullable instanceof byte[] || nullable instanceof String) {
            value = nullable;
        } else if (nullable instanceof Blob) {
            final Blob blob = (Blob) nullable;
            value = io.jdbd.type.Blob.from(blob.value());
        } else if (nullable instanceof Clob) {
            final Clob clob = (Clob) nullable;
            value = io.jdbd.type.Clob.from(clob.value());
        } else if (nullable instanceof io.army.type.BlobPath) {
            final io.army.type.BlobPath path = (io.army.type.BlobPath) nullable;
            value = io.jdbd.type.BlobPath.from(path.isDeleteOnClose(), path.value());
        } else if (nullable instanceof io.army.type.TextPath) {
            final io.army.type.TextPath armyPath = (io.army.type.TextPath) nullable;
            value = io.jdbd.type.TextPath.from(armyPath.isDeleteOnClose(), armyPath.charset(), armyPath.value());
        } else {
            throw beforeBindMethodError(type, dataType, nullable);
        }
        return value;
    }

    @Nullable
    final Object toJdbdLongTextValue(final MappingType type, final DataType dataType, final @Nullable Object nullable) {
        final Object value;
        if (nullable == null || nullable instanceof String) {
            value = nullable;
        } else if (nullable instanceof io.army.type.TextPath) {
            final io.army.type.TextPath armyPath = (io.army.type.TextPath) nullable;
            value = io.jdbd.type.TextPath.from(armyPath.isDeleteOnClose(), armyPath.charset(), armyPath.value());
        } else if (nullable instanceof Clob) {
            final Clob clob = (Clob) nullable;
            value = io.jdbd.type.Clob.from(clob.value());
        } else {
            throw beforeBindMethodError(type, dataType, nullable);
        }
        return value;
    }

    @Nullable
    final Object toJdbdLongBinaryValue(final MappingType type, final DataType dataType, final @Nullable Object nullable) {
        final Object value;
        if (nullable == null || nullable instanceof byte[]) {
            value = nullable;
        } else if (nullable instanceof io.army.type.BlobPath) {
            final io.army.type.BlobPath path = (io.army.type.BlobPath) nullable;
            value = io.jdbd.type.BlobPath.from(path.isDeleteOnClose(), path.value());
        } else if (nullable instanceof Blob) {
            final Blob blob = (Blob) nullable;
            value = io.jdbd.type.Blob.from(blob.value());
        } else {
            throw beforeBindMethodError(type, dataType, nullable);
        }
        return value;
    }

    @Nullable
    final Object getLongText(final DataRow row, final int indexBasedZero) {
        final Object value;
        if (row.isNull(indexBasedZero)) {
            value = null;
        } else if (row.isBigColumn(indexBasedZero)) {
            final io.jdbd.type.TextPath path = row.getNonNull(indexBasedZero, io.jdbd.type.TextPath.class);
            value = TextPath.from(path.isDeleteOnClose(), path.charset(), path.value());
        } else {
            value = row.get(indexBasedZero, String.class);
        }
        return value;
    }

    @Nullable
    final Object getLongBinary(final DataRow row, final int indexBasedZero, final MappingType type) {
        final Object value;
        if (row.isNull(indexBasedZero)) {
            value = null;
        } else if (row.isBigColumn(indexBasedZero)) {
            final io.jdbd.type.BlobPath path = row.getNonNull(indexBasedZero, io.jdbd.type.BlobPath.class);
            value = BlobPath.from(path.isDeleteOnClose(), path.value());
        } else if (type instanceof MappingType.SqlStringType) {
            value = row.get(indexBasedZero, String.class);
        } else {
            value = row.get(indexBasedZero, byte[].class);
        }
        return value;
    }

    final ArmyException wrapExecutingError(final Exception cause) {
        return this.factory.wrapExecuteError(cause);
    }


    final Function<io.jdbd.session.Option<?>, ?> mapToJdbdOptionFunc(final Function<Option<?>, ?> optionFunc) {
        return addJdbdLogOptionIfNeed(this.factory.mapToJdbdOptionFunc(optionFunc));
    }

    final Function<io.jdbd.session.Option<?>, ?> addJdbdLogOptionIfNeed(final Function<io.jdbd.session.Option<?>, ?> func) {

        final Function<io.jdbd.session.Option<?>, ?> finalJdbdFunc;
        if (this.jdbdSqlLogger == null) {
            finalJdbdFunc = func;
        } else {
            finalJdbdFunc = jdbdOption -> {
                if (io.jdbd.session.Option.SQL_LOGGER == jdbdOption) {
                    return this.jdbdSqlLogger;
                }
                return func.apply(jdbdOption);
            };
        }
        return finalJdbdFunc;
    }

    final Throwable wrapExecuteIfNeed(final Throwable cause) {
        if (!(cause instanceof Exception)) {
            return cause;
        }
        return wrapExecutingError((Exception) cause);
    }



    /*-------------------below private instance methods-------------------*/

    private void jdbdSqlLogger(final String sql) {
        printSqlIfNeed(this.factory, this.sessionName, getLogger(), sql);
    }

    /**
     * @see #start(Xid, int, TransactionOption)
     */
    private io.jdbd.session.Xid mapToJdbdXid(final Xid xid) {
        return io.jdbd.session.Xid.from(xid.getGtrid(), xid.getBqual(), xid.getFormatId());
    }

    private Xid mapToArmyXid(io.jdbd.session.Xid xid) {
        return Xid.from(xid.getGtrid(), xid.getBqual(), xid.getFormatId(), this.factory.mapToArmyOptionFunc(xid::valueOf));
    }


    /**
     * @see #transactionInfo()
     */
    @SuppressWarnings("unchecked")
    private TransactionInfo mapToArmyTransactionInfo(final io.jdbd.session.TransactionInfo info) {
        final TransactionInfo.InfoBuilder builder;
        builder = TransactionInfo.builder(info.inTransaction(), mapToArmyIsolation(info.isolation()), info.isReadOnly());

        Option<?> armyOption;
        for (io.jdbd.session.Option<?> jdbdOption : info.optionSet()) {
            armyOption = this.factory.mapToArmyOption(jdbdOption);
            if (armyOption != null) {
                builder.option((Option<Object>) armyOption, info.valueOf(jdbdOption));
            }
        }
        return builder.build();
    }


    /**
     * @throws ArmyException throw when isolation is unsupported by driver.
     * @see #setTransactionCharacteristics(TransactionOption)
     * @see #startTransaction(TransactionOption, HandleMode)
     * @see #start(Xid, int, TransactionOption)
     */
    @SuppressWarnings("unchecked")
    private io.jdbd.session.TransactionOption mapToJdbdTransactionOption(final TransactionOption option)
            throws ArmyException {

        final Set<Option<?>> armyOptionSet = option.optionSet();

        final io.jdbd.session.Isolation jdbdIsolation = mapToJdbdIsolation(option.isolation());
        final io.jdbd.session.TransactionOption jdbdTransactionOption;

        final SqlLogMode sqlLogMode;
        sqlLogMode = readSqlLogMode(this.factory);

        if (armyOptionSet.size() == 0 && sqlLogMode == SqlLogMode.OFF) {
            jdbdTransactionOption = io.jdbd.session.TransactionOption.option(jdbdIsolation, option.isReadOnly());
        } else {
            final io.jdbd.session.TransactionOption.Builder builder;
            builder = io.jdbd.session.TransactionOption.builder();
            io.jdbd.session.Option<?> jdbdOption;
            for (Option<?> armyOption : armyOptionSet) {
                jdbdOption = this.factory.mapToJdbdOption(armyOption);
                if (jdbdOption == null) {
                    continue;
                }
                builder.option((io.jdbd.session.Option<Object>) jdbdOption, option.valueOf(armyOption));

            }
            builder.option(io.jdbd.session.Option.ISOLATION, jdbdIsolation)
                    .option(io.jdbd.session.Option.READ_ONLY, option.isReadOnly());

            if (this.jdbdSqlLogger != null) {
                builder.option(io.jdbd.session.Option.SQL_LOGGER, this.jdbdSqlLogger);
            }

            jdbdTransactionOption = builder.build();
        }
        return jdbdTransactionOption;
    }


    @SuppressWarnings("all")
    private Optional<Xid> mapToOptionalArmyXid(Optional<io.jdbd.session.Xid> optional) {
        if (optional.isPresent()) {
            return Optional.of(mapToArmyXid(optional.get()));
        }
        return Optional.empty();
    }

    /**
     * @throws ArmyException throw when isolation is unknown.
     */
    private Isolation mapToArmyIsolation(final io.jdbd.session.Isolation isolation) throws ArmyException {
        final Isolation armyIsolation;
        if (isolation == io.jdbd.session.Isolation.READ_COMMITTED) {
            armyIsolation = Isolation.READ_COMMITTED;
        } else if (isolation == io.jdbd.session.Isolation.REPEATABLE_READ) {
            armyIsolation = Isolation.REPEATABLE_READ;
        } else if (isolation == io.jdbd.session.Isolation.SERIALIZABLE) {
            armyIsolation = Isolation.SERIALIZABLE;
        } else if (isolation == io.jdbd.session.Isolation.READ_UNCOMMITTED) {
            armyIsolation = Isolation.READ_UNCOMMITTED;
        } else {
            armyIsolation = mapToArmyDialectIsolation(isolation);
        }
        return armyIsolation;
    }

    /**
     * @throws ArmyException throw when isolation is unsupported by driver.
     * @see #mapToJdbdTransactionOption(TransactionOption)
     */
    @Nullable
    private io.jdbd.session.Isolation mapToJdbdIsolation(final @Nullable Isolation isolation) throws ArmyException {
        final io.jdbd.session.Isolation jdbdIsolation;
        if (isolation == null) {
            jdbdIsolation = null;
        } else if (isolation == Isolation.READ_COMMITTED) {
            jdbdIsolation = io.jdbd.session.Isolation.READ_COMMITTED;
        } else if (isolation == Isolation.REPEATABLE_READ) {
            jdbdIsolation = io.jdbd.session.Isolation.REPEATABLE_READ;
        } else if (isolation == Isolation.SERIALIZABLE) {
            jdbdIsolation = io.jdbd.session.Isolation.SERIALIZABLE;
        } else if (isolation == Isolation.READ_UNCOMMITTED) {
            jdbdIsolation = io.jdbd.session.Isolation.READ_UNCOMMITTED;
        } else {
            jdbdIsolation = mapToJdbdDialectIsolation(isolation);
        }
        return jdbdIsolation;
    }


    /**
     * @see #commit(Function)
     * @see #rollback(Function)
     */
    @SuppressWarnings("all")
    private Optional<TransactionInfo> mapToArmyOptionalTransactionInfo(Optional<io.jdbd.session.TransactionInfo> jdbdOptional) {
        if (jdbdOptional.isPresent()) {
            return Optional.of(mapToArmyTransactionInfo(jdbdOptional.get()));
        }
        return Optional.empty();
    }


    private ResultStates mapToArmyUpdateStates(final io.jdbd.result.ResultStates jdbdStates, Function<Option<?>, ?> optionFunc) {
        return new JdbdResultStates(jdbdStates, this.factory, optionFunc);
    }

    private ResultStates mapToArmyQueryStates(final io.jdbd.result.ResultStates jdbdStates, Function<Option<?>, ?> optionFunc) {

        return new JdbdResultStates(jdbdStates, this.factory, optionFunc);
    }


    private <R> Function<CurrentRow, R> returnIdQueryRowFunc(final GeneratedKeyStmt keyStmt) {


        final int indexBasedZero = keyStmt.idSelectionIndex();
        final MappingType type = keyStmt.idField().mappingType();
        final DataType dataType = type.map(this.factory.serverMeta);
        final MappingEnv env = this.factory.mappingEnv;

        final int[] rowIndexHolder = new int[]{0};
        return dataRow -> {
            final int rowIndex = rowIndexHolder[0]++;
            if (dataRow.rowNumber() != rowIndexHolder[0]) {
                throw jdbdRowNumberNotMatch(rowIndex, dataRow.rowNumber());
            }
            Object idValue;
            idValue = get(dataRow, indexBasedZero, type, dataType);

            if (idValue == null) {
                throw _Exceptions.idValueIsNull(rowIndex, keyStmt.idField());
            }
            idValue = type.afterGet(dataType, env, idValue);
            keyStmt.setGeneratedIdValue(rowIndex, idValue);

            throw new UnsupportedOperationException();
        };
    }


    /**
     * @see #insert(SimpleStmt, ReactiveStmtOption, Function)
     */
    private ResultStates handleInsertStates(final io.jdbd.result.ResultStates jdbdStates,
                                            final GeneratedKeyStmt stmt, final Function<Option<?>, ?> optionFunc) {
        final int rowSize = stmt.rowSize();

        if (jdbdStates.affectedRows() != rowSize) {
            if (!(rowSize == 1 && stmt.hasConflictClause()) || jdbdStates.affectedRows() != 2) {
                throw _Exceptions.insertedRowsAndGenerateIdNotMatch(rowSize, jdbdStates.affectedRows());
            }
        } else if (!jdbdStates.isSupportInsertId()) {
            String m = String.format("error ,%s don't support lastInsertId() method", jdbdStates.getClass().getName());
            throw new DataAccessException(m);
        }

        final PrimaryFieldMeta<?> idField = stmt.idField();
        final MappingType type = idField.mappingType();
        final DataType dataType = type.map(this.factory.serverMeta);
        final MappingEnv env = this.factory.mappingEnv;

        final int lastRowIndex = rowSize - 1;

        long lastInsertedId = jdbdStates.lastInsertedId();
        BigInteger bigId = null;
        if (lastInsertedId < 0 || (lastInsertedId + rowSize) < 0) {
            bigId = new BigInteger(Long.toUnsignedString(lastInsertedId));
        }

        Object idValue;
        for (int i = 0; i < rowSize; i++) {
            if (bigId == null) {
                idValue = lastInsertedId++;
            } else {
                idValue = bigId;
                if (i < lastRowIndex) {
                    bigId = bigId.add(BigInteger.ONE);
                }
            }

            idValue = type.afterGet(dataType, env, idValue);
            stmt.setGeneratedIdValue(i, idValue);
        }

        return mapToArmyUpdateStates(jdbdStates, optionFunc);
    }


    private BindStatement bindStatement(final SingleSqlStmt stmt, final ReactiveStmtOption option)
            throws TimeoutException, JdbdException {

        final BindStatement statement;
        statement = this.session.bindStatement(stmt.sqlText(), option.isPreferServerPrepare());

        if (stmt instanceof SimpleStmt) {
            bindParameters(statement, ((SimpleStmt) stmt).paramGroup());
        } else if (stmt instanceof BatchStmt) {
            final List<List<SQLParam>> groupList = ((BatchStmt) stmt).groupList();
            final int groupSize = groupList.size();
            for (int i = 0; i < groupSize; i++) {
                bindParameters(statement, groupList.get(i));
                statement.addBatch();
            }
        } else {
            throw _Exceptions.unexpectedStmt(stmt);
        }

        if (option.isSupportTimeout()) {
            statement.setTimeout(option.restMillSeconds());
        }

        final int fetchSize, frequency;
        fetchSize = option.fetchSize();
        if (fetchSize > 0) {
            statement.setFetchSize(fetchSize);
        }

        frequency = option.frequency();
        if (frequency > -1) {
            statement.setFrequency(frequency);
        }


        return statement;
    }


    private void bindParameters(final ParametrizedStatement statement, final List<SQLParam> paramList) {

        final ServerMeta serverMeta = this.factory.serverMeta;
        final MappingEnv mappingEnv = this.factory.mappingEnv;
        final boolean truncatedTimeType = this.factory.truncatedTimeType;

        final int paramSize = paramList.size();

        SQLParam sqlParam;
        Object value;
        MappingType type;
        TypeMeta typeMeta;
        DataType dataType;
        Iterator<?> rowParamIterator;
        List<?> rowParamList;
        for (int itemIndex = 0, paramIndex = 0, rowParamSize; itemIndex < paramSize; itemIndex++) {
            sqlParam = paramList.get(itemIndex);
            typeMeta = sqlParam.typeMeta();

            if (typeMeta instanceof MappingType) {
                type = (MappingType) typeMeta;
            } else {
                type = typeMeta.mappingType();
            }

            dataType = type.map(serverMeta);

            if (sqlParam instanceof SingleParam) {
                rowParamList = null;
                rowParamIterator = null;
                rowParamSize = 1;
            } else if ((rowParamList = ((MultiParam) sqlParam).valueList()) instanceof ArrayList) {
                rowParamSize = rowParamList.size();
                rowParamIterator = null;
            } else {
                rowParamSize = rowParamList.size();
                rowParamIterator = rowParamList.iterator();

            }

            for (int rowParamIndex = 0; rowParamIndex < rowParamSize; rowParamIndex++) {

                if (rowParamList == null) {
                    value = ((SingleParam) sqlParam).value();
                } else if (rowParamIterator == null) {
                    value = rowParamList.get(rowParamIndex);
                } else {
                    value = rowParamIterator.next();
                }

                if (value == null || value == void.class) {
                    // jdbd client-prepared support dialect type null ,for example postgre : null::text
                    //   void.class representing out parameter
                    bind(statement, paramIndex++, type, dataType, value);
                    continue;
                }

                value = type.beforeBind(dataType, mappingEnv, value);

                //TODO field codec
                if (truncatedTimeType && value instanceof Temporal && typeMeta instanceof FieldMeta) {
                    value = _TimeUtils.truncatedIfNeed(((FieldMeta<?>) typeMeta).scale(), (Temporal) value);
                }

                bind(statement, paramIndex++, type, dataType, value);

            } // inner for loop


        }// for loop
    }


    /*-------------------below package static methods -------------------*/


    /**
     * @param cause not {@link io.jdbd.result.ServerException}
     */
    static ArmyException wrapException(final Exception cause) {
        final ArmyException e;
        if (cause instanceof ArmyException) {
            e = (ArmyException) cause;
        } else if (!(cause instanceof JdbdException)) {
            e = _Exceptions.unknownError(cause);
        } else {  // TODO mapping server exception and option function
            final JdbdException je = (JdbdException) cause;
            e = new DriverException(cause, je.getSqlState(), je.getVendorCode());
        }
        return e;
    }


    static ArmyException unknownJdbdIsolation(io.jdbd.session.Isolation isolation) {
        return new ArmyException(String.format("unknown %s", isolation));
    }


    /*-------------------below private static methods -------------------*/

    private static DataAccessException jdbdRowNumberNotMatch(int rowIndex, long jdbdRowNumber) {
        String m = String.format("jdbd row index error,expected %s but %s", rowIndex, jdbdRowNumber);
        return new DataAccessException(m);
    }

    /*-------------------below static class -------------------*/


    private static final class JdbdCurrentRecord<R> extends ArmyStmtCurrentRecord {

        private final JdbdExecutor executor;

        private final SingleSqlStmt stmt;

        private final ReactiveStmtOption stmtOption;

        private final List<? extends Selection> selectionList;

        private final Function<? super CurrentRecord, R> function;

        private final Consumer<ResultStates> statesConsumer;


        private final Function<Option<?>, ?> sessionOptionFunc;

        private final DataType[] dataTypeArray;

        private final MappingType[] originalTypeArray;

        private CurrentRow jdbdRow;

        private JdbdStmtRecordMeta meta;

        private long rowCount;

        private JdbdCurrentRecord(JdbdExecutor executor, SingleSqlStmt stmt, ReactiveStmtOption stmtOption,
                                  Function<? super CurrentRecord, R> function,
                                  Function<Option<?>, ?> sessionOptionFunc) {
            this.executor = executor;
            this.stmt = stmt;
            this.stmtOption = stmtOption;
            this.selectionList = stmt.selectionList();

            this.function = function;
            this.statesConsumer = stmtOption.stateConsumer();
            this.sessionOptionFunc = sessionOptionFunc;
            this.dataTypeArray = new DataType[selectionList.size()];

            this.originalTypeArray = new MappingType[this.dataTypeArray.length];
        }

        @Override
        public ArmyResultRecordMeta getRecordMeta() {
            final JdbdStmtRecordMeta meta = this.meta;
            assert meta != null;
            return meta;
        }


        @Override
        public long rowNumber() {
            return this.rowCount;
        }

        @Nullable
        @Override
        public Object get(final int indexBasedZero) {
            final JdbdStmtRecordMeta meta = this.meta;
            assert meta != null;

            return readOneColumn(meta.checkIndex(indexBasedZero), null, null);
        }

        @Nullable
        @Override
        public Object get(final int indexBasedZero, final @Nullable MappingType type) {
            final JdbdStmtRecordMeta meta = this.meta;
            assert meta != null;
            if (type == null) {
                throw new NullPointerException();
            }
            return readOneColumn(meta.checkIndex(indexBasedZero), type, null);
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T get(final int indexBasedZero, final Class<T> columnClass) {

            final JdbdStmtRecordMeta meta = this.meta;
            assert meta != null;

            DataType dataType = null;

            MappingType type;
            type = this.originalTypeArray[meta.checkIndex(indexBasedZero)];
            if (!columnClass.isAssignableFrom(type.javaType())) {
                dataType = this.dataTypeArray[meta.checkIndex(indexBasedZero)];
                type = type.compatibleFor(dataType, columnClass);
            }

            return (T) readOneColumn(indexBasedZero, type, dataType);
        }

        @Override
        protected Object[] copyValueArray() {
            final Object[] valueArray = new Object[this.dataTypeArray.length];
            final int arrayLength = valueArray.length;
            for (int i = 0; i < arrayLength; i++) {
                valueArray[i] = readOneColumn(i, null, null);
            }
            return valueArray;
        }


        private R readOneRow(final CurrentRow jdbdRow) {
            if (this.meta == null) {
                if (jdbdRow.rowNumber() != 1L) {
                    throw driverError();
                }
                this.meta = createRecordMeta(jdbdRow.getRowMeta());
            }

            final R row;
            try {
                this.jdbdRow = jdbdRow;
                this.rowCount++;

                row = this.function.apply(this);
            } catch (Exception e) {
                throw _Exceptions.recordMapFuncInvokeError(this.function);
            } finally {
                this.jdbdRow = null; /// must clear
            }

            if (row instanceof CurrentRecord) {
                throw _Exceptions.recordMapFuncReturnError(this.function);
            }

            return row;

        }


        @Nullable
        private Object readOneColumn(final int indexBasedZero, @Nullable MappingType type, @Nullable DataType dataType) {
            if (dataType == null) {
                dataType = this.dataTypeArray[indexBasedZero];
            }

            if (type == null) {
                type = this.originalTypeArray[indexBasedZero];
            }

            final CurrentRow jdbdRow = this.jdbdRow;
            assert jdbdRow != null;


            try {
                Object value;
                value = this.executor.get(jdbdRow, indexBasedZero, type, dataType);

                if (value == null) {
                    return null;
                }

                // TODO 解密 ,脱敏

                value = type.afterGet(dataType, this.executor.factory.mappingEnv, value);
                if (value == MappingType.DOCUMENT_NULL_VALUE) {
                    if (!(type instanceof MappingType.SqlDocumentType)) {
                        throw afterGetMethodError(type, dataType, value);
                    }
                    value = null;
                }

                return value;
            } catch (Exception e) {
                final DataAccessException error;
                error = _Exceptions.columnGetError(indexBasedZero, getColumnLabel(indexBasedZero), e);
                throw this.executor.wrapExecutingError(error);
            }
        }

        private JdbdStmtRecordMeta createRecordMeta(final ResultRowMeta rowMeta) {

            this.rowCount = 0;   // reset

            final DataType[] dataTypeArray = this.dataTypeArray, metaDataTypeArray;

            final int columnLength = dataTypeArray.length;
            if (rowMeta.getColumnCount() != columnLength) {
                throw _Exceptions.columnCountAndSelectionCountNotMatch(rowMeta.getColumnCount(), columnLength);
            }
            final List<? extends Selection> selectionList = this.selectionList;

            final MappingType[] originalTypeArray = this.originalTypeArray;
            final JdbdExecutor executor = this.executor;
            TypeMeta typeMeta;
            for (int i = 0; i < columnLength; i++) {

                dataTypeArray[i] = executor.getDataType(rowMeta, i);

                if (originalTypeArray[i] != null) {
                    continue;
                }
                typeMeta = selectionList.get(i).typeMeta();
                if (!(typeMeta instanceof MappingType)) {
                    typeMeta = typeMeta.mappingType();
                }
                originalTypeArray[i] = (MappingType) typeMeta;
            }


            if (this.stmt instanceof SimpleStmt && this.stmtOption.fetchSize() > 0) {
                metaDataTypeArray = dataTypeArray;
            } else {
                metaDataTypeArray = new DataType[dataTypeArray.length];
                System.arraycopy(dataTypeArray, 0, metaDataTypeArray, 0, dataTypeArray.length);
            }
            return new JdbdStmtRecordMeta(rowMeta.resultNo(), metaDataTypeArray, selectionList, this.executor, rowMeta);
        }

        private void acceptStates(final @Nullable io.jdbd.result.ResultStates jdbdStates) {

            this.meta = null; // must clear meta

            if (jdbdStates == null) {
                throw driverError();
            }

            final Consumer<ResultStates> statesConsumer = this.statesConsumer;
            if (statesConsumer == null || statesConsumer == ResultStates.IGNORE_STATES) {
                return;
            }

            final ResultStates armyStates;
            armyStates = this.executor.mapToArmyQueryStates(jdbdStates, this.sessionOptionFunc);

            try {
                statesConsumer.accept(armyStates);
            } catch (Exception e) {
                throw _Exceptions.statesConsumerInvokeError(statesConsumer, e);
            }
        }


    } //CurrentRecordRowReader


    private static final class JdbdResultStates implements ResultStates {

        private final io.jdbd.result.ResultStates jdbdStates;

        private final JdbdStmtExecutorFactory executorFactory;

        private final Function<Option<?>, ?> sessionOptionFunc;

        private Warning warning;

        private Set<Option<?>> optionSet;


        private JdbdResultStates(io.jdbd.result.ResultStates jdbdStates,
                                 JdbdStmtExecutorFactory executorFactory, Function<Option<?>, ?> sessionOptionFunc) {
            this.jdbdStates = jdbdStates;
            this.executorFactory = executorFactory;
            this.sessionOptionFunc = sessionOptionFunc;
        }


        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(final Option<T> option) {

            Object value = null;
            if (this.sessionOptionFunc != Option.EMPTY_FUNC) {
                value = this.sessionOptionFunc.apply(option);
            }

            if (value == null) {
                final io.jdbd.session.Option<?> jdbdOption;
                jdbdOption = this.executorFactory.mapToJdbdOption(option);
                if (jdbdOption != null) {
                    value = this.jdbdStates.valueOf(jdbdOption);
                }
            }
            return (T) value;
        }


        @Override
        public int batchSize() {
            return this.jdbdStates.batchSize();
        }

        @Override
        public int batchNo() {
            return this.jdbdStates.batchNo();
        }

        @Override
        public int resultNo() {
            return this.jdbdStates.resultNo();
        }

        @Override
        public boolean isSupportInsertId() {
            return this.jdbdStates.isSupportInsertId();
        }

        @Override
        public long lastInsertedId() throws DataAccessException {
            try {
                return this.jdbdStates.lastInsertedId();
            } catch (JdbdException e) {
                throw new DataAccessException(e);
            }
        }

        @Override
        public boolean inTransaction() {
            try {
                return this.jdbdStates.inTransaction();
            } catch (Exception e) {
                throw wrapException(e);
            }
        }

        @Override
        public String message() {
            return this.jdbdStates.message();
        }

        @Override
        public boolean hasMoreResult() {
            return this.jdbdStates.hasMoreResult();
        }

        @Override
        public boolean isLastStates() {
            final int batchSize = batchSize();
            final boolean match;
            if (batchSize > 0) {
                match = batchNo() == batchSize && !hasMoreResult() && !hasMoreFetch();
            } else {
                match = !hasMoreResult() && !hasMoreFetch();
            }
            return match;
        }

        @Override
        public boolean hasMoreFetch() {
            return this.jdbdStates.hasMoreFetch();
        }

        @Override
        public Warning warning() {
            Warning w = this.warning;
            if (w != null) {
                return w;
            }
            final io.jdbd.result.Warning jdbdWarning;
            jdbdWarning = this.jdbdStates.warning();
            if (jdbdWarning != null) {
                this.warning = w = new ArmyWarning(jdbdWarning, this.executorFactory);
            }
            return w;
        }

        @Override
        public long affectedRows() {
            return this.jdbdStates.affectedRows();
        }

        @Override
        public boolean hasColumn() {
            return this.jdbdStates.hasColumn();
        }

        @Override
        public long rowCount() {
            return this.jdbdStates.rowCount();
        }


    }// ArmyResultStates


    private static final class ArmyWarning implements Warning {

        private final io.jdbd.result.Warning jdbdWarning;

        private final JdbdStmtExecutorFactory executorFactory;


        private ArmyWarning(io.jdbd.result.Warning jdbdWarning, JdbdStmtExecutorFactory executorFactory) {
            this.jdbdWarning = jdbdWarning;
            this.executorFactory = executorFactory;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(Option<T> option) {
            final io.jdbd.session.Option<?> jdbdOption;
            jdbdOption = this.executorFactory.mapToJdbdOption(option);
            final Object value;
            if (jdbdOption == null) {
                value = null;
            } else {
                value = this.jdbdWarning.valueOf(jdbdOption);
            }
            return (T) value;
        }


        @Override
        public String message() {
            return this.jdbdWarning.message();
        }

    } // ArmyWarning


}
