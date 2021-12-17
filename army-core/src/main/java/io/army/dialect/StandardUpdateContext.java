package io.army.dialect;

import io.army.criteria.FieldPredicate;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sharding._TableRouteUtils;
import io.army.stmt.SimpleStmt;

class StandardUpdateContext extends AbstractStandardDomainContext implements UpdateContext {

    static StandardUpdateContext build(_SingleUpdate update, Dialect dialect, final Visible visible) {
        TableMeta<?> tableMeta = update.table();
        String primaryRouteSuffix = _TableRouteUtils.singleDmlPrimaryRouteSuffix(update, dialect);

        TablesContext tableContext = TablesContext.singleTable(update, false, primaryRouteSuffix);
        return new StandardUpdateContext(dialect, visible
                , tableContext
                , tableMeta
                , tableMeta
                , DmlUtils.hasVersionPredicate(update.predicateList()));
    }

    static StandardUpdateContext buildParent(_SingleUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.table();
        String primaryRouteSuffix = _TableRouteUtils.singleDmlPrimaryRouteSuffix(update, dialect);

        TablesContext tableContext = TablesContext.singleTable(update, true, primaryRouteSuffix);
        return new DomainUpdateContext(dialect, visible
                , tableContext
                , childMeta.parentMeta()
                , childMeta
                , DmlUtils.hasVersionPredicate(update.predicateList())
        );
    }

    static StandardUpdateContext buildChild(_SingleUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.table();
        String primaryRouteSuffix = _TableRouteUtils.singleDmlPrimaryRouteSuffix(update, dialect);

        TablesContext tableContext = TablesContext.singleTable(update, false, primaryRouteSuffix);
        return new DomainUpdateContext(dialect, visible
                , tableContext
                , childMeta
                , childMeta.parentMeta()
                , DmlUtils.hasVersionPredicate(update.predicateList())
        );
    }


    private final boolean hasVersion;

    private StandardUpdateContext(Dialect dialect, Visible visible, TablesContext tableContext
            , TableMeta<?> primaryTable, TableMeta<?> relationTable, boolean hasVersion) {
        super(dialect, visible, tableContext, primaryTable, relationTable);
        this.hasVersion = hasVersion;
    }

    @Override
    public final SimpleStmt build() {
        return null;
    }


    private static final class DomainUpdateContext extends StandardUpdateContext {


        private DomainUpdateContext(Dialect dialect, Visible visible, TablesContext tableContext
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
        public final void appendFieldPredicate(FieldPredicate predicate) {
            appendDomainFieldPredicate(predicate);
        }

    }


}
