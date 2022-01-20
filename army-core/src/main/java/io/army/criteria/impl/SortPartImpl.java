package io.army.criteria.impl;

import io.army.criteria.SortPart;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._SortPart;
import io.army.dialect._SqlContext;

final class SortPartImpl implements _SortPart {

    private final SortPart sortPart;

    private final boolean ascExp;

    SortPartImpl(SortPart sortPart, boolean ascExp) {
        this.sortPart = sortPart;
        this.ascExp = ascExp;
    }


    @Override
    public void appendSortPart(final _SqlContext context) {
        ((_SelfDescribed) this.sortPart).appendSql(context);
        if (this.ascExp) {
            context.sqlBuilder()
                    .append(" ASC");
        } else {
            context.sqlBuilder()
                    .append(" DESC");
        }
    }

    @Override
    public String toString() {
        String text = sortPart.toString();
        if (ascExp) {
            text += " ASC";
        } else {
            text += " DESC";
        }
        return text;
    }

}
