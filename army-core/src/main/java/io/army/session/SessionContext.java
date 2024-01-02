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

/**
 * <p>This interface representing session context,it is designed for some framework ,for example :
 * {@code org.springframework.transaction.PlatformTransactionManager}.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code  io.army.sync.SyncSessionContext}</li>
 *     <li>{@code io.army.reactive.ReactiveSessionContext}</li>
 * </ul>
 *
 * @since 0.6.2
 */
public interface SessionContext {

    SessionFactory sessionFactory();

    <T extends SessionFactory> T sessionFactory(Class<T> factoryClass);


}
