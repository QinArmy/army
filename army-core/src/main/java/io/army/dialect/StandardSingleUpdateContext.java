package io.army.dialect;

import io.army.criteria.SpecialPredicate;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardUpdate;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.wrapper.SimpleSQLWrapper;

class StandardSingleUpdateContext extends AbstractStandardDomainContext implements UpdateContext {

    static StandardSingleUpdateContext build(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        TableMeta<?> tableMeta = update.tableMeta();
        String primaryRouteSuffix = TableRouteUtils.singleDmlPrimaryRouteSuffix(update, dialect);

        TableContext tableContext = TableContext.singleTable(update, primaryRouteSuffix);
        return new StandardSingleUpdateContext(dialect, visible
                , tableContext
                , tableMeta
                , tableMeta
                , DMLUtils.hasVersionPredicate(update.predicateList()));
    }

    static StandardSingleUpdateContext buildParent(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();
        String primaryRouteSuffix = TableRouteUtils.singleDmlPrimaryRouteSuffix(update, dialect);

        TableContext tableContext = TableContext.singleTable(update, primaryRouteSuffix);
        return new DomainUpdateContext(dialect, visible
                , tableContext
                , childMeta.parentMeta()
                , childMeta
                , DMLUtils.hasVersionPredicate(update.predicateList())
        );
    }

    static StandardSingleUpdateContext buildChild(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();
        String primaryRouteSuffix = TableRouteUtils.singleDmlPrimaryRouteSuffix(update, dialect);

        TableContext tableContext = TableContext.singleTable(update, primaryRouteSuffix);
        return new DomainUpdateContext(dialect, visible
                , tableContext
                , childMeta
                , childMeta.parentMeta()
                , DMLUtils.hasVersionPredicate(update.predicateList())
        );
    }


    private final boolean hasVersion;

    private StandardSingleUpdateContext(Dialect dialect, Visible visible, TableContext tableContext
            , TableMeta<?> primaryTable, TableMeta<?> relationTable, boolean hasVersion) {
        super(dialect, visible, tableContext, primaryTable, relationTable);
        this.hasVersion = hasVersion;
    }

    @Override
    public final SimpleSQLWrapper build() {
        return SimpleSQLWrapper.build(this.sqlBuilder.toString(), this.paramList, this.hasVersion);
    }


    private static final class DomainUpdateContext extends StandardSingleUpdateContext {


        private DomainUpdateContext(Dialect dialect, Visible visible, TableContext tableContext
                , TableMeta<?> primaryTable, TableMeta<?> relationTable, boolean hasVersion) {
            super(dialect, visible, tableContext, primaryTable, relationTable, hasVersion);
        }

        @Override
        public final void appendTable(TableMeta<?> tableMeta, @Nullable String tableAlias) {
            appendDomainTable(tableMeta, tableAlias);
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
