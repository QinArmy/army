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

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.SimpleExpression;
import io.army.criteria.impl.SQLs;
import io.army.criteria.standard.StandardQuery;
import io.army.meta.ChildTableMeta;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.session.SyncSession;
import io.army.session.SyncSessionContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;


public abstract class ArmySyncBaseDao implements SyncBaseDao {

    protected SyncSessionContext sessionContext;


    @Override
    public <T extends Domain> void save(final T domain) {
        this.sessionContext.currentSession().save(domain);
    }


    @Override
    public <T extends Domain> void batchSave(final List<T> domainList) {
        this.sessionContext.currentSession().batchSave(domainList);
    }

    @Override
    public <T extends Domain> T get(Class<T> domainClass, Object id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Domain> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Domain> T findById(Class<T> domainClass, Object id) {
        final SyncSession session;
        session = this.sessionContext.currentSession();
        final Select stmt;
        stmt = createFindByIdStmt(session, domainClass, SQLs::param, id).asQuery();
        return session.queryOne(stmt, domainClass);
    }

    @Override
    public Map<String, Object> findByIdAsMap(Class<?> domainClass, Object id) {
        final SyncSession session;
        session = this.sessionContext.currentSession();
        final Select stmt;
        stmt = createFindByIdStmt(session, domainClass, SQLs::param, id).asQuery();
        return session.queryOneObject(stmt, HashMap::new);
    }

    @Override
    public void flush() {
        //no-op
    }


    protected final <P, T> StandardQuery._WhereAndSpec<Select> createFindByIdStmt(
            SyncSession session, Class<T> domainClass,
            BiFunction<SimpleExpression, Object, Expression> valueOperator, Object id) {
        final TableMeta<T> table;
        table = session.tableMeta(domainClass);

        final StandardQuery._WhereAndSpec<Select> clause;
        if (table instanceof ChildTableMeta) {
            final ComplexTableMeta<P, T> child = (ComplexTableMeta<P, T>) table;
            final ParentTableMeta<P> parent = child.parentMeta();
            clause = SQLs.query()
                    .select("p", PERIOD, parent, "c", SQLs.PERIOD, child)
                    .from(child, AS, "c")
                    .join(parent, AS, "p").on(table.id().equal(parent.id()))
                    .where(child.id().equal(valueOperator, id));
        } else {
            clause = SQLs.query()
                    .select("t", PERIOD, table)
                    .from(table, AS, "t")
                    .where(table.id().equal(valueOperator, id));
        }
        return clause;
    }


}
