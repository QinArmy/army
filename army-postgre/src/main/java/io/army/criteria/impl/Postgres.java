package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.postgre.PostgreInsert;
import io.army.criteria.postgre.PostgreQuery;
import io.army.dialect._Constant;
import io.army.util._StringUtils;

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


    public static PostgreInsert._PrimaryOptionSpec singleInsert() {
        return PostgreInserts.primaryInsert();
    }

    public static PostgreQuery._WithCteSpec<Select> query() {
        return PostgreQueries.primaryQuery();
    }


    public static PostgreQuery._SubWithCteSpec<SubQuery> subQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), SQLs::_identity);
    }


    public static PostgreQuery._SubWithCteSpec<Expression> scalarSubQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), ScalarExpression::from);
    }


}
