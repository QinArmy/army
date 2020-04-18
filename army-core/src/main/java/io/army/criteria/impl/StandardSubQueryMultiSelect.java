package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.criteria.SubQuery;
import io.army.meta.TableMeta;

import java.util.Map;

final class StandardSubQueryMultiSelect<C> extends AbstractStandardSelect<C> implements SubQuerySelect<C> {


    private Map<String, Selection> selectionMap;

    StandardSubQueryMultiSelect(C criteria) {
        super(criteria);
    }


    @Override
    public C criteria() {
        return this.criteria;
    }

    @Override
    public String toString() {
        return "#SubQuery@" + System.identityHashCode(this);
    }

    @Override
    public Selection selection(String derivedFieldName) {
        if (this.selectionMap == null) {
            this.selectionMap = CriteriaUtils.createSelectionMap(selectPartList());
        }
        Selection s = this.selectionMap.get(derivedFieldName);
        if (s == null) {
            throw new CriteriaException(ErrorCode.NO_SELECTION
                    , "not found Selection[%s] from SubQuery.", derivedFieldName);
        }
        return s;
    }


    @Override
    public void appendSQL(SQLContext context) {
        context.dql().subQuery(this, context);
    }

    @Override
    public void clear() {
        super.clear();
        this.selectionMap = null;
    }

    /*################################## blow package template method ##################################*/

    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {

    }

    @Override
    void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        CriteriaContextHolder.getContext()
                .onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    void doAsSelect() {

    }

    @Override
    int tableWrapperCount() {
        return 6;
    }
}
