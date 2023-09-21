package io.army.jdbd;

import io.army.ArmyException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.ExecutorProvider;
import io.army.reactive.executor.StmtExecutorFactory;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;
import io.jdbd.meta.DatabaseMetaData;
import io.jdbd.meta.SchemaMeta;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.DatabaseSessionFactory;
import io.jdbd.session.ServerVersion;
import reactor.core.publisher.Mono;

/**
 * <p>This class is a implementation of {@link ExecutorProvider} with jdbd spi.
 *
 * @since 10
 */
public final class JdbdExecutorProvider implements ExecutorProvider {

    public static JdbdExecutorProvider create(final Object sessionFactory) {
        if (!(sessionFactory instanceof DatabaseSessionFactory)) {
            String m = String.format("%s support only %s,but passing %s",
                    JdbdExecutorProvider.class.getName(),
                    DatabaseSessionFactory.class.getName(),
                    _ClassUtils.safeClassName(sessionFactory)
            );
            throw new ArmyException(m);
        }
        return new JdbdExecutorProvider((DatabaseSessionFactory) sessionFactory);
    }


    private final DatabaseSessionFactory sessionFactory;

    private JdbdExecutorProvider(DatabaseSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Mono<ServerMeta> createServerMeta(final Dialect usedDialect) {
        return Mono.from(this.sessionFactory.localSession())
                .map(DatabaseSession::databaseMetaData)
                .flatMap(metaData -> Mono.from(metaData.currentSchema()))
                .map(metaData -> mapServerMeta(metaData, usedDialect));
    }

    @Override
    public Mono<StmtExecutorFactory> createExecutorFactory(ExecutorEnv env) {
        return Mono.just(JdbdStmtExecutorFactory.create(this.sessionFactory, env));
    }


    private ServerMeta mapServerMeta(final SchemaMeta schemaMeta, final Dialect usedDialect) {
        final DatabaseMetaData metaData = schemaMeta.databaseMetadata();

        final ServerVersion serverVersion;
        serverVersion = metaData.serverVersion();

        final ServerMeta serverMeta;
        serverMeta = ServerMeta.builder()

                .name(metaData.productName())
                .database(mapToDatabase(metaData.productFamily()))
                .catalog(schemaMeta.catalog())
                .schema(schemaMeta.schema())

                .version(serverVersion.getVersion())
                .major(serverVersion.getMajor())
                .minor(serverVersion.getMinor())
                .subMinor(serverVersion.getSubMinor())

                .supportSavePoint(metaData.isSupportSavePoints())
                .usedDialect(usedDialect)
                .build();

        return serverMeta;
    }

    private Database mapToDatabase(final String productFamily) {
        Database database;
        switch (productFamily) {
            case "MySQL":
                database = Database.MySQL;
                break;
            case "PostgreSQL":
                database = Database.PostgreSQL;
                break;
            case "Oracle":
                database = Database.Oracle;
                break;
            default:
                throw _Exceptions.unsupportedDatabaseFamily(productFamily);
        }
        return database;
    }


}
