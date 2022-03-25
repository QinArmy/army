package io.army.util;

import io.army.criteria.IPredicate;
import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.modelgen._MetaBridge;

import java.util.ArrayList;
import java.util.List;

public abstract class Criteria {

    protected Criteria() {
        throw new UnsupportedOperationException();
    }


    public static <T extends IDomain> Select createSelectDomainById(final TableMeta<T> table, final Object id) {
        final Select stmt;
        if (table instanceof ChildTableMeta) {
            final ChildTableMeta<?> child = (ChildTableMeta<?>) table;
            final ParentTableMeta<?> parent = child.parentMeta();

            stmt = SQLs.query()
                    .select(SQLs.childGroup(child, "c", "p"))
                    .from(child, "c") // small table first
                    .join(parent, "p").on(child.id().equal(parent.id()))
                    .where(child.id().equal(id))
                    .asQuery();
        } else {
            stmt = SQLs.query()
                    .select(SQLs.group(table, "t"))
                    .from(table, "t")
                    .where(table.id().equal(id))
                    .asQuery();
        }
        return stmt;
    }

    public static <T extends IDomain> Select createSelectDomainByUnique(final TableMeta<T> table
            , final UniqueFieldMeta<? super T> field, final Object value) {
        final Select stmt;
        if (table instanceof ChildTableMeta) {
            final ChildTableMeta<T> child = (ChildTableMeta<T>) table;
            final ParentTableMeta<?> parent = child.parentMeta();
            stmt = SQLs.query()
                    .select(SQLs.childGroup(child, "c", "p"))
                    .from(child, "c")
                    .join(parent, "p").on(child.id().equal(parent.id()))
                    .where(field.equal(value))
                    .limit(2)
                    .asQuery();
        } else {
            stmt = SQLs.query()
                    .select(SQLs.group(table, "t"))
                    .from(table, "t")
                    .where(field.equal(value))
                    .limit(2)
                    .asQuery();
        }
        return stmt;
    }


    protected static <T extends IDomain> List<IPredicate> createPredicateList(TableMeta<T> tableMeta
            , List<String> propNameList, List<Object> valueList) {

        _Assert.isTrue(propNameList.size() == valueList.size(), "propNameList size and valueList size not match.");

        ParentTableMeta<?> parentMeta = null;

        List<IPredicate> list = new ArrayList<>(propNameList.size());
        final int size = propNameList.size();
        String propName;
        for (int i = 0; i < size; i++) {
            propName = propNameList.get(i);
            if (_MetaBridge.ID.equals(propName)) {
                list.add(tableMeta.id().equal(valueList.get(i)));
            } else if (tableMeta.containField(propName)) {
                list.add(tableMeta.getField(propName).equal(valueList.get(i)));
            } else if (parentMeta != null && parentMeta.containField(propName)) {
                list.add(parentMeta.getField(propName).equal(valueList.get(i)));
            } else {
                throw new IllegalArgumentException(String.format("propName[%s] isn't prop of TableMeta[%s]"
                        , propName, tableMeta));
            }
        }
        return list;
    }
}
