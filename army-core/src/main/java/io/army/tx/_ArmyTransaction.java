package io.army.tx;

import io.army.dialect._Constant;
import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;
import io.army.lang.Nullable;
import io.army.session._ArmySessionFactory;
import io.army.util._Exceptions;
import org.slf4j.Logger;

import java.util.List;

public abstract class _ArmyTransaction implements Transaction {

    protected final Isolation isolation;

    protected final boolean readonly;

    protected final String name;

    protected final long timeoutMills;

    protected final long startMills;

    protected _ArmyTransaction(final TransactionOptions options) {
        this.readonly = options.readonly;
        this.isolation = options.isolation;
        assert this.isolation != null;

        final String name = options.name;
        this.name = name == null ? "unnamed" : name;

        final int timeout = options.timeout;
        if (timeout > 0) {
            this.timeoutMills = timeout * 1000L;
            this.startMills = System.currentTimeMillis();
        } else {
            this.timeoutMills = -1;
            this.startMills = -1L;
        }
    }

    @Nullable
    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final Isolation isolation() {
        return this.isolation;
    }

    @Override
    public final boolean readOnly() {
        return this.readonly;
    }

    @Override
    public final int nextTimeout() throws TransactionTimeOutException {
        final long timeoutMills = this.timeoutMills;
        if (timeoutMills < 1000L) {
            return 0;
        }
        final long restMills;
        restMills = timeoutMills - (System.currentTimeMillis() - this.startMills);
        if (restMills < 0L) {
            throw _Exceptions.timeout((int) (timeoutMills / 1000L), restMills);
        }
        final int timeout;
        if (restMills % 1000L == 0L) {
            timeout = (int) (restMills / 1000L);
        } else {
            timeout = ((int) (restMills / 1000L)) + 1;
        }
        return timeout;
    }


    @Override
    public final String toString() {
        return String.format("%s[name:%s,hash:%s,session:%s,status:%s,isolation:%s,readonly:%s,timeout:%s s,cost:%s ms]",
                this.getClass().getName(),
                this.name,
                System.identityHashCode(this),
                this.session().name(),
                this.status(),
                this.isolation,
                this.readonly,
                this.timeoutMills / 1000L,
                System.currentTimeMillis() - this.startMills);
    }

    protected final void printStmtIfNeed(final _ArmySessionFactory factory, final Logger logger, final String stmt) {
        final Boolean debugLevel;
        debugLevel = logDebugLevel(factory);
        if (debugLevel == null) {
            return;
        }
        if (debugLevel) {
            logger.debug("transaction[name : {}] \n{}", this.name, stmt);
        } else {
            logger.info("transaction[name : {}] \n{}", this.name, stmt);
        }
    }


    protected final void printStmtListIfNeed(final _ArmySessionFactory factory, final Logger logger,
                                             final List<String> stmtList) {

        final Boolean debugLevel;
        debugLevel = logDebugLevel(factory);
        if (debugLevel == null) {
            return;
        }

        final String stmtLog;
        final int stmtSize;
        if ((stmtSize = stmtList.size()) == 1) {
            stmtLog = stmtList.get(0);
        } else {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < stmtSize; i++) {
                if (i > 0) {
                    builder.append('\n');
                }
                builder.append(stmtList.get(i))
                        .append(_Constant.SPACE_SEMICOLON);
            }
            stmtLog = builder.toString();
        }

        if (debugLevel) {
            logger.debug("transaction[name : {}] \n{}", this.name, stmtLog);
        } else {
            logger.info("transaction[name : {}] \n{}", this.name, stmtLog);
        }
    }

    @Nullable
    private static Boolean logDebugLevel(final _ArmySessionFactory factory) {
        final SqlLogMode mode;
        if (factory.sqlLogDynamic) {
            mode = factory.env.getOrDefault(ArmyKey.SQL_LOG_MODE);
        } else {
            mode = factory.sqlLogMode;
        }

        final Boolean debugLevel;
        switch (mode) {
            case OFF:
                debugLevel = null;
                break;
            case SIMPLE:
            case BEAUTIFY:
                debugLevel = Boolean.FALSE;
                break;
            case DEBUG:
            case BEAUTIFY_DEBUG:
                debugLevel = Boolean.TRUE;
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return debugLevel;

    }


}
