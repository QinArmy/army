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

package io.army.spring.reactive;


import io.army.session.*;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

final class SpringReactiveSessionContext implements ReactiveSessionContext {

    static SpringReactiveSessionContext create(ReactiveSessionFactory factory) {
        return new SpringReactiveSessionContext(factory);
    }

    private final ReactiveSessionFactory factory;

    private SpringReactiveSessionContext(ReactiveSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public ReactiveSessionFactory sessionFactory() {
        return this.factory;
    }

    @Override
    public <T extends SessionFactory> T sessionFactory(Class<T> factoryClass) {
        return factoryClass.cast(this.factory);
    }

    @Override
    public Mono<Boolean> hasCurrentSession() {
        return TransactionSynchronizationManager.forCurrentTransaction()
                .map(manager -> manager.hasResource(this.factory));
    }


    @Override
    public Mono<ReactiveSession> currentSession() {
        return currentSession(ReactiveSession.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ReactiveSession> Mono<T> currentSession(final Class<T> sessionClass) {
        return TransactionSynchronizationManager.forCurrentTransaction()
                .flatMap(manager -> {
                    final Object value;
                    value = manager.getResource(this.factory);
                    if (sessionClass.isInstance(value)) {
                        return Mono.just((T) value);
                    }
                    return Mono.error(new NoCurrentSessionException("no current session"));
                });
    }


}
