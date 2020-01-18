package io.army.schema.extract;

import java.sql.Connection;

public interface SchemaExtractor {

    SchemaInfo extractor(Connection connection);


    static SchemaExtractor newInstance(){
        return new SchemaExtractorImpl();
    }
}
