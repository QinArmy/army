package io.army.util;

import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;

import static io.army.criteria.impl.SQLs.AS;

public abstract class Criteria {

    protected Criteria() {
        throw new UnsupportedOperationException();
    }


    public static <T> Select createSelectDomainById(final TableMeta<T> table, final Object id) {
        final Select stmt;
        if (table instanceof ChildTableMeta) {
            final ChildTableMeta<?> child = (ChildTableMeta<?>) table;
            final ParentTableMeta<?> parent = child.parentMeta();

            stmt = SQLs.query()
                    .select(SQLs.childGroup(child, "c", "p"))
                    .from(child, AS, "c") // small table first
                    .join(parent,AS, "p").on(child.id().equal(parent.id()))
                    .where(child.id().equal(SQLs::param,id))
                    .asQuery();
        } else {
            stmt = SQLs.query()
                    .select(SQLs.group(table, "t"))
                    .from(table,AS, "t")
                    .where(table.id().equal(SQLs::param,id))
                    .asQuery();
        }
        return stmt;
    }

    public static <T> Select createSelectDomainByUnique(final TableMeta<T> table
            , final UniqueFieldMeta<? super T> field, final Object value) {
        final Select stmt;
        if (table instanceof ChildTableMeta) {
            final ChildTableMeta<T> child = (ChildTableMeta<T>) table;
            final ParentTableMeta<?> parent = child.parentMeta();
            stmt = SQLs.query()
                    .select(SQLs.childGroup(child, "c", "p"))
                    .from(child,AS, "c")
                    .join(parent,AS, "p").on(child.id().equal(parent.id()))
                    .where(field.equal(SQLs::param,value))
                    .limit(SQLs::param,2)
                    .asQuery();
        } else {
            stmt = SQLs.query()
                    .select(SQLs.group(table, "t"))
                    .from(table, AS,"t")
                    .where(field.equal(SQLs::param,value))
                    .limit(SQLs::param,2)
                    .asQuery();
        }
        return stmt;
    }


}
