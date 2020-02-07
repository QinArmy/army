package io.army.criteria.impl;

import io.army.criteria.Predicate;
import io.army.criteria.SQLContext;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.util.ArrayUtils;

import java.util.Iterator;
import java.util.List;

final class OrtPredicate extends AbstractPredicate implements Predicate {

    private final Predicate orPredicate;

    private final List<Predicate> andPredicateList;

    OrtPredicate(Predicate orPredicate, List<Predicate> andPredicateList) {
        this.orPredicate = orPredicate;
        this.andPredicateList = ArrayUtils.asUnmodifiableList(andPredicateList);
    }


    @Override
    protected void afterSpace(SQLContext context) {
        StringBuilder builder = context.stringBuilder()
                .append("(");
        orPredicate.appendSQL(context);
        builder.append(" OR (");
        for (Iterator<Predicate> iterator = andPredicateList.iterator(); iterator.hasNext(); ) {
            iterator.next().appendSQL(context);
            if(iterator.hasNext()){
                builder.append(" AND ");
            }
        }
        builder.append(" ) )");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("( ");
        builder.append(orPredicate)
                .append(" OR (")
        ;
        for (Iterator<Predicate> iterator = andPredicateList.iterator(); iterator.hasNext(); ) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(" AND");
            }

        }
        builder.append(" )");
        return builder.toString();
    }
}
