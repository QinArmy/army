package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.SQLContext;
import io.army.util.ArrayUtils;

import java.util.Iterator;
import java.util.List;

final class OrtPredicate extends AbstractPredicate implements IPredicate {

    private final IPredicate orIPredicate;

    private final List<IPredicate> andIPredicateList;

    OrtPredicate(IPredicate orIPredicate, List<IPredicate> andIPredicateList) {
        this.orIPredicate = orIPredicate;
        this.andIPredicateList = ArrayUtils.asUnmodifiableList(andIPredicateList);
    }


    @Override
    protected void afterSpace(SQLContext context) {
        StringBuilder builder = context.sqlBuilder()
                .append("(");
        orIPredicate.appendSQL(context);
        builder.append(" OR (");
        for (Iterator<IPredicate> iterator = andIPredicateList.iterator(); iterator.hasNext(); ) {
            iterator.next().appendSQL(context);
            if(iterator.hasNext()){
                builder.append(" AND");
            }
        }
        builder.append(" ) )");
    }

    @Override
    public String beforeAs() {
        StringBuilder builder = new StringBuilder("( ");
        builder.append(orIPredicate)
                .append(" OR (")
        ;
        for (Iterator<IPredicate> iterator = andIPredicateList.iterator(); iterator.hasNext(); ) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(" AND");
            }

        }
        builder.append(" )");
        return builder.toString();
    }
}
