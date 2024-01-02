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

package io.army.reactive.executor;

import io.army.reactive.ReactiveCloseable;
import io.army.schema.SchemaInfo;
import io.army.session.DataAccessException;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReactiveMetaExecutor extends ReactiveCloseable {


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
    Mono<Void> executeDdl(List<String> ddlList);


}
