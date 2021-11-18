package io.army.criteria.impl;

import io.army.meta.SchemaMeta;
import io.army.util.StringUtils;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class _SchemaMetaFactory {

    private _SchemaMetaFactory() {
        throw new UnsupportedOperationException();
    }

    private static final ConcurrentMap<String, SchemaMeta> SCHEMA_META_HOLDER = new ConcurrentHashMap<>();

    public static SchemaMeta getSchema(String catalog, String schema) {
        Objects.requireNonNull(catalog, "catalog required");
        Objects.requireNonNull(schema, "schema required");

        final String key = StringUtils.toLowerCase(catalog + "." + schema);
        return SCHEMA_META_HOLDER.computeIfAbsent(key, k -> new SchemaMetaImpl(catalog, schema));
    }


    private static final class SchemaMetaImpl implements SchemaMeta {

        private final String catalog;

        private final String schema;

        SchemaMetaImpl(final String catalog, final String schema) {
            this.catalog = StringUtils.toLowerCase(catalog);
            this.schema = StringUtils.toLowerCase(schema);
        }

        @NonNull
        @Override
        public String catalog() {
            return this.catalog;
        }

        @Override
        public boolean defaultSchema() {
            return this.catalog.isEmpty() && this.schema.isEmpty();
        }

        @NonNull
        @Override
        public String schema() {
            return this.schema;
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;

            if (obj == this) {
                match = true;
            } else if (obj instanceof SchemaMetaImpl) {
                final SchemaMetaImpl v = (SchemaMetaImpl) obj;
                match = this.catalog.equals(v.catalog) && this.schema.equals(v.schema);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.catalog, this.schema);
        }

        @Override
        public String toString() {
            return this.catalog + "." + this.schema;
        }

    }


}
