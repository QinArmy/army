package io.army.boot.migratioin;

import io.army.lang.Nullable;

import java.sql.Connection;

/**
 * extract {@link SchemaInfo} from database. eg:{@link java.sql.Connection}
 */
interface SchemaExtractor {

    SchemaInfo extract(@Nullable String routeSuffix);

    static SchemaExtractor build(Connection conn) {
        return new SyncSchemaExtractorImpl(conn);
    }
}
