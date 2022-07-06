package io.army.criteria.impl;

import io.army.criteria.RowSet;
import io.army.criteria.Visible;
import io.army.criteria.mysql.MySQLDqlValues;
import io.army.dialect.Dialect;

abstract class MySQLUnionValues<C, U extends RowSet.DqlValues> extends UnionRowSet<
        C,
        U,
        MySQLDqlValues._UnionOrderBySpec<C, U>,
        MySQLDqlValues._UnionLimitSpec<C, U>,
        MySQLDqlValues._UnionSpec<C, U>,
        Void> implements RowSet.DqlValues, MySQLDqlValues._UnionOrderBySpec<C, U> {


    static <C, U extends RowSet.DqlValues> MySQLDqlValues._UnionOrderBySpec<C, U> bracket(final RowSet.DqlValues values) {

        return null;
    }

    static <C, U extends RowSet.DqlValues> MySQLDqlValues._UnionOrderBySpec<C, U> union(final U left
            , final UnionType unionType, final RowSet.DqlValues right) {
        return null;
    }

    static <C, U extends RowSet.DqlValues> MySQLDqlValues._UnionOrderBySpec<C, U> noActionValues(final RowSet.DqlValues values) {
        return null;
    }

    private MySQLUnionValues(U left, CriteriaContext criteriaContext) {
        super(left, criteriaContext);
    }

    @Override
    public U asValues() {
        return (U) this;
    }

    @Override
    MySQLDqlValues._UnionOrderBySpec<C, U> createBracketQuery(RowSet rowSet) {
        return null;
    }

    @Override
    MySQLDqlValues._UnionOrderBySpec<C, U> getNoActionUnionRowSet(RowSet rowSet) {
        return bracket((DqlValues) rowSet);
    }

    @Override
    MySQLDqlValues._UnionOrderBySpec<C, U> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return union((U) left, unionType, (DqlValues) right);
    }

    @Override
    Void asUnionAndRowSet(UnionType unionType) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }


    @Override
    public final String toString() {
        final String s;
        if (this instanceof MySQLDqlValues && this.isPrepared()) {
            s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }


}
