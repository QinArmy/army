package io.army.dialect;

import io.army.criteria.SetLeftItem;
import io.army.criteria.SetRightItem;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;
import io.army.util._Exceptions;

import java.util.List;

final class SingleUpdateContext extends _BaseSqlContext implements _SingleUpdateContext {


    private final TableMeta<?> table;

    private final List<? extends SetLeftItem> leftItemList;

    private final List<? extends SetRightItem> rightItemList;


    private SingleUpdateContext(_SingleUpdate update, _Dialect dialect, Visible visible) {
        super(dialect, visible);
        final TableMeta<?> table;
        this.table = table = update.table();
        final List<? extends SetLeftItem> leftItemList;
        leftItemList = update.fieldList();
        final boolean supportRowLeftItem = dialect().supportRowLeftItem();
        TableMeta<?> belongOf;
        for (SetLeftItem leftItem : leftItemList) {
            if (!(leftItem instanceof TableField)) {
                if (!supportRowLeftItem) {
                    throw _Exceptions.dontSupportRowLeftItem(dialect.dialect());
                }
                continue;
            }
            belongOf = ((TableField<?>) leftItem).tableMeta();
            if (belongOf instanceof ChildTableMeta) {
                throw _Exceptions.singleUpdateChildField((TableField<?>) leftItem, dialect.dialect());
            }
            if (table instanceof ChildTableMeta) {

            } else if (belongOf != table) {
                throw _Exceptions.unknownColumn((TableField<?>) leftItem);
            }
        }
        this.leftItemList = leftItemList;
        this.rightItemList = update.valueExpList();
    }

    @Override
    public TableMeta<?> table() {
        return null;
    }

    @Override
    public String tableAlias() {
        return null;
    }

    @Override
    public String safeTableAlias() {
        return null;
    }

    @Override
    public String safeTableAlias(TableField<?> field) {
        return null;
    }

    @Override
    public boolean hasSelfJoint() {
        return false;
    }

    @Override
    public List<? extends SetLeftItem> targetParts() {
        return null;
    }

    @Override
    public List<? extends SetRightItem> valueParts() {
        return null;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {

    }

    @Override
    public void appendField(FieldMeta<?> field) {

    }

    @Override
    public SimpleStmt build() {
        return null;
    }


}
