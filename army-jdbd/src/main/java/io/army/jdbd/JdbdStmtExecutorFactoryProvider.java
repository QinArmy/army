package io.army.jdbd;

import io.army.ArmyException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.env.ArmyEnvironment;
import io.army.executor.ExecutorEnv;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.ReactiveStmtExecutorFactory;
import io.army.reactive.executor.ReactiveStmtExecutorFactoryProvider;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;
import io.jdbd.meta.DatabaseMetaData;
import io.jdbd.meta.SchemaMeta;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.DatabaseSessionFactory;
import io.jdbd.session.ServerVersion;
import reactor.core.publisher.Mono;

/**
 * <p>This class is a implementation of {@link ReactiveStmtExecutorFactoryProvider} with jdbd spi.
 *
 * @since 10
 */
public final class JdbdStmtExecutorFactoryProvider implements ReactiveStmtExecutorFactoryProvider {

    public static JdbdStmtExecutorFactoryProvider create(final Object datasource, final String factoryName,
                                                         final ArmyEnvironment env) {
        if (!(datasource instanceof DatabaseSessionFactory)) {
            String m = String.format("%s support only %s,but passing %s",
                    JdbdStmtExecutorFactoryProvider.class.getName(),
                    DatabaseSessionFactory.class.getName(),
                    _ClassUtils.safeClassName(datasource)
            );
            throw new ArmyException(m);
        }
        return new JdbdStmtExecutorFactoryProvider((DatabaseSessionFactory) datasource, factoryName);
    }


    final DatabaseSessionFactory sessionFactory;

    final String factoryName;


    private JdbdStmtExecutorFactoryProvider(DatabaseSessionFactory sessionFactory, String factoryName) {
        this.sessionFactory = sessionFactory;
        this.factoryName = factoryName;
    }

    @Override
    public Mono<ServerMeta> createServerMeta(final Dialect usedDialect) {
        return Mono.from(this.sessionFactory.localSession())
                .map(DatabaseSession::databaseMetaData)
                .flatMap(metaData -> Mono.from(metaData.currentSchema()))
                .map(metaData -> mapServerMeta(metaData, usedDialect));
    }

    @Override
    public Mono<ReactiveStmtExecutorFactory> createFactory(ExecutorEnv env) {
        return Mono.just(JdbdStmtExecutorFactory.create(this.sessionFactory, this.factoryName, env));
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
