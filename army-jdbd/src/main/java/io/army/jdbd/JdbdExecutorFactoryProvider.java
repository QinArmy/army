package io.army.jdbd;

import io.army.ArmyException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.env.ArmyEnvironment;
import io.army.executor.ExecutorEnv;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.ReactiveExecutorFactory;
import io.army.reactive.executor.ReactiveExecutorFactoryProvider;
import io.army.util.ClassUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import io.jdbd.meta.DatabaseMetaData;
import io.jdbd.meta.SchemaMeta;
import io.jdbd.session.DatabaseSessionFactory;
import io.jdbd.session.ServerVersion;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>This class is a implementation of {@link ReactiveExecutorFactoryProvider} with jdbd spi.
 *
 * @see <a href="https://github.com/QinArmy/jdbd">jdbd-spi</a>
 * @since 10
 */
public final class JdbdExecutorFactoryProvider implements ReactiveExecutorFactoryProvider {

    public static JdbdExecutorFactoryProvider create(final Object datasource, final String factoryName, ArmyEnvironment env) {
        if (!(datasource instanceof DatabaseSessionFactory)) {
            String m = String.format("%s support only %s,but passing %s",
                    JdbdExecutorFactoryProvider.class.getName(),
                    DatabaseSessionFactory.class.getName(),
                    ClassUtils.safeClassName(datasource)
            );
            throw new ArmyException(m);
        } else if (!_StringUtils.hasText(factoryName)) {
            throw new IllegalArgumentException();
        }
        return new JdbdExecutorFactoryProvider((DatabaseSessionFactory) datasource, factoryName);
    }


    final DatabaseSessionFactory sessionFactory;

    final String factoryName;


    private JdbdExecutorFactoryProvider(DatabaseSessionFactory sessionFactory, String factoryName) {
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
                ).onErrorMap(_Exceptions::wrapIfNeed);
    }

    @Override
    public Mono<ReactiveExecutorFactory> createFactory(ExecutorEnv env) {
        Mono<ReactiveExecutorFactory> mono;
        final JdbdStmtExecutorFactory factory;
        try {
            factory = JdbdStmtExecutorFactory.create(this, env);
            mono = Mono.just(factory);
        } catch (Throwable e) {
            mono = Mono.error(_Exceptions.wrapIfNeed(e));
        }
        return mono;
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
                .driverSpi("io.jdbd")

                .build();
        return serverMeta;
    }


}
