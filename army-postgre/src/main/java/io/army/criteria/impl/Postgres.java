package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.postgre.PostgreInsert;
import io.army.criteria.postgre.PostgreQuery;
import io.army.dialect._Constant;
import io.army.util._StringUtils;

import java.util.Objects;

/**
 * <p>
 * This class is Postgre SQL syntax utils.
 * </p>
 *
 * @since 1.0
 */
public abstract class Postgres extends PostgreFuncSyntax {


    /**
     * private constructor
     */
    private Postgres() {
    }

    public interface SelectModifier extends Query.SelectModifier {

    }

    public static final class Modifier implements SQLWords {

        private final String spaceWords;


        /**
         * private constructor
         */
        private Modifier(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public String render() {
            return this.spaceWords;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(Postgres.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(Modifier.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.spaceWords.substring(1))
                    .toString();
        }


    }//Modifier

    public static final Modifier ALL = new Modifier(" ALL");

    public static final Modifier DISTINCT = new Modifier(" DISTINCT");

    public static final Modifier ONLY = new Modifier(" ONLY");


    public static PostgreInsert._PrimaryOptionSpec<Void> singleInsert() {
        return PostgreInserts.primaryInsert(null);
    }

    /**
     * @param criteria non-null criteria instance,java bean or {@link java.util.Map}.
     */
    public static <C> PostgreInsert._PrimaryOptionSpec<C> singleInsert(C criteria) {
        return PostgreInserts.primaryInsert(criteria);
    }


    public static PostgreQuery._WithCteSpec<Void, Select> query() {
        return PostgreQueries.primaryQuery(null);
    }

    /**
     * @param criteria non-null criteria instance,java bean or {@link java.util.Map}.
     */
    public static <C> PostgreQuery._WithCteSpec<C, Select> query(C criteria) {
        Objects.requireNonNull(criteria);
        return PostgreQueries.primaryQuery(criteria);
    }

    public static PostgreQuery._SubWithCteSpec<Void, SubQuery> subQuery() {
        return PostgreQueries.subQuery(null, ContextStack.peek(), SQLs::_thisSubQuery);
    }

    /**
     * @param criteria non-null criteria instance,java bean or {@link java.util.Map}.
     */
    public static <C> PostgreQuery._SubWithCteSpec<C, SubQuery> subQuery(C criteria) {
        return PostgreQueries.subQuery(criteria, ContextStack.peek(criteria), SQLs::_thisSubQuery);
    }

    public static PostgreQuery._SubWithCteSpec<Void, Expression> scalarSubQuery() {
        return PostgreQueries.subQuery(null, ContextStack.peek(), ScalarExpression::from);
    }

    /**
     * @param criteria non-null criteria instance,java bean or {@link java.util.Map}.
     */
    public static <C> PostgreQuery._SubWithCteSpec<C, Expression> scalarSubQuery(C criteria) {
        return PostgreQueries.subQuery(criteria, ContextStack.peek(criteria), ScalarExpression::from);
    }


}
