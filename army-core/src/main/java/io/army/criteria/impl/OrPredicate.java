package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class OrPredicate extends OperationPredicate {

    static _Predicate create(OperationPredicate left, IPredicate right) {
        return new OrPredicate(left, Collections.singletonList((OperationPredicate) right));
    }


    static _Predicate create(final OperationPredicate left, final List<IPredicate> rights) {
        final int size = rights.size();
        final _Predicate result;
        switch (size) {
            case 0:
                result = left;
                break;
            case 1:
                result = new OrPredicate(left, Collections.singletonList((OperationPredicate) rights.get(0)));
                break;
            default: {
                final List<OperationPredicate> predicateList = new ArrayList<>(size);
                for (IPredicate right : rights) {
                    predicateList.add((OperationPredicate) right);
                }
                result = new OrPredicate(left, Collections.unmodifiableList(predicateList));
            }
        }
        return result;
    }


    private final OperationPredicate left;

    private final List<OperationPredicate> rights;

    private OrPredicate(OperationPredicate left, List<OperationPredicate> predicateList) {
        this.left = left;
        this.rights = predicateList;
    }


    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(_Constant.SPACE_LEFT_PAREN);

        final OperationPredicate left = this.left;
        if (left instanceof AndPredicate) {
            builder.append(_Constant.SPACE_LEFT_PAREN); //left inner left bracket
        }
        left.appendSql(context);
        if (left instanceof AndPredicate) {
            builder.append(_Constant.SPACE_RIGHT_PAREN); //left inner left bracket
        }
        for (OperationPredicate right : this.rights) {
            builder.append(_Constant.SPACE_OR);

            if (right instanceof AndPredicate) {
                builder.append(_Constant.SPACE_LEFT_PAREN); // inner left bracket
            }

            right.appendSql(context);

            if (right instanceof AndPredicate) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);// inner right bracket
            }
        }
        builder.append(_Constant.SPACE_RIGHT_PAREN);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(128)
                .append(_Constant.SPACE_LEFT_PAREN);

        final OperationPredicate left = this.left;
        if (left instanceof AndPredicate) {
            builder.append(_Constant.SPACE_LEFT_PAREN); //left inner left bracket
        }
        builder.append(left);
        if (left instanceof AndPredicate) {
            builder.append(_Constant.SPACE_RIGHT_PAREN); //left inner left bracket
        }

        for (OperationPredicate right : this.rights) {
            builder.append(_Constant.SPACE_OR);

            if (right instanceof AndPredicate) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            builder.append(right);

            if (right instanceof AndPredicate) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
        }

        return builder.append(_Constant.SPACE_RIGHT_PAREN)
                .toString();
    }


}
