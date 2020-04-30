package io.army.boot.migratioin;

/**
 * extract {@link SchemaInfo} from database. eg:{@link java.sql.Connection}
 */
public interface SchemaExtractor {

    SchemaInfo extract();
}
