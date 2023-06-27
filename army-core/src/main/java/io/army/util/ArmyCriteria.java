package io.army.util;

import io.army.criteria.Insert;
import io.army.criteria.impl.SQLs;
import io.army.meta.*;
import io.army.session.Session;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.List;

public abstract class ArmyCriteria {

    private ArmyCriteria() {
        throw new UnsupportedOperationException();
    }


    @SuppressWarnings("unchecked")
    public static <T> Insert insertStmt(final Session session, final T domain) {
        final TableMeta<T> table;
        table = (TableMeta<T>) session.tableMeta(domain.getClass());
        final Insert stmt;
        if (table instanceof SimpleTableMeta) {
            stmt = SQLs.singleInsert()
                    .insertInto((SimpleTableMeta<T>) table)
                    .value(domain)
                    .asInsert();
        } else if (table instanceof ChildTableMeta) {
            stmt = childInsertStatement((ChildTableMeta<T>) table, domain);
        } else {
            stmt = SQLs.singleInsert()
                    .insertInto((ParentTableMeta<T>) table)
                    .value(domain)
                    .asInsert();
        }
        return stmt;
    }

    @SuppressWarnings("unchecked")
    public static <T> Insert batchInsertStmt(final Session session, final List<T> domainList) {
        final TableMeta<T> table;
        table = (TableMeta<T>) session.tableMeta(domainList.get(0).getClass());

        final Insert stmt;
        if (table instanceof SimpleTableMeta) {
            stmt = SQLs.singleInsert()
                    .insertInto((SimpleTableMeta<T>) table)
                    .values(domainList)
                    .asInsert();
        } else if (table instanceof ChildTableMeta) {
            stmt = childBatchInsertStatement((ChildTableMeta<T>) table, domainList);
        } else {
            stmt = SQLs.singleInsert()
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
    public static <T> List<FieldMeta<?>> fieldListOf(TableMeta<T> table) {
        final List<?> fieldList;
        fieldList = table.fieldList();
        return (List<FieldMeta<?>>) fieldList;
    }


    private static <P, T extends P> Insert childInsertStatement(final ChildTableMeta<T> table, final T domain) {
        final ComplexTableMeta<P, T> child = (ComplexTableMeta<P, T>) table;

        return SQLs.singleInsert()
                .insertInto(child.parentMeta())
                .value(domain)
                .asInsert()

                .child()

                .insertInto(child)
                .value(domain)
                .asInsert();
    }


    private static <P, T extends P> Insert childBatchInsertStatement(final ChildTableMeta<T> table,
                                                                     final List<T> domainList) {
        final ComplexTableMeta<P, T> child = (ComplexTableMeta<P, T>) table;

        return SQLs.singleInsert()
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
