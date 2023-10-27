package io.army.session;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._MultiDml;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._Statement;
import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import org.slf4j.Logger;

public abstract class _ArmySession implements Session {

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
    public final Visible visible() {
        return this.visible;
    }

    @Override
    public final boolean isAllowQueryInsert() {
        return this.allowQueryInsert;
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
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this;
    }


    @Override
    public final String toString() {
        return String.format("%s[name:%s,factory:%s,hash:%s,readonly:%s,transaction:%s,visible:%s,allow query insert:%s]",
                this.getClass().getName(),
                this.name,
                this.sessionFactory().name(),
                System.identityHashCode(this),
                this.readonly,
                this.transactionName(),
                this.visible,
                this.allowQueryInsert
        );
    }


    @Nullable
    protected abstract String transactionName();

    protected abstract Logger getLogger();


    protected final void printSqlIfNeed(final Stmt stmt) {
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

    protected final int restSeconds(final ChildTableMeta<?> domainTable, final long startTime, final int timeout) {
        final int restSeconds;
        final long restMills;
        if (timeout == 0) {
            restSeconds = 0;
        } else if ((restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime)) < 1L) {
            String m;
            m = String.format("session[%s]\n %s first statement completion,but timeout,so no time insert child or update parent.",
                    this.name, domainTable);
            throw new ChildUpdateException(m, _Exceptions.timeout(timeout, restMills));
        } else if ((restMills % 1000L) == 0) {
            restSeconds = (int) (restMills / 1000L);
        } else {
            restSeconds = (int) (restMills / 1000L) + 1;
        }
        return restSeconds;
    }

    protected final Stmt parseDqlStatement(final DqlStatement statement, final StmtOption option) {
        final boolean useMultiStmt;
        useMultiStmt = isUseStaticMultiStmt(option);

        final Stmt stmt;
        if (statement instanceof SelectStatement) {
            stmt = this.factory.dialectParser.select((SelectStatement) statement, useMultiStmt, this.visible);
        } else if (!(statement instanceof DmlStatement)) {
            stmt = this.factory.dialectParser.dialectDql(statement, this.visible);
        } else if (statement instanceof InsertStatement) {
            stmt = this.factory.dialectParser.insert((InsertStatement) statement, this.visible);
        } else if (statement instanceof _Statement._ChildStatement) {
            throw new ArmyException("current api don't support child dml statement.");
        } else if (statement instanceof UpdateStatement) {
            stmt = this.factory.dialectParser.update((UpdateStatement) statement, useMultiStmt, this.visible);
        } else if (statement instanceof DeleteStatement) {
            stmt = this.factory.dialectParser.delete((DeleteStatement) statement, useMultiStmt, this.visible);
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
        final boolean useMultiStmt;
        useMultiStmt = isUseStaticMultiStmt(option);

        final Stmt stmt;
        if (statement instanceof UpdateStatement) {
            stmt = this.factory.dialectParser.update((UpdateStatement) statement, useMultiStmt, this.visible);
        } else if (statement instanceof DeleteStatement) {
            stmt = this.factory.dialectParser.delete((DeleteStatement) statement, useMultiStmt, this.visible);
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
            } else if (!hasTransaction() && statement instanceof _Statement._ChildStatement) {
                final TableMeta<?> domainTable;
                domainTable = ((_Statement._ChildStatement) statement).table();
                throw _Exceptions.childDmlNoTransaction(this, (ChildTableMeta<?>) domainTable);
            } else if (statement instanceof _Insert._QueryInsert && !this.allowQueryInsert) {
                throw _Exceptions.dontSupportSubQueryInsert(this);
            }
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

    static boolean isUseStaticMultiStmt(StmtOption option) {
        final boolean use;
        switch (option.multiStmtMode()) {
            case EXECUTOR:
                use = false;
                break;
            case DEFAULT:
            case ARMY:
            default:
                use = true;
        }
        return use;
    }


    protected static ChildDmlNoTractionException updateChildNoTransaction() {
        return new ChildDmlNoTractionException("update/delete child must in transaction.");
    }

    protected static SessionException streamApiDontSupportTowStatement() {
        return new SessionException("stream api don't support two statement.");
    }

    protected static SessionException wrapSessionError(final Exception cause) {
        if (cause instanceof SessionException) {
            throw (SessionException) cause;
        }
        return new SessionException("unknown session error," + cause.getMessage(), cause);
    }

    protected static Throwable wrapIfNeed(final Throwable cause) {
        return _Exceptions.wrapIfNeed(cause);
    }


}
