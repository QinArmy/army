package io.army.dialect;

import io.army.criteria.FieldPredicate;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._StandardDelete;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.sharding._TableRouteUtils;
import io.army.stmt.SimpleStmt;

class StandardDeleteContext extends AbstractStandardDomainContext implements DeleteContext {

    static StandardDeleteContext build(_StandardDelete delete, Dialect dialect, Visible visible) {
        TableMeta<?> tableMeta = delete.table();
        String primarySuffix = _TableRouteUtils.singleDmlPrimaryRouteSuffix(delete, dialect);

        TablesContext tableContext = TablesContext.singleTable(delete, false, primarySuffix);
        return new StandardDeleteContext(dialect, visible
                , tableContext
                , tableMeta
                , tableMeta);
    }

    static StandardDeleteContext buildParent(_StandardDelete delete, Dialect dialect, final Visible visible) {
        ParentTableMeta<?> parentMeta = ((ChildTableMeta<?>) delete.table()).parentMeta();

        String primarySuffix = _TableRouteUtils.singleDmlPrimaryRouteSuffix(delete, dialect);

        TablesContext tableContext = TablesContext.singleTable(delete, true, primarySuffix);
        return new StandardDeleteContext(dialect, visible
                , tableContext
                , parentMeta
                , parentMeta);
    }

    static StandardDeleteContext buildChild(_StandardDelete delete, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) delete.table();
        String primarySuffix = _TableRouteUtils.singleDmlPrimaryRouteSuffix(delete, dialect);

        TablesContext tableContext = TablesContext.singleTable(delete, false, primarySuffix);
        return new DomainDeleteContext(dialect, visible
                , tableContext
                , childMeta
                , childMeta.parentMeta()
        );
    }

    @Override
    public SimpleStmt build() {
        return null;
    }

    private StandardDeleteContext(Dialect dialect, Visible visible, TablesContext tableContext
            , TableMeta<?> primaryTable, TableMeta<?> relationTable) {
        super(dialect, visible, tableContext, primaryTable, relationTable);
    }

    /*################################## blow private static inner class ##################################*/

    private static final class DomainDeleteContext extends StandardDeleteContext {

        private DomainDeleteContext(Dialect dialect, Visible visible, TablesContext tableContext
                , TableMeta<?> primaryTable, TableMeta<?> relationTable) {
            super(dialect, visible, tableContext, primaryTable, relationTable);
        }

        @Override
        public void appendField(String tableAlias, FieldMeta<?, ?> field) {
            appendDomainField(tableAlias, field);
        }

        @Override
        public void appendField(FieldMeta<?, ?> field) {
            appendDomainField(field);
        }

        @Override
        public void appendFieldPredicate(FieldPredicate predicate) {
            appendDomainFieldPredicate(predicate);
        }
    }


}
