package io.army.criteria.impl;

import io.army.criteria.impl.inner._SortPart;
import io.army.dialect._SqlContext;

final class SortPartImpl implements _SortPart {

    private final _SortPart sortPart;

    private final boolean ascExp;

    SortPartImpl(_SortPart sortPart, boolean ascExp) {
        this.sortPart = sortPart;
        this.ascExp = ascExp;
    }


    @Override
    public void appendSortPart(_SqlContext context) {

        sortPart.appendSortPart(context);

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
