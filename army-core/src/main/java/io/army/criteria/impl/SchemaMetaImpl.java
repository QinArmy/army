package io.army.criteria.impl;

import io.army.meta.SchemaMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;
import org.springframework.lang.NonNull;

import java.util.Objects;

final class SchemaMetaImpl implements SchemaMeta {

    private final String catalog;

    private final String schema;

    private final boolean defaultSchema;

     SchemaMetaImpl(String catalog, String schema) {
        Assert.notNull(catalog, "catalog required");
        Assert.notNull(schema, "schema required");

        this.catalog = catalog;
        this.schema = schema;
        this.defaultSchema = StringUtils.isEmpty(catalog)
                && StringUtils.isEmpty(schema);

    }

    @NonNull
    @Override
    public String catalog() {
        return catalog;
    }

    @Override
    public boolean defaultSchema() {
        return defaultSchema;
    }

    @NonNull
    @Override
    public String schema() {
        return schema;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SchemaMeta)) {
            return false;
        }
        SchemaMeta schemaMeta = (SchemaMeta) obj;

        return catalog.equals(schemaMeta.catalog())
                && schema.equals(schemaMeta.schema());
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalog,schema);
    }

    @Override
    public String toString() {
        String str;
        if (defaultSchema()) {
            str = "";
        } else {
            str = catalog + "." + schema;
        }
        return str;
    }
}
