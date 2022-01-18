package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class OrPredicate extends OperationPredicate {

    static _Predicate create(_Predicate left, IPredicate right) {
        return new OrPredicate(left, Collections.singletonList((_Predicate) right));
    }

    static _Predicate create(_Predicate left, IPredicate right1, IPredicate right2) {
        return new OrPredicate(left, ArrayUtils.asUnmodifiableList((_Predicate) right1, (_Predicate) right2));
    }


    static _Predicate create(final _Predicate left, final List<IPredicate> rights) {
        final int size = rights.size();
        final _Predicate result;
        switch (size) {
            case 0:
                result = left;
                break;
            case 1:
                result = new OrPredicate(left, Collections.singletonList((_Predicate) rights.get(0)));
                break;
            default: {
                final List<_Predicate> predicateList = new ArrayList<>(size);
                for (IPredicate right : rights) {
                    predicateList.add((_Predicate) right);
                }
                result = new OrPredicate(left, predicateList);
            }
        }
        return result;
    }


    private final _Predicate left;

    private final List<_Predicate> rights;

    private OrPredicate(_Predicate left, List<_Predicate> predicateList) {
        this.left = left;
        this.rights = predicateList;
    }


    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE)
                .append(Constant.LEFT_BRACKET);

        this.left.appendSql(context);

        builder.append(" OR");

        final List<_Predicate> rights = this.rights;
        final int size = rights.size();
        if (size == 1) {
            rights.get(0).appendSql(context);
        } else {
            builder.append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(" AND");
                }
                rights.get(i).appendSql(context);
            }
            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }

        builder.append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(128)
                .append(Constant.SPACE)
                .append(Constant.LEFT_BRACKET);

        builder.append(this.left)
                .append(" OR");

        final List<_Predicate> rights = this.rights;
        final int size = rights.size();
        if (size == 1) {
            builder.append(rights.get(0));
        } else {
            builder.append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(" AND");
                }
                builder.append(rights.get(i));
            }
            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }

        builder.append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);

        return builder.toString();
    }

    @Override
    public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        boolean contains = this.left.containsField(fieldMetas);
        if (!contains) {
            for (_Predicate predicate : this.rights) {
                if (predicate.containsField(fieldMetas)) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    @Override
    public boolean containsFieldOf(TableMeta<?> tableMeta) {
        boolean contains = this.left.containsFieldOf(tableMeta);
        if (!contains) {
            for (_Predicate predicate : this.rights) {
                if (predicate.containsFieldOf(tableMeta)) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    @Override
    public int containsFieldCount(TableMeta<?> tableMeta) {
        int count = this.left.containsFieldCount(tableMeta);
        for (_Predicate predicate : this.rights) {
            count += predicate.containsFieldCount(tableMeta);
        }
        return count;
    }

    @Override
    public boolean containsSubQuery() {
        boolean contains = this.left.containsSubQuery();
        if (!contains) {
            for (_Predicate predicate : this.rights) {
                if (predicate.containsSubQuery()) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }
}
