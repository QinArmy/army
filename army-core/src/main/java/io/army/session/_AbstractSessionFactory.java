package io.army.session;

import io.army.ArmyException;
import io.army.dialect.DialectParser;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.lang.Nullable;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;
import io.army.util._Assert;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * a abstract GenericSessionFactoryAdvice
 *
 * @since 1.0
 */
public abstract class _AbstractSessionFactory implements SessionFactory {

    protected static final ConcurrentMap<String, Boolean> FACTORY_MAP = new ConcurrentHashMap<>(3);

    protected final String name;

    public final ArmyEnvironment env;

    protected final SchemaMeta schemaMeta;

    public final Map<Class<?>, TableMeta<?>> tableMap;

    protected final Function<ArmyException, RuntimeException> exceptionFunction;

    protected final QueryInsertMode subQueryInsertMode;

    protected final boolean readonly;

    public final boolean uniqueCache;

    private final boolean sqlLogDynamic;

    private final boolean sqlLogShow;

    private final boolean sqlLogBeautify;

    private final boolean sqlLogDebug;


    protected _AbstractSessionFactory(final FactoryBuilderSupport support) throws SessionFactoryException {
        final String name = _Assert.assertHasText(support.name, "factory name required");
        final ArmyEnvironment env = Objects.requireNonNull(support.environment);

        if (FACTORY_MAP.containsKey(name)) {
            throw new SessionFactoryException(String.format("factory name[%s] duplication", name));
        }
        this.name = name;
        this.env = env;
        this.schemaMeta = Objects.requireNonNull(support.schemaMeta);
        this.tableMap = Objects.requireNonNull(support.tableMap);

        this.exceptionFunction = exceptionFunction(support.exceptionFunction);
        this.subQueryInsertMode = env.getOrDefault(ArmyKey.SUBQUERY_INSERT_MODE);
        this.readonly = env.getOrDefault(ArmyKey.READ_ONLY);
        this.uniqueCache = Objects.requireNonNull(support.ddlMode) != DdlMode.NONE;

        this.sqlLogDynamic = env.getOrDefault(ArmyKey.SQL_LOG_DYNAMIC);
        this.sqlLogShow = env.getOrDefault(ArmyKey.SQL_LOG_SHOW);
        this.sqlLogBeautify = env.getOrDefault(ArmyKey.SQL_LOG_BEAUTIFY);
        this.sqlLogDebug = env.getOrDefault(ArmyKey.SQL_LOG_DEBUG);
    }


    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final boolean uniqueCache() {
        return this.uniqueCache;
    }

    @Override
    public final ArmyEnvironment environment() {
        return this.env;
    }


    @Override
    public final SchemaMeta schemaMeta() {
        return this.schemaMeta;
    }

    @Override
    public final Map<Class<?>, TableMeta<?>> tableMap() {
        return this.tableMap;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public final <T> TableMeta<T> getTable(Class<T> domainClass) {
        return (TableMeta<T>) this.tableMap.get(domainClass);
    }

    @Override
    public final boolean readonly() {
        return this.readonly;
    }

    @Override
    public final Function<ArmyException, RuntimeException> exceptionFunction() {
        return this.exceptionFunction;
    }


    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this;
    }


    @Nullable
    protected final void printSqlLog(final DialectParser parser, final Stmt stmt, final Logger factoryLogger) {
        String sqlLog = null;
        boolean debugLevel = false;
        if (this.sqlLogDynamic) {
            final ArmyEnvironment env = this.env;
            if (env.getOrDefault(ArmyKey.SQL_LOG_SHOW)) {
                sqlLog = parser.printStmt(stmt, env.getOrDefault(ArmyKey.SQL_LOG_BEAUTIFY));
                debugLevel = env.getOrDefault(ArmyKey.SQL_LOG_DEBUG);
            }
        } else if (this.sqlLogShow) {
            sqlLog = parser.printStmt(stmt, this.sqlLogBeautify);
            debugLevel = this.sqlLogDebug;
        }

        if (sqlLog != null) {
            if (debugLevel) {
                factoryLogger.debug(sqlLog);
            } else {
                factoryLogger.info(sqlLog);
            }
        }

    }



    /*################################## blow protected method ##################################*/


    /*################################## blow private static method ##################################*/

    private static Function<ArmyException, RuntimeException> exceptionFunction(
            @Nullable Function<ArmyException, RuntimeException> function) {
        if (function == null) {
            function = e -> e;
        }
        return function;
    }

    private static byte tableCountPerDatabase(final int tableCount) {
        if (tableCount < 1 || tableCount > 99) {
            String m = String.format("Table count[%s] per database must great than 0 .", tableCount);
            throw new SessionFactoryException(m);
        }
        return (byte) tableCount;
    }


}
