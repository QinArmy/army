package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.SubQuery;

final class ExistsPredicate extends AbstractPredicate {

    private final boolean not;

    private final SubQuery subQuery;

     ExistsPredicate(SubQuery subQuery) {
        this(false,subQuery);
    }

    ExistsPredicate(boolean not, SubQuery subQuery) {
        this.not = not;
        this.subQuery = subQuery;
    }

    @Override
    protected void afterSpace(SQLContext context) {
         StringBuilder builder = context.stringBuilder();
        if(not){
            builder.append("NOT ");
        }
        builder.append("EXISTS");
        subQuery.appendSQL(context);
    }

    @Override
    protected String beforeAs() {
        return "EXISTS " + subQuery;
    }


}
