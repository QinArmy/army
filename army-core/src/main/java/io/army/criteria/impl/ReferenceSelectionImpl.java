package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

final class ReferenceSelectionImpl<E> extends AbstractExpression<E> implements ReferenceSelection {

    private final String tableAlias;

    private final String derivedFieldName;

    private Selection selection;

    ReferenceSelectionImpl(String tableAlias, String derivedFieldName) {
        this.tableAlias = tableAlias;
        this.derivedFieldName = derivedFieldName;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        Assert.state(selection != null, "selectionList is null,criteria error.");

        SQL sql = context.dml();
        context.stringBuilder()
                .append(sql.quoteIfNeed(tableAlias))
                .append(".")
                .append(sql.quoteIfNeed(derivedFieldName))
               ;
    }


    @Override
    public final String alias() {
        return alias;
    }

    @Override
    public MappingType mappingType() {
        Assert.state(selection != null, "selectionList is null,criteria error.");
        return selection.mappingType();
    }

    @Override
    public void selection(Selection selection) {
        Assert.state(this.selection == null, "selectionList only set once.");
        Assert.isTrue(this.derivedFieldName.equals(selection.alias()),()->String.format
                ("selection[%s] alias and %s.%s not match.",selection,this.tableAlias,this.derivedFieldName));
        this.selection = selection;
    }

    @Override
    public String toString() {
        return tableAlias + "." + derivedFieldName;
    }
}
