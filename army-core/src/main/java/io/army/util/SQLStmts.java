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

package io.army.util;

import io.army.criteria.CriteriaException;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.Session;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.List;

public abstract class SQLStmts {

    SQLStmts() {
        throw new UnsupportedOperationException();
    }


    @SuppressWarnings("unchecked")
    public static <T> Insert insertStmt(Session session, LiteralMode mode, T domain) {
        return insertStmtOf((TableMeta<T>) session.tableMeta(domain.getClass()), mode, domain);
    }

    public static <T> Insert insertStmtOf(final TableMeta<T> table, final LiteralMode mode, final T domain) {
        final Insert stmt;
        if (table instanceof SimpleTableMeta) {
            stmt = SQLs.singleInsert()
                    .literalMode(mode)
                    .insertInto((SimpleTableMeta<T>) table)
                    .value(domain)
                    .asInsert();
        } else if (table instanceof ChildTableMeta) {
            stmt = childInsertStatement((ChildTableMeta<T>) table, mode, domain);
        } else {
            stmt = SQLs.singleInsert()
                    .literalMode(mode)
                    .insertInto((ParentTableMeta<T>) table)
                    .value(domain)
                    .asInsert();
        }
        return stmt;
    }

    public static <P, T extends P> Insert childInsertStatement(final ChildTableMeta<T> table, final LiteralMode mode,
                                                               final T domain) {
        final ComplexTableMeta<P, T> child = (ComplexTableMeta<P, T>) table;

        return SQLs.singleInsert()
                .literalMode(mode)
                .insertInto(child.parentMeta())
                .value(domain)
                .asInsert()

                .child()

                .insertInto(child)
                .value(domain)
                .asInsert();
    }

    @SuppressWarnings("unchecked")
    public static <T> Insert batchInsertStmt(final Session session, LiteralMode literalMode, final List<T> domainList) {
        final TableMeta<T> table;
        table = (TableMeta<T>) session.tableMeta(domainList.get(0).getClass());
        return batchInsertStmtOf(table, literalMode, domainList);
    }

    public static <T> Insert batchInsertStmtOf(final TableMeta<T> table, LiteralMode literalMode, final List<T> domainList) {
        final Insert stmt;
        if (table instanceof SimpleTableMeta) {
            stmt = SQLs.singleInsert()
                    .literalMode(literalMode)
                    .insertInto((SimpleTableMeta<T>) table)
                    .values(domainList)
                    .asInsert();
        } else if (table instanceof ChildTableMeta) {
            stmt = childBatchInsertStatement((ChildTableMeta<T>) table, literalMode, domainList);
        } else {
            stmt = SQLs.singleInsert()
                    .literalMode(literalMode)
                    .insertInto((ParentTableMeta<T>) table)
                    .values(domainList)
                    .asInsert();
        }
        return stmt;
    }

    /**
     * @see TableMeta#fieldList()
     */
    @SuppressWarnings("unchecked")
    public static List<FieldMeta<?>> castFieldList(TableMeta<?> table) {
        final List<?> fieldList;
        fieldList = table.fieldList();
        return (List<FieldMeta<?>>) fieldList;
    }

    public static <T> Select queryDomainByUniqueStmt(final Session session, final Class<T> domainClass,
                                                     final String filedName, final Object fieldValue) {
        final TableMeta<T> domainTable;
        domainTable = session.tableMeta(domainClass);
        return queryDomainByUniqueStmtFor(domainTable, filedName, fieldValue);
    }

    public static <P, T> Select queryDomainByUniqueStmtFor(final TableMeta<T> domainTable, final String filedName,
                                                           final Object fieldValue) {

        final FieldMeta<?> uniqueFiled;
        if (_MetaBridge.ID.equals(filedName)) {
            uniqueFiled = domainTable.id();
        } else {
            uniqueFiled = domainTable.getComplexFiled(filedName);
            if (!(uniqueFiled instanceof UniqueFieldMeta)) {
                String m = String.format("%s isn't %s", uniqueFiled, UniqueFieldMeta.class.getName());
                throw new CriteriaException(m);
            }
        }

        final Select stmt;
        if (domainTable instanceof ChildTableMeta) {
            final ComplexTableMeta<P, T> child = (ComplexTableMeta<P, T>) domainTable;
            final ParentTableMeta<P> parent = child.parentMeta();

            final PrimaryFieldMeta<?> childId;
            if (uniqueFiled instanceof PrimaryFieldMeta) {
                childId = (PrimaryFieldMeta<?>) uniqueFiled;
            } else {
                childId = child.id();
            }

            stmt = SQLs.query()
                    .select("p", SQLs.PERIOD, parent, "c", SQLs.PERIOD, child)
                    .from(child, SQLs.AS, "c")
                    .join(parent, SQLs.AS, "p").on(childId.equal(parent.id()))
                    .where(uniqueFiled.equal(SQLs::param, fieldValue))
                    .asQuery();
        } else {
            stmt = SQLs.query()
                    .select("t", SQLs.PERIOD, domainTable)
                    .from(domainTable, SQLs.AS, "t")
                    .where(uniqueFiled.equal(SQLs::param, fieldValue))
                    .asQuery();
        }

        return stmt;
    }


    public static <T> Select rowCountStmt(final Session session, final Class<T> domainClass) {
        return rowCountStmtOf(session.tableMeta(domainClass));
    }


    public static <T> Select rowCountStmtOf(final TableMeta<T> domainTable) {
        final Select stmt;
        if (domainTable instanceof ChildTableMeta) {
            final ParentTableMeta<?> parent = ((ChildTableMeta<T>) domainTable).parentMeta();
            stmt = SQLs.query()
                    .select(SQLs.countAsterisk().as("count"))
                    .from(domainTable, SQLs.AS, "c")
                    .join(parent, SQLs.AS, "p").on(domainTable.id().equal(parent.id()))
                    .asQuery();
        } else {
            stmt = SQLs.query()
                    .select(SQLs.countAsterisk().as("count"))
                    .from(domainTable, SQLs.AS, "t")
                    .asQuery();
        }
        return stmt;
    }





    private static <P, T extends P> Insert childBatchInsertStatement(final ChildTableMeta<T> table,
                                                                     final LiteralMode literalMode,
                                                                     final List<T> domainList) {
        final ComplexTableMeta<P, T> child = (ComplexTableMeta<P, T>) table;

        return SQLs.singleInsert()
                .literalMode(literalMode)
                .insertInto(child.parentMeta())
                .values(domainList)
                .asInsert()

                .child()

                .insertInto(child)
                .values(domainList)
                .asInsert();
    }


    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        throw new InvalidObjectException("can't deserialize this");
    }

    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize this");
    }


}
