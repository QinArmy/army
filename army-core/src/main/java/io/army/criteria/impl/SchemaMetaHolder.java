package io.army.criteria.impl;

import io.army.meta.SchemaMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class SchemaMetaHolder {

    private static ConcurrentMap<String, SchemaMeta> SCHEMA_META_HOLDER = new ConcurrentHashMap<>();

    static SchemaMeta getSchema(String catalog, String schema) {
        Assert.notNull(catalog,"catalog required");
        Assert.notNull(schema,"schema required");

        return SCHEMA_META_HOLDER.computeIfAbsent(
                StringUtils.toLowerCase(catalog + "." + schema)
                , key -> new SchemaMetaImpl(catalog, schema)
        );
    }

}
