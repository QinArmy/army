package io.army.criteria.impl;

import io.army.meta.SchemaMeta;

public abstract class SchemaMetaFactory {

    public static SchemaMeta getSchema(String catalog, String schema) {
        return SchemaMetaHolder.getSchema(catalog, schema);
    }
}
