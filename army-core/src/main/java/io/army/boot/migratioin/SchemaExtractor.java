package io.army.boot.migratioin;

import java.sql.Connection;


/**
 * <p>
 *      a inner interface , extract schema info from database's current schema.
 * </p>
 * @see  MetaSchemaComparator
 * @see Meta2Schema
 */
interface SchemaExtractor {

    SchemaInfo extractor(Connection connection);


    static SchemaExtractor newInstance() {
        return new SchemaExtractorImpl();
    }
}
