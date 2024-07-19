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

package io.army.sync.dao;

import io.army.criteria.Select;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.sync.SyncSession;
import io.army.sync.SyncSessionContext;
import io.army.util.SQLStmts;

import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>This class is a abstract implementation of {@link SyncDaoSupport}
 *
 * @since 0.6.0
 */
public abstract class ArmySyncDaoSupport implements SyncDaoSupport {

    protected final SyncSessionContext sessionContext;

    protected ArmySyncDaoSupport(SyncSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public <T> void save(T domain) {
        this.sessionContext.currentSession().save(domain);
    }

    @Override
    public <T> void batchSave(List<T> domainList) {
        this.sessionContext.currentSession().batchSave(domainList);
    }

    @Nullable
    @Override
    public <T> T get(Class<T> domainClass, Object id) {
        return findByUnique(domainClass, _MetaBridge.ID, id);
    }

    @Nullable
    @Override
    public <T> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue) {
        return findByUnique(domainClass, fieldName, fieldValue);
    }

    @Nullable
    @Override
    public <T> T findById(Class<T> domainClass, Object id) {
        return findByUnique(domainClass, _MetaBridge.ID, id);
    }

    @Nullable
    @Override
    public <T> T findByUnique(Class<T> domainClass, String fieldName, Object fieldValue) {
        final SyncSession session;
        session = this.sessionContext.currentSession();
        return findByUniqueFor(session.tableMeta(domainClass), domainClass, session, fieldName, fieldValue);
    }

    @Override
    public <T> long countRow(final Class<T> domainClass) {
        final SyncSession session;
        session = this.sessionContext.currentSession();
        return countRowOf(session.tableMeta(domainClass), session);
    }


    protected static <T> long countRowOf(final TableMeta<T> table, final SyncSession session) {
        final Long rowCount;
        rowCount = session.queryOne(SQLStmts.rowCountStmtOf(table), Long.class);
        assert rowCount != null;
        return rowCount;
    }


    @Nullable
    protected static <T, R> R findByUniqueFor(final TableMeta<T> domainTable, final Class<R> returnClass,
                                              final SyncSession session, final String fieldName,
                                              final Object fieldValue) {
        final Select stmt;
        stmt = SQLStmts.queryDomainByUniqueStmtFor(domainTable, fieldName, fieldValue);
        return session.queryOne(stmt, returnClass);
    }

}
