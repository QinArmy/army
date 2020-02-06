package io.army.criteria.impl;

import io.army.criteria.Predicate;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.util.ArrayUtils;

import java.util.List;

final class OrtPredicate extends AbstractPredicate implements Predicate {

    private final Predicate orPredicate;

    private final List<Predicate> andPredicateList;

    OrtPredicate(Predicate orPredicate, List<Predicate> andPredicateList) {
        this.orPredicate = orPredicate;
        this.andPredicateList = ArrayUtils.asUnmodifiableList(andPredicateList);
    }


    @Override
    protected void appendSQLBeforeWhitespace(SQL sql, StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        builder.append("(");
        orPredicate.appendSQL(sql,builder, paramWrapperList);
        builder.append(" OR (");
        for (Predicate predicate : andPredicateList) {
            predicate.appendSQL(sql,builder, paramWrapperList);
            builder.append(" AND ");
        }
        builder.append("))");
    }

}
