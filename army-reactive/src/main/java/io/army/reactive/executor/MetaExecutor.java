package io.army.reactive.executor;

import io.army.reactive.Closeable;
import io.army.schema.SchemaInfo;
import io.army.session.DataAccessException;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MetaExecutor extends Closeable {


    /**
     * <p>extract database meta
     *
     * @throws DataAccessException emit(not throw) when access database occur error.
     */
    Mono<SchemaInfo> extractInfo();

    /**
     * <p>Execute ddl statements
     *
     * @param ddlList non-null
     * @return emit <strong>this</strong>
     * @throws DataAccessException emit(not throw) when access database occur error.
     */
    Mono<MetaExecutor> executeDdl(List<String> ddlList);


}
