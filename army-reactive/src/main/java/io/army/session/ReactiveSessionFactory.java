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

package io.army.session;

import io.army.spec.ReactiveCloseable;
import reactor.core.publisher.Mono;

/**
 * <p>This interface representing a reactive {@link SessionFactory}.
 * <p>The instance of this interface is created by {@link ReactiveFactoryBuilder}
 *
 * @since 0.6.0
 */
public sealed interface ReactiveSessionFactory extends PackageSessionFactory, ReactiveCloseable permits ArmyReactiveSessionFactory {

    Mono<ReactiveLocalSession> localSession();

    Mono<ReactiveRmSession> rmSession();


    LocalSessionBuilder localBuilder();


    RmSessionBuilder rmBuilder();


    interface LocalSessionBuilder extends SessionBuilderSpec<LocalSessionBuilder, Mono<ReactiveLocalSession>> {


    }

    interface RmSessionBuilder extends SessionBuilderSpec<RmSessionBuilder, Mono<ReactiveRmSession>> {


    }


}
