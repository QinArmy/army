package io.army.dialect;

import io.army.criteria.SpecialPredicate;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardUpdate;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.wrapper.SimpleSQLWrapper;

class SingleUpdateContext extends AbstractStandardDomainContext implements UpdateContext {

    static SingleUpdateContext build(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        TableMeta<?> tableMeta = update.tableMeta();
        String primaryRouteSuffix = TableRouteUtils.singleDmlPrimaryRouteSuffix(update, dialect);

        TableContext tableContext = TableContext.singleTable(tableMeta, update.tableAlias()
                , update.tableIndex(),primaryRouteSuffix );
        return new SingleUpdateContext(dialect, visible
                , tableContext
                , tableMeta
                , tableMeta
                , DMLUtils.hasVersionPredicate(update.predicateList()));
    }

    static SingleUpdateContext buildParent(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();
        String primaryRouteSuffix = TableRouteUtils.singleDmlPrimaryRouteSuffix(update, dialect);

        TableContext tableContext = TableContext.singleTable(childMeta.parentMeta(), update.tableAlias()
                , update.tableIndex(),primaryRouteSuffix );
        return new DomainUpdateContext(dialect, visible
                , tableContext
                , childMeta.parentMeta()
                , childMeta
                , DMLUtils.hasVersionPredicate(update.predicateList())
        );
    }

    static SingleUpdateContext buildChild(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();
        String primaryRouteSuffix = TableRouteUtils.singleDmlPrimaryRouteSuffix(update, dialect);

        TableContext tableContext = TableContext.singleTable(childMeta, update.tableAlias()
                , update.tableIndex(),primaryRouteSuffix );
        return new DomainUpdateContext(dialect, visible
                , tableContext
                , childMeta
                , childMeta.parentMeta()
                , DMLUtils.hasVersionPredicate(update.predicateList())
        );
    }


    private final boolean hasVersion;

    private SingleUpdateContext(Dialect dialect, Visible visible, TableContext tableContext
            , TableMeta<?> primaryTable, TableMeta<?> relationTable, boolean hasVersion) {
        super(dialect, visible, tableContext, primaryTable, relationTable);
        this.hasVersion = hasVersion;
    }

    @Override
    public final SimpleSQLWrapper build() {
        return SimpleSQLWrapper.build(this.sqlBuilder.toString(), this.paramList, this.hasVersion);
    }

    @Override
    protected void doAppendTableSuffix(TableMeta<?> tableMeta, @Nullable String tableAlias, StringBuilder builder) {

    }

    private static final class DomainUpdateContext extends SingleUpdateContext {


        private DomainUpdateContext(Dialect dialect, Visible visible, TableContext tableContext
                , TableMeta<?> primaryTable, TableMeta<?> relationTable, boolean hasVersion) {
            super(dialect, visible, tableContext, primaryTable, relationTable, hasVersion);
        }

        @Override
        public final void appendTable(TableMeta<?> tableMeta, @Nullable String tableAlias) {
            appendDomainTable(tableMeta,tableAlias);
        }


        @Override
        public final void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
            appendDomainField(tableAlias, fieldMeta);
        }

        @Override
        public final void appendField(FieldMeta<?, ?> fieldMeta) {
            appendDomainField(fieldMeta);
        }

        @Override
        public final void appendFieldPredicate(SpecialPredicate predicate) {
            appendDomainFieldPredicate(predicate);
        }

    }


}
