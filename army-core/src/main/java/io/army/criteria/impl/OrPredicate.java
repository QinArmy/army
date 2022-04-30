package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect.Constant;
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
                .append(Constant.SPACE_LEFT_BRACKET);

        final OperationPredicate left = this.left;
        if (left instanceof AndPredicate) {
            builder.append(Constant.SPACE_LEFT_BRACKET); //left inner left bracket
        }
        left.appendSql(context);
        if (left instanceof AndPredicate) {
            builder.append(Constant.SPACE_RIGHT_BRACKET); //left inner left bracket
        }
        for (OperationPredicate right : this.rights) {
            builder.append(Constant.SPACE_OR);

            if (right instanceof AndPredicate) {
                builder.append(Constant.SPACE_LEFT_BRACKET); // inner left bracket
            }

            right.appendSql(context);

            if (right instanceof AndPredicate) {
                builder.append(Constant.SPACE_RIGHT_BRACKET);// inner right bracket
            }
        }
        builder.append(Constant.SPACE_RIGHT_BRACKET);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(128)
                .append(Constant.SPACE_LEFT_BRACKET);

        final OperationPredicate left = this.left;
        if (left instanceof AndPredicate) {
            builder.append(Constant.SPACE_LEFT_BRACKET); //left inner left bracket
        }
        builder.append(left);
        if (left instanceof AndPredicate) {
            builder.append(Constant.SPACE_RIGHT_BRACKET); //left inner left bracket
        }

        for (OperationPredicate right : this.rights) {
            builder.append(Constant.SPACE_OR);

            if (right instanceof AndPredicate) {
                builder.append(Constant.SPACE_LEFT_BRACKET);
            }
            builder.append(right);

            if (right instanceof AndPredicate) {
                builder.append(Constant.SPACE_RIGHT_BRACKET);
            }
        }

        return builder.append(Constant.SPACE_RIGHT_BRACKET)
                .toString();
    }


}
