package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.SortPart;

final class SortPartImpl implements SortPart {

    private final SortPart sortPart;

    private final boolean ascExp;

    SortPartImpl(SortPart sortPart, boolean ascExp) {
        this.sortPart = sortPart;
        this.ascExp = ascExp;
    }


    @Override
    public void appendSortPart(SQLContext context) {

        sortPart.appendSortPart(context);

        if (this.ascExp) {
            context.stringBuilder()
                    .append(" ASC");
        } else {
            context.stringBuilder()
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
