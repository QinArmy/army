package io.army.jdbd;

import io.army.reactive.executor.ReactiveMetaExecutor;
import io.army.schema.SchemaInfo;
import io.jdbd.session.DatabaseSession;
import reactor.core.publisher.Mono;

import java.util.List;

final class JdbdMetaExecutor implements ReactiveMetaExecutor {

    static ReactiveMetaExecutor create(String name, JdbdStmtExecutorFactory factory, DatabaseSession session) {
        return new JdbdMetaExecutor(name, factory, session);
    }

    private final String name;

    private final JdbdStmtExecutorFactory factory;

    private final DatabaseSession session;

    private JdbdMetaExecutor(String name, JdbdStmtExecutorFactory factory, DatabaseSession session) {
        this.name = name;
        this.factory = factory;
        this.session = session;
    }

    @Override
    public Mono<SchemaInfo> extractInfo() {
        return Mono.empty();
    }

    @Override
    public Mono<Void> executeDdl(List<String> ddlList) {
        return Mono.empty();
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public <T> Mono<T> close() {
        return null;
    }


}
