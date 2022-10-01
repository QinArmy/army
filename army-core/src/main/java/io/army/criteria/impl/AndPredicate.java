package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.IPredicate;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class AndPredicate extends OperationPredicate {


    static AndPredicate create(OperationPredicate left, @Nullable IPredicate right) {
        assert right != null;
        return new AndPredicate(left, Collections.singletonList((OperationPredicate) right));
    }

    static AndPredicate create(OperationPredicate left, List<IPredicate> rights) {
        final int size = rights.size();
        if (size == 0) {
            throw new CriteriaException("and method list must be not empty.");
        }
        final List<OperationPredicate> rightList = new ArrayList<>(size);
        for (IPredicate p : rights) {
            rightList.add((OperationPredicate) p);
        }
        return new AndPredicate(left, _CollectionUtils.unmodifiableList(rightList));
    }


    private final OperationPredicate left;

    private final List<OperationPredicate> rightList;

    private AndPredicate(OperationPredicate left, List<OperationPredicate> rightList) {
        this.left = left;
        this.rightList = rightList;
    }


    @Override
    public void appendSql(final _SqlContext context) {
        this.left.appendSql(context);

        final StringBuilder builder = context.sqlBuilder();

        for (OperationPredicate p : this.rightList) {
            builder.append(_Constant.SPACE_AND);
            p.appendSql(context);
        }

    }


    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.left);

        for (OperationPredicate p : this.rightList) {
            builder.append(_Constant.SPACE_AND);
            builder.append(p);
        }
        return builder.toString();
    }


}
