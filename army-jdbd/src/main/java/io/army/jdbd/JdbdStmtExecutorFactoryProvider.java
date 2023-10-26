package io.army.jdbd;

import io.army.ArmyException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.ReactiveStmtExecutorFactory;
import io.army.reactive.executor.ReactiveStmtExecutorFactoryProvider;
import io.army.util._ClassUtils;
import io.army.util._StringUtils;
import io.jdbd.meta.DatabaseMetaData;
import io.jdbd.meta.SchemaMeta;
import io.jdbd.session.DatabaseSessionFactory;
import io.jdbd.session.ServerVersion;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * <p>This class is a implementation of {@link ReactiveStmtExecutorFactoryProvider} with jdbd spi.
 *
 * @since 10
 */
public final class JdbdStmtExecutorFactoryProvider implements ReactiveStmtExecutorFactoryProvider {

    public static JdbdStmtExecutorFactoryProvider create(final Object datasource, final String factoryName) {
        if (!(datasource instanceof DatabaseSessionFactory)) {
            String m = String.format("%s support only %s,but passing %s",
                    JdbdStmtExecutorFactoryProvider.class.getName(),
                    DatabaseSessionFactory.class.getName(),
                    _ClassUtils.safeClassName(datasource)
            );
            throw new ArmyException(m);
        } else if (!_StringUtils.hasText(factoryName)) {
            throw new IllegalArgumentException();
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
    public Mono<ServerMeta> createServerMeta(final Dialect usedDialect, final @Nullable Function<String, Database> func) {
        return Mono.from(this.sessionFactory.localSession())
                .flatMap(session -> Mono.from(session.databaseMetaData().currentSchema())
                        .map(schemaMeta -> mapServerMeta(schemaMeta, usedDialect, func))
                        .onErrorResume(error -> Mono.from(session.close()))
                        .concatWith(Mono.defer(() -> Mono.from(session.close())))
                        .last()
                );
    }

    @Override
    public Mono<ReactiveStmtExecutorFactory> createFactory(ExecutorEnv env) {
        return Mono.just(JdbdStmtExecutorFactory.create(this, env));
    }


    private ServerMeta mapServerMeta(final SchemaMeta schemaMeta, final Dialect usedDialect,
                                     final @Nullable Function<String, Database> func) {
        final DatabaseMetaData metaData = schemaMeta.databaseMetadata();

        final ServerVersion serverVersion;
        serverVersion = metaData.serverVersion();

        final ServerMeta serverMeta;
        serverMeta = ServerMeta.builder()

                .name(metaData.productName())
                .database(Database.mapToDatabase(metaData.productFamily(), func))
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


}
