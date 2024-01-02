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

package io.army.example.common;


import reactor.core.publisher.Mono;

import java.util.Map;

public interface BaseService {

    String REACTIVE = "reactive";

    String SYNC = "sync";


    <T extends Domain> Mono<T> get(Class<T> domainClass, Object id);

    <T extends Domain> Mono<Void> save(T domain);

    <T extends Domain> Mono<T> findById(Class<T> domainClass, Object id);

    Mono<Map<String, Object>> findByIdAsMap(Class<?> domainClass, Object id);
}
