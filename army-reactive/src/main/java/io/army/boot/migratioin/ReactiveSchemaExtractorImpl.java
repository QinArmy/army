package io.army.boot.migratioin;

import io.army.meta.SchemaMeta;
import reactor.core.publisher.Mono;

final class ReactiveSchemaExtractorImpl implements ReactiveSchemaExtractor {


    ReactiveSchemaExtractorImpl(Object databaseSession) {

    }

    @Override
    public Mono<SchemaInfo> extract(SchemaMeta schemaMeta, String routeSuffix) throws SchemaExtractException {
        return null;
    }
}
