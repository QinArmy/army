package io.army.boot.migratioin;

import io.army.lang.Nullable;
import io.army.meta.SchemaMeta;
import reactor.core.publisher.Mono;


interface ReactiveSchemaExtractor {

    Mono<SchemaInfo> extract(SchemaMeta schemaMeta, @Nullable String routeSuffix) throws SchemaExtractException;

    static ReactiveSchemaExtractor build(Object session) {
        return new ReactiveSchemaExtractorImpl(session);
    }
}
