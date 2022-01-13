package io.army.boot.migratioin;

import io.army.dialect.DDLSQLExecuteException;
import io.army.meta.MetaException;
import io.army.schema.SchemaInfoException;
import io.army.session.DialectSessionFactory;

import java.sql.Connection;

/**
 * This interface can migrate all {@link io.army.meta.TableMeta} meta to database
 * ,when {@link DialectSessionFactory} initialize.
 */
public interface SyncMetaMigrator {

    /**
     * migrate all {@link io.army.meta.TableMeta} meta to database than {@link DialectSessionFactory} representing .
     *
     * @param conn a {@link Connection} of writeable {@link javax.sql.DataSource} or {@link javax.transaction.xa.XAResource}
     */
    void migrate(Connection conn, DialectSessionFactory sessionFactory)
            throws SchemaExtractException, SchemaInfoException, MetaException, DDLSQLExecuteException;

    static SyncMetaMigrator build() {
        return new SyncMetaMigratorImpl();
    }
}
