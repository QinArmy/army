package io.army.schema.migration;

import java.sql.Connection;

interface SchemaExtractor {

    SchemaInfo extractor(Connection connection);


    static SchemaExtractor newInstance() {
        return new SchemaExtractorImpl();
    }
}
