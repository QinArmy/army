package io.army.dialect;

import io.army.criteria.SpecialPredicate;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardDelete;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

class StandardDeleteContext extends AbstractStandardDomainContext implements DeleteContext {

    static StandardDeleteContext build(InnerStandardDelete delete, Dialect dialect, Visible visible) {
        TableMeta<?> tableMeta = delete.tableMeta();

        TableContext tableContext = TableContext.singleTable(delete, false, )
        return new StandardDeleteContext(dialect, visible
                , TableContext.singleTable(tableMeta, false, delete.tableAlias())
                , tableMeta
                , tableMeta);
    }

    static StandardDeleteContext buildParent(InnerStandardDelete delete, Dialect dialect, final Visible visible) {
        ParentTableMeta<?> parentMeta = ((ChildTableMeta<?>) delete.tableMeta()).parentMeta();

        return new StandardDeleteContext(dialect, visible
                , TableContext.singleTable(parentMeta, true, delete.tableAlias())
                , parentMeta
                , parentMeta);
    }

    static StandardDeleteContext buildChild(InnerStandardDelete delete, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) delete.tableMeta();

        return new DomainDeleteContext(dialect, visible
                , TableContext.singleTable(childMeta, false, delete.tableAlias())
                , childMeta
                , childMeta.parentMeta()
        );
    }

    private StandardDeleteContext(Dialect dialect, Visible visible, TableContext tableContext
            , TableMeta<?> primaryTable, TableMeta<?> relationTable) {
        super(dialect, visible, tableContext, primaryTable, relationTable);
    }

    /*################################## blow private static inner class ##################################*/

    private static final class DomainDeleteContext extends StandardDeleteContext {

        private DomainDeleteContext(Dialect dialect, Visible visible, TableContext tableContext
                , TableMeta<?> primaryTable, TableMeta<?> relationTable) {
            super(dialect, visible, tableContext, primaryTable, relationTable);
        }

        @Override
        public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
            appendDomainField(tableAlias, fieldMeta);
        }

        @Override
        public void appendField(FieldMeta<?, ?> fieldMeta) {
            appendDomainField(fieldMeta);
        }

        @Override
        public void appendFieldPredicate(SpecialPredicate predicate) {
            appendDomainFieldPredicate(predicate);
        }
    }


}
