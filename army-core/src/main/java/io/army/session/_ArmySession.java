package io.army.session;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._MultiDml;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._Statement;
import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.session.record.ResultStates;
import io.army.stmt.Stmt;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>This class is all implementation of {@link Session}.
 * <p>This class is direct base class of following :
 * <ul>
 *     <li>{@code  io.army.sync.ArmySyncSession}</li>
 *     <li>{@code io.army.reactive.ArmyReactiveSession}</li>
 * </ul>
 *
 * @since 1.0
 */
public abstract class _ArmySession implements Session {

    protected static final String PSEUDO_SAVE_POINT = "ARMY_PSEUDO_SAVE_POINT";

    protected final _ArmySessionFactory factory;

    protected final String name;

    protected final boolean readonly;
    protected final boolean allowQueryInsert;

    private final Visible visible;

    protected _ArmySession(_ArmySessionFactory.ArmySessionBuilder<?, ?> builder) {

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
    public final Visible visible() {
        return this.visible;
    }

    @Override
    public final boolean isQueryInsertAllowed() {
        return this.allowQueryInsert;
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
    public final <T> TableMeta<T> tableMeta(Class<T> domainClass) {
        final TableMeta<T> table;
        table = this.factory.getTable(domainClass);
        if (table == null) {
            String m = String.format("Not found %s for %s.", TableMeta.class.getName(), domainClass.getName());
            throw new IllegalArgumentException(m);
        }
        return table;
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
                .append(",factory:")
                .append(this.factory)
                .append(']')
                .toString();
    }

    /*-------------------below protected template methods -------------------*/

    protected abstract Logger getLogger();

    @Nullable
    protected abstract TransactionInfo obtainTransactionInfo();


    protected abstract void rollbackOnlyOnError(ChildUpdateException cause);


    protected final Stmt parseDqlStatement(final DqlStatement statement, final StmtOption option) {

        final Stmt stmt;
        if (statement instanceof SelectStatement) {
            stmt = this.factory.dialectParser.select((SelectStatement) statement, option.isParseBatchAsMultiStmt(), this.visible);
        } else if (!(statement instanceof DmlStatement)) {
            stmt = this.factory.dialectParser.dialectDql(statement, this.visible);
        } else if (statement instanceof InsertStatement) {
            stmt = this.factory.dialectParser.insert((InsertStatement) statement, this.visible);
        } else if (statement instanceof _Statement._ChildStatement) {
            throw new ArmyException("current api don't support child dml statement.");
        } else if (statement instanceof UpdateStatement) {
            stmt = this.factory.dialectParser.update((UpdateStatement) statement, option.isParseBatchAsMultiStmt(), this.visible);
        } else if (statement instanceof DeleteStatement) {
            stmt = this.factory.dialectParser.delete((DeleteStatement) statement, option.isParseBatchAsMultiStmt(), this.visible);
        } else {
            stmt = this.factory.dialectParser.dialectDml((DmlStatement) statement, this.visible);
        }
        this.printSqlIfNeed(stmt);
        return stmt;
    }


    protected final Stmt parseInsertStatement(final InsertStatement statement) {
        final Stmt stmt;
        stmt = this.factory.dialectParser.insert(statement, this.visible);
        this.printSqlIfNeed(stmt);
        return stmt;
    }

    protected final Stmt parseDmlStatement(final DmlStatement statement, final StmtOption option) {

        final Stmt stmt;
        if (statement instanceof UpdateStatement) {
            stmt = this.factory.dialectParser.update((UpdateStatement) statement, option.isParseBatchAsMultiStmt(), this.visible);
        } else if (statement instanceof DeleteStatement) {
            stmt = this.factory.dialectParser.delete((DeleteStatement) statement, option.isParseBatchAsMultiStmt(), this.visible);
        } else {
            stmt = this.factory.dialectParser.dialectDml(statement, this.visible);
        }
        this.printSqlIfNeed(stmt);
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

    /*-------------------below private methods -------------------*/

    private void printSqlIfNeed(final Stmt stmt) {
        final SqlLogMode mode;
        final _ArmySessionFactory factory = this.factory;
        if (factory.sqlLogDynamic) {
            mode = factory.env.getOrDefault(ArmyKey.SQL_LOG_MODE);
        } else {
            mode = factory.sqlLogMode;
        }
        final boolean debug, beautify;
        switch (mode) {
            case OFF:
                return;
            case SIMPLE:
                beautify = debug = false;
                break;
            case DEBUG:
                debug = true;
                beautify = false;
                break;
            case BEAUTIFY:
                debug = false;
                beautify = true;
                break;
            case BEAUTIFY_DEBUG:
                beautify = debug = true;
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }

        final Logger logger;
        logger = this.getLogger();
        if ((debug && !logger.isDebugEnabled()) || (!debug && !logger.isInfoEnabled())) {
            return;
        }
        final StringBuilder builder = new StringBuilder(128);
        builder.append("session[name : ")
                .append(this.name)
                .append("]\n");
        factory.dialectParser.printStmt(stmt, beautify, builder::append);
        if (debug) {
            logger.debug(builder.toString());
        } else {
            logger.info(builder.toString());
        }

    }


    /*-------------------below static method -------------------*/

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

    protected static Function<Option<?>, ?> wrapStartMillisIfNeed(final @Nullable Xid xid, final TransactionOption option) {
        final Integer timeoutMillis;
        timeoutMillis = option.valueOf(Option.TIMEOUT_MILLIS);

        if (timeoutMillis == null || timeoutMillis < 1) {
            return option::valueOf;
        }

        final Object startTime;
        startTime = System.currentTimeMillis();
        return o -> {
            final Object value;
            if (o == Option.START_MILLIS) {
                value = startTime;
            } else if (o == Option.XID) {
                value = xid;
            } else {
                value = option.valueOf(o);
            }
            return value;
        };
    }

    protected static TransactionInfo wrapRollbackOnly(final TransactionInfo info) {
        if (Boolean.TRUE.equals(info.valueOf(Option.ROLLBACK_ONLY))) {
            return info;
        }
        final Function<Option<?>, ?> function;
        function = o -> {
            if (o == Option.ROLLBACK_ONLY) {
                return Boolean.TRUE;
            }
            return info.valueOf(o);
        };

        return TransactionInfo.info(info.inTransaction(), info.isolation(), info.isReadOnly(), function);
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

    protected static abstract class WrapStmtOption implements StmtOption {

        protected final StmtOption option;

        private final Consumer<ResultStates> statesConsumer;

        protected WrapStmtOption(StmtOption option, Consumer<ResultStates> statesConsumer) {
            this.option = option;
            final Consumer<ResultStates> source;
            source = option.stateConsumer();

            if (source == ResultStates.IGNORE_STATES) {
                this.statesConsumer = statesConsumer;
            } else {
                this.statesConsumer = statesConsumer.andThen(source);
            }

        }

        @Override
        public final boolean isPreferServerPrepare() {
            return this.option.isPreferServerPrepare();
        }

        @Override
        public final boolean isSupportTimeout() {
            return this.option.isSupportTimeout();
        }

        @Override
        public final boolean isParseBatchAsMultiStmt() {
            return this.option.isParseBatchAsMultiStmt();
        }

        @Override
        public final int restSeconds() throws TimeoutException {
            return this.option.restSeconds();
        }

        @Override
        public final int restMillSeconds() throws TimeoutException {
            return this.option.restMillSeconds();
        }

        @Override
        public final int fetchSize() {
            return this.option.fetchSize();
        }

        @Override
        public final MultiStmtMode multiStmtMode() {
            return this.option.multiStmtMode();
        }

        @Override
        public final Consumer<ResultStates> stateConsumer() {
            return this.statesConsumer;
        }


    } // WrapStmtOption


}
