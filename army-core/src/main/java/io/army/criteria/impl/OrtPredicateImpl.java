package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.OrPredicate;
import io.army.criteria.SqlContext;
import io.army.dialect.SqlBuilder;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;

import java.util.Collection;
import java.util.List;

final class OrtPredicateImpl extends AbstractPredicate implements OrPredicate {

    private final IPredicate leftPredicate;

    private final List<IPredicate> rightPredicate;

    OrtPredicateImpl(IPredicate leftPredicate, List<IPredicate> rightPredicate) {
        this.leftPredicate = leftPredicate;
        this.rightPredicate = ArrayUtils.asUnmodifiableList(rightPredicate);
    }


    @Override
    public IPredicate leftPredicate() {
        return this.leftPredicate;
    }

    @Override
    public List<IPredicate> rightPredicate() {
        return this.rightPredicate;
    }

    @Override
    public void appendSQL(SqlContext context) {
        SqlBuilder builder = context.sqlBuilder()
                .append(" (");
        this.leftPredicate.appendSQL(context);
        builder.append(" OR");
        if (this.rightPredicate.size() > 1) {
            builder.append(" (");
        }
        int index = 0;
        for (IPredicate predicate : this.rightPredicate) {
            if (index > 0) {
                builder.append(" AND");
            }
            predicate.appendSQL(context);
            index++;
        }
        if (this.rightPredicate.size() > 1) {
            builder.append(" )");
        }
        builder.append(" )");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("(");
        builder.append(leftPredicate)
                .append(" OR ")
        ;
        if (rightPredicate.size() > 1) {
            builder.append(" (");
        }
        int index = 0;
        for (IPredicate predicate : rightPredicate) {
            if (index > 0) {
                builder.append(" AND");
            }
            builder.append(predicate);
            index++;
        }
        if (rightPredicate.size() > 1) {
            builder.append(" )");
        }
        builder.append(" )");
        return builder.toString();
    }

    @Override
    public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        boolean contains = this.leftPredicate.containsField(fieldMetas);
        if (!contains) {
            for (IPredicate predicate : this.rightPredicate) {
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
        boolean contains = this.leftPredicate.containsFieldOf(tableMeta);
        if (!contains) {
            for (IPredicate predicate : this.rightPredicate) {
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
        int count = this.leftPredicate.containsFieldCount(tableMeta);
        for (IPredicate predicate : this.rightPredicate) {
            count += predicate.containsFieldCount(tableMeta);
        }
        return count;
    }

    @Override
    public boolean containsSubQuery() {
        boolean contains = this.leftPredicate.containsSubQuery();
        if (!contains) {
            for (IPredicate predicate : this.rightPredicate) {
                if (predicate.containsSubQuery()) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }
}
