package io.army.boot.migratioin;

import java.sql.Connection;

public abstract class SyncSchemaExtractorFactory {

    protected SyncSchemaExtractorFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param connection after finished,{@link Connection#close()} is invoked.
     */
    public static SchemaExtractor build(Connection connection) {
        return new SyncSchemaExtractorImpl(connection);
    }
}
