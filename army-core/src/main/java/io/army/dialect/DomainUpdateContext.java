package io.army.dialect;

import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.List;

final class DomainUpdateContext extends DomainDmlStmtContext implements _SingleUpdateContext {

    static DomainUpdateContext forSingle(@Nullable _SqlContext outerContext, _DomainUpdate stmt, ArmyParser parser
            , Visible visible) {
        return new DomainUpdateContext((StatementContext) outerContext, stmt, parser, visible);
    }

    static DomainUpdateContext forChild(_DomainUpdate stmt, DomainUpdateContext parentContext) {
        return new DomainUpdateContext(stmt, parentContext);
    }

    final DomainUpdateContext parentContext;


    private List<FieldMeta<?>> conditionFieldList;

    private DomainUpdateContext(@Nullable StatementContext outerContext, _DomainUpdate stmt, ArmyParser parser,
                                Visible visible) {
        super(outerContext, stmt, parser, visible);
        this.parentContext = null;
    }

    private DomainUpdateContext(_DomainUpdate stmt, DomainUpdateContext parentContext) {
        super(stmt, parentContext);
        this.parentContext = parentContext;
    }

    @Override
    public _UpdateContext parentContext() {
        return this.parentContext;
    }

    @Override
    public void appendConditionFields() {
        final List<FieldMeta<?>> list = this.conditionFieldList;
        if (list != null) {
            _DialectUtils.appendConditionFields(this, list);
        }
    }

    @Override
    void onAddConditionField(final TableField field) {
        if (!(field instanceof FieldMeta)) {
            throw _Exceptions.castCriteriaApi();
        }
        List<FieldMeta<?>> list = this.conditionFieldList;
        if (list == null) {
            list = _Collections.arrayList();
            this.conditionFieldList = list;
        }
        list.add((FieldMeta<?>) field);
    }


}
