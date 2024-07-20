/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.lang.NonNull;
import io.army.meta.SchemaMeta;
import io.army.util._StringUtils;

import java.util.Locale;
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

        final String key = _StringUtils.toLowerCaseIfNonNull(catalog + "." + schema);
        return SCHEMA_META_HOLDER.computeIfAbsent(key, k -> new SchemaMetaImpl(catalog, schema));
    }


    private static final class SchemaMetaImpl implements SchemaMeta {

        private final String catalog;

        private final String schema;

        SchemaMetaImpl(final String catalog, final String schema) {
            this.catalog = catalog.toLowerCase(Locale.ROOT);
            this.schema = schema.toLowerCase(Locale.ROOT);
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
