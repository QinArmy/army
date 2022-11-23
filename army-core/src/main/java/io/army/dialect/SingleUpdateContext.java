package io.army.dialect;

import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

final class SingleUpdateContext extends SingleDmlContext implements _SingleUpdateContext {

    static SingleUpdateContext create(@Nullable _SqlContext outerContext, _SingleUpdate stmt
            , ArmyParser dialect, Visible visible) {
        return new SingleUpdateContext((StatementContext) outerContext, stmt, dialect, visible);
    }

    static SingleUpdateContext forParent(_SingleUpdate._ChildUpdate stmt, ArmyParser dialect, Visible visible) {
        return new SingleUpdateContext(null, stmt, dialect, visible);
    }

    static SingleUpdateContext forChild(_SingleUpdate._ChildUpdate stmt, SingleUpdateContext parentContext) {
        return new SingleUpdateContext(stmt, parentContext);
    }

    private final _UpdateContext parentContext;


    private List<TableField> conditionFieldList;

    private SingleUpdateContext(@Nullable StatementContext outerContext, _SingleUpdate stmt, ArmyParser dialect
            , Visible visible) {
        super(outerContext, stmt, dialect, visible);
        this.parentContext = null;
    }


    private SingleUpdateContext(_SingleUpdate stmt, SingleUpdateContext parentContext) {
        super(stmt, parentContext);
        this.parentContext = parentContext;
    }

    @Override
    public _UpdateContext parentContext() {
        return this.parentContext;
    }


    @Override
    public void appendConditionFields() {
        final List<TableField> conditionFieldList = this.conditionFieldList;
        if (conditionFieldList != null) {
            _DialectUtils.appendConditionFields(this, conditionFieldList);
        }

    }


    @Override
    void onAddConditionField(final TableField field) {
        List<TableField> list = this.conditionFieldList;
        if (list == null) {
            list = new ArrayList<>();
            this.conditionFieldList = list;
        }
        list.add(field);
    }


}
