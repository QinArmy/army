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

import io.army.lang.Nullable;

/**
 * <p>This interface representing current {@link SyncSession} context.
 * <p>This interface is designed for some framework,for example {@code  org.springframework.transaction.PlatformTransactionManager}
 *
 * @since 0.6.0
 */
public interface SyncSessionContext extends SessionContext {

    @Override
    SyncSessionFactory sessionFactory();

    /**
     * Retrieve the current session according to the scoping defined
     * by this implementation.
     *
     * @return The current session.
     * @throws NoCurrentSessionException Typically indicates an issue
     *                                   locating or creating the current session.
     */
    SyncSession currentSession() throws NoCurrentSessionException;

    <T extends SyncSession> T currentSession(Class<T> sessionClass) throws NoCurrentSessionException;

    @Nullable
    SyncSession tryCurrentSession();

    @Nullable
    <T extends SyncSession> T tryCurrentSession(Class<T> sessionClass);


}
