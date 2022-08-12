package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQLDqlValues;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.util._Exceptions;


/**
 * @see MySQLSimpleValues
 */
@SuppressWarnings("unchecked")
abstract class MySQLUnionValues<C, U extends RowSet.DqlValues> extends UnionRowSet<
        C,
        U,
        MySQLDqlValues._UnionOrderBySpec<C, U>,
        MySQLDqlValues._UnionLimitSpec<C, U>,
        MySQLDqlValues._UnionSpec<C, U>,
        Void> implements MySQLDqlValues, MySQLDqlValues._UnionOrderBySpec<C, U> {


    static <C, U extends RowSet.DqlValues> MySQLDqlValues._UnionOrderBySpec<C, U> bracket(final RowSet left) {
        final MySQLDqlValues._UnionOrderBySpec<C, ?> spec;
        if (left instanceof Values) {
            spec = new BracketValues<>((Values) left, CriteriaContexts.bracketContext(left));
        } else if (left instanceof SubValues) {
            spec = new BracketSubValues<>((SubValues) left, CriteriaContexts.bracketContext(left));
        } else {
            throw _Exceptions.unknownRowSetType(left);
        }
        return (MySQLDqlValues._UnionOrderBySpec<C, U>) spec;
    }

    static <C, U extends RowSet.DqlValues> MySQLDqlValues._UnionOrderBySpec<C, U> union(final U left
            , final UnionType unionType, final RowSet right) {
        switch (unionType) {
            case UNION:
            case UNION_ALL:
            case UNION_DISTINCT:
                break;
            default:
                throw CriteriaUtils.unsupportedUnionType(left, unionType);
        }

        CriteriaUtils.assertTypeMatch(left, right, MySQLUnionValues::unionRightItem);
        CriteriaUtils.assertSelectionSize(left, right);

        final MySQLDqlValues._UnionOrderBySpec<C, ?> spec;
        if (left instanceof Values) {
            spec = new UnionValues<>((Values) left, unionType, right, CriteriaContexts.unionContext(left, right));
        } else if (left instanceof SubValues) {
            spec = new UnionSubValues<>((SubValues) left, unionType, right, CriteriaContexts.unionContext(left, right));
        } else {
            throw _Exceptions.unknownRowSetType(left);
        }
        return (MySQLDqlValues._UnionOrderBySpec<C, U>) spec;
    }

    static <C, U extends RowSet.DqlValues> MySQLDqlValues._UnionOrderBySpec<C, U> noActionValues(final RowSet rowSet) {
        final MySQLDqlValues._UnionOrderBySpec<C, ?> spec;
        if (rowSet instanceof Values) {
            spec = new NoActionValues<>((Values) rowSet, CriteriaContexts.noActionContext(rowSet));
        } else if (rowSet instanceof SubValues) {
            spec = new NoActionSubValues<>((SubValues) rowSet, CriteriaContexts.noActionContext(rowSet));
        } else {
            throw _Exceptions.unknownRowSetType(rowSet);
        }
        return (MySQLDqlValues._UnionOrderBySpec<C, U>) spec;
    }

    @Nullable
    private static String unionRightItem(final RowSet right) {
        final String message;
        if (right instanceof Select || right instanceof Values) {
            if (right instanceof MySQLQuery || right instanceof StandardQuery || right instanceof MySQLDqlValues) {
                message = null;
            } else {
                message = String.format("union right item isn't MySQL %s or %s."
                        , Select.class.getName(), Values.class.getName());
            }
        } else if (right instanceof SubQuery || right instanceof SubValues) {
            if (right instanceof MySQLQuery || right instanceof StandardQuery || right instanceof MySQLDqlValues) {
                message = null;
            } else {
                message = String.format("union right item isn't MySQL %s or %s."
                        , SubQuery.class.getName(), SubValues.class.getName());
            }
        } else {
            message = "unknown union right item";
        }
        return message;
    }


    private MySQLUnionValues(U left, CriteriaContext criteriaContext) {
        super(left, criteriaContext);
    }

    @Override
    public final U asValues() {
        return this.asQuery();
    }

    @Override
    final MySQLDqlValues._UnionOrderBySpec<C, U> createBracketQuery(RowSet rowSet) {
        return bracket(rowSet);
    }

    @Override
    final MySQLDqlValues._UnionOrderBySpec<C, U> getNoActionUnionRowSet(RowSet rowSet) {
        return noActionValues(rowSet);
    }

    @Override
    final MySQLDqlValues._UnionOrderBySpec<C, U> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return union((U) left, unionType, right);
    }

    @Override
    final Void asUnionAndRowSet(UnionType unionType) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }



    @Override
    public final String toString() {
        final String s;
        if (this instanceof Values && this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }

    private static final class BracketValues<C> extends MySQLUnionValues<C, Values>
            implements Values, BracketRowSet {

        private BracketValues(Values left, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
        }

    }//BracketValues

    private static final class BracketSubValues<C> extends MySQLUnionValues<C, SubValues>
            implements SubValues, BracketRowSet {

        private BracketSubValues(SubValues left, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
        }

    }//BracketSubValues


    private static abstract class UnionDqlValues<C, U extends DqlValues> extends MySQLUnionValues<C, U>
            implements RowSetWithUnion {

        private final UnionType unionType;

        private final RowSet right;

        private UnionDqlValues(U left, UnionType unionType, RowSet right, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public final UnionType unionType() {
            return this.unionType;
        }

        @Override
        public final RowSet rightRowSet() {
            return this.right;
        }

    }//UnionDqlValues


    private static final class UnionValues<C> extends UnionDqlValues<C, Values>
            implements Values {

        private UnionValues(Values left, UnionType unionType, RowSet right, CriteriaContext criteriaContext) {
            super(left, unionType, right, criteriaContext);
        }

    }//UnionValues

    private static final class UnionSubValues<C> extends UnionDqlValues<C, SubValues>
            implements SubValues {

        private UnionSubValues(SubValues left, UnionType unionType, RowSet right, CriteriaContext criteriaContext) {
            super(left, unionType, right, criteriaContext);
        }

    }//UnionSubValues


    private static final class NoActionValues<C> extends MySQLUnionValues<C, Values>
            implements Values, NoActionRowSet {

        private NoActionValues(Values left, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
        }
    }//NoActionValues

    private static final class NoActionSubValues<C> extends MySQLUnionValues<C, SubValues>
            implements SubValues, NoActionRowSet {

        private NoActionSubValues(SubValues left, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
        }

    }//NoActionValues


}
