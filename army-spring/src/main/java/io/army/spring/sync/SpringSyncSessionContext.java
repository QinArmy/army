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

package io.army.spring.sync;

import io.army.session.*;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.army.lang.Nullable;

final class SpringSyncSessionContext implements SyncSessionContext {

    static SpringSyncSessionContext create(SyncSessionFactory factory) {
        return new SpringSyncSessionContext(factory);
    }

    private final SyncSessionFactory factory;

    private SpringSyncSessionContext(SyncSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public SyncSessionFactory sessionFactory() {
        return this.factory;
    }


    @Override
    public <T extends SessionFactory> T sessionFactory(Class<T> factoryClass) {
        return factoryClass.cast(this.factory);
    }

    @Override
    public SyncSession currentSession() throws NoCurrentSessionException {
        final Object session;
        session = TransactionSynchronizationManager.getResource(this.factory);
        if (!(session instanceof SyncSession)) {
            throw new NoCurrentSessionException("no current session");
        }
        return (SyncSession) session;
    }

    @Override
    public <T extends SyncSession> T currentSession(Class<T> sessionClass) throws NoCurrentSessionException {
        return sessionClass.cast(currentSession());
    }

    @Nullable
    @Override
    public SyncSession tryCurrentSession() {
        final Object session;
        session = TransactionSynchronizationManager.getResource(this.factory);
        if (session instanceof SyncSession) {
            return (SyncSession) session;
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends SyncSession> T tryCurrentSession(Class<T> sessionClass) {
        final Object session;
        session = TransactionSynchronizationManager.getResource(this.factory);
        if (session instanceof SyncSession) {
            return sessionClass.cast(session);
        }
        return null;
    }


}
