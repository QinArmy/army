package io.army.criteria.impl;

import io.army.criteria.SortPart;
import io.army.criteria._SqlContext;

final class SortPartImpl implements SortPart {

    private final SortPart sortPart;

    private final boolean ascExp;

    SortPartImpl(SortPart sortPart, boolean ascExp) {
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
