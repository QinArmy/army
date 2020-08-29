package io.army.boot.migratioin;

import io.army.GenericRmSessionFactory;
import io.army.dialect.DDLSQLExecuteException;
import io.army.meta.MetaException;
import io.army.schema.SchemaInfoException;
import reactor.core.publisher.Mono;


public interface ReactiveMigrator {


    Mono<Void> migrate(Object databaseSession, GenericRmSessionFactory sessionFactory)
            throws SchemaExtractException, SchemaInfoException, MetaException, DDLSQLExecuteException;

    static ReactiveMigrator build() {
        return new ReactiveMigratorImpl();
    }

}
