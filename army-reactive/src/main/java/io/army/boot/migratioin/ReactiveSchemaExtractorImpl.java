package io.army.boot.migratioin;

import io.army.lang.Nullable;
import io.jdbd.StatelessSession;
import reactor.core.publisher.Mono;

final class ReactiveSchemaExtractorImpl implements ReactiveSchemaExtractor {

    private final StatelessSession databaseSession;

    ReactiveSchemaExtractorImpl(StatelessSession databaseSession) {
        this.databaseSession = databaseSession;
    }

    @Override
    public Mono<SchemaInfo> extract(@Nullable String routeSuffix) throws SchemaExtractException {
        return Mono.empty();
    }
}
