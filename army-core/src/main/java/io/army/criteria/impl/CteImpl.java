package io.army.criteria.impl;

import io.army.criteria.DerivedTable;
import io.army.criteria.Selection;
import io.army.criteria.SubStatement;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.mysql.inner._SelectionMap;

import java.util.Collections;
import java.util.List;

public final class CteImpl implements _Cte {

    public final String name;

    public final List<String> columnNameList;

    public final SubStatement subStatement;

    private final _SelectionMap selectionMap;

    public CteImpl(String name, SubStatement subStatement) {
        this(name, Collections.emptyList(), subStatement);
    }

    /**
     * @param columnNameList unmodified list
     */
    public CteImpl(String name, List<String> columnNameList, SubStatement subStatement) {
        this.name = name;
        this.columnNameList = columnNameList;
        this.subStatement = subStatement;

        if (!(subStatement instanceof DerivedTable)) {
            throw CriteriaUtils.subDmlNoReturningClause(name);
        } else if (this.columnNameList.size() == 0) {
            this.selectionMap = (_DerivedTable) subStatement;
        } else {
            this.selectionMap = CriteriaUtils.createAliasSelectionMap(this.columnNameList,
                    ((_DerivedTable) subStatement).refAllSelection(), name);
        }

    }

    @Override
    public String name() {
        return this.name;
    }


    @Override
    public List<String> columnAliasList() {
        return this.columnNameList;
    }

    @Override
    public SubStatement subStatement() {
        return this.subStatement;
    }

    @Override
    public List<? extends Selection> refAllSelection() {
        return this.selectionMap.refAllSelection();
    }


    @Override
    public Selection refSelection(final String name) {
        return this.selectionMap.refSelection(name);
    }


}//CteImpl
