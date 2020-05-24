package io.army.dialect;

import io.army.criteria.SpecialPredicate;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.wrapper.SimpleSQLWrapper;

class StandardUpdateContext extends AbstractStandardDomainContext implements UpdateContext {

    static StandardUpdateContext build(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        TableMeta<?> tableMeta = update.tableMeta();

        return new StandardUpdateContext(dialect, visible
                , TableContext.singleTable(tableMeta, update.tableAlias())
                , tableMeta
                , tableMeta
                , DMLUtils.hasVersionPredicate(update.predicateList()));
    }

    static StandardUpdateContext buildParent(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();

        return new DomainUpdateContext(dialect, visible
                , TableContext.singleTable(childMeta.parentMeta(), update.tableAlias())
                , childMeta.parentMeta()
                , childMeta
                , DMLUtils.hasVersionPredicate(update.predicateList())
        );
    }

    static StandardUpdateContext buildChild(InnerStandardUpdate update, Dialect dialect, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();

        return new DomainUpdateContext(dialect, visible
                , TableContext.singleTable(childMeta, update.tableAlias())
                , childMeta
                , childMeta.parentMeta()
                , DMLUtils.hasVersionPredicate(update.predicateList())
        );
    }


    private final boolean hasVersion;

    public StandardUpdateContext(Dialect dialect, Visible visible, TableContext tableContext
            , TableMeta<?> primaryTable, TableMeta<?> relationTable, boolean hasVersion) {
        super(dialect, visible, tableContext, primaryTable, relationTable);
        this.hasVersion = hasVersion;
    }

    @Override
    public final SimpleSQLWrapper build() {
        return SimpleSQLWrapper.build(this.sqlBuilder.toString(), this.paramList, this.hasVersion);
    }

    private static final class DomainUpdateContext extends StandardUpdateContext {

        public DomainUpdateContext(Dialect dialect, Visible visible, TableContext tableContext
                , TableMeta<?> primaryTable, TableMeta<?> relationTable, boolean hasVersion) {
            super(dialect, visible, tableContext, primaryTable, relationTable, hasVersion);
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
