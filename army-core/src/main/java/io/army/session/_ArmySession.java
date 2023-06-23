package io.army.session;

import io.army.criteria.BatchDmlStatement;
import io.army.criteria.Visible;
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


    protected final String name;

    protected final boolean readonly;

    protected final Visible visible;

    protected final boolean allowQueryInsert;

    private _ArmySessionFactory armyFactory;

    protected _ArmySession(_ArmySessionFactory.ArmySessionBuilder<?, ?> builder) {

        this.name = builder.name;
        this.readonly = builder.readonly;
        this.visible = builder.visible;
        this.allowQueryInsert = builder.allowQueryInsert;

        assert _StringUtils.hasText(this.name);
        assert this.visible != null;
        this.armyFactory = builder.armyFactory;
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
        final _ArmySessionFactory factory = this.armyFactory;
        if (factory.sqlLogDynamic) {
            mode = factory.env.getOrDefault(ArmyKey.SQL_LOG_MODE);
        } else {
            mode = factory.sqlLogMode;
        }

        switch (mode) {
            case OFF:
                break;
            case SIMPLE:
                this.getLogger().info(factory.dialectParser().printStmt(stmt, false));
                break;
            case DEBUG:
                this.getLogger().debug(factory.dialectParser().printStmt(stmt, false));
                break;
            case FORMAT:
                this.getLogger().info(factory.dialectParser().printStmt(stmt, true));
                break;
            case FORMAT_DEBUG:
                this.getLogger().debug(factory.dialectParser().printStmt(stmt, true));
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }

    }

    protected static int restSecond(final ChildTableMeta<?> domainTable, final long startTime, final int timeout) {
        final int restSeconds;
        final long restMills;
        if (timeout == 0) {
            restSeconds = 0;
        } else if ((restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime)) < 1L) {
            String m;
            m = String.format("%s first statement completion,but timeout,so no time insert child or update parent.",
                    domainTable);
            throw new ChildUpdateException(m, _Exceptions.timeout(timeout, restMills));
        } else if ((restMills % 1000L) == 0) {
            restSeconds = (int) (restMills / 1000L);
        } else {
            restSeconds = (int) (restMills / 1000L) + 1;
        }
        return restSeconds;
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
        return new ChildDmlNoTractionException("update/delete child must in transaction.");
    }

    protected static SessionException streamApiDontSupportTowStatement() {
        return new SessionException("stream api don't support two statement.");
    }


}
