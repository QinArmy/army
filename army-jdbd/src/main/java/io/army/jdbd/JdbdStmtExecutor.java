package io.army.jdbd;

import io.army.ArmyException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.SQLParam;
import io.army.criteria.Selection;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.reactive.ReactiveStmtOption;
import io.army.reactive.executor.ReactiveLocalExecutor;
import io.army.reactive.executor.ReactiveRmExecutor;
import io.army.reactive.executor.ReactiveStmtExecutor;
import io.army.session.*;
import io.army.session.executor.ExecutorSupport;
import io.army.session.executor.StmtExecutor;
import io.army.session.record.CurrentRecord;
import io.army.session.record.ResultItem;
import io.army.session.record.ResultStates;
import io.army.sqltype.ArmyType;
import io.army.sqltype.DataType;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.type.BlobPath;
import io.army.type.ImmutableSpec;
import io.army.type.TextPath;
import io.army.util._Collections;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
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
 * <p>This class is a abstract implementation of {@link ReactiveStmtExecutor} with jdbd spi.
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
abstract class JdbdStmtExecutor extends JdbdExecutorSupport
        implements ReactiveStmtExecutor,
        ReactiveStmtExecutor.LocalTransactionSpec,
        ReactiveStmtExecutor.XaTransactionSpec,
        Session.XaTransactionSupportSpec {


    final JdbdStmtExecutorFactory factory;

    final DatabaseSession session;

    final String name;

    JdbdStmtExecutor(JdbdStmtExecutorFactory factory, DatabaseSession session, String name) {
        this.name = name;
        this.factory = factory;
        this.session = session;
    }


    @Override
    public final long sessionIdentifier() throws DataAccessException {
        try {
            return this.session.sessionIdentifier();
        } catch (Exception e) {
            throw wrapExecutingError(e);
        }
    }

    @Override
    public final boolean inTransaction() throws DataAccessException {
        try {
            return this.session.inTransaction();
        } catch (JdbdException e) {
            throw wrapExecutingError(e);
        }
    }

    @Override
    public final boolean isSameFactory(StmtExecutor s) {
        return s instanceof JdbdStmtExecutor && ((JdbdStmtExecutor) s).factory == this.factory;
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
        return Mono.from(this.session.transactionInfo())
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
        return Mono.from(this.session.setSavePoint(this.factory.mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed);
    }

    @Override
    public final Mono<Void> releaseSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!(savepoint instanceof SavePoint)) {
            return Mono.error(_Exceptions.unknownSavePoint(savepoint));
        }
        return Mono.from(this.session.releaseSavePoint((SavePoint) savepoint, this.factory.mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed)
                .then();
    }


    @Override
    public final Mono<Void> rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!(savepoint instanceof SavePoint)) {
            return Mono.error(_Exceptions.unknownSavePoint(savepoint));
        }
        return Mono.from(this.session.rollbackToSavePoint((SavePoint) savepoint, this.factory.mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed)
                .then();
    }

    @Override
    public final Mono<ResultStates> insert(final SimpleStmt stmt, final ReactiveStmtOption option) {

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
                return Mono.just(mapToArmyResultStates(jdbdStatesHolder.get()));
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
                    .map(states -> handleInsertStates(states, (GeneratedKeyStmt) stmt));
        } else {
            mono = Mono.from(statement.executeUpdate())
                    .map(this::mapToArmyResultStates);
        }
        return mono.onErrorMap(this::wrapExecuteIfNeed);

    }


    @Override
    public final Mono<ResultStates> update(final SimpleStmt stmt, final ReactiveStmtOption option,
                                           Function<Option<?>, ?> optionFunc) {
        Mono<ResultStates> mono;
        try {
            final BindStatement statement;
            statement = bindStatement(stmt, option);

            mono = Mono.from(statement.executeUpdate())
                    .map(this::mapToArmyResultStates)
                    .onErrorMap(this::wrapExecuteIfNeed);
        } catch (Throwable e) {
            mono = Mono.error(wrapExecuteIfNeed(e));
        }
        return mono;
    }

    @Override
    public final Flux<ResultStates> batchUpdate(final BatchStmt stmt, final ReactiveStmtOption option) {
        Flux<ResultStates> flux;
        try {
            final BindStatement statement;
            statement = bindStatement(stmt, option);

            flux = Flux.from(statement.executeBatchUpdate())
                    .map(this::mapToArmyResultStates)
                    .onErrorMap(this::wrapExecuteIfNeed);
        } catch (Throwable e) {
            flux = Flux.error(wrapExecuteIfNeed(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> query(final SingleSqlStmt stmt, final Class<R> resultClass, final ReactiveStmtOption option) {
        Flux<R> flux;
        try {
            flux = executeQuery(stmt, mapBeanFunc(stmt, resultClass), option);
        } catch (Throwable e) {
            flux = Flux.error(wrapExecuteIfNeed(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<Optional<R>> queryOptional(SingleSqlStmt stmt, final Class<R> resultClass, ReactiveStmtOption option) {
        Flux<Optional<R>> flux;
        try {
            final List<? extends Selection> selectionList;
            selectionList = stmt.selectionList();
            if (selectionList.size() != 1) {
                return Flux.error(new IllegalArgumentException("queryOptional method support only single selection"));
            }

            final OptionalSingleColumnRowReader<R> rowReader;
            rowReader = new OptionalSingleColumnRowReader<>(this, selectionList, resultClass);

            final Function<CurrentRow, Optional<R>> function;
            if (stmt instanceof GeneratedKeyStmt) {
                function = returnIdQueryRowFunc((GeneratedKeyStmt) stmt, rowReader);
            } else {
                function = rowReader::readOneRow;
            }
            flux = executeQuery(stmt, function, option);
        } catch (Throwable e) {
            flux = Flux.error(wrapExecuteIfNeed(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> queryObject(SingleSqlStmt stmt, Supplier<R> constructor, ReactiveStmtOption option) {
        Flux<R> flux;
        try {
            flux = executeQuery(stmt, mapObjectFunc(stmt, constructor), option);
        } catch (Throwable e) {
            flux = Flux.error(wrapExecuteIfNeed(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> queryRecord(SingleSqlStmt stmt, Function<CurrentRecord, R> function, ReactiveStmtOption option) {
        Flux<R> flux;
        try {
            flux = executeQuery(stmt, mapRecordFunc(stmt, function), option);
        } catch (Throwable e) {
            flux = Flux.error(wrapExecuteIfNeed(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> secondQuery(TwoStmtQueryStmt stmt, ReactiveStmtOption option, List<R> resultList) {
        Flux<R> flux;
        try {
            final SecondRowReader<R> rowReader;
            rowReader = new SecondRowReader<>(this, stmt, resultList);

            flux = executeQuery(stmt, rowReader::readOneRow, option);
        } catch (Throwable e) {
            flux = Flux.error(wrapExecuteIfNeed(e));
        }
        return flux;
    }

    @Override
    public final <R> Flux<R> pairBatchQuery(PairBatchStmt stmt, Class<R> resultClass, ReactiveStmtOption option,
                                            ChildTableMeta<?> childTable) {
        return null;
    }

    @Override
    public final <R> Flux<R> pairBatchQueryObject(PairBatchStmt stmt, Supplier<R> constructor,
                                                  ReactiveStmtOption option, ChildTableMeta<?> childTable) {
        return null;
    }

    @Override
    public final <R> Flux<R> pairBatchQueryRecord(PairBatchStmt stmt, Function<CurrentRecord, R> function,
                                                  ReactiveStmtOption option, ChildTableMeta<?> childTable) {
        return null;
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
    public final Mono<Optional<TransactionInfo>> commit(Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveLocalExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }
        return Mono.from(((LocalDatabaseSession) this.session).commit(this.factory.mapToJdbdOptionFunc(optionFunc)))
                .map(this::mapToArmyOptionalTransactionInfo)
                .onErrorMap(this::wrapExecuteIfNeed);
    }


    @Override
    public final Mono<Optional<TransactionInfo>> rollback(Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveLocalExecutor)) {
            return Mono.error(new UnsupportedOperationException());
        }
        return Mono.from(((LocalDatabaseSession) this.session).rollback(this.factory.mapToJdbdOptionFunc(optionFunc)))
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
        return Mono.from(((RmDatabaseSession) this.session).end(jdbdXid, flags, this.factory.mapToJdbdOptionFunc(optionFunc)))
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
        return Mono.from(((RmDatabaseSession) this.session).prepare(jdbdXid, this.factory.mapToJdbdOptionFunc(optionFunc)))
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
        return Mono.from(((RmDatabaseSession) this.session).commit(jdbdXid, flags, this.factory.mapToJdbdOptionFunc(optionFunc)))
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
        return Mono.from(((RmDatabaseSession) this.session).rollback(jdbdXid, this.factory.mapToJdbdOptionFunc(optionFunc)))
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
        return Mono.from(((RmDatabaseSession) this.session).forget(jdbdXid, this.factory.mapToJdbdOptionFunc(optionFunc)))
                .onErrorMap(this::wrapExecuteIfNeed)
                .then();
    }

    @Override
    public final Flux<Optional<Xid>> recover(int flags, Function<Option<?>, ?> optionFunc) {
        if (!(this instanceof ReactiveRmExecutor)) {
            return Flux.error(new UnsupportedOperationException());
        }
        return Flux.from(((RmDatabaseSession) this.session).recover(flags, this.factory.mapToJdbdOptionFunc(optionFunc)))
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
        } else if (s instanceof JdbdStmtExecutor) {
            match = this.session.isSameFactory(((JdbdStmtExecutor) s).session);
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
        return _StringUtils.builder(48)
                .append(getClass().getName())
                .append("[name:")
                .append(this.name)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }

    /*-------------------below package instance methods-------------------*/

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
     * @see #bindParameter(ParametrizedStatement, List)
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
        } else if (nullable instanceof io.army.reactive.type.Blob) {
            final io.army.reactive.type.Blob blob = (io.army.reactive.type.Blob) nullable;
            value = io.jdbd.type.Blob.from(blob.value());
        } else if (nullable instanceof io.army.reactive.type.Clob) {
            final io.army.reactive.type.Clob clob = (io.army.reactive.type.Clob) nullable;
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
        } else if (nullable instanceof io.army.reactive.type.Clob) {
            final io.army.reactive.type.Clob clob = (io.army.reactive.type.Clob) nullable;
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
        } else if (nullable instanceof io.army.reactive.type.Blob) {
            final io.army.reactive.type.Blob blob = (io.army.reactive.type.Blob) nullable;
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
    final Object getLongBinary(final DataRow row, final int indexBasedZero) {
        final Object value;
        if (row.isNull(indexBasedZero)) {
            value = null;
        } else if (row.isBigColumn(indexBasedZero)) {
            final io.jdbd.type.BlobPath path = row.getNonNull(indexBasedZero, io.jdbd.type.BlobPath.class);
            value = BlobPath.from(path.isDeleteOnClose(), path.value());
        } else {
            value = row.get(indexBasedZero, byte[].class);
        }
        return value;
    }

    final ArmyException wrapExecutingError(final Exception cause) {
        return this.factory.wrapExecuteError(cause);
    }

    final Throwable wrapExecuteIfNeed(final Throwable cause) {
        if (!(cause instanceof Exception)) {
            return cause;
        }
        return wrapExecutingError((Exception) cause);
    }

    /*-------------------below private instance methods-------------------*/

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
    private TransactionInfo mapToArmyTransactionInfo(io.jdbd.session.TransactionInfo info) {
        return TransactionInfo.info(
                info.inTransaction(), mapToArmyIsolation(info.isolation()), info.isReadOnly(),
                this.factory.mapToArmyOptionFunc(info::valueOf)
        );
    }


    /**
     * @throws ArmyException throw when isolation is unsupported by driver.
     * @see #setTransactionCharacteristics(TransactionOption)
     * @see #startTransaction(TransactionOption, HandleMode)
     * @see #start(Xid, int, TransactionOption)
     */
    private io.jdbd.session.TransactionOption mapToJdbdTransactionOption(final TransactionOption option)
            throws ArmyException {
        return io.jdbd.session.TransactionOption.option(
                mapToJdbdIsolation(option.isolation()), option.isReadOnly(), this.factory.mapToJdbdOptionFunc(option::valueOf)
        );

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
            Optional.of(mapToArmyTransactionInfo(jdbdOptional.get()));
        }
        return Optional.empty();
    }


    private ResultStates mapToArmyResultStates(io.jdbd.result.ResultStates jdbdStates) {
        return new ArmyResultStates(jdbdStates, this.factory::mapToJdbdOption);
    }


    /**
     * @see #query(SingleSqlStmt, Class, ReactiveStmtOption)
     * @see #queryObject(SingleSqlStmt, Supplier, ReactiveStmtOption)
     * @see #queryRecord(SingleSqlStmt, Function, ReactiveStmtOption)
     */
    private <R> Flux<R> executeQuery(final SingleSqlStmt stmt, final Function<CurrentRow, R> func,
                                     final ReactiveStmtOption option) throws JdbdException, TimeoutException {
        return Flux.from(bindStatement(stmt, option).executeQuery(func, createStatesConsumer(option)))
                .onErrorMap(this::wrapExecuteIfNeed);
    }


    /**
     * @see #query(SingleSqlStmt, Class, ReactiveStmtOption)
     * @see #queryObject(SingleSqlStmt, Supplier, ReactiveStmtOption)
     * @see #queryRecord(SingleSqlStmt, Function, ReactiveStmtOption)
     */
    private <R> Function<CurrentRow, R> mapBeanFunc(final SingleSqlStmt stmt, final Class<R> resultClass) {
        final List<? extends Selection> selectionList;
        selectionList = stmt.selectionList();

        final RowReader<R> rowReader;
        if ((stmt instanceof TwoStmtQueryStmt && ((TwoStmtQueryStmt) stmt).maxColumnSize() == 1)
                || selectionList.size() == 1) {
            rowReader = new SingleColumnRowReader<>(this, selectionList, resultClass);
        } else {
            rowReader = new BeanReader<>(this, selectionList, resultClass);
        }

        final Function<CurrentRow, R> function;
        if (stmt instanceof GeneratedKeyStmt) {
            function = returnIdQueryRowFunc((GeneratedKeyStmt) stmt, rowReader);
        } else {
            function = rowReader::readOneRow;
        }
        return function;
    }

    /**
     * @see #query(SingleSqlStmt, Class, ReactiveStmtOption)
     * @see #queryObject(SingleSqlStmt, Supplier, ReactiveStmtOption)
     * @see #queryRecord(SingleSqlStmt, Function, ReactiveStmtOption)
     */
    private <R> Function<CurrentRow, R> mapObjectFunc(final SingleSqlStmt stmt, final Supplier<R> constructor) {

        final RowReader<R> rowReader;
        rowReader = new ObjectRowReader<>(this, stmt.selectionList(), constructor, stmt instanceof TwoStmtModeQuerySpec);

        final Function<CurrentRow, R> function;
        if (stmt instanceof GeneratedKeyStmt) {
            function = returnIdQueryRowFunc((GeneratedKeyStmt) stmt, rowReader);
        } else {
            function = rowReader::readOneRow;
        }
        return function;
    }

    /**
     * @see #query(SingleSqlStmt, Class, ReactiveStmtOption)
     * @see #queryObject(SingleSqlStmt, Supplier, ReactiveStmtOption)
     * @see #queryRecord(SingleSqlStmt, Function, ReactiveStmtOption)
     */
    private <R> Function<CurrentRow, R> mapRecordFunc(final SingleSqlStmt stmt, final Function<CurrentRecord, R> recordFunc) {
        final RowReader<R> rowReader;
        rowReader = new CurrentRecordRowReader<>(this, stmt.selectionList(), recordFunc);

        final Function<CurrentRow, R> function;
        if (stmt instanceof GeneratedKeyStmt) {
            function = returnIdQueryRowFunc((GeneratedKeyStmt) stmt, rowReader);
        } else {
            function = rowReader::readOneRow;
        }
        return function;
    }


    /**
     * @see #query(SingleSqlStmt, Class, ReactiveStmtOption)
     * @see #queryObject(SingleSqlStmt, Supplier, ReactiveStmtOption)
     * @see #queryRecord(SingleSqlStmt, Function, ReactiveStmtOption)
     */
    private Consumer<io.jdbd.result.ResultStates> createStatesConsumer(final ReactiveStmtOption option) {
        final Consumer<ResultStates> armyConsumer;
        armyConsumer = option.stateConsumer();
        if (armyConsumer == ResultStates.IGNORE_STATES) {
            return io.jdbd.result.ResultStates.IGNORE_STATES;
        }
        return states -> {
            final ResultStates armyStates;
            armyStates = mapToArmyResultStates(states);
            try {
                armyConsumer.accept(armyStates);
            } catch (Exception e) {
                String m = String.format("%s %s throw error, %s", ResultStates.class.getName(),
                        armyConsumer, e.getMessage());
                throw new ArmyException(m);
            }
        };
    }

    /**
     * @see #mapBeanFunc(SingleSqlStmt, Class)
     */
    private <R> Function<CurrentRow, R> returnIdQueryRowFunc(final GeneratedKeyStmt keyStmt,
                                                             final RowReader<R> rowReader) {


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

            return rowReader.readOneRow(dataRow);
        };
    }


    /**
     * @see #insert(SimpleStmt, ReactiveStmtOption)
     */
    private ResultStates handleInsertStates(final io.jdbd.result.ResultStates jdbdStates,
                                            final GeneratedKeyStmt stmt) {
        final int rowSize = stmt.rowSize();

        if (jdbdStates.affectedRows() != rowSize) {
            throw _Exceptions.insertedRowsAndGenerateIdNotMatch(rowSize, jdbdStates.affectedRows());
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

        return mapToArmyResultStates(jdbdStates);
    }


    private BindStatement bindStatement(final SingleSqlStmt stmt, final ReactiveStmtOption option)
            throws TimeoutException, JdbdException {

        final BindStatement statement;
        statement = this.session.bindStatement(stmt.sqlText(), option.isPreferServerPrepare());

        if (stmt instanceof SimpleStmt) {
            bindParameter(statement, ((SimpleStmt) stmt).paramGroup());
        } else if (stmt instanceof BatchStmt) {
            final List<List<SQLParam>> groupList = ((BatchStmt) stmt).groupList();
            final int groupSize = groupList.size();
            for (int i = 0; i < groupSize; i++) {
                bindParameter(statement, groupList.get(i));
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


    private void bindParameter(final ParametrizedStatement statement, final List<SQLParam> paramList) {

        final ServerMeta serverMeta = this.factory.serverMeta;
        final MappingEnv mappingEnv = this.factory.mappingEnv;
        final boolean truncatedTimeType = this.factory.truncatedTimeType;

        final int paramSize = paramList.size();

        SQLParam sqlParam;
        Object value;
        MappingType type;
        TypeMeta typeMeta;
        DataType dataType;
        Iterator<?> iterator;
        List<?> list;
        boolean hasMore;
        for (int itemIndex = 0, paramIndex = 0, columnItemSize = 0; itemIndex < paramSize; itemIndex++) {
            sqlParam = paramList.get(itemIndex);
            typeMeta = sqlParam.typeMeta();

            if (typeMeta instanceof MappingType) {
                type = (MappingType) typeMeta;
            } else {
                type = typeMeta.mappingType();
            }

            dataType = type.map(serverMeta);

            if (sqlParam instanceof SingleParam) {
                list = null;
                iterator = null;
            } else if ((list = ((MultiParam) sqlParam).valueList()) instanceof ArrayList) {
                columnItemSize = list.size();
                iterator = null;
            } else {
                iterator = list.iterator();

            }

            hasMore = true;
            for (int columnItemIndex = 0; hasMore; columnItemIndex++) {

                if (list == null) {
                    value = ((SingleParam) sqlParam).value();
                    hasMore = false;
                } else if (iterator == null) {
                    if (columnItemIndex < columnItemSize) {
                        value = list.get(columnItemIndex);
                    } else {
                        break;
                    }
                } else if (iterator.hasNext()) {
                    value = iterator.next();
                } else {
                    break;
                }

                if (value == null) { // jdbd client-prepared support dialect type null ,for example postgre : null::text
                    bind(statement, paramIndex++, type, dataType, null);
                    continue;
                }

                value = type.beforeBind(dataType, mappingEnv, value);

                //TODO field codec
                if (truncatedTimeType && value instanceof Temporal && typeMeta instanceof FieldMeta) {
                    value = _TimeUtils.truncatedIfNeed(((FieldMeta<?>) typeMeta).scale(), (Temporal) value);
                }

                bind(statement, paramIndex++, type, dataType, value);

            } // while loop


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
        } else {
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


    private static abstract class RowReader<R> extends ArmyStmtCurrentRecord {

        final JdbdStmtExecutor executor;

        final List<? extends Selection> selectionList;

        final DataType[] dataTypeArray;

        private final MappingType[] compatibleTypeArray;

        private final Class<?> resultClass;

        private RowReader(JdbdStmtExecutor executor, List<? extends Selection> selectionList,
                          @Nullable Class<?> resultClass) {
            this.executor = executor;
            this.selectionList = selectionList;
            this.dataTypeArray = new SqlType[selectionList.size()];
            this.compatibleTypeArray = new MappingType[this.dataTypeArray.length];

            this.resultClass = resultClass;
        }

        @Override
        public ArmyResultRecordMeta getRecordMeta() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Object[] copyValueArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long rowNumber() {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public Object get(int indexBasedZero) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        final R readOneRow(final DataRow dataRow) {

            final JdbdStmtExecutor executor = this.executor;
            final MappingEnv env = executor.factory.mappingEnv;
            final DataType[] dataTypeArray = this.dataTypeArray;
            final List<? extends Selection> selectionList = this.selectionList;

            final MappingType[] compatibleTypeArray = this.compatibleTypeArray;

            final int columnCount;
            if (dataTypeArray[0] == null
                    || (this instanceof CurrentRecordRowReader
                    && getResultNo() < dataRow.getResultNo())) {
                columnCount = dataRow.getColumnCount();
                if (columnCount != dataTypeArray.length) {
                    throw _Exceptions.columnCountAndSelectionCountNotMatch(columnCount, dataTypeArray.length);
                }
                final ResultRowMeta meta = dataRow.getRowMeta();
                for (int i = 0; i < columnCount; i++) {
                    dataTypeArray[i] = executor.getDataType(meta, i);
                }
                if (this instanceof CurrentRecordRowReader) {
                    ((CurrentRecordRowReader<?>) this).acceptRowMeta(dataRow.getRowMeta(), dataTypeArray);
                }
            } else {
                columnCount = dataTypeArray.length;
            }

            final ObjectAccessor accessor;
            accessor = createRow();

            TypeMeta typeMeta;
            MappingType type;
            Selection selection;
            Object columnValue;
            DataType dataType;
            String fieldName;

            for (int i = 0; i < columnCount; i++) {

                selection = selectionList.get(i);
                fieldName = selection.label();

                dataType = dataTypeArray[i];

                if ((type = compatibleTypeArray[i]) == null) {
                    if (!(this instanceof CurrentRecordRowReader)) {
                        type = compatibleTypeFrom(selection, dataType, this.resultClass, accessor, fieldName);
                    } else if ((typeMeta = selection.typeMeta()) instanceof MappingType) {
                        type = (MappingType) typeMeta;
                    } else {
                        type = typeMeta.mappingType();
                    }
                    compatibleTypeArray[i] = type;
                }


                columnValue = executor.get(dataRow, i, type, dataType);

                if (columnValue == null) {
                    acceptColumn(i, fieldName, null);
                    continue;
                }

                columnValue = type.afterGet(dataType, env, columnValue);
                //TODO field codec
                acceptColumn(i, fieldName, columnValue);

            } // for loop

            return endOneRow();
        }


        abstract ObjectAccessor createRow();

        abstract void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value);

        @Nullable
        abstract R endOneRow();


        private DataType getDataType(int index) {
            return this.dataTypeArray[index];
        }


    }// RowReader

    private static final class SingleColumnRowReader<R> extends RowReader<R> {

        private R row;

        private SingleColumnRowReader(JdbdStmtExecutor executor, List<? extends Selection> selectionList,
                                      Class<R> resultClass) {
            super(executor, selectionList, resultClass);
        }

        @Override
        ObjectAccessor createRow() {
            this.row = null;
            return ExecutorSupport.SINGLE_COLUMN_PSEUDO_ACCESSOR;
        }

        @SuppressWarnings("unchecked")
        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            assert indexBasedZero == 0;
            this.row = (R) value;
        }

        @Override
        R endOneRow() {
            final R row = this.row;
            this.row = null;
            return row;
        }

    }// SingleColumnRowReader

    private static final class OptionalSingleColumnRowReader<R> extends RowReader<Optional<R>> {

        private R row;

        private OptionalSingleColumnRowReader(JdbdStmtExecutor executor, List<? extends Selection> selectionList,
                                              Class<R> resultClass) {
            super(executor, selectionList, resultClass);
        }

        @Override
        ObjectAccessor createRow() {
            this.row = null;
            return ExecutorSupport.SINGLE_COLUMN_PSEUDO_ACCESSOR;
        }

        @SuppressWarnings("unchecked")
        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            assert indexBasedZero == 0;
            this.row = (R) value;
        }

        @Override
        Optional<R> endOneRow() {
            final R row = this.row;
            this.row = null;
            return Optional.ofNullable(row);
        }


    }// OptionalSingleColumnRowReader

    private static final class BeanReader<R> extends RowReader<R> {

        private final ObjectAccessor accessor;
        private final Constructor<R> constructor;

        private R row;

        private BeanReader(JdbdStmtExecutor executor, List<? extends Selection> selectionList,
                           Class<R> resultClass) {
            super(executor, selectionList, resultClass);
            this.accessor = ObjectAccessorFactory.forBean(resultClass);
            this.constructor = ObjectAccessorFactory.getConstructor(resultClass);
        }

        @Override
        ObjectAccessor createRow() {
            this.row = ObjectAccessorFactory.createBean(this.constructor);
            return this.accessor;
        }

        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            this.accessor.set(this.row, fieldName, value);
        }

        @Override
        R endOneRow() {
            final R row = this.row;
            this.row = null;
            return row;
        }

    }// BeanReader


    private static final class ObjectRowReader<R> extends RowReader<R> {

        private final Supplier<R> constructor;

        private final boolean twoStmtMode;

        private R row;

        private Class<?> rowJavaClass;

        private ObjectAccessor accessor;

        private ObjectRowReader(JdbdStmtExecutor executor, List<? extends Selection> selectionList,
                                Supplier<R> constructor, boolean twoStmtMode) {
            super(executor, selectionList, null);
            this.constructor = constructor;
            this.twoStmtMode = twoStmtMode;
        }

        @Override
        ObjectAccessor createRow() {
            assert this.row == null;

            final R row;
            this.row = row = this.constructor.get();
            if (row == null) {
                throw _Exceptions.objectConstructorError();
            }

            Class<?> rowJavaClass = this.rowJavaClass;
            final ObjectAccessor accessor;
            if (rowJavaClass == null || rowJavaClass != row.getClass()) {
                this.rowJavaClass = row.getClass();
                this.accessor = accessor = ObjectAccessorFactory.fromInstance(row);
            } else {
                accessor = this.accessor;
                assert accessor != null;
            }
            return accessor;
        }

        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            this.accessor.set(this.row, fieldName, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        R endOneRow() {
            R row = this.row;
            assert row != null;
            this.row = null;

            if (row instanceof Map && row instanceof ImmutableSpec && !this.twoStmtMode) {
                row = (R) _Collections.unmodifiableMapForDeveloper((Map<?, ?>) row);
            }
            return row;
        }


    }// ObjectReader

    private static final class SecondRowReader<R> extends RowReader<R> {

        private final List<R> rowList;

        private final int rowSize;

        private final boolean singleColumn;

        private ObjectAccessor accessor;

        private R row;

        private Class<?> rowJavaClass;

        /**
         * from -1 not 0
         */
        private int rowIndex = -1;


        private SecondRowReader(JdbdStmtExecutor executor, TwoStmtQueryStmt stmt, List<R> rowList) {
            super(executor, stmt.selectionList(), rowResultClass(rowList.get(0)));
            this.rowList = rowList;
            this.rowSize = rowList.size();
            if (stmt.maxColumnSize() == 1) {
                this.singleColumn = true;
                this.accessor = ExecutorSupport.SINGLE_COLUMN_PSEUDO_ACCESSOR;
            } else {
                this.singleColumn = false;
            }

        }

        @Override
        ObjectAccessor createRow() {
            final int rowIndex = ++this.rowIndex;
            if (rowIndex >= this.rowSize) {
                throw secondQueryRowCountNotMatch(this.rowSize, rowIndex + 1);
            }

            final R row;
            this.row = row = this.rowList.get(rowIndex);


            final ObjectAccessor accessor;
            if (this.singleColumn) {
                accessor = this.accessor;
                assert accessor != null;
            } else if (this.rowJavaClass != row.getClass()) {
                this.rowJavaClass = row.getClass();
                this.accessor = accessor = ObjectAccessorFactory.fromInstance(row);
            } else {
                accessor = this.accessor;
                assert accessor != null;
            }
            return accessor;
        }

        @Override
        void acceptColumn(final int indexBasedZero, String fieldName, @Nullable Object value) {
            if (!this.singleColumn) {
                this.accessor.set(this.row, fieldName, value);
            } else if (Objects.equals(value, this.row)) {
                assert indexBasedZero == 0;
            } else {
                String m = String.format("error , single column row[rowIndexBasedZero : %s ,indexBasedZero : %s , selection label : %s] and first query not match.",
                        this.rowIndex, indexBasedZero, fieldName);
                throw new DataAccessException(m);
            }

        }

        @SuppressWarnings("unchecked")
        @Override
        R endOneRow() {
            R row = this.row;
            if (row instanceof Map && row instanceof ImmutableSpec) {
                row = (R) _Collections.unmodifiableMapForDeveloper((Map<?, ?>) row);
            }

            this.row = null;
            return row;
        }

    }// SecondRowReader

    private static final class CurrentRecordRowReader<R> extends RowReader<R> {

        private final Function<CurrentRecord, R> function;

        private final Object[] valueArray;

        private JdbdStmtRowMeta meta;

        private int columnIndex;

        private long rowCount;

        private CurrentRecordRowReader(JdbdStmtExecutor executor, List<? extends Selection> selectionList,
                                       Function<CurrentRecord, R> function) {
            super(executor, selectionList, null);
            this.function = function;
            this.valueArray = new Object[selectionList.size()];
        }

        @Override
        public ArmyResultRecordMeta getRecordMeta() {
            final JdbdStmtRowMeta meta = this.meta;
            assert meta != null;
            return meta;
        }

        @Override
        protected Object[] copyValueArray() {
            final Object[] array = new Object[this.valueArray.length];
            System.arraycopy(this.valueArray, 0, array, 0, array.length);
            return array;
        }

        @Override
        public long rowNumber() {
            return this.rowCount;
        }

        @Nullable
        @Override
        public Object get(int indexBasedZero) {
            final JdbdStmtRowMeta meta = this.meta;
            assert meta != null;
            return this.valueArray[meta.checkIndex(indexBasedZero)];
        }

        @Override
        ObjectAccessor createRow() {
            assert this.columnIndex == this.valueArray.length;
            this.columnIndex = 0;
            return ExecutorSupport.SINGLE_COLUMN_PSEUDO_ACCESSOR;
        }

        @Override
        void acceptColumn(final int indexBasedZero, String fieldName, @Nullable Object value) {
            final int currentIndex = this.columnIndex++;
            assert indexBasedZero == currentIndex;
            this.valueArray[indexBasedZero] = value;
        }

        @Override
        R endOneRow() {
            assert this.columnIndex == this.valueArray.length;
            this.rowCount++;  // firstly ,++

            final R row;
            row = this.function.apply(this); // secondly , invoke user function
            if (row instanceof CurrentRecord) {
                throw _Exceptions.recordMapFuncReturnError(this.function);
            }
            return row;
        }

        private void acceptRowMeta(final ResultRowMeta rowMeta, final DataType[] dataTypeArray) {
            final JdbdStmtRowMeta meta = this.meta;
            final int resultNo;
            if (meta == null) {
                resultNo = 1;
            } else {
                resultNo = meta.getResultNo() + 1;
            }

            if (resultNo != rowMeta.getResultNo()) {
                throw driverError();
            }

            this.meta = new JdbdStmtRowMeta(resultNo, dataTypeArray, this.selectionList, this.executor, rowMeta);
            this.rowCount = 0L; // reset
        }


    }//CurrentRecordRowReader


    private static abstract class JdbdBatchQueryResults extends ArmyReactiveMultiResultSpec {

        private final JdbdStmtExecutor executor;

        private final List<? extends Selection> selectionList;

        private final io.jdbd.result.QueryResults jdbdResults;


        private JdbdBatchQueryResults(JdbdStmtExecutor executor, List<? extends Selection> selectionList,
                                      io.jdbd.result.QueryResults jdbdResults) {
            this.executor = executor;
            this.selectionList = selectionList;
            this.jdbdResults = jdbdResults;
        }

        @Override
        public final <R> Flux<R> nextQuery(Class<R> resultClass, Consumer<ResultStates> consumer) {
            final RowReader<R> reader;
            final List<? extends Selection> selectionList = this.selectionList;
            if (selectionList.size() == 1) {
                reader = new SingleColumnRowReader<>(this.executor, selectionList, resultClass);
            } else {
                reader = new BeanReader<>(this.executor, selectionList, resultClass);
            }
            return Flux.from(this.jdbdResults.nextQuery(reader::readOneRow, getJdbdStatesConsumer(consumer)));
        }

        @Override
        public final <R> Flux<Optional<R>> nextQueryOptional(Class<R> resultClass, Consumer<ResultStates> consumer) {
            return Flux.empty();
        }

        @Override
        public final <R> Flux<R> nextQueryObject(Supplier<R> constructor, Consumer<ResultStates> consumer) {
            final RowReader<R> reader;
            reader = new ObjectRowReader<>(this.executor, this.selectionList, constructor, false);
            return Flux.from(this.jdbdResults.nextQuery(reader::readOneRow, getJdbdStatesConsumer(consumer)));
        }

        @Override
        public final <R> Flux<R> nextQueryRecord(Function<CurrentRecord, R> function, Consumer<ResultStates> consumer) {
            //TODO
            return Flux.empty();
        }

        @Override
        public final Flux<ResultItem> nextQueryAsFlux() {
            return Flux.empty();
        }

        Consumer<io.jdbd.result.ResultStates> getJdbdStatesConsumer(Consumer<ResultStates> armyConsumer) {
            throw new UnsupportedOperationException();
        }


    }// JdbdMultiResultSpec

    private static final class ArmyResultStates implements ResultStates {

        private final io.jdbd.result.ResultStates jdbdStates;

        private final Function<Option<?>, io.jdbd.session.Option<?>> optionFunc;

        private Warning warning;


        private ArmyResultStates(io.jdbd.result.ResultStates jdbdStates,
                                 Function<Option<?>, io.jdbd.session.Option<?>> optionFunc) {
            this.jdbdStates = jdbdStates;
            this.optionFunc = optionFunc;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(Option<T> option) {
            final io.jdbd.session.Option<?> jdbdOption;
            jdbdOption = this.optionFunc.apply(option);
            final Object value;
            if (jdbdOption == null) {
                value = null;
            } else {
                value = this.jdbdStates.valueOf(jdbdOption);
            }
            return (T) value;
        }

        @Override
        public int getResultNo() {
            return this.jdbdStates.getResultNo();
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
                this.warning = w = new ArmyWarning(jdbdWarning, this.optionFunc);
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


        private final Function<Option<?>, io.jdbd.session.Option<?>> optionFunc;

        private ArmyWarning(io.jdbd.result.Warning jdbdWarning,
                            Function<Option<?>, io.jdbd.session.Option<?>> optionFunc) {
            this.jdbdWarning = jdbdWarning;
            this.optionFunc = optionFunc;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(Option<T> option) {
            final io.jdbd.session.Option<?> jdbdOption;
            jdbdOption = this.optionFunc.apply(option);
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

    }// ArmyWarning


}
